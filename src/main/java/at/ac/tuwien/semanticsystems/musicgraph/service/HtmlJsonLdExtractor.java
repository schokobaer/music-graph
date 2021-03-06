package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class HtmlJsonLdExtractor {


    private SimpleHttpClient httpClient;

    @Autowired
    public void setHttpClient(SimpleHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public JSONObject loadJsonLdByUrl(String resourceUri) {
        String html = httpClient.get(resourceUri);
        if (html == null) {
            return null;
        }
        String jsonLd = extractJsonLdFromHtml(html);
        JSONObject json = new JSONObject(jsonLd);
        return json;
    }

    private String extractJsonLdFromHtml(String html) {
        String searchString = "<script type=\"application/ld+json\"";
        if (!html.contains(searchString)) {
            return "{}";
        }
        int posStart = html.indexOf(">", html.indexOf(searchString) + searchString.length()) + 1;
        int posEnd = html.indexOf("</script>", posStart);
        return html.substring(posStart, posEnd);
    }

    public Model discogsModel(JSONObject json) {
        JSONObject neededJson = copyOptionalFields(json,
                "name", "@id", "@context", "@type", "releaseOf");
        return jsonToModel(neededJson, "https://www.discogs.com/");
    }

    public Model musicbrainzSongModel(JSONObject json) {
        JSONObject neededJson = copyOptionalFields(json,
                "name", "@id", "@context", "@type", "releaseOf", "sameAs", "creditedTo");
        if (neededJson.getString("@type").equals("MusicRelease")) {
            return jsonToModel(neededJson, "https://musicbrainz.org/");
        }
        return null;
    }

    public Model musicbrainzArtistModel(JSONObject json) {
        JSONObject neededJson = copyOptionalFields(json,
                "name", "@id", "@context", "@type", "sameAs");
        Object type = neededJson.get("@type");
        boolean valid = false;
        if (type instanceof String) {
            valid = type.equals("MusicGroup");
        } else if (type instanceof JSONArray) {
            valid = ((JSONArray) type).toList().contains("MusicGroup");
        }
        if (valid) {
            return jsonToModel(neededJson, "https://musicbrainz.org/");
        }
        return null;
    }

    private JSONObject copyOptionalFields(JSONObject obj, String... fields) {
        JSONObject json = new JSONObject();
        for (String field: fields) {
            if (obj.has(field)) {
                json.put(field, obj.get(field));
            }
        }
        return json;
    }

    public Model jsonToModel(JSONObject json, String baseUri) {
        Model model = ModelFactory.createDefaultModel();
        String jsonLd = json.toString();
        InputStream stream = new ByteArrayInputStream(jsonLd.getBytes(StandardCharsets.UTF_8));
        model.read(stream, baseUri, "JSON-LD");

        return model;
    }
}
