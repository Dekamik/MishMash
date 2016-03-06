package se.vonbargen.dennis.mishmash.core.externalapi.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.RateLimiter;

import javax.naming.ServiceUnavailableException;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.net.URI;

/**
 * Created by dennis on 2016-02-20.
 *
 * Interface to Wikipedias API
 */
public final class WikipediaAPI extends API {

    public WikipediaAPI(String userAgent) {
        super(userAgent);
    }

    /**
     *
     * @param articleName       Name of Wikipedia article
     * @return                  Returns a short extract from the queried article. If no article is found, "N/A" is returned.
     * @throws IOException
     * @throws HTTPException
     */
    @Override
    public ObjectNode get(String articleName, RateLimiter rateLimiter) throws IOException, HTTPException {
        try {
            URI uri = URI.create("http://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles=" + articleName);
            return extract(super.get(uri));
        } catch (IOException e) {
            throw new IOException("Error occurred when accessing Wikipedia API");
        } catch (HTTPException e) {
            // TODO: Handle 404 responses
            throw e;
        }
    }

    ObjectNode extract(String json) throws IOException {
        JsonNode response = new ObjectMapper().readTree(json);
        ObjectNode ret = JsonNodeFactory.instance.objectNode();

        // Tried addressing the 0th element directly, but it didn't work.
        // For-loop is temporary, better solution pending
        for (JsonNode node : response.get("query").get("pages")) {
            ret.put("description", node.get("extract").asText());
        }

        return ret;
    }
}
