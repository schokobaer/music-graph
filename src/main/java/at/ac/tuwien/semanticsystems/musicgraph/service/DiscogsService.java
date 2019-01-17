package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.springframework.stereotype.Component;

@Component
public class DiscogsService {

    private static final String BASE_URI = "https://www.discogs.com/release/";

    private String findDiscogsUriList(String[] uris) {
        for (String uri: uris) {
            if (uri.startsWith(BASE_URI)) {
                return uri;
            }
        }
        return "";
    }

    private String findDiscogsUriString(String uri) {
        return uri.startsWith(BASE_URI) ? uri : "";
    }

    public String findDiscogsUri(Object obj) {
        if (obj instanceof String[]) {
            return findDiscogsUriList((String[]) obj);
        } else if (obj instanceof JSONArray) {
            JSONArray arr = (JSONArray) obj;
            String[] strings = new String[arr.length()];
            for (int i = 0; i < strings.length; i++) {
                strings[i] = arr.getString(i);
            }
            return findDiscogsUriList(strings);
        } else if (obj instanceof String) {
            return findDiscogsUriString((String) obj);
        }

        return "";
    }
}
