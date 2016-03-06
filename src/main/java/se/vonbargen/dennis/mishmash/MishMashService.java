package se.vonbargen.dennis.mishmash;

import com.google.common.util.concurrent.RateLimiter;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import se.vonbargen.dennis.mishmash.config.MishMashConfig;
import se.vonbargen.dennis.mishmash.core.ArtistHandler;
import se.vonbargen.dennis.mishmash.core.externalapi.APIFacade;
import se.vonbargen.dennis.mishmash.core.externalapi.io.API;
import se.vonbargen.dennis.mishmash.core.externalapi.io.CoverArtArchiveAPI;
import se.vonbargen.dennis.mishmash.core.externalapi.io.MusicBrainzAPI;
import se.vonbargen.dennis.mishmash.core.externalapi.io.WikipediaAPI;
import se.vonbargen.dennis.mishmash.resources.MishMashResource;

/**
 * Created by dennis on 2016-02-17.
 *
 * This is the main entry point, where all objects get created and injected
 */
class MishMashService extends Service<MishMashConfig> {

    public static void main(String[] args) throws Exception {
        new MishMashService().run(args);
    }

    @Override
    public void initialize(Bootstrap<MishMashConfig> bootstrap) {
        bootstrap.setName("mish-mash");
    }

    @Override
    public void run(MishMashConfig mishMashConfig, Environment environment) throws Exception {
        String userAgent = "MishMash/1.0 ( dennis@vonbargen.se )";

        // Initialize API objects and facade
        API musicBrainz = new MusicBrainzAPI(userAgent);
        API wikipedia = new WikipediaAPI(userAgent);
        API coverArtArchive = new CoverArtArchiveAPI(userAgent);
        APIFacade api = new APIFacade(musicBrainz, wikipedia, coverArtArchive);

        // Create handler
        ArtistHandler.init(api, RateLimiter.create(1.2), mishMashConfig.getCacheMaxSize());
        ArtistHandler artistHandler = ArtistHandler.instance();

        MishMashResource resource = new MishMashResource(artistHandler);
        environment.addResource(resource);
    }
}
