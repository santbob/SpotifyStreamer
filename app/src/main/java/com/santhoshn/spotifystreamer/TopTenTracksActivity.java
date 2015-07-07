package com.santhoshn.spotifystreamer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;


public class TopTenTracksActivity extends ActionBarActivity {

    private static final String TRACKS_FRAGMENT_TAG = "top_ten_tracks";
    private TopTenTracksActivityFragment mTracksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the Artist clicked as Subtitle
        setSubTitle();
        setContentView(R.layout.activity_top_ten_tracks);

        //Find the existing Fragment using the Tag it was created with.
        FragmentManager fragmentManager = getSupportFragmentManager();
        mTracksFragment = (TopTenTracksActivityFragment) fragmentManager.findFragmentByTag(TRACKS_FRAGMENT_TAG);

        //create new Fragment and begin transaction using fragment Manager, if its not created already
        if (mTracksFragment == null) {
            mTracksFragment = new TopTenTracksActivityFragment();
            fragmentManager.beginTransaction().add(mTracksFragment, TRACKS_FRAGMENT_TAG).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_ten_tracks, menu);
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

    private void setSubTitle() {
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_SUBJECT)){
            String subTitle = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            if(subTitle != null) {
                ActionBar ab = getSupportActionBar();
                ab.setSubtitle(subTitle);
            }
        }

    }
}
