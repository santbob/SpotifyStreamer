package com.santhoshn.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        int trackIndex = getIntent().getIntExtra(PlayerActivityFragment.TRACK_INDEX, 0);
        ArrayList playlist = getIntent().getParcelableArrayListExtra(PlayerActivityFragment.PLAY_LIST);
        int seekUntil = getIntent().getIntExtra(PlayerActivityFragment.SEEK_TO,0);

        Bundle arguments = new Bundle();
        arguments.putInt(PlayerActivityFragment.TRACK_INDEX, trackIndex);
        arguments.putParcelableArrayList(PlayerActivityFragment.PLAY_LIST, playlist);
        arguments.putInt(PlayerActivityFragment.SEEK_TO, seekUntil);

        PlayerActivityFragment fragment = new PlayerActivityFragment();
        fragment.setArguments(arguments);

        getFragmentManager().beginTransaction().add(R.id.player_container, fragment).commit();
    }
}
