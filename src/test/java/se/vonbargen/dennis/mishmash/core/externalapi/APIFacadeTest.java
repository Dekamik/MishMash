package se.vonbargen.dennis.mishmash.core.externalapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import se.vonbargen.dennis.mishmash.core.externalapi.io.API;
import se.vonbargen.dennis.mishmash.core.externalapi.io.CoverArtArchiveAPI;
import se.vonbargen.dennis.mishmash.core.externalapi.io.MusicBrainzAPI;
import se.vonbargen.dennis.mishmash.core.externalapi.io.WikipediaAPI;
import se.vonbargen.dennis.mishmash.core.model.Album;
import se.vonbargen.dennis.mishmash.core.model.Artist;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by dennis on 2016-02-21.
 *
 * Here we use Mockito in order to mock the APIs for testing purposes.
 * However, Mockito has limitations: it cannot mock final classes or methods.
 *
 * In order to test these classes without sacrificing code quality, we compliment mockito with PowerMock,
 * which allows us to mock final classes and methods.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        // Final classes that will be mocked
        MusicBrainzAPI.class,
        WikipediaAPI.class,
        CoverArtArchiveAPI.class
})
public class APIFacadeTest {

    private static APIFacade api;
    private static ObjectMapper mapper;
    private static String albums;

    @BeforeClass
    public static void setUp() throws Exception {
        mapper = new ObjectMapper();

        API musicBrainz = mock(MusicBrainzAPI.class);
        API wikipedia = mock(WikipediaAPI.class);
        API coverArtArchive = mock(CoverArtArchiveAPI.class);

        albums = "{\"albums\": [{\n" +
                "\t\t\"first-release-date\": \"2000\",\n" +
                "\t\t\"title\": \"Unreleased Tracks\",\n" +
                "\t\t\"id\": \"albumid1\",\n" +
                "\t\t\"disambiguation\": \"\",\n" +
                "\t\t\"secondary-types\": [\"Compilation\"],\n" +
                "\t\t\"primary-type\": \"Album\"\n" +
                "\t}, {\n" +
                "\t\t\"primary-type\": \"Album\",\n" +
                "\t\t\"secondary-types\": [\"Compilation\"],\n" +
                "\t\t\"title\": \"First Live Show\",\n" +
                "\t\t\"first-release-date\": \"2001\",\n" +
                "\t\t\"disambiguation\": \"\",\n" +
                "\t\t\"id\": \"albumid2\"\n" +
                "\t}, {\n" +
                "\t\t\"secondary-types\": [\"Compilation\"],\n" +
                "\t\t\"primary-type\": \"Album\",\n" +
                "\t\t\"title\": \"Secret Songs: The Unreleased Album\",\n" +
                "\t\t\"first-release-date\": \"2000-02-14\",\n" +
                "\t\t\"id\": \"albumid3\",\n" +
                "\t\t\"disambiguation\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"disambiguation\": \"\",\n" +
                "\t\t\"id\": \"albumid4\",\n" +
                "\t\t\"first-release-date\": \"1994\",\n" +
                "\t\t\"title\": \"Outcesticide: In Memory of Kurt Cobain\",\n" +
                "\t\t\"primary-type\": \"Album\",\n" +
                "\t\t\"secondary-types\": [\"Compilation\"]\n" +
                "\t}]}";

        // MusicBrainz mock logic
        ObjectNode musicBrainzResponse = JsonNodeFactory.instance.objectNode();
        musicBrainzResponse.putAll((ObjectNode) mapper.readTree(albums));
        musicBrainzResponse.put("wikipedia-article", "Nirvana_(band)");
        when(musicBrainz.get("id-TR8R", null)).thenReturn(musicBrainzResponse);

        // Wikipedia mock logic
        ObjectNode wikipediaResponse = JsonNodeFactory.instance.objectNode();
        wikipediaResponse.put("description", "This is the description bit");
        when(wikipedia.get("Nirvana_(band)", null)).thenReturn(wikipediaResponse);

        // Cover Art Archive mock logic
        when(coverArtArchive.get("albumid1", null)).thenReturn(
                (ObjectNode) mapper.readTree("{\"albumid1\": \"album1.jpg\"}")
        );
        when(coverArtArchive.get("albumid2", null)).thenReturn(
                (ObjectNode) mapper.readTree("{\"albumid2\": \"album2.jpg\"}")
        );
        when(coverArtArchive.get("albumid3", null)).thenReturn(
                (ObjectNode) mapper.readTree("{\"albumid3\": \"album3.jpg\"}")
        );
        when(coverArtArchive.get("albumid4", null)).thenReturn(
                (ObjectNode) mapper.readTree("{\"albumid4\": \"album4.jpg\"}")
        );

        api = new APIFacade(musicBrainz, wikipedia, coverArtArchive);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testQueryArtist() throws Exception {
        Artist artist = api.queryArtist("id-TR8R", null);
        Album[] expected = {
                new Album("Unreleased Tracks", "albumid1", "album1.jpg"),
                new Album("First Live Show", "albumid2", "album2.jpg"),
                new Album("Secret Songs: The Unreleased Album", "albumid3", "album3.jpg"),
                new Album("Outcesticide: In Memory of Kurt Cobain", "albumid4", "album4.jpg"),
        };

        assertEquals("id-TR8R", artist.getMbid());
        assertEquals("This is the description bit", artist.getDescription());
        assertEquals(4, artist.getAlbums().length);

        assertArrayEquals(expected, artist.getAlbums());
    }

    /**
     * Test extraction of raw album data into
     *
     * @throws Exception
     */
    @Test
    public void testGetAlbumCovers() throws Exception {
        Album[] actual = api.getAlbumCovers(mapper.readTree(albums).get("albums"));
        Album[] expected = {
                new Album("Unreleased Tracks", "albumid1", "album1.jpg"),
                new Album("First Live Show", "albumid2", "album2.jpg"),
                new Album("Secret Songs: The Unreleased Album", "albumid3", "album3.jpg"),
                new Album("Outcesticide: In Memory of Kurt Cobain", "albumid4", "album4.jpg"),
        };

        assertArrayEquals(expected, actual);
    }
}