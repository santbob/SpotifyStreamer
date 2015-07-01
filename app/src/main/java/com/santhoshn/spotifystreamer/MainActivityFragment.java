package com.santhoshn.spotifystreamer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArtistListAdapter mArtistAdapter = null;

    private EditText searchText = null;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        searchText = (EditText) rootView.findViewById(R.id.search_artist);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager in = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
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
        List<Artist> artistList = new ArrayList<Artist>();
        artistList.add(new Artist("1", "Britney", "https://d3rt1990lpmkn.cloudfront.net/640/aced81eca24558d0ce2d128bac24f1a714316bae"));
        artistList.add(new Artist("2", "Katy Perry", "https://d3rt1990lpmkn.cloudfront.net/640/d962669f8cb25b4d41b5f8970960d819ea88e2fc"));
        artistList.add(new Artist("3", "Iggy Azalea", "https://d3rt1990lpmkn.cloudfront.net/640/54664af051f70685bc26529ac53d0527de79b059"));
        artistList.add(new Artist("4", "Rihanna", "https://d3rt1990lpmkn.cloudfront.net/640/d2370c8f8e57525bd4c76931c61e97787b40823b"));
        artistList.add(new Artist("5", "Christina Aguilera", "https://d3rt1990lpmkn.cloudfront.net/640/3411fc406684ec4f073314fe3c679c0000c15fbd"));
        artistList.add(new Artist("6", "Lady Gaga", "https://d3rt1990lpmkn.cloudfront.net/640/90427345523c8bbfbdfb981cec36a4ff7d8924ce"));
        artistList.add(new Artist("7", "Nicki Minaj", "https://d3rt1990lpmkn.cloudfront.net/640/60022a8b1b807f1cd7b540769bdf8e655e72fc32"));
        mArtistAdapter.addAll(artistList);
    }
}
