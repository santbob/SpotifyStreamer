package com.santhoshn.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.santhoshn.spotifystreamer.artist.Artist;
import com.santhoshn.spotifystreamer.artist.ArtistListAdapter;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A Main fragment containing a searchbox and listview full or artists search Result.
 */
public class ArtistFragment extends Fragment {

    private ArtistListAdapter mArtistAdapter;
    private SpotifyService mSpotifyService = new SpotifyApi().getService();
    private EditText mSearchText = null;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_ARTIST_INDEX = "selected_artist_index";

    public ArtistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artists, container, false);

        mSearchText = (EditText) rootView.findViewById(R.id.search_artist);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
                    if(Utilities.isNetworkAvailable(getActivity())) {
                        searchArtist();
                    } else {
                        showToast(getString(R.string.error_no_internet));
                    }
                    //set the scroll position of listview to default
                    //reference - https://discussions.udacity.com/t/listview-scroll-position-stays-the-same-on-new-search/24601
                    mListView.setSelection(View.SCROLLBAR_POSITION_DEFAULT);
                    return true;
                }
                return false;
            }
        });

        if(mArtistAdapter == null) {
            mArtistAdapter = new ArtistListAdapter(
                    getActivity(),
                    R.layout.artist_list_item, new ArrayList<Artist>());
        }


        mListView = (ListView) rootView.findViewById(R.id.listview_artistList);
        mListView.setAdapter(mArtistAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String spotifyId = mArtistAdapter.getItem(position).getSpotifyId();
                String artistName = mArtistAdapter.getItem(position).getName();
                ((ArtistCallback) getActivity()).onArtistSelected(spotifyId, artistName);
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_ARTIST_INDEX)) {
            mPosition = savedInstanceState.getInt(SELECTED_ARTIST_INDEX);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_ARTIST_INDEX, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    private void searchArtist() {
        SearchArtistTask task = new SearchArtistTask();
        task.execute(mSearchText.getText().toString());
    }

    private class SearchArtistTask extends AsyncTask<String, Void, List> {
        private final String LOG_TAG = SearchArtistTask.class.getSimpleName();

        protected List<Artist> doInBackground(String... params) {
            List<Artist> artistList;
            // If there's no search string, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            try {
                ArtistsPager results = mSpotifyService.searchArtists(params[0]);
                artistList = new ArrayList<Artist>();
                if (results != null && results.artists != null && results.artists.items != null) {
                    for (int i = 0; i < results.artists.items.size(); i++) {
                        kaaes.spotify.webapi.android.models.Artist artist = results.artists.items.get(i);
                        //initialize the imageUrl with defaultImageUrl
                        String imageUrl = "http://img.santhoshn.com/music.png";
                        if (artist.images != null && artist.images.size() > 0) {
                            //once we know there are images for the artist, pick the first one and update imageUrl.
                            //we can fetch the right size image based on the target device.
                            imageUrl = artist.images.get(0).url;
                        }
                        artistList.add(new Artist(artist.id, artist.name, imageUrl));
                    }
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the artist data, there's no point in attempting to parse it.
                return null;
            }
            return artistList;
        }

        @Override
        protected void onPostExecute(List artists) {
            //Clear adapter always before updating new ones, to avoid previous results being shown for empty results or error results
            mArtistAdapter.clear();
            if (artists != null) {
                if (artists.isEmpty()) {
                    //Spotify returned empty result, show toast to user informing search resulted in empty results
                    showToast(getString(R.string.error_empty_artist_search_result));
                } else {
                    //add the new results we got.
                    mArtistAdapter.addAll(artists);
                    if (mPosition != ListView.INVALID_POSITION) {
                        // If we don't need to restart the loader, and there's a desired position to restore
                        // to, do so now.
                        mListView.smoothScrollToPosition(mPosition);
                    }
                }
            } else {
                //Spotify Result null response which is bad, ideally should never happen. just for this project show it in toast.
                showToast(getString(R.string.error_null_result_for_artist));
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
