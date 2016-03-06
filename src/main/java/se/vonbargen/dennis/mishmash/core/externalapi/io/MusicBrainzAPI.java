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
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dennis on 2016-02-20.
 *
 * Interface to MusicBrainz API
 */
public final class MusicBrainzAPI extends API {

    public MusicBrainzAPI(String userAgent) {
        super(userAgent);
    }

    @Override
    public ObjectNode get(String mbid, RateLimiter rateLimiter) throws IOException, HTTPException {
        try {
            if (rateLimiter.tryAcquire(2, 10, TimeUnit.SECONDS)) {
                URI uri = URI.create("http://www.musicbrainz.org/ws/2/artist/" + mbid + "?&fmt=json&inc=url-rels+release-groups");
                return extract(super.get(uri));
            } else {
                // Gateway timeout
                throw new HTTPException(504);
            }
        } catch (IOException e) {
            throw new IOException("IOException occurred when accessing MusicBrainz API");
        } catch (HTTPException e) {
            // TODO: Handle 404 responses
            throw e;
        }
    }

    ObjectNode extract(String json) throws IOException {
        JsonNode response = new ObjectMapper().readTree(json);
        JsonNode releases = response.get("release-groups");
        JsonNode relations = response.get("relations");
        JsonNode current;

        String wikiname = null;
        ObjectNode ret = JsonNodeFactory.instance.objectNode();

        // Filter out all releases that aren't albums
        for (Iterator<JsonNode> it = releases.iterator(); it.hasNext();) {
            current = it.next();
            if (!"Album".equals(current.get("primary-type").asText())) it.remove();
        }

        // Get name of Wikipedia article
        for (JsonNode node : relations) {
            if ("wikipedia".equals(node.get("type").asText())) {
                // Extract the article name from the Wikipedia url (Everything after the last forward slash)
                Matcher matcher = Pattern.compile("([^/]+$)")
                        .matcher(node.get("url").get("resource").asText());
                // TODO: decide on what to do with boolean (suppress or use)
                matcher.find();
                wikiname = matcher.group(0);
                break;
            }
        }

        ret.put("albums", releases);
        ret.put("wikipedia-article", wikiname);
        return ret;
    }
}
