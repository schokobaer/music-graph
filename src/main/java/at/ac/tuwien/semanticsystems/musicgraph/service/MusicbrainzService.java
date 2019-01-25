package at.ac.tuwien.semanticsystems.musicgraph.service;

import at.ac.tuwien.semanticsystems.musicgraph.util.JsonUtil;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.Schema;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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


    private SimpleHttpClient httpClient;
    private HtmlJsonLdExtractor htmlJsonLdExtractor;

    @Autowired
    public void setHttpClient(SimpleHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Autowired
    public void setHtmlJsonLdExtractor(HtmlJsonLdExtractor htmlJsonLdExtractor) {
        this.htmlJsonLdExtractor = htmlJsonLdExtractor;
    }


    /**
     * Searches for the mixed input on the musicbrainz.org REST web service.
     * It returns a list of JSON objects (no JSON+LD).
     *
     * @param query
     * @return
     */
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

    /**
     * Searches for the artist on the musicbrainz.org REST web service.
     * It returns a list of JSON objects (no JSON+LD).
     *
     * @param query
     * @return
     */
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

    /**
     * Gets as argument a JSON object from the musicbrainz.org REST web service
     * (no JSON+LD) and looks for the id tag. It returns the URI for that song.
     * @param json
     * @return URL if found, otherwise null
     */
    public String getSongUrl(JSONObject json) {
        if (json.has("id")) {
            return BASE_URL + "release/" +  json.getString("id");
        }

        return null;
    }

    /**
     * Gets as argument a JSON object from the musicbrainz.org REST web service
     * (no JSON+LD) and looks for the id tag. It returns the URI for that song.
     * @param json
     * @return URL if found, otherwise null
     */
    public String getArtistUrl(JSONObject json) {
        if (json.has("id")) {
            return BASE_URL + "artist/" + json.getString("id");
        }

        return null;
    }

    /**
     * Searches for the Resource of type MusicRelease or MusicAlbum.
     * @param model
     * @return
     */
    public Resource findSongResource(Model model) {
        ResIterator itr = model.listResourcesWithProperty(RDF.type, Schema.MusicRelease);
        if (!itr.hasNext()) {
            itr = model.listResourcesWithProperty(RDF.type, Schema.MusicAlbum);
        }

        return itr.hasNext() ? itr.nextResource() : null;
    }

    /**
     * Searches for the type MusicGroup.
     * @param model
     * @return
     */
    public Resource findArtistResource(Model model) {
        ResIterator itr = model.listResourcesWithProperty(RDF.type, Schema.MusicGroup);

        return itr.hasNext() ? itr.nextResource() : null;
    }

    /**
     * Creates a new {@link Model} with a single {@link Resource}.
     * The input has to be JSON/LD.
     * @param json
     * @return
     */
    public Model getSongModel(JSONObject json) {
        JSONObject neededJson = JsonUtil.copyOptionalFields(json,
                "name", "@id", "@context", "@type", "releaseOf", "sameAs", "creditedTo");
        if (neededJson.getString("@type").equals("MusicRelease")) {
            return JsonUtil.jsonLdToModel(neededJson, "https://musicbrainz.org/");
        }
        return null;
    }

    /**
     * Creates a new {@link Model} with a single {@link Resource}.
     * The input has to be JSON/LD.
     * @param json
     * @return
     */
    public Model getArtistModel(JSONObject json) {
        JSONObject neededJson = JsonUtil.copyOptionalFields(json,
                "name", "@id", "@context", "@type", "sameAs");
        Object type = neededJson.get("@type");
        boolean valid = false;
        if (type instanceof String) {
            valid = type.equals("MusicGroup");
        } else if (type instanceof JSONArray) {
            valid = ((JSONArray) type).toList().contains("MusicGroup");
        }
        if (valid) {
            return JsonUtil.jsonLdToModel(neededJson, "https://musicbrainz.org/");
        }
        return null;
    }

    /**
     * Searches for the artistName on MusicBrainz.org then parses
     * the HTML page of the artists entry for the JSON+LD. There it
     * goes through the "sameAs" statements and looks for an entry
     * with wikidata.org. This URI will be returned as String.
     *
     * @param artistName
     * @return
     */
    public String getWikidataResourceByArtistName(String artistName) {
        List<JSONObject> queryResults = searchArtist(artistName);
        for (JSONObject qResult: queryResults) {
            String uri = getArtistUrl(qResult);
            JSONObject jsonLd = htmlJsonLdExtractor.loadJsonLdByUrl(uri);
            if (!jsonLd.has("sameAs")) {
                return null;
            }
            JSONArray sameAs = jsonLd.getJSONArray("sameAs");
            for (int i = 0; i < sameAs.length(); i++) {
                Object obj = sameAs.get(i);
                if (!(obj instanceof String)) {
                    continue;
                }
                String link = (String) obj;
                if (link.startsWith("https://www.wikidata.org/")) {
                    return link;
                }
            }
        }
        return null;
    }

}
