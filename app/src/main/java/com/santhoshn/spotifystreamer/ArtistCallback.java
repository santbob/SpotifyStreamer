package com.santhoshn.spotifystreamer;

/**
 * Created by santhosh on 20/08/15.
 */
public interface ArtistCallback extends TrackCallback{
    public void onArtistSelected(String spotifyId, String subTitle);
}
