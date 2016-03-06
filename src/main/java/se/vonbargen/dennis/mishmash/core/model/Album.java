package se.vonbargen.dennis.mishmash.core.model;

/**
 * Created by dennis on 2016-02-17.
 *
 * Representation of an album that belongs to an Artist
 * Gets encoded into JSON by Jackson
 */
public class Album {
    private final String title;
    private final String mbid;
    private final String image;

    public Album(String title, String mbid, String image) {
        this.title = title;
        this.mbid = mbid;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getMbid() {
        return mbid;
    }

    public String getImage() {
        return image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;

        return mbid != null ? mbid.equals(album.mbid) : album.mbid == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (mbid != null ? mbid.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Album{" +
                "title='" + title + '\'' +
                ", mbid='" + mbid + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
