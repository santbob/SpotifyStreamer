package com.santhoshn.spotifystreamer.artist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by santhosh on 30/06/15.
 */
public class Artist implements Parcelable{
    public final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        @Override
        public Artist[] newArray(int index) {
            return new Artist[index];
        }
    };
    private String spotifyId;
    private String name;
    private String thumbnailImageUrl;
    public Artist() {}
    public Artist(String spotifyId, String name, String thumbImgUrl) {
        this.spotifyId = spotifyId;
        this.name = name;
        this.thumbnailImageUrl = thumbImgUrl;
    }

    private Artist(Parcel in){
        this.spotifyId = in.readString();
        this.name = in.readString();
        this.thumbnailImageUrl = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(spotifyId);
        parcel.writeString(name);
        parcel.writeString(thumbnailImageUrl);
    }
}
