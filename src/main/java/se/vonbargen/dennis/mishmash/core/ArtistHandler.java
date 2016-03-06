package se.vonbargen.dennis.mishmash.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.UncheckedExecutionException;
import se.vonbargen.dennis.mishmash.core.externalapi.APIFacade;
import se.vonbargen.dennis.mishmash.core.model.Artist;

import javax.naming.ServiceUnavailableException;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by dennis on 2016-02-17.
 *
 * Handler that manages the APIFacade and the cache.
 * Singleton pattern is used to ensure that there's only one managed cache.
 */
public final class ArtistHandler {

    private static ArtistHandler instance = null;
    private static LoadingCache<String, Artist> cache;
    private final APIFacade api;
    private final RateLimiter rateLimiter;

    private ArtistHandler(APIFacade api, RateLimiter rateLimiter, Long maxCacheSize) {
        this.api = api;
        this.rateLimiter = rateLimiter;
        cache = initCache(maxCacheSize);
    }

    private LoadingCache<String, Artist> initCache(Long maxCacheSize) {
        return CacheBuilder
                .newBuilder()
                .maximumSize(maxCacheSize)
                .build(new CacheLoader<String, Artist>() {
                    // TODO: Read up on null-related annotation problems here
                    @Override
                    public Artist load(String mbid) throws IOException, HTTPException, ServiceUnavailableException {
                        return api.queryArtist(mbid, rateLimiter);
                    }
                });
    }

    public static void init(APIFacade api, RateLimiter rateLimiter, Long maxSize) {
        instance = new ArtistHandler(api, rateLimiter, maxSize);
    }

    public static ArtistHandler instance() {
        return instance;
    }

    /**
     *
     * @param mbid                  MusicBrainz Identifier
     * @return                      Artist represented by mbid
     * @throws ExecutionException   thrown by the cache if a checked exception was thrown while loading the value
     */
    public Artist getArtist(String mbid) throws ExecutionException, IOException, HTTPException, ServiceUnavailableException {
        try {
            return cache.get(mbid);
        } catch (UncheckedExecutionException e) {
            // TODO: Develop and test error handling here
            try {
                throw e.getCause();
            } catch (IOException | HTTPException | ServiceUnavailableException e2) {
                throw e2;
            } catch (Throwable throwable) {
                // Better to fail early
                throw new RuntimeException("Unknown exception occurred when loading/getting artist");
            }
        }
    }
}
