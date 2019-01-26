package at.ac.tuwien.semanticsystems.musicgraph.service;

import at.ac.tuwien.semanticsystems.musicgraph.vocab.WikiData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class WikiDataQueryTest {

    private WikiDataQueryService wikiDataQueryService;

    @Before
    public void setup() {
        wikiDataQueryService = new WikiDataQueryService();
        SimpleHttpClient httpClient = new SimpleHttpClient();
        MusicbrainzService musicbrainzService = new MusicbrainzService();
        musicbrainzService.setHttpClient(httpClient);
        HtmlJsonLdExtractor htmlJsonLdExtractor = new HtmlJsonLdExtractor();
        htmlJsonLdExtractor.setHttpClient(httpClient);
        musicbrainzService.setHtmlJsonLdExtractor(htmlJsonLdExtractor);
        WikidataService wikidataService = new WikidataService();
        wikiDataQueryService.setWikidataServices(wikidataService,musicbrainzService);
    }

    @Test
    public void testGetSimilarArtistsForAlterBridge() {
        Map<String, String> map = wikiDataQueryService.getSimilarArtistsGenre("Alter Bridge");
        Assert.assertTrue(map.keySet().contains("As Lions"));
        map = wikiDataQueryService.getSimilarArtistsCountry("Alter Bridge");
        Assert.assertTrue(map.keySet().contains("Taylor Swift"));
        map = wikiDataQueryService.getSimilarArtistsDecade("Alter Bridge");
        Assert.assertTrue(map.keySet().contains("Snarky Puppy"));
        map = wikiDataQueryService.getSimilarArtistsDecade("Taylor Swift");
        Assert.assertTrue(map.isEmpty());
    }
}
