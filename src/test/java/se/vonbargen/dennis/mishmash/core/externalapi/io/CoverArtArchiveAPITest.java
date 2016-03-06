package se.vonbargen.dennis.mishmash.core.externalapi.io;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dennis on 2016-02-20.
 *
 * Tests basic functionality of CoverArtArchiveAPI
 */
public class CoverArtArchiveAPITest {

    private static CoverArtArchiveAPI api;

    @BeforeClass
    public static void setUp() {
        api = new CoverArtArchiveAPI("MishMash_TEST/1.0 ( dennis@vonbargen.se )");
    }

    @Test
    public void testExtract() throws Exception {
        String testJson = "{" +
                "\"images\": [{" +
                "\"types\": [\"Front\"]," +
                "\"front\": true," +
                "\"back\": false," +
                "\"edit\": 20473306," +
                "\"image\": \"http://coverartarchive.org/release/a146429a-cedc-3ab0-9e41-1aaf5f6cdc2d/3012495605.jpg\"," +
                "\"comment\": \"\"," +
                "\"approved\": true," +
                "\"thumbnails\": {" +
                "\"large\": \"http://coverartarchive.org/release/a146429a-cedc-3ab0-9e41-1aaf5f6cdc2d/3012495605-500.jpg\"," +
                "\"small\": \"http://coverartarchive.org/release/a146429a-cedc-3ab0-9e41-1aaf5f6cdc2d/3012495605-250.jpg\"" +
                "}," +
                "\"id\": \"3012495605\"" +
                "}]," +
                "\"release\": \"http://musicbrainz.org/release/a146429a-cedc-3ab0-9e41-1aaf5f6cdc2d\"" +
                "}";
        ObjectNode node = api.extract(testJson, "a146429a-cedc-3ab0-9e41-1aaf5f6cdc2d");

        assertTrue(node.has("a146429a-cedc-3ab0-9e41-1aaf5f6cdc2d"));
        assertEquals("http://coverartarchive.org/release/a146429a-cedc-3ab0-9e41-1aaf5f6cdc2d/3012495605.jpg",
                node.get("a146429a-cedc-3ab0-9e41-1aaf5f6cdc2d").asText());
    }
}