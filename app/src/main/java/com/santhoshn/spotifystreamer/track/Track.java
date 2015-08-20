package com.santhoshn.spotifystreamer.track;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by santhosh on 30/06/15.
 */
public class Track implements Parcelable{
    public final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        @Override
        public Track[] newArray(int index) {
            return new Track[index];
        }
    };
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

    private Track(Parcel in){
        this.spotifyId = in.readString();
        this.trackName = in.readString();
        this.albumName = in.readString();
        this.thumbnailImageUrl = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(spotifyId);
        parcel.writeString(trackName);
        parcel.writeString(albumName);
        parcel.writeString(thumbnailImageUrl);
    }
}
