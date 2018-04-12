package io.github.michalsmolarek.firebasedatabase;

/**
 * Created by michalsmolarek on 20.02.2018.
 */

// model artysty, w sumie tylko gettery i settery no i konstruktor

public class Artist {
    private String artistId;
    private String artistName;
    private String artistGenre;

    public Artist(String artistId, String artistName, String artistGenre) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.artistGenre = artistGenre;
    }

    public Artist() {}

    public String getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistGenre() {
        return artistGenre;
    }
}
