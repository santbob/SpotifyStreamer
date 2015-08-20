package com.santhoshn.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        String trackIndex = getIntent().getStringExtra(PlayerActivityFragment.TRACK_INDEX);
        ArrayList playlist = getIntent().getParcelableArrayListExtra(PlayerActivityFragment.PLAY_LIST);
        Bundle arguments = new Bundle();
        arguments.putString(PlayerActivityFragment.TRACK_INDEX, trackIndex);
        arguments.putParcelableArrayList(PlayerActivityFragment.PLAY_LIST, playlist);

        PlayerActivityFragment fragment = new PlayerActivityFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.topten_tracks_container, fragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
