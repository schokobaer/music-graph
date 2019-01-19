package at.ac.tuwien.semanticsystems.musicgraph.service;

import at.ac.tuwien.semanticsystems.musicgraph.vocab.Schema;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

@Component
public class MusicbrainzService {

    private static final String SEARCH_BASE_URL = "https://musicbrainz.org/ws/2/release?fmt=json&query=";
    private static final String BASE_URL = "https://musicbrainz.org/release/";

    @Autowired
    private SimpleHttpClient httpClient;

    public List<JSONObject> search(String query) {
        try {
            String response = httpClient.get(SEARCH_BASE_URL + URLEncoder.encode(query, "UTF-8") + "&limit=1");
            JSONObject json = new JSONObject(response);
            if (!json.has("releases")) {
                return new LinkedList<>();
            }
            JSONArray releases = json.getJSONArray("releases");
            List<JSONObject> results = new LinkedList<>();
            for (int i = 0; i < releases.length(); i++) {
                JSONObject rel = releases.getJSONObject(i);
                if (!rel.has("score") || rel.getInt("score") < 100) {
                    break;
                }
                results.add(rel);
            }
            return results;
        } catch (UnsupportedEncodingException e) {
            return new LinkedList<>();
        }
    }

    public String getSongUrl(JSONObject json) {
        if (json.has("id")) {
            return BASE_URL + json.getString("id");
        }

        return "";
    }

    public Resource findSongResource(Model model) {
        ResIterator itr = model.listResourcesWithProperty(RDF.type, Schema.MusciAlbum);
        if (!itr.hasNext()) {
            itr = model.listResourcesWithProperty(RDF.type, Schema.MusicRelease);
        }

        return itr.hasNext() ? itr.nextResource() : null;
    }

}
