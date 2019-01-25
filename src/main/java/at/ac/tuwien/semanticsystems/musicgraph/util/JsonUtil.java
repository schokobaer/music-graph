package at.ac.tuwien.semanticsystems.musicgraph.util;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class JsonUtil {

    public static JSONObject copyOptionalFields(JSONObject obj, String... fields) {
        JSONObject json = new JSONObject();
        for (String field: fields) {
            if (obj.has(field)) {
                json.put(field, obj.get(field));
            }
        }
        return json;
    }

    public static Model jsonLdToModel(JSONObject json, String baseUri) {
        Model model = ModelFactory.createDefaultModel();
        String jsonLd = json.toString();
        InputStream stream = new ByteArrayInputStream(jsonLd.getBytes(StandardCharsets.UTF_8));
        model.read(stream, baseUri, "JSON-LD");

        return model;
    }
}
