package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
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