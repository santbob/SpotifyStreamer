package com.santhoshn.spotifystreamer.track;

/**
 * Created by santhosh on 30/06/15.
 */
public class Track {
    private String spotifyId;
    private String trackName;
    private String albumName;
    private String thumbnailImageUrl;

    public Track(){

    }
    public Track(String spotifyId, String trackName, String albumName, String thumbnailImageUrl) {
        this.spotifyId = spotifyId;
        this.trackName = trackName;
        this.albumName = albumName;
        this.thumbnailImageUrl = thumbnailImageUrl;
    }
    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }
}
