package at.ac.tuwien.semanticsystems.musicgraph.vocab;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class MusicGraph {

    public static final String baseUri = "http://semantics.tuwien.ac.at/group4#";
    public static final String entityBaseUri = "http://semantics.tuwien.ac.at/group4/";
    private static final Model baseModel = ModelFactory.createDefaultModel();

    /** Properties **/
    public static final Property numberListenings = baseModel.createProperty(baseUri, "numberListenings");
    public static final Property listenedAt = baseModel.createProperty(baseUri, "listenedAt");
    public static final Property clickedAt = baseModel.createProperty(baseUri, "clickedAt");
    public static final Property fromDecade = baseModel.createProperty(baseUri, "fromDecade");
    public static final Property isSimilar = baseModel.createProperty(baseUri, "isSimilar");
    public static final Property numberViews = baseModel.createProperty(baseUri, "numberViews");

    /** Classes **/
    public static final Resource AmazonMusicSong = baseModel.createResource(baseUri + "AmazonMusicSong");
    public static final Resource Artist = baseModel.createResource(baseUri + "Artist");
    public static final Resource Decade = baseModel.createResource(baseUri + "Decade");
    public static final Resource Song = baseModel.createResource(baseUri + "Song");
    public static final Resource SpotifySong = baseModel.createResource(baseUri + "SpotifySong");
    public static final Resource YoutubeSongVideo = baseModel.createResource(baseUri + "YoutubeSongVideo");
    public static final Resource YoutubeVideo = baseModel.createResource(baseUri + "YoutubeVideo");


}
