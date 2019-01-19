package at.ac.tuwien.semanticsystems.musicgraph;


import at.ac.tuwien.semanticsystems.musicgraph.service.DiscogsService;
import at.ac.tuwien.semanticsystems.musicgraph.service.HtmlJsonLdExtractor;
import at.ac.tuwien.semanticsystems.musicgraph.service.MusicbrainzService;
import at.ac.tuwien.semanticsystems.musicgraph.service.YoutubeHistoryParser;
import github.jjbinks.bandsintown.api.BITAPI;
import github.jjbinks.bandsintown.api.BITAPIClient;
import github.jjbinks.bandsintown.dto.Artist;
import github.jjbinks.bandsintown.dto.ArtistEvent;
import github.jjbinks.bandsintown.impl.BITAPIClientImpl;
import github.jjbinks.bandsintown.impl.BITAPIImpl;
import github.jjbinks.bandsintown.impl.resource.ArtistEventsResource;
import github.jjbinks.bandsintown.impl.resource.ArtistInfoResource;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.sparql.core.DatasetImpl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner youtubeHistoryToRdfDataGraphTest(HtmlJsonLdExtractor htmlJsonLdExtractor,
                                                              YoutubeHistoryParser youtubeHistoryParser,
                                                              MusicbrainzService musicbrainzService,
                                                              DiscogsService discogsService) {
        return args -> {
            Model dataModel = ModelFactory.createDefaultModel();

            String youtubeHistoryFilePath = "resources/wiedergabeverlauf.html";
            List<YoutubeHistoryParser.YoutubeVideo> youtubeVideos = youtubeHistoryParser.parseFile(youtubeHistoryFilePath);
            LOGGER.info("Found Videos in youtube history: {}", youtubeVideos.size());

            for (YoutubeHistoryParser.YoutubeVideo video: youtubeVideos) {
                // Query for releases on musicbrainz
                List<JSONObject> queryResults = musicbrainzService.search(video.getVideoTitle());
                if (queryResults.isEmpty()) {
                    LOGGER.info("No query results for video {}", video.getVideoTitle());
                    continue;
                }
                LOGGER.info("Found query results for video {}: {}", video.getVideoTitle(), queryResults.size());

                // Get RDF Model from the first result
                String musicbrainzSongUri = musicbrainzService.getSongUrl(queryResults.get(0));
                JSONObject musicbrainzJson = htmlJsonLdExtractor.loadJsonLdByUrl(musicbrainzSongUri);
                Model musicbrainzModel = htmlJsonLdExtractor.musicbrainzModel(musicbrainzJson);
                dataModel = dataModel.union(musicbrainzModel);
                LOGGER.info("Merged musicbrainz model from {}", video.getVideoTitle());

                // Optional: Discogs Model linked by musicbrainz
                if (musicbrainzJson.has("sameAs")
                        && !discogsService.findDiscogsUri(musicbrainzJson.get("sameAs")).isEmpty()) {
                    String discogsSongUri = discogsService.findDiscogsUri(musicbrainzJson.get("sameAs"));
                    JSONObject discogsJson = htmlJsonLdExtractor.loadJsonLdByUrl(discogsSongUri);
                    if (discogsJson == null) {
                        continue;
                    }
                    Model discogsModel = htmlJsonLdExtractor.discogsModel(discogsJson);
                    dataModel = dataModel.union(discogsModel);
                    LOGGER.info("Found discogs URI for video {}", video.getVideoTitle());
                }

            }

            // Print data graph
            dataModel.write(System.out, "TURTLE");

            /*
            FUSEKI connection
             */

            // Setup and start Fuseki
            Dataset ds = new DatasetImpl(dataModel);
            FusekiServer server = FusekiServer.create()
                    .add("/dataset", ds)
                    .build() ;
            server.start() ;

            RDFConnection conn = RDFConnectionFactory.connect(ds);
            conn.load(dataModel) ;
            QueryExecution qExec = conn.query("SELECT DISTINCT ?s { ?s ?p ?o }") ;
            ResultSet rs = qExec.execSelect() ;
            while(rs.hasNext()) {
                QuerySolution qs = rs.next() ;
                Resource subject = qs.getResource("s") ;
                System.out.println("Subject: "+subject) ;
            }
            qExec.close() ;
            conn.close() ;

            server.stop();

            /*
            BandsInTOwn API Connection example
            */

            BITAPI bitapi = new BITAPIImpl("JJBinksTest");
            Client client = ClientBuilder.newClient();
            BITAPIClient bitapiClient = new BITAPIClientImpl(client, "JJBinksTest");

            // get artist information
            Artist artist =  bitapiClient.getBITResource(new ArtistInfoResource("Metallica"));
            System.out.println(artist.getName() + " " + artist.getUrl());
            LocalDate fromDate = LocalDate.now();
            LocalDate toDate = fromDate.plusYears(10);

            // list events for artist
            List<ArtistEvent> artistEvents =  bitapiClient.getBITResource(new ArtistEventsResource("Metallica", fromDate, toDate));
            for (ArtistEvent event : artistEvents) {
                System.out.println("Venue: " + event.getVenue().getCountry() + " - " + event.getVenue().getCity() + "\n" +
                        "Date: " + event.getDatetime() + "\n" +
                        "Description: " + event.getDescription() + "\n" +
                        "Event-Link" + event.getUrl() + "\n" +
                        "-----------------------");
            }
        };
    }
}
