package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.junit.Before;
import org.junit.Test;


public class DataLinkingServiceTest {


    private DataLinkingService dataLinkingService;
    private MusicbrainzService musicbrainzService;

    @Before
    public void setup() {
        dataLinkingService = new DataLinkingService();
        musicbrainzService = new MusicbrainzService();
        HtmlJsonLdExtractor jsonLdExtractor = new HtmlJsonLdExtractor();
        SimpleHttpClient httpClient = new SimpleHttpClient();

        jsonLdExtractor.setHttpClient(httpClient);
        musicbrainzService.setHttpClient(httpClient);
        musicbrainzService.setHtmlJsonLdExtractor(jsonLdExtractor);

        dataLinkingService.setWikidataService(new WikidataService());
    }

    @Test
    public void getSimilarArtistsForAlterBridge() {
        /* get artist id */
        String wikidataId = musicbrainzService.getWikidataResourceByArtistName("Alter Bridge");
        wikidataId = "wd:" + wikidataId.substring(wikidataId.lastIndexOf('Q'));
        dataLinkingService.addSimilarArtists(wikidataId);


    }
}
