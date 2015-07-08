package com.santhoshn.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

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

    private void fetchTop10Tracks(String artistSpotifyId) {
        FetchTracksTask fetchTracksTask = new FetchTracksTask();
        fetchTracksTask.execute(artistSpotifyId);
    }

    //Reference - http://developer.android.com/guide/topics/resources/runtime-changes.html#RetainingAnObject
    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String artistSpotifyId = intent.getStringExtra(Intent.EXTRA_TEXT);
            fetchTop10Tracks(artistSpotifyId);
        }
        //create new instance of Adapter only if its not already created
        if(mTracksAdapter == null) {
            mTracksAdapter = new TrackListAdapter(getActivity(), R.layout.track_list_item, new ArrayList<Track>());
        }

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
                Tracks results = mSpotifyService.getArtistTopTrack(params[0], options);
                trackList = new ArrayList<Track>();
                if (results != null && results.tracks != null) {
                    for (int i = 0; i < results.tracks.size(); i++) {
                        kaaes.spotify.webapi.android.models.Track track = results.tracks.get(i);
                        //initialize the imageUrl with defaultImageUrl
                        String imageUrl = "http://img.santhoshn.com/music.png";
                        if (track.album != null && track.album.images != null && track.album.images.size() > 0) {
                            //once we know there are images for the artist, pick the first one and update imageUrl.
                            //we can fetch the right size image based on the target device.
                            imageUrl = track.album.images.get(0).url;
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
                if (tracks.isEmpty()) {
                    //Spotify returned empty result, show toast to user informing search resulted in empty results
                    showToast(getString(R.string.error_empty_top10_tracks_search_result));
                } else {
                    //clear the adapater and add the new results we got.
                    mTracksAdapter.clear();
                    mTracksAdapter.addAll(tracks);
                }
            } else {
                //Spotify Result null response which is bad, ideally should never happen. just for this project show it in toast.
                showToast(getString(R.string.error_null_result_for_tracks));
            }
        }

        /*
           Shows the Toast message when there is no data for listview.
        */
        private void showToast(String displayText) {
            Toast toast = Toast.makeText(getActivity(), displayText, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 10, 30);
            toast.show();
        }
    }
}
