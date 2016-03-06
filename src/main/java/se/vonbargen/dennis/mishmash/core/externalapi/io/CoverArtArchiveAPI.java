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
 * Interface to Cover Art Archive API
 */
public final class CoverArtArchiveAPI extends API {

    public CoverArtArchiveAPI(String userAgent) {
        super(userAgent);
    }

    @Override
    public ObjectNode get(String mbid, RateLimiter rateLimiter) throws IOException, HTTPException {
        try {
            URI uri = URI.create("http://coverartarchive.org/release-group/" + mbid);
            return extract(super.get(uri), mbid);
        } catch (IOException e) {
            throw new IOException("Error occurred when accessing Cover Art Archive API");
        } catch (HTTPException e) {
            // TODO: Handle 404 responses
            throw e;
        }
    }

    ObjectNode extract(String json, String mbid) throws IOException {
        JsonNode response = new ObjectMapper().readTree(json);
        ObjectNode ret = JsonNodeFactory.instance.objectNode();

        for (JsonNode node : response.get("images")) {
            if (node.get("front").asBoolean()) {
                ret.put(mbid, node.get("image").asText());
                break;
            }
        }

        return ret;
    }
}
