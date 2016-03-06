package se.vonbargen.dennis.mishmash.core.externalapi.io;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dennis on 2016-02-20.
 *
 * Tests basic functionality of WikipediaAPI
 */
public class WikipediaAPITest {

    private static WikipediaAPI api;

    @BeforeClass
    public static void setUp() {
        api = new WikipediaAPI("MishMash_TEST/1.0 ( dennis@vonbargen.se )");
    }

    @Test
    public void testExtract() throws Exception {
        String testJson = "{" +
                "\"batchcomplete\": \"\"," +
                "\"query\": {" +
                "\"normalized\": [{" +
                "\"from\": \"Nirvana_(band)\"," +
                "\"to\": \"Nirvana (band)\"" +
                "}]," +
                "\"pages\": {" +
                "\"21231\": {" +
                "\"pageid\": 21231," +
                "\"ns\": 0," +
                "\"title\": \"Nirvana (band)\"," +
                "\"extract\": \"This is what we want\"" +
                "}" +
                "}" +
                "}" +
                "}";
        ObjectNode json = api.extract(testJson);

        assertTrue(json.has("description"));
        assertEquals("This is what we want", json.get("description").asText());
    }
}