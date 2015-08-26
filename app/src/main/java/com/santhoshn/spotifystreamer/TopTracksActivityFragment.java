package com.santhoshn.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class TopTracksActivityFragment extends Fragment {

    public static final String ARTIST_SPOTIFY_ID = "trackSpotifyId";
    public static final String ARTIST_NAME = "artistName";
    public static final String ARTIST_TOP_TRACKS = "artistTracks";

    private ArrayList<Track> mTracks = new ArrayList<Track>();
    private String mArtistName;
    private String mArtistSpotifyId;
    private MenuItem mNowPlayingItem = null;

    private TrackListAdapter mTracksAdapter;
    private SpotifyService mSpotifyService = new SpotifyApi().getService();

    public TopTracksActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            mArtistSpotifyId = savedInstanceState.getString(ARTIST_SPOTIFY_ID);
            mArtistName = savedInstanceState.getString(ARTIST_NAME);
            ArrayList<Track> tracks = savedInstanceState.getParcelableArrayList(ARTIST_TOP_TRACKS);
            if(tracks !=null) {
                mTracks = tracks;
            }
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mArtistSpotifyId = arguments.getString(ARTIST_SPOTIFY_ID);
                mArtistName = arguments.getString(ARTIST_NAME);
            }
        }


        View rootView = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        //create new instance of Adapter only if its not already created
        if (mTracksAdapter == null) {
            mTracksAdapter = new TrackListAdapter(getActivity(), R.layout.track_list_item, new ArrayList<Track>());
        }

        if (mArtistSpotifyId != null && mTracks.isEmpty()) {
            fetchTop10Tracks(mArtistSpotifyId);
        } else if (!mTracks.isEmpty()){
            updateTracksAdapter(mTracks);
        }

        ListView listView = (ListView) rootView.findViewById(R.id.listview_tracksList);
        listView.setAdapter(mTracksAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mNowPlayingItem.setVisible(true);
                    ((ArtistCallback) getActivity()).onTrackSelected(position, mTracks, 0);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mNowPlayingItem = menu.findItem(R.id.action_now_playing);
        mNowPlayingItem.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_now_playing) {
            ((ArtistCallback) getActivity()).onTrackSelected(0, mTracks, -1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void fetchTop10Tracks(String artistSpotifyId) {
        if(Utilities.isNetworkAvailable(getActivity())) {
            FetchTracksTask fetchTracksTask = new FetchTracksTask();
            fetchTracksTask.execute(artistSpotifyId);
        } else {
            showToast(getString(R.string.error_no_internet));
        }
    }

    private void updateTracksAdapter(ArrayList<Track> tracks) {
        if(mTracksAdapter != null) {
            mTracksAdapter.clear();
            mTracksAdapter.addAll(tracks);
        }
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
                options.put(SpotifyService.COUNTRY, Utilities.getCountry(getActivity()));
                Tracks results = mSpotifyService.getArtistTopTrack(params[0], options);
                trackList = new ArrayList<Track>();
                if (results != null && results.tracks != null) {
                    for (int i = 0; i < results.tracks.size(); i++) {
                        kaaes.spotify.webapi.android.models.Track apiTrack = results.tracks.get(i);
                        //initialize the imageUrl with defaultImageUrl
                        String imageUrl = "http://img.santhoshn.com/music.png";
                        if (apiTrack.album != null && apiTrack.album.images != null && apiTrack.album.images.size() > 0) {
                            //once we know there are images for the artist, pick the first one and update imageUrl.
                            //we can fetch the right size image based on the target device.
                            imageUrl = apiTrack.album.images.get(0).url;
                        }
                        Track track = new Track(apiTrack.id, apiTrack.name, apiTrack.album.name, imageUrl);
                        track.setArtistName(apiTrack.artists.get(0).name);
                        track.setTrackDuration(apiTrack.duration_ms);
                        track.setTrackUrl(apiTrack.preview_url);
                        trackList.add(track);
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
                    mTracks.clear();
                    mTracks.addAll(tracks);
                    updateTracksAdapter(mTracks);
                }
            } else {
                //Spotify Result null response which is bad, ideally should never happen. just for this project show it in toast.
                showToast(getString(R.string.error_null_result_for_tracks));
            }
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
