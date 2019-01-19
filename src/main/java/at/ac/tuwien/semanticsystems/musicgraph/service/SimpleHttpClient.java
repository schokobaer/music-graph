package at.ac.tuwien.semanticsystems.musicgraph.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SimpleHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpClient.class);

    private OkHttpClient httpClient = new OkHttpClient();

    /**
     * Simple GET Call to the given URL.
     * If there is an error, null is returned.
     * @param url The URL to call.
     * @return Content of the result, or null if not working.
     */
    public String get(String url) {

        Request req = new Request.Builder().url(url).get().build();
        try {
            Response resp = httpClient.newCall(req).execute();
            if (resp.isSuccessful()) {
                return resp.body().string();
            }
            LOGGER.warn("Not successful loading {}: {}", url, resp.message());
            return null;
        } catch (IOException e) {
            LOGGER.warn("Could not load {}: {}", url, e.getMessage());
            return null;
        }

    }
}
