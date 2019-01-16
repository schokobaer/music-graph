package at.ac.tuwien.semanticsystems.musicgraph;


import at.ac.tuwien.semanticsystems.musicgraph.service.HtmlJsonLdExtractor;
import at.ac.tuwien.semanticsystems.musicgraph.service.YoutubeHistoryParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner testYoutube(YoutubeHistoryParser youtubeHistoryParser) {
        return args -> {
            String youtubeHistoryFilePath = "C:\\Users\\Andreas\\Desktop\\tmp\\aic-takeout\\takeout-20190115T123738Z-001\\Takeout\\YouTube\\Verlauf\\Wiedergabeverlauf.html";
            youtubeHistoryParser.parseFile(youtubeHistoryFilePath);
        };
    }

    @Bean
    public CommandLineRunner testMusicBrainz(HtmlJsonLdExtractor htmlJsonLdExtractor) {
        return args -> {
            String musicBrainzUrl = "https://musicbrainz.org/release/9a56edc6-3a60-395f-8b8a-57f79c107b05";
            String doscogsUrl = "https://www.discogs.com/The-Beatles-Twist-And-Shout/release/616506";
            htmlJsonLdExtractor.musicbrainzModel(htmlJsonLdExtractor.loadJsonLdByUrl(musicBrainzUrl));
        };
    }
}
