package com.santhoshn.spotifystreamer;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String ARTIST_FRAGEMENT_TAG = "artists_fragment";
    private MainActivityFragment mArtistFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find the existing Fragment using the Tag it was created with.
        FragmentManager fragmentManager = getSupportFragmentManager();
        mArtistFragment = (MainActivityFragment) fragmentManager.findFragmentByTag(ARTIST_FRAGEMENT_TAG);

        //create new Fragment and begin transaction using fragment Manager, if its not created already
        if(mArtistFragment == null) {
            mArtistFragment = new MainActivityFragment();
            fragmentManager.beginTransaction().add(mArtistFragment, ARTIST_FRAGEMENT_TAG).commit();
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
}
