package at.ac.tuwien.semanticsystems.musicgraph.service;

import at.ac.tuwien.semanticsystems.musicgraph.imports.YoutubeDataImport;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.MusicGraph;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.Schema;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.WikiData;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;


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
/*
    @Test
    public void getOneYoutubeSong() {
        Model model = ModelFactory.createDefaultModel();

        // Denkmal - Wir Sind Helden
        Resource res = model.createResource();
        res.addProperty(RDF.type, MusicGraph.YoutubeVideo);
        res.addProperty(Schema.name, "Denkmal - Wir sind Helden");
        res.addProperty(MusicGraph.clickedAt, "15.01.2019, 13:35:23 MEZ");

        // Denkmal - Live
        Resource res2 = model.createResource();
        res2.addProperty(RDF.type, MusicGraph.YoutubeVideo);
        res2.addProperty(Schema.name, "Wir sind Helden - Denkmal (Rock am Ring 2004) LIVE");
        res2.addProperty(MusicGraph.clickedAt, "16.01.2019, 13:35:23 MEZ");

        Model result = youtubeVideoService.getMusicVideos(model);
        result.write(System.out, "TURTLE");


        // Assertions
        // Denkmal Resource
        ResIterator itr = result.listResourcesWithProperty(RDF.type, MusicGraph.YoutubeSongVideo);
        Assert.assertTrue(itr.hasNext());

        res = itr.nextResource();
        Assert.assertTrue(res.hasProperty(Schema.name));
        Assert.assertEquals("Denkmal", res.getProperty(Schema.name).getLiteral().getString());
        Assert.assertFalse(itr.hasNext());

        // Wir sind Helden Resource
        itr = result.listResourcesWithProperty(RDF.type, MusicGraph.Artist);
        Assert.assertTrue(itr.hasNext());

        res = itr.nextResource();
        Assert.assertEquals("Wir sind Helden", res.getProperty(Schema.name).getLiteral().getString());
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void getTwoSongsOfSameArtist() {
        Model model = ModelFactory.createDefaultModel();

        // Nur Ein Wort - Wir Sind Helden
        Resource res1 = model.createResource();
        res1.addProperty(RDF.type, MusicGraph.YoutubeVideo);
        res1.addProperty(Schema.name, "Wir Sind Helden - Nur Ein Wort (Video)");
        res1.addProperty(MusicGraph.clickedAt, "22.01.2018 16:34:51 MEZ");

        // Denkmal - Wir Sind Helden
        Resource res2 = model.createResource();
        res2.addProperty(RDF.type, MusicGraph.YoutubeVideo);
        res2.addProperty(Schema.name, "Denkmal - Wir sind Helden");
        res2.addProperty(MusicGraph.clickedAt, "15.01.2019, 13:35:23 MEZ");

        Model result = youtubeVideoService.getMusicVideos(model);
        result.write(System.out, "TURTLE");


        // Assertions

        // 2 Song Resources
        ResIterator itr = result.listResourcesWithProperty(RDF.type, MusicGraph.YoutubeSongVideo);
        Assert.assertTrue(itr.hasNext());
        itr.nextResource();
        Assert.assertTrue(itr.hasNext());
        itr.nextResource();
        Assert.assertFalse(itr.hasNext());


        // Wir sind Helden Resource
        itr = result.listResourcesWithProperty(RDF.type, MusicGraph.Artist);
        Assert.assertTrue(itr.hasNext());

        res1 = itr.nextResource();
        Assert.assertEquals("Wir sind Helden", res1.getProperty(Schema.name).getLiteral().getString());
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void testWithYoutubeHistoryFile() throws IOException {
        List<YoutubeVideoService.YoutubeVideo> videos = youtubeVideoService.parseFile("resources/wiedergabeverlauf.html");

        Model model = ModelFactory.createDefaultModel();
        for (YoutubeVideoService.YoutubeVideo video: videos) {
            Resource res = model.createResource();
            res.addProperty(RDF.type, MusicGraph.YoutubeVideo);
            res.addProperty(Schema.name, video.getVideoTitle());
            res.addProperty(MusicGraph.clickedAt, video.getViewDate());
        }

        Model result = youtubeVideoService.getMusicVideos(model);
        result.write(System.out, "TURTLE");
    }
*/
    @Test
    public void testDataImporter() throws IOException {

        File file = new File("resources/youtube_2.html");
        YoutubeDataImport dataImport = new YoutubeDataImport();
        dataImport.setYoutubeVideoService(youtubeVideoService);
        Model result = dataImport.importData(file);

        int sum = 0;
        StmtIterator itr = result.listStatements(null, RDF.type, MusicGraph.Artist);
        while (itr.hasNext()) {
            sum++;
            System.out.println(itr.nextStatement().getSubject().getProperty(Schema.name).getString());
        }
        System.out.println("Found Artists:" + sum);

        result.write(System.out, "TURTLE");
    }
    /*

    @Test
    public void getAllDataWithArtistsAndSongs() throws IOException {
        List<YoutubeVideoService.YoutubeVideo> videos = youtubeVideoService.parseFile("resources/wiedergabeverlauf.html");

        Model model = ModelFactory.createDefaultModel();
        for (YoutubeVideoService.YoutubeVideo video: videos) {
            Resource res = model.createResource();
            res.addProperty(RDF.type, MusicGraph.YoutubeVideo);
            res.addProperty(Schema.name, video.getVideoTitle());
            res.addProperty(MusicGraph.clickedAt, video.getViewDate());
        }

        Model result = youtubeVideoService.getMusicVideos(model);
        result = youtubeVideoService.getArtists(result);

        // Check artists
        int sum = 0;
        StmtIterator itr = result.listStatements(null, RDF.type, MusicGraph.Artist);
        while (itr.hasNext()) {
            sum++;
            System.out.println(itr.nextStatement().getSubject().getProperty(Schema.name).getString());
        }
        System.out.println("Found Artists:" + sum);

        // Check songs
        sum = 0;
        itr = result.listStatements(null, RDF.type, MusicGraph.YoutubeSongVideo);
        while (itr.hasNext()) {
            sum++;
            System.out.println(itr.nextStatement().getSubject().getProperty(Schema.name).getString());
        }
        System.out.println("Found Songs:" + sum);

    }


    @Test
    public void convertYoutubeToRdf() throws IOException {
        List<YoutubeVideoService.YoutubeVideo> videos = youtubeVideoService.parseFile("resources/youtube_2.html");

        System.out.println("Size: " + videos.size());

        Model model = ModelFactory.createDefaultModel();
        for (YoutubeVideoService.YoutubeVideo video: videos) {
            Resource res = model.getResource("http://sematics.tuwien.ac.at/group4/videos/" + URLEncoder.encode(video.getVideoTitle(), "UTF-8"));
            res.addProperty(RDF.type, MusicGraph.YoutubeVideo);
            res.addProperty(Schema.name, video.getVideoTitle());
            res.addProperty(MusicGraph.clickedAt, video.getViewDate());
        }

        model.write(new FileOutputStream("resources/youtube_2.ttl"), "TURTLE");


    }

    @Test
    public void getFirst100Songs() throws IOException {
        System.out.println("Started parsing");
        List<YoutubeVideoService.YoutubeVideo> videos = youtubeVideoService.parseFile("resources/youtube_2.html");
        System.out.println("Finished Parsing");

        System.out.println("Size: " + videos.size());

        long time = System.currentTimeMillis();

        Model model = ModelFactory.createDefaultModel();
        Model targetModel = ModelFactory.createDefaultModel();
        for (int i = 0; i < videos.size() && i < 100; i++) {
            YoutubeVideoService.YoutubeVideo video = videos.get(i);
            Resource res = model.getResource("http://sematics.tuwien.ac.at/group4/videos/" + URLEncoder.encode(video.getVideoTitle(), "UTF-8"));
            res.addProperty(RDF.type, MusicGraph.YoutubeVideo);
            res.addProperty(Schema.name, video.getVideoTitle());
            res.addProperty(MusicGraph.clickedAt, video.getViewDate());
            System.out.print("\rConverting " + i);
            targetModel = targetModel.union(youtubeVideoService.getMusicVideo(res, targetModel));
        }
        System.out.println("Finished Converting: " + ((System.currentTimeMillis() - time) / 1000) + " seconds");

        // Check artists
        int sum = 0;
        StmtIterator itr = targetModel.listStatements(null, RDF.type, MusicGraph.Artist);
        while (itr.hasNext()) {
            sum++;
            System.out.println(itr.nextStatement().getSubject().getProperty(Schema.name).getString());
        }
        System.out.println("Found Artists:" + sum);

        // Check songs
        sum = 0;
        itr = targetModel.listStatements(null, RDF.type, MusicGraph.YoutubeSongVideo);
        while (itr.hasNext()) {
            sum++;
            Resource song = itr.nextStatement().getSubject();

            System.out.println(song.getProperty(Schema.name).getString() + ":" + song.getProperty(MusicGraph.listenedAt));
        }
        System.out.println("Found Songs:" + sum);
    } */

}
