package com.santhoshn.spotifystreamer;

import com.santhoshn.spotifystreamer.track.Track;

import java.util.ArrayList;

/**
 * Created by santhosh on 20/08/15.
 */
public interface TrackCallback {
    public void onTrackSelected(int trackIndex, ArrayList<Track> tracks, int seekUntil);
}
