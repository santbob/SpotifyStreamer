package com.santhoshn.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.santhoshn.spotifystreamer.track.Track;
import com.santhoshn.spotifystreamer.track.TrackListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A fragment containing a Top 10 Tracks for selected Artist
 */
public class TopTenTracksActivityFragment extends Fragment {

    private TrackListAdapter mTracksAdapter;
    private SpotifyService mSpotifyService = new SpotifyApi().getService();

    public TopTenTracksActivityFragment() {
    }

    private void updateDataforView(String artistSpotifyId) {
        FetchTracksTask fetchTracksTask = new FetchTracksTask();
        fetchTracksTask.execute(artistSpotifyId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            String artistSpotifyId = intent.getStringExtra(Intent.EXTRA_TEXT);
            updateDataforView(artistSpotifyId);
        }
        mTracksAdapter = new TrackListAdapter(getActivity(), R.layout.track_list_item, new ArrayList<Track>());
        ListView listView = (ListView) rootView.findViewById(R.id.listview_tracksList);
        listView.setAdapter(mTracksAdapter);
        return rootView;
    }

    private class FetchTracksTask extends AsyncTask<String, Void, List> {
        private final String LOG_TAG = FetchTracksTask.class.getSimpleName();

        protected List<Track> doInBackground(String... params) {
            List<Track> trackList;
            // If there's no search string, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            try {
                final Map<String, Object> options = new HashMap<String, Object>();
                options.put(SpotifyService.COUNTRY, "US");
                Tracks results = mSpotifyService.getArtistTopTrack(params[0],options);
                trackList = new ArrayList<Track>();
                if (results != null && results.tracks != null) {
                    for (int i = 0; i < results.tracks.size(); i++) {
                        kaaes.spotify.webapi.android.models.Track track = results.tracks.get(i);
                        String imageUrl = null;
                        if (track.album != null && track.album.images != null && track.album.images.size() > 0) {
                            imageUrl = track.album.images.get(2).url;
                        }
                        trackList.add(new Track(track.id, track.name, track.album.name, imageUrl));
                    }
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the artist data, there's no point in attempting to parse it.
                return null;
            }
            return trackList;
        }

        @Override
        protected void onPostExecute(List tracks) {
            if (tracks != null) {
                mTracksAdapter.clear();
                mTracksAdapter.addAll(tracks);
            }
        }
    }
}
