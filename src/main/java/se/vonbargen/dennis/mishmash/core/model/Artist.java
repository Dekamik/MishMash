package se.vonbargen.dennis.mishmash.core.model;

import java.util.Arrays;

/**
 * Created by dennis on 2016-02-17.
 *
 * Representation of an artist and the artists album.
 * Gets encoded into JSON by Jackson
 */
public class Artist {

    private final String mbid;
    private final String description;
    private final Album[] albums;

    public Artist(String mbid, String description, Album[] albums) {
        this.mbid = mbid;
        this.description = description;
        this.albums = albums;
    }

    public String getMbid() { return mbid; }

    public String getDescription() { return description; }

    public Album[] getAlbums() { return albums; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        return mbid != null ? mbid.equals(artist.mbid) : artist.mbid == null;
    }

    @Override
    public int hashCode() {
        int result = mbid != null ? mbid.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(albums);
        return result;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "mbid='" + mbid + '\'' +
                ", description='" + description + '\'' +
                ", albums=" + Arrays.toString(albums) +
                '}';
    }
}
