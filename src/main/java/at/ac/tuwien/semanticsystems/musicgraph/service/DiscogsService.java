package at.ac.tuwien.semanticsystems.musicgraph.service;

import at.ac.tuwien.semanticsystems.musicgraph.util.JsonUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jena.rdf.model.Model;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Deprecated
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

    public Model discogsModel(JSONObject json) {
        JSONObject neededJson = JsonUtil.copyOptionalFields(json,
                "name", "@id", "@context", "@type", "releaseOf");
        return JsonUtil.jsonLdToModel(neededJson, "https://www.discogs.com/");
    }
}
