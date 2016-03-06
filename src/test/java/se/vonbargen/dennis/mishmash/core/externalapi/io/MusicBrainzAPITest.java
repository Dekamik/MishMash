package se.vonbargen.dennis.mishmash.core.externalapi.io;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Created by dennis on 2016-02-20.
 *
 * Tests basic functionality of MusicBrainzAPI
 */
public class MusicBrainzAPITest {

    private static MusicBrainzAPI api;

    @BeforeClass
    public static void setUp() {
        api = new MusicBrainzAPI("MishMash_TEST/1.0 ( dennis@vonbargen.se )");
    }

    @Test
    public void patternTest() {
        String url = "http://en.wikipedia.org/wiki/Nirvana_(band)";
        Matcher matcher = Pattern.compile("([^/]+$)").matcher(url);

        assertTrue(matcher.find());
    }

    @Test
    public void testExtract() throws Exception {
        String testJson = "{" +
                "\"release-groups\": [" +
                "{\"primary-type\": \"Album\"}," +
                "{\"primary-type\":\"Album\"}," +
                "{\"primary-type\":\"Compilation\"}," +
                "{\"primary-type\":\"Album\"}," +
                "{\"primary-type\":\"Album\"}," +
                "{\"primary-type\":\"Compilation\"}," +
                "{\"primary-type\":\"Compilation\"}" +
                "]," +
                "\"relations\": [" +
                "{\"type\": \"wikimedia\"," +
                "\"url\": {\"resource\": \"http://en.wikipedia.org/wiki/Nirhana\"}}," +
                "{\"type\": \"wikipedia\"," +
                "\"url\": {\"resource\": \"http://en.wikipedia.org/wiki/Nirvana_(band)\"}}," +
                "{\"type\": \"wikispeedia\"," +
                "\"url\": {\"resource\": \"http://en.wikipedia.org/wiki/Nikrana\"}}" +
                "]" +
                "}";
        JsonNode json = api.extract(testJson);

        assertTrue(json.has("albums"));
        assertTrue(json.has("wikipedia-article"));

        assertEquals(4, json.get("albums").size());
        assertEquals("Nirvana_(band)", json.get("wikipedia-article").asText());
    }
}