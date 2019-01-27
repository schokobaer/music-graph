package at.ac.tuwien.semanticsystems.musicgraph.web.Model;

public class DecadeModel {
    private String year;
    private String decadeName;
    private int numberOfFavouriteBandsInDecade;

    public DecadeModel() {
    }

    public DecadeModel(String year, String decadeName, int numberOfFavouriteBandsInDecade) {
        this.year = year;
        this.decadeName = decadeName;
        this.numberOfFavouriteBandsInDecade = numberOfFavouriteBandsInDecade;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDecadeName() {
        return decadeName;
    }

    public void setDecadeName(String decadeName) {
        this.decadeName = decadeName;
    }

    public int getNumberOfFavouriteBandsInDecade() {
        return numberOfFavouriteBandsInDecade;
    }

    public void setNumberOfFavouriteBandsInDecade(int numberOfFavouriteBandsInDecade) {
        this.numberOfFavouriteBandsInDecade = numberOfFavouriteBandsInDecade;
    }
}
