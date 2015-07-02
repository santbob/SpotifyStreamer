package com.santhoshn.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.santhoshn.spotifystreamer.artist.Artist;
import com.santhoshn.spotifystreamer.artist.ArtistListAdapter;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArtistListAdapter mArtistAdapter = null;
    private SpotifyService mSpotifyService = new SpotifyApi().getService();
    private EditText mSearchText = null;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mSearchText = (EditText) rootView.findViewById(R.id.search_artist);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
                    searchArtist();
                    return true;
                }
                return false;
            }
        });

        mArtistAdapter = new ArtistListAdapter(
                getActivity(),
                R.layout.artist_list_item, new ArrayList<Artist>());

        ListView listView = (ListView) rootView.findViewById(R.id.listview_artistList);
        listView.setAdapter(mArtistAdapter);
        return rootView;
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
                        String imageUrl = null;
                        if (artist.images != null && artist.images.size() > 0) {
                            imageUrl = artist.images.get(2).url;
                        }
                        artistList.add(new Artist(artist.id, artist.name, imageUrl));
                    }
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the artist data, there's no point in attempin to parse it.
                return null;
            }
            return artistList;
        }

        @Override
        protected void onPostExecute(List artists) {
            if (artists != null) {
                mArtistAdapter.clear();
                mArtistAdapter.addAll(artists);
            }
        }
    }
}
