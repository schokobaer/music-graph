package at.ac.tuwien.semanticsystems.musicgraph;


import at.ac.tuwien.semanticsystems.musicgraph.service.DiscogsService;
import at.ac.tuwien.semanticsystems.musicgraph.service.HtmlJsonLdExtractor;
import at.ac.tuwien.semanticsystems.musicgraph.service.MusicbrainzService;
import at.ac.tuwien.semanticsystems.musicgraph.service.YoutubeVideoService;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.MusicGraph;
import com.taxonic.carml.engine.RmlMapper;
import com.taxonic.carml.logical_source_resolver.JsonPathResolver;
import com.taxonic.carml.model.TriplesMap;
import com.taxonic.carml.util.RmlMappingLoader;
import com.taxonic.carml.vocab.Rdf;
import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.Dataset;
import github.jjbinks.bandsintown.api.BITAPI;
import github.jjbinks.bandsintown.api.BITAPIClient;
import github.jjbinks.bandsintown.dto.Artist;
import github.jjbinks.bandsintown.dto.ArtistEvent;
import github.jjbinks.bandsintown.exception.BITException;
import github.jjbinks.bandsintown.impl.BITAPIClientImpl;
import github.jjbinks.bandsintown.impl.BITAPIImpl;
import github.jjbinks.bandsintown.impl.resource.ArtistEventsResource;
import github.jjbinks.bandsintown.impl.resource.ArtistInfoResource;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.Schema;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner youtubeHistoryToRdfDataGraphTest(HtmlJsonLdExtractor htmlJsonLdExtractor,
                                                              YoutubeVideoService youtubeVideoService,
                                                              MusicbrainzService musicbrainzService,
                                                              DiscogsService discogsService) {
        return args -> {
            Model dataModel = ModelFactory.createDefaultModel();

            String youtubeHistoryFilePath = "resources/wiedergabeverlauf.html";
            List<YoutubeVideoService.YoutubeVideo> youtubeVideos = youtubeVideoService.parseFile(youtubeHistoryFilePath);
            LOGGER.info("Found Videos in youtube history: {}", youtubeVideos.size());

            for (YoutubeVideoService.YoutubeVideo video: youtubeVideos) {

                Set<TriplesMap> mapping =
                        RmlMappingLoader
                                .build()
                                .load(RDFFormat.TURTLE, Paths.get("resources/spotifyMapping.ttl"));

                String test = Paths.get("resources").toAbsolutePath().toString();
                String abc = "";
                RmlMapper mapper =
                        RmlMapper
                                .newBuilder()
                                // Add the resolvers to suit your need
                                .setLogicalSourceResolver(Rdf.Ql.JsonPath, new JsonPathResolver())
                                // optional:
                                // specify IRI unicode normalization form (default = NFC)
                                // see http://www.unicode.org/unicode/reports/tr15/tr15-23.html
                                .iriUnicodeNormalization(Normalizer.Form.NFKC)
                                // set file directory for sources in mapping
                                .fileResolver(Paths.get("resources/data"))
                                .build();

                org.eclipse.rdf4j.model.Model result = mapper.map(mapping);
                Rio.write(result,System.out,RDFFormat.TURTLE);




            // Print data graph
            dataModel.write(System.out, "TURTLE");

            /*
            FUSEKI connection example
             */

            // Setup and start Fuseki
            Dataset ds = new DatasetImpl(dataModel);
            FusekiServer server = FusekiServer.create()
                    .add("/dataset", ds)
                    .build();
            server.start();

            RDFConnection conn = RDFConnectionFactory.connect(ds);
            conn.load(dataModel);
            QueryExecution qExec = conn.query("PREFIX schema: <http://schema.org/> " +
                    "SELECT DISTINCT ?artistname " +
                    "WHERE  { " +
                    "?song a schema:MusicRelease. " +
                    "?song schema:creditedTo ?artist. " +
                    "?artist schema:name ?artistname" +
                    "}");
            ResultSet rs = qExec.execSelect();

            // SPARQL query example for all artists in model

            List<String> artistNameList = new ArrayList<>();
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                Literal artistName = qs.getLiteral("artistname");
                if (artistName == null) {
                    continue;
                }
                LOGGER.info("Artist: {}", artistName);
                artistNameList.add(artistName.toString());
            }
            qExec.close();
            conn.close();

            server.stop();

            /*
            BandsInTown API Connection example
            */

            BITAPI bitapi = new BITAPIImpl("JJBinksTest");
            Client client = ClientBuilder.newClient();
            BITAPIClient bitapiClient = new BITAPIClientImpl(client, "JJBinksTest");


            // list events for artist

            for (String artistName : artistNameList) {

                System.out.println("****************" + artistName + "****************");

                // example get artist information

                try {
                    Artist artist = bitapiClient.getBITResource(new ArtistInfoResource(artistName));
                    System.out.println(artist.getName() + " " + artist.getUrl());

                } catch (BITException e) {
                    System.err.println("No information for this artist found");
                }

                // example get upcoming events for artist

                System.out.println("--> UPCOMING EVENTS:");
                LocalDate fromDate = LocalDate.now();
                LocalDate toDate = fromDate.plusYears(10);
                try {
                    List<ArtistEvent> artistEvents = bitapiClient.getBITResource(new ArtistEventsResource(artistName, fromDate, toDate));
                    for (ArtistEvent event : artistEvents) {
                        System.out.println("Venue: " + event.getVenue().getCountry() + " - " + event.getVenue().getCity() + "\n" +
                                "Date: " + event.getDatetime() + "\n" +
                                "Description: " + event.getDescription() + "\n" +
                                "Event-Link" + event.getUrl() + "\n" +
                                "-----------------------");
                    }
                } catch (BITException e) {
                    System.err.println("No events for this artist found");
                }
            }
            }
        };
    }
}
