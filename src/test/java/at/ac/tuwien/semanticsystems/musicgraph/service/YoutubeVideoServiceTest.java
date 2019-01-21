package at.ac.tuwien.semanticsystems.musicgraph.service;

import at.ac.tuwien.semanticsystems.musicgraph.vocab.MusicGraph;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.Schema;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.WikiData;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class YoutubeVideoServiceTest {

    private YoutubeVideoService youtubeVideoService;

    @Before
    public void setup() {
        youtubeVideoService = new YoutubeVideoService();
        SimpleHttpClient httpClient = new SimpleHttpClient();
        HtmlJsonLdExtractor htmlJsonLdExtractor = new HtmlJsonLdExtractor();
        MusicbrainzService musicbrainzService = new MusicbrainzService();
        htmlJsonLdExtractor.setHttpClient(httpClient);
        musicbrainzService.setHttpClient(httpClient);
        youtubeVideoService.setHtmlJsonLdExtractor(htmlJsonLdExtractor);
        youtubeVideoService.setMusicbrainzService(musicbrainzService);
    }

    @Test
    public void getYoutubeSongVideos() {
        Model model = ModelFactory.createDefaultModel();
        Resource res = model.createResource();
        res.addProperty(RDF.type, MusicGraph.YoutubeVideo);
        res.addProperty(Schema.name, "Red Hot Chili Peppers - Snow");
        res.addProperty(MusicGraph.clickedAt, "15.01.2019, 13:35:23 MEZ");

        Model result = youtubeVideoService.getMusicVideos(model);
        result.write(System.out, "TURTLE");

        ResIterator itr = result.listResourcesWithProperty(RDF.type, MusicGraph.YoutubeSongVideo);
        Assert.assertTrue(itr.hasNext());

        res = itr.nextResource();
        Assert.assertTrue(res.hasProperty(Schema.name));
        Assert.assertEquals("Snow", res.getProperty(Schema.name).getLiteral().getString());
    }


}
