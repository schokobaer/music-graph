package at.ac.tuwien.semanticsystems.musicgraph;


import at.ac.tuwien.semanticsystems.musicgraph.service.DiscogsService;
import at.ac.tuwien.semanticsystems.musicgraph.service.HtmlJsonLdExtractor;
import at.ac.tuwien.semanticsystems.musicgraph.service.MusicbrainzService;
import at.ac.tuwien.semanticsystems.musicgraph.service.YoutubeHistoryParser;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.MusicGraph;
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
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.system.Txn;
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
import java.util.ArrayList;
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

            for (YoutubeHistoryParser.YoutubeVideo video : youtubeVideos) {
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

                // Add date and increase listenings
                Resource songResource = musicbrainzService.findSongResource(musicbrainzModel);
                if (songResource != null) {
                    songResource.addProperty(MusicGraph.listenedAt, video.getViewDate());
                    int amountListenings = 1;
                    if (songResource.hasProperty(MusicGraph.numberOfListenings)) {
                        amountListenings = songResource.getProperty(MusicGraph.numberOfListenings).getObject().asLiteral().getInt();
                        amountListenings++;
                        songResource.removeAll(MusicGraph.numberOfListenings);
                    }
                    songResource.addProperty(MusicGraph.numberOfListenings, amountListenings + "");
                }

                // Merge
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
            //dataModel.write(System.out, "TURTLE");

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
                    "SELECT DISTINCT ?artist " +
                    "WHERE  { " +
                    "?song a schema:MusicRelease. " +
                    "?song schema:creditedTo ?artist. " +
                    "}");
            ResultSet rs = qExec.execSelect();

            // SPARQL query example for all artists in model

            List<String> artistNameList = new ArrayList<>();
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                Literal artistName = qs.getLiteral("artist");
//                System.out.println("Artist: " + artistName);
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
        };
    }
}
