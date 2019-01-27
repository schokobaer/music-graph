package at.ac.tuwien.semanticsystems.musicgraph.web.Model;

public class CountryModel {
    private String countryWikiDataID;
    private String countryWikiDataUri;
    private String countryName;
    private int numberOfFavouriteBandsInCountry;

    public CountryModel() {
    }

    public CountryModel(String countryWikiDataID, String countryWikiDataUri, String countryName, int numberOfFavouriteBandsInCountry) {
        this.countryWikiDataID = countryWikiDataID;
        this.countryWikiDataUri = countryWikiDataUri;
        this.countryName = countryName;
        this.numberOfFavouriteBandsInCountry = numberOfFavouriteBandsInCountry;
    }

    public String getCountryWikiDataID() {
        return countryWikiDataID;
    }

    public void setCountryWikiDataID(String countryWikiDataID) {
        this.countryWikiDataID = countryWikiDataID;
    }

    public String getCountryWikiDataUri() {
        return countryWikiDataUri;
    }

    public void setCountryWikiDataUri(String countryWikiDataUri) {
        this.countryWikiDataUri = countryWikiDataUri;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getNumberOfFavouriteBandsInCountry() {
        return numberOfFavouriteBandsInCountry;
    }

    public void setNumberOfFavouriteBandsInCountry(int numberOfFavouriteBandsInCountry) {
        this.numberOfFavouriteBandsInCountry = numberOfFavouriteBandsInCountry;
    }
}
