package se.vonbargen.dennis.mishmash.core.externalapi.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.naming.ServiceUnavailableException;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by dennis on 2016-02-21.
 *
 * Tests functionality of the actual HTTP implementation, as well as the throttling
 */
public class APITest {

    private static class MockAPI extends API {

        MockAPI(String userAgent) {
            super(userAgent);
        }

        @Override
        public ObjectNode get(String key, RateLimiter rateLimiter) throws IOException, HTTPException {
            URI uri = URI.create("http://jsonplaceholder.typicode.com/albums/1");
            return extract(super.get(uri));
        }

        private ObjectNode extract(String json) throws IOException {
            return (ObjectNode) new ObjectMapper().readTree(json);
        }
    }

    private static class MockThrottledAPI extends API {
        MockThrottledAPI(String userAgent) {
            super(userAgent);
        }

        @Override
        public ObjectNode get(String key, RateLimiter rateLimiter) throws IOException, HTTPException {
            if (rateLimiter.tryAcquire(2, 10, TimeUnit.SECONDS)) {
                URI uri = URI.create("http://jsonplaceholder.typicode.com/albums/" + key);
                return extract(super.get(uri));
            } else {
                // Gateway timeout
                throw new HTTPException(504);
            }
        }

        private ObjectNode extract(String json) throws IOException {
            return (ObjectNode) new ObjectMapper().readTree(json);
        }
    }

    private static API mock;
    private static API mockThrottled;

    @BeforeClass
    public static void setUp() {
        // Remove verbose Apache logging
        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "ERROR");

        mock = new MockAPI("MishMash_TEST/1.0 ( dennis@vonbargen.se )");
        mockThrottled = new MockThrottledAPI("MishMash_TEST/1.0 ( dennis@vonbargen.se )");
    }

    @Test
    public void testGet() throws Exception {
        String json = "{\n" +
                "  \"userId\": 1,\n" +
                "  \"id\": 1,\n" +
                "  \"title\": \"quidem molestiae enim\"\n" +
                "}";
        ObjectNode expected = (ObjectNode) new ObjectMapper().readTree(json);
        ObjectNode actual = mock.get("1", null);

        assertEquals(expected, actual);
    }

    /**
     * RateLimiter doesn't seem to be entirely accurate.
     * To ensure that limit isn't breached, rate limit is put to 1.2, with 2 required permits
     *
     * @throws Exception
     */
    @Test
    public void testGetThrottled() throws Exception {
        System.out.println("### HTTP Throttling test ###");
        int rateLimitBreaches = 0;
        int iterations = 10;
        GregorianCalendar[] timestamps = new GregorianCalendar[iterations];

        RateLimiter rateLimiter = RateLimiter.create(1.2);

        for (int i = 0; i < iterations; i++) {
            mockThrottled.get("" + (i + 1), rateLimiter);
            timestamps[i] = (GregorianCalendar) GregorianCalendar.getInstance();
            timestamps[i].setTime(new Date());
            timestamps[i].set(Calendar.MILLISECOND, 0);
            System.out.println(timestamps[i].getTime());
        }

        for (int i = 0; i <= timestamps.length - 2; i++) {
            if (!timestamps[i].before(timestamps[i+1])) rateLimitBreaches++;
        }

        if (rateLimitBreaches > 3) fail("Rate limit breached too many times (" + rateLimitBreaches + ")");
        System.out.println("Rate limit breached " + rateLimitBreaches + " time(s)");
        System.out.println("### Test complete ###");
    }
}