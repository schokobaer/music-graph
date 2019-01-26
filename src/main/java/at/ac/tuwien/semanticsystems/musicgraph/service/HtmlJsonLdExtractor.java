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

    public static String extractJsonLdFromHtml(String html) {
        String searchString = "<script type=\"application/ld+json\"";
        if (!html.contains(searchString)) {
            return "{}";
        }
        int posStart = html.indexOf(">", html.indexOf(searchString) + searchString.length()) + 1;
        int posEnd = html.indexOf("</script>", posStart);
        return html.substring(posStart, posEnd);
    }


}
