package at.ac.tuwien.semanticsystems.musicgraph;


import at.ac.tuwien.semanticsystems.musicgraph.service.DiscogsService;
import at.ac.tuwien.semanticsystems.musicgraph.service.HtmlJsonLdExtractor;
import at.ac.tuwien.semanticsystems.musicgraph.service.MusicbrainzService;
import at.ac.tuwien.semanticsystems.musicgraph.service.YoutubeVideoService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
