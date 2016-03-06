package se.vonbargen.dennis.mishmash.core.externalapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.RateLimiter;
import se.vonbargen.dennis.mishmash.core.externalapi.io.API;
import se.vonbargen.dennis.mishmash.core.model.Album;
import se.vonbargen.dennis.mishmash.core.model.Artist;

import javax.naming.ServiceUnavailableException;
import javax.validation.constraints.NotNull;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dennis on 2016-02-17.
 *
 * APIFacade binds all APIs together into one single interface and produces Artists
 */
public final class APIFacade {
    @NotNull
    private final API musicBrainz;
    @NotNull
    private final API wikipedia;
    @NotNull
    private final API coverArtArchive;

    /**
     *
     * @param musicBrainz       MusicBrainz API constructor injection
     * @param wikipedia         Wikipedia API constructor injection
     * @param coverArtArchive   Cover Art Archive API constructor injection
     */
    public APIFacade(API musicBrainz, API wikipedia, API coverArtArchive) {
        this.musicBrainz = musicBrainz;
        this.wikipedia = wikipedia;
        this.coverArtArchive = coverArtArchive;
    }

    /**
     *
     * @param mbid              MusicBrainz Identifier
     * @param musicBrainzLimit  Limiter for MusicBrainz API (recommended 1.2 seconds)
     * @return                  Returns information about the queried artist in an Artist object
     * @throws IOException      Thrown if an I/O error occurs when communicating with external APIs
     * @throws HTTPException    Thrown if external API communication breaks down
     */
    public Artist queryArtist(String mbid, RateLimiter musicBrainzLimit) throws IOException, HTTPException {
        String description;
        Album[] albums;
        ObjectNode musicBrainzResponse;

        // No throttling for wikipedia is necessary, since the call has to go through MusicBrainz rate limiter before that;
        // thus effectively limiting the load on Wikipedia.
        musicBrainzResponse = musicBrainz.get(mbid, musicBrainzLimit);
        description = wikipedia.get(musicBrainzResponse.get("wikipedia-article").asText(), null)
                .get("description").asText();
        albums = getAlbumCovers(musicBrainzResponse.get("albums"));

        return new Artist(mbid, description, albums);
    }

    Album[] getAlbumCovers(JsonNode data) throws IOException, HTTPException {
        ArrayList<Album> albums = new ArrayList<>();
        String currentId, currentTitle, currentCoverArt;

        for (JsonNode album : data) {
            currentId = album.get("id").asText();
            currentTitle = album.get("title").asText();
            try {
                currentCoverArt = coverArtArchive.get(currentId, null)
                        .get(currentId).asText();
            } catch (HTTPException e) {
                int code = e.getStatusCode();

                // 400 = mbid cannot be parsed as a valid UUID (
                // 404 = There's no release with this id or no image has been chosen (thus N/A)
                // All other error cases: loop cannot continue
                if (code != 400 && code != 404) throw e;
                currentCoverArt = "N/A";
            }
            albums.add(new Album(currentTitle, currentId, currentCoverArt));
        }
        return albums.toArray(new Album[albums.size()]);
    }
}
