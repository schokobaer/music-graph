package at.ac.tuwien.semanticsystems.musicgraph.service.amazonArtistMapping;

import io.github.bonigarcia.wdm.DriverManagerType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AmazonArtistIDToNameMapperThread extends Thread {


    public static Map<String, String> getArtisIdNameMap(Set<String> artistIDs) {
        HashMap<String, String> mapping = new HashMap<>();

        WebDriverManager.getInstance(DriverManagerType.CHROME).setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");

        ChromeDriver driver = new ChromeDriver(options);

        for (String artistId: artistIDs) {
            boolean found = false;
            try {
                driver.get("https://music.amazon.de/artists/" + artistId);
                sleep(300);
                for (int i = 200; i > 0 && !found; i--) {
                    String title = driver.getTitle();
                    if (title.startsWith("Jetzt")) {
                        int artistEndIndex = title.indexOf(" bei Amazon Music Unlimited streamen");
                        mapping.put(artistId,title.substring(6, artistEndIndex));
                        found = true;
                    } else {
                        sleep(50);
                    }

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        driver.close();
        return mapping;
    }

}
