package at.ac.tuwien.semanticsystems.musicgraph.web.Model;

public class ArtistModel {
    private final String name;
    private final String bestKnownSong;
    private final String url;

    public ArtistModel(String name, String bestKnownSong, String url) {
        this.name = name;
        this.bestKnownSong = bestKnownSong;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getBestKnownSong() {
        return bestKnownSong;
    }

    public String getUrl() {
        return url;
    }

}
