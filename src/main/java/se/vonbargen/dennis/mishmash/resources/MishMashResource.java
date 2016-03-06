package se.vonbargen.dennis.mishmash.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.vonbargen.dennis.mishmash.core.ArtistHandler;

import javax.naming.ServiceUnavailableException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by dennis on 2016-02-17.
 *
 * Simple RESTful resource, which gets loaded in the service
 */
@Path("/mishmash")
@Produces(MediaType.APPLICATION_JSON)
public final class MishMashResource {

    private final ArtistHandler handler;
    private final ObjectMapper mapper;

    public MishMashResource(ArtistHandler handler) {
        this.handler = handler;
        mapper = new ObjectMapper();
    }

    @GET
    @Path("/{id}")
    public String getMishMash(@PathParam("id") String id) {
        try {
            return mapper.writeValueAsString(handler.getArtist(id));
            // TODO: Implement proper logger
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "{\"error\":\"An internal error occurred\"}";
        } catch (ServiceUnavailableException e) {
            e.printStackTrace();
            return "{\"error\":\"Service unavailable\"}";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\":\"Exception occurred when outputting JSON\"}";
        } catch (HTTPException e) {
            // TODO: Implement more informative HTTPException handling
            e.printStackTrace();
            return "{\"error\":\"HTTPException occurred when accessing APIs (" + e.getStatusCode() + ")\"}";
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\":\"Internal IOException occurred when accessing APIs\"}";
        }
    }
}
