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
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionLocal;
import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
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
    public RDFConnection startServer(@Value("${tbd.path}") String path) throws IOException {
        Model dataModel = ModelFactory.createDefaultModel();
        File tbdFile = new File(path);
        if (!tbdFile.exists()) {
            tbdFile.createNewFile();
        }
        dataModel.read(tbdFile.toURI().toURL().toString());
        Dataset ds = new DatasetImpl(dataModel);
        FusekiServer server = FusekiServer.create()
                .setPort(8081)
                .add("/musicgraph", ds)
                .build();
        server.start();
        RDFConnection con = new RDFConnectionLocal(ds);
        return con;
    }

    @Bean
    public CommandLineRunner youtubeHistoryToRdfDataGraphTest(HtmlJsonLdExtractor htmlJsonLdExtractor,
                                                              YoutubeVideoService youtubeVideoService,
                                                              MusicbrainzService musicbrainzService,
                                                              DiscogsService discogsService) {
        return args -> {
            Model dataModel = ModelFactory.createDefaultModel();




            /*RDFConnection conn = RDFConnectionFactory.connect(ds);
            conn.load(dataModel);
            QueryExecution qExec = conn.query("PREFIX schema: <http://schema.org/> " +
                    "SELECT DISTINCT ?artistname " +
                    "WHERE  { " +
                    "?song a schema:MusicRelease. " +
                    "?song schema:creditedTo ?artist. " +
                    "?artist schema:name ?artistname" +
                    "}");
            ResultSet rs = qExec.execSelect();
*/
            // SPARQL query example for all artists in model
/*
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
            conn.close();*/

            /*
            BandsInTown API Connection example
            */

            /*BITAPI bitapi = new BITAPIImpl("JJBinksTest");
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
            }*/
        };
    }
}
