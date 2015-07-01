package com.santhoshn.spotifystreamer.artist;

/**
 * Created by santhosh on 30/06/15.
 */
public class Artist {
    private String spotifyId;
    private String name;
    private String thumbnailImageUrl;

    public Artist() {}
    public Artist(String spotifyId, String name, String thumbImgUrl) {
        this.spotifyId = spotifyId;
        this.name = name;
        this.thumbnailImageUrl = thumbImgUrl;
    }
    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String artistsName) {
        this.name = artistsName;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }
}
