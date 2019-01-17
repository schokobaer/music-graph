package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

@Service
public class SimpleHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpClient.class);

    /**
     * Simple GET Call to the given URL.
     * If there is an error, null is returned.
     * @param url The URL to call.
     * @return Content of the result, or null if not working.
     */
    public String get(String url) {
        try {
            URL u = new URL(url);
            InputStream is = u.openStream();
            return IOUtils.toString(is, Charset.defaultCharset());
        } catch (IOException e) {
            LOGGER.warn("Could not fetch {}", url, e);
            return null;
        }
    }
}
