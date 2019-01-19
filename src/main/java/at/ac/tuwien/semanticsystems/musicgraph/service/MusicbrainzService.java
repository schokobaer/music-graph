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


    private static final String BASE_URL = "https://musicbrainz.org/";

    @Autowired
    private SimpleHttpClient httpClient;

    public List<JSONObject> searchSong(String query) {
        String SEARCH_BASE_URL = "https://musicbrainz.org/ws/2/release?fmt=json&query=";
        try {
            String response = httpClient.get(SEARCH_BASE_URL + URLEncoder.encode(query, "UTF-8") + "&limit=5");
            if (response == null) {
                return new LinkedList<>();
            }
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

    public List<JSONObject> searchArtist(String query) {
        String SEARCH_BASE_URL = "https://musicbrainz.org/ws/2/artist?fmt=json&query=";
        try {
            String response = httpClient.get(SEARCH_BASE_URL + URLEncoder.encode(query, "UTF-8") + "&limit=5");
            if (response == null) {
                return new LinkedList<>();
            }
            JSONObject json = new JSONObject(response);
            if (!json.has("artists")) {
                return new LinkedList<>();
            }
            JSONArray releases = json.getJSONArray("artists");
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
            return BASE_URL + "release/" +  json.getString("id");
        }

        return "";
    }

    public String getArtistUrl(JSONObject json) {
        if (json.has("id")) {
            return BASE_URL + "artist/" + json.getString("id");
        }

        return "";
    }

    public Resource findSongResource(Model model) {
        ResIterator itr = model.listResourcesWithProperty(RDF.type, Schema.MusicRelease);
        if (!itr.hasNext()) {
            itr = model.listResourcesWithProperty(RDF.type, Schema.MusicAlbum);
        }

        return itr.hasNext() ? itr.nextResource() : null;
    }

    public Resource findArtistResource(Model model) {
        ResIterator itr = model.listResourcesWithProperty(RDF.type, Schema.MusicGroup);

        return itr.hasNext() ? itr.nextResource() : null;
    }

}
