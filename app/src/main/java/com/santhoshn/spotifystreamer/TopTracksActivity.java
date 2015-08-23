package com.santhoshn.spotifystreamer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.santhoshn.spotifystreamer.track.Track;

import java.util.ArrayList;


public class TopTracksActivity extends ActionBarActivity implements ArtistCallback {

    private static final String TRACKS_FRAGMENT_TAG = "top_ten_tracks";
    private TopTracksActivityFragment mTracksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the Artist clicked as Subtitle
        setSubTitle();
        setContentView(R.layout.activity_top_ten_tracks);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            String spotifyId = getIntent().getStringExtra(TopTracksActivityFragment.TRACK_SPOTIFY_ID);
            Bundle arguments = new Bundle();
            arguments.putString(TopTracksActivityFragment.TRACK_SPOTIFY_ID, spotifyId);

            TopTracksActivityFragment fragment = new TopTracksActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.topten_tracks_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onArtistSelected(String spotifyId, String subTitle) {
        //do nothing
    }

    @Override
    public void onTrackSelected(int trackIndex, ArrayList<Track> tracks) {
        Intent intent = new Intent(this, PlayerActivity.class)
                .putParcelableArrayListExtra(PlayerActivityFragment.PLAY_LIST, tracks)
                .putExtra(PlayerActivityFragment.TRACK_INDEX, trackIndex);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_share) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSubTitle() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(TopTracksActivityFragment.ARTIST_NAME)) {
            String subTitle = intent.getStringExtra(TopTracksActivityFragment.ARTIST_NAME);
            if (subTitle != null) {
                ActionBar ab = getSupportActionBar();
                ab.setSubtitle(subTitle);
            }
        }

    }
}
