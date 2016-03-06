package se.vonbargen.dennis.mishmash.core.externalapi.io;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import javax.naming.ServiceUnavailableException;
import javax.validation.constraints.NotNull;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by dennis on 2016-02-17.
 *
 * Abstract API class that each API must extend
 */
public abstract class API {
    @NotNull
    private final String userAgent;

    API(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     *
     * @param uri                           Uniform Resource Identifier to get from
     * @return                              Performs an http get request and returns the response content as a string
     * @throws IOException                  thrown if an input/output error is encountered
     * @throws HTTPException                thrown if returned status-code reads an error
     */
    final String get(URI uri) throws IOException, HTTPException {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        // Error handling
        if (statusCode >= 400 && statusCode < 600) throw new HTTPException(statusCode);

        try (InputStream stream = response.getEntity().getContent()) {
            return IOUtils.toString(stream);
        }
    }

    /**
     * Acts as an HTTP request wrapper for each API subclass.
     * Each API will get and extract relevant data through this method.
     *
     * @param key               API-key
     * @return                  structured API response data as a JSON object
     * @throws IOException      thrown if an input/output error is encountered
     * @throws HTTPException    thrown if API server returns an error status (400 & 500 codes)
     */
    public abstract ObjectNode get(String key, RateLimiter rateLimiter) throws IOException, HTTPException;
}
