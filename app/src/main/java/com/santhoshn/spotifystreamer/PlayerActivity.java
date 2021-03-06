package com.santhoshn.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.santhoshn.spotifystreamer.track.Track;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    private final String PLAYER_FRAGMENT_TAG = "player";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //handle back/up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        int trackIndex;
        int seekUntil;
        ArrayList<Track> playlist;

        //use the savedInstance values if available else read it from intent
        if (savedInstanceState == null) {
            trackIndex = getIntent().getIntExtra(PlayerActivityFragment.TRACK_INDEX, 0);
            playlist = getIntent().getParcelableArrayListExtra(PlayerActivityFragment.PLAY_LIST);
            seekUntil = getIntent().getIntExtra(PlayerActivityFragment.SEEK_TO, 0);
            PlayerActivityFragment fragment = (PlayerActivityFragment) getFragmentManager().findFragmentByTag(PLAYER_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new PlayerActivityFragment();
            }

            //Now create the arguments bundle and start the fragment.
            Bundle arguments = new Bundle();
            arguments.putInt(PlayerActivityFragment.TRACK_INDEX, trackIndex);
            if (playlist != null) {
                arguments.putParcelableArrayList(PlayerActivityFragment.PLAY_LIST, playlist);
            }

            arguments.putInt(PlayerActivityFragment.SEEK_TO, seekUntil);
            //if seekUntil is -1 its means just show whats playing.
            if (seekUntil == PlayerActivityFragment.INVALID_INDEX) {
                arguments.putBoolean(PlayerActivityFragment.IS_SHOW_NOW_PLAYING, true);
            } else {
                arguments.putBoolean(PlayerActivityFragment.IS_SHOW_NOW_PLAYING, false);
            }

            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction().replace(R.id.player_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
