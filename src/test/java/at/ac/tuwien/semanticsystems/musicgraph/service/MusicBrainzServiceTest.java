package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.junit.Assert;
import org.junit.Test;

public class MusicBrainzServiceTest {


    @Test
    public void findWikiDataResourceForLinkinPark() {
        MusicbrainzService musicbrainzService = new MusicbrainzService();
        HtmlJsonLdExtractor jsonLdExtractor = new HtmlJsonLdExtractor();
        SimpleHttpClient httpClient = new SimpleHttpClient();

        jsonLdExtractor.setHttpClient(httpClient);
        musicbrainzService.setHttpClient(httpClient);
        musicbrainzService.setHtmlJsonLdExtractor(jsonLdExtractor);

        String wikiUrl = musicbrainzService.getWikidataResourceByArtistName("Linkin Park");
        Assert.assertEquals("https://www.wikidata.org/wiki/Q261", wikiUrl);
    }
}
