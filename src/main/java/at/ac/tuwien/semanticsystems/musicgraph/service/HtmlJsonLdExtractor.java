package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class HtmlJsonLdExtractor {

    @Autowired
    private SimpleHttpClient httpClient;

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

    public Model musicbrainzModel(JSONObject json) {
        JSONObject neededJson = copyOptionalFields(json,
                "name", "@id", "@context", "@type", "releaseOf", "sameAs", "creditedTo");
        return jsonToModel(neededJson, "https://musicbrainz.org/");

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
