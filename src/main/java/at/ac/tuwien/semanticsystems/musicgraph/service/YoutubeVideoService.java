package at.ac.tuwien.semanticsystems.musicgraph.service;

import at.ac.tuwien.semanticsystems.musicgraph.vocab.MusicGraph;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.Schema;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class YoutubeVideoService {

    private final static String SEARCH_STRING = "<a href=\"https://www.youtube.com/watch?v=";
    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");

    private static final Logger LOGGER = LoggerFactory.getLogger(YoutubeVideoService.class);

    public List<YoutubeVideo> parseFile(String path) throws IOException {
        String fileContent = new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
        List<YoutubeVideo> videos = new LinkedList<>();

        while (fileContent.contains(SEARCH_STRING)) {
            int posStart = fileContent.indexOf(SEARCH_STRING);
            posStart = fileContent.indexOf(">", posStart + SEARCH_STRING.length()) + 1;
            int posEnd = fileContent.indexOf("<", posStart);
            String title = StringEscapeUtils.unescapeHtml(fileContent.substring(posStart, posEnd));

            posStart = fileContent.indexOf("<br>", posEnd) + 4;
            posStart = fileContent.indexOf("<br>", posStart) + 4;
            posEnd = fileContent.indexOf("<", posStart);
            String viewDate = fileContent.substring(posStart, posEnd);

            YoutubeVideo video = new YoutubeVideo(title, viewDate);
            videos.add(video);
            fileContent = fileContent.substring(posEnd);
        }

        return videos;

    }


    private MusicbrainzService musicbrainzService;
    private HtmlJsonLdExtractor htmlJsonLdExtractor;

    @Autowired
    public void setMusicbrainzService(MusicbrainzService musicbrainzService) {
        this.musicbrainzService = musicbrainzService;
    }

    @Autowired
    public void setHtmlJsonLdExtractor(HtmlJsonLdExtractor htmlJsonLdExtractor) {
        this.htmlJsonLdExtractor = htmlJsonLdExtractor;
    }


    /**
     * Searches for the given videotitel for a song. If a song found it creates a new Resource
     * and also creates a resoucre for the artist of the song in the given model. If no song
     * was found it returns null.
     *
     * @param video
     * @param model
     * @return
     */
    public Resource getMusicVideo(Resource video, Model model) {
        String videoTitle = video.getProperty(Schema.name).getLiteral().getString();

        List<JSONObject> queryResults = musicbrainzService.searchSong(videoTitle);
        if (queryResults.isEmpty()) {
            LOGGER.info("No query results for video {}", videoTitle);
        }
        LOGGER.info("Found query results for video {}: {}", videoTitle, queryResults.size());

        // Get Song RDF Model from MusicBrainz
        Model songModel = null;
        Resource songResource = null;
        JSONObject songJson = null;
        for (int i = 0; i < queryResults.size() && songModel == null; i++) {
            String musicbrainzSongUri = musicbrainzService.getSongUrl(queryResults.get(i));
            songJson = htmlJsonLdExtractor.loadJsonLdByUrl(musicbrainzSongUri);
            if (songJson == null) {
                continue;
            }
            songModel = musicbrainzService.getSongModel(songJson);

            if (songModel == null) {
                continue;
            }

            // Check if Song title is in youtube video title
            Resource songResourceMusicBrainz = musicbrainzService.findSongResource(songModel);
            String songTitle = songResourceMusicBrainz.getProperty(Schema.name).getString();
            if (!videoTitle.toLowerCase().contains(songTitle.toLowerCase())) {
                continue;
            }

            // Check if the artist is mentioned in the video title
            String artist = songJson.getString("creditedTo");
            if (!videoTitle.toLowerCase().contains(artist.toLowerCase())) {
                continue;
            }

            // now we know it is the right result !!!
            // Make song resource and check if it is already in the model
            // Create the Song Resource
            String songUri = null;
            String artistUri = null;
            try {
                artistUri = URLDecoder.decode(artist.toLowerCase().replaceAll(" ", "_"), "UTF-8");
                songUri = URLDecoder.decode(MusicGraph.entityBaseUri + artistUri + "_-_" + songTitle.toLowerCase().replaceAll(" ", "_"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            songResource = model.getResource(songUri);
            if (songResource.hasProperty(RDF.type)) {
                // Add timestamps and exit
                songResource.addProperty(MusicGraph.listenedAt, video.getProperty(MusicGraph.clickedAt).getObject());
                return songResource;
            }

            // It is a new song, so get the artist first
            Resource artistResource = model.getResource(MusicGraph.entityBaseUri + artistUri);
            if (!artistResource.hasProperty(RDF.type, MusicGraph.Artist)) {
                // Search the Artist
                List<JSONObject> artistQueryResults = musicbrainzService.searchArtist(artist);
                if (artistQueryResults.isEmpty()) {
                    LOGGER.info("No query results for artist {}", artist);
                    continue;
                }
                Model artistModel = null;
                String artistUriMusicBrainz = "";
                int j = 0;
                while (artistModel == null) {
                    artistUriMusicBrainz = musicbrainzService.getArtistUrl(artistQueryResults.get(j));
                    JSONObject artistJson = htmlJsonLdExtractor.loadJsonLdByUrl(artistUriMusicBrainz);
                    artistModel = musicbrainzService.getArtistModel(artistJson);
                }
                if (artistModel == null) {
                    LOGGER.info("Could not find an Artist on musicBrainz for {}", artist);
                    continue;
                }

                // Search for wikiData -> sameAs for song and artist
                Resource artistResourceMusicBrainz = musicbrainzService.findArtistResource(artistModel);
                StmtIterator stmtItr = artistResourceMusicBrainz.listProperties(Schema.sameAs);
                while (stmtItr.hasNext()) {
                    artistResource.addProperty(OWL.sameAs, stmtItr.nextStatement().getObject());
                }
                artistResource.addProperty(OWL.sameAs, model.getResource(artistUriMusicBrainz));

                artistResource.addProperty(Schema.name, artist);
                artistResource.addProperty(RDF.type, MusicGraph.Artist);

            }


            if (!songResource.hasProperty(RDF.type, MusicGraph.YoutubeSongVideo)) {
                songResource.addProperty(RDF.type, MusicGraph.YoutubeSongVideo);
                songResource.addProperty(Schema.creditedTo, artistResource);
                songResource.addProperty(Schema.name, songResourceMusicBrainz.getProperty(Schema.name).getObject());
                songResource.addProperty(OWL.sameAs, video);
            }
            songResource.addProperty(MusicGraph.listenedAt, video.getProperty(MusicGraph.clickedAt).getObject());

        }

        return songResource;
    }

    public Model getMusicVideos(Model model) {

        ResIterator itr = model.listResourcesWithProperty(RDF.type, MusicGraph.YoutubeVideo);
        while (itr.hasNext()) {
            Resource video = itr.nextResource();

            getMusicVideo(video, model);

        }

        return model;
    }

    /**
     * Looks for the artist given by the video and creates a new resource
     * in the given model. The artist resource gets filled with necessary data
     * and gets returned. If no artist is found by the video, it returns null.
     * @param video
     * @param model Model where to create the artist resource.
     * @return
     */
    public Resource getArtist(Resource video, Model model) {
        String videoTitle = video.getProperty(Schema.name).getLiteral().getString();

        List<JSONObject> artistQueryResults = musicbrainzService.searchArtist(videoTitle);
        if (artistQueryResults.isEmpty()) {
            LOGGER.info("No query results for artist {}", videoTitle);
            return null;
        }

        Model artistModel = null;
        Resource artistResource = null;
        int j = 0;
        while (artistModel == null) {
            String artistUri = musicbrainzService.getArtistUrl(artistQueryResults.get(j));
            JSONObject artistJson = htmlJsonLdExtractor.loadJsonLdByUrl(artistUri);
            artistModel = musicbrainzService.getArtistModel(artistJson);

            if (artistModel == null) {
                LOGGER.info("Could not find an Artist on musicBrainz for {}", videoTitle);
                j++;
                continue;
            }

            Resource artistResourceMusicBrainz = musicbrainzService.findArtistResource(artistModel);
            String artist = artistResourceMusicBrainz.getProperty(Schema.name).getString();
            if (!videoTitle.toLowerCase().contains(artist.toLowerCase())) {
                j++;
                continue;
            }

            String artistUrified = null;
            try {
                artistUrified = URLDecoder.decode(artist.toLowerCase().replaceAll(" ", "_"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            artistResource = model.getResource(MusicGraph.entityBaseUri + artistUrified);
            if (artistResource.hasProperty(RDF.type, MusicGraph.Artist)) {
                continue;
            }


            StmtIterator stmtItr = artistResourceMusicBrainz.listProperties(Schema.sameAs);
            while (stmtItr.hasNext()) {
                artistResource.addProperty(OWL.sameAs, stmtItr.nextStatement().getObject());
            }
            artistResource.addProperty(OWL.sameAs, model.getResource(artistUri));

            artistResource.addProperty(Schema.name, artist);
            artistResource.addProperty(RDFS.label, artist);
            artistResource.addProperty(RDF.type, MusicGraph.Artist);

        }

        return artistResource;
    }

    public Model getArtists(Model model) {

        ResIterator itr = model.listResourcesWithProperty(RDF.type, MusicGraph.YoutubeVideo);
        while (itr.hasNext()) {
            Resource video = itr.nextResource();

            getArtist(video, model);
        }

        return model;
    }

    public static class YoutubeVideo {
        private String videoTitle;
        private String viewDate;

        public YoutubeVideo(String videoTitle, String viewDate) {
            this.videoTitle = videoTitle;
            this.viewDate = viewDate;
        }

        public String getVideoTitle() {
            return videoTitle;
        }

        public void setVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
        }

        public String getViewDate() {
            return viewDate;
        }

        public void setViewDate(String viewDate) {
            this.viewDate = viewDate;
        }
    }


}
