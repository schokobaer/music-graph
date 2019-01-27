package at.ac.tuwien.semanticsystems.musicgraph.web.Model;

public class ArtistModel {
    private String artistTdbUri;
    private String artistWikiDataUri;
    private String artistWikiDataID;
    private int numberOfSongsListened;
    private String artistName;

    public ArtistModel() {
    }

    public ArtistModel(String artistTdbUri, String artistName, String artistWikiDataUri, String artistWikiDataID, int numberOfSongsListened) {
        this.artistTdbUri = artistTdbUri;
        this.artistName = artistName;
        this.artistWikiDataUri = artistWikiDataUri;
        this.artistWikiDataID = artistWikiDataID;
        this.numberOfSongsListened = numberOfSongsListened;
    }

    public String getArtistTdbUri() {
        return artistTdbUri;
    }

    public void setArtistTdbUri(String artistTdbUri) {
        this.artistTdbUri = artistTdbUri;
    }

    public String getArtistWikiDataUri() {
        return artistWikiDataUri;
    }

    public void setArtistWikiDataUri(String artistWikiDataUri) {
        this.artistWikiDataUri = artistWikiDataUri;
    }

    public String getArtistWikiDataID() {
        return artistWikiDataID;
    }

    public void setArtistWikiDataID(String artistWikiDataID) {
        this.artistWikiDataID = artistWikiDataID;
    }

    public int getNumberOfSongsListened() {
        return numberOfSongsListened;
    }

    public void setNumberOfSongsListened(int numberOfSongsListened) {
        this.numberOfSongsListened = numberOfSongsListened;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
