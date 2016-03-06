package se.vonbargen.dennis.mishmash.config;

import com.yammer.dropwizard.config.Configuration;

import javax.validation.constraints.NotNull;

/**
 * Created by dennis on 2016-02-17.
 *
 * Representation of configuration.yml
 */
public class MishMashConfig extends Configuration {
    @NotNull
    private Long cacheMaxSize;

    public Long getCacheMaxSize() {
        return cacheMaxSize;
    }
}
