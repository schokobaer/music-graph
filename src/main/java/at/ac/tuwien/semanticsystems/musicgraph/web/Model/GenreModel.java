package at.ac.tuwien.semanticsystems.musicgraph.web.Model;

public class GenreModel {
    private String genreWikiDataID;
    private String genreWikiDataUri;
    private String genreName;
    private int numberOfFavouriteBandsWithGenre;

    public GenreModel() {
    }

    public GenreModel(String genreWikiDataID, String genreWikiDataUri, String genreName, int numberOfFavouriteBandsWithGenre) {
        this.genreWikiDataID = genreWikiDataID;
        this.genreWikiDataUri = genreWikiDataUri;
        this.genreName = genreName;
        this.numberOfFavouriteBandsWithGenre = numberOfFavouriteBandsWithGenre;
    }

    public String getGenreWikiDataID() {
        return genreWikiDataID;
    }

    public void setGenreWikiDataID(String genreWikiDataID) {
        this.genreWikiDataID = genreWikiDataID;
    }

    public String getGenreWikiDataUri() {
        return genreWikiDataUri;
    }

    public void setGenreWikiDataUri(String genreWikiDataUri) {
        this.genreWikiDataUri = genreWikiDataUri;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public int getNumberOfFavouriteBandsWithGenre() {
        return numberOfFavouriteBandsWithGenre;
    }

    public void setNumberOfFavouriteBandsWithGenre(int numberOfFavouriteBandsWithGenre) {
        this.numberOfFavouriteBandsWithGenre = numberOfFavouriteBandsWithGenre;
    }
}
