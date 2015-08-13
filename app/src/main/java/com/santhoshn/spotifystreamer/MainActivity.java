package com.santhoshn.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements ArtistFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String TOPTEN_TRACKS_FRAGEMENT_TAG = "top10_tracks_fragment";
    private ArtistFragment mArtistFragment;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.topten_tracks_container) != null) {
            // The topten tracks container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the top10 tracks view in this activity by
            // adding or replacing the top10tracks fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.topten_tracks_container, new TopTenTracksActivityFragment(), TOPTEN_TRACKS_FRAGEMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onItemSelected(String spotifyId, String subTitle) {
        if (mTwoPane) {
            // In two-pane mode, show the top10tracks view in this activity by
            // adding or replacing the top10tracks fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putString(TopTenTracksActivityFragment.TRACK_SPOTIFY_ID, spotifyId);

            TopTenTracksActivityFragment fragment = new TopTenTracksActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.topten_tracks_container, fragment, TOPTEN_TRACKS_FRAGEMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, TopTenTracksActivity.class)
                    .putExtra(TopTenTracksActivityFragment.TRACK_SPOTIFY_ID, spotifyId)
                    .putExtra(TopTenTracksActivityFragment.TRACK_SUBTITLE, subTitle);
            startActivity(intent);
        }
    }
}
