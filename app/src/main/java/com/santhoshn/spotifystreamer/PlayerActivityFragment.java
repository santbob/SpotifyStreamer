package com.santhoshn.spotifystreamer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.santhoshn.spotifystreamer.service.MediaPlayerReceiver;
import com.santhoshn.spotifystreamer.service.MediaPlayerService;
import com.santhoshn.spotifystreamer.service.MediaPlayerService.MediaPlayerBinder;
import com.santhoshn.spotifystreamer.track.Track;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A Dialog Fragment which acts as Media Player
 */
public class PlayerActivityFragment extends DialogFragment implements MediaPlayerReceiver.Receiver {

    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();

    public static final String TRACK_INDEX = "trackIndex";
    public static final String PLAY_LIST = "playList";
    public static final String SEEK_TO = "seekTo";
    public static final String IS_SHOW_NOW_PLAYING = "isShowNowPlaying";
    public static final int INVALID_INDEX = -1;

    private static final String SHARE_HASHTAG = "#SpotifyStreamerApp ";
    private ShareActionProvider mShareActionProvider;

    private Handler mHandler = new Handler();
    private MediaPlayerService mPlayerService;
    private MediaPlayerReceiver mReceiver;


    private TextView mArtistName;
    private TextView mAlbumName;
    private ImageView mAlbumArtWork;
    private TextView mTrackName;
    private TextView mTrackDuration;
    private SeekBar seekBar;
    private ImageButton mPlayPrevBtn;
    private ImageButton mPlayPauseBtn;
    private ImageButton mPlayNextBtn;
    private ArrayList<Track> mTracks;

    private int mPlayingIndex;
    private int mSeekTo;

    private boolean isPlaying = true;
    private boolean serviceBound = false;
    private Intent playerIntent;

    private String mNowPlaying;

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (mPlayerService != null) {
                if (mPlayerService.getPlayer() != null) {
                    try {
                        if (mPlayerService.getPlayer().isPlaying()) {
                            int progress = Utilities.getProgressPercentage(mPlayerService.getPlayer().getCurrentPosition(), mPlayerService.getPlayer().getDuration());
                            seekBar.setProgress(progress);
                        }
                    } catch (IllegalStateException stateException) {
                        Log.e(LOG_TAG, stateException.toString());
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.toString());
                    }
                }
            }
            mHandler.postDelayed(this, 500);
        }
    };
    //connect to the service
    private ServiceConnection mediaConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerBinder binder = (MediaPlayerBinder) service;
            //get service
            mPlayerService = binder.getService();
            if (mTracks != null) {
                mPlayerService.setTracks(mTracks);
            }
            if (mSeekTo != INVALID_INDEX) {
                mPlayerService.setTrackIndex(mPlayingIndex);
                if (mSeekTo != INVALID_INDEX) {
                    mPlayerService.setCompleted(mSeekTo);
                }
                startTrack();
            } else {
                mPlayingIndex = mPlayerService.getTrackIndex();
                showPlayer();
            }
            mPlayerService.setReceiver(mReceiver);
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    public PlayerActivityFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        //return inflater.inflate(R.layout.fragment_player, container, false);
        mReceiver = new MediaPlayerReceiver(new Handler());
        mReceiver.setReceiver(this);
        Bundle arguments = getArguments();
        if (arguments != null) {
            boolean isShowNowPlaying = arguments.getBoolean(IS_SHOW_NOW_PLAYING);
            if (isShowNowPlaying) {
                mTracks = arguments.getParcelableArrayList(PLAY_LIST);
                mSeekTo = arguments.getInt(SEEK_TO);
            } else {
                mTracks = arguments.getParcelableArrayList(PLAY_LIST);
                mPlayingIndex = arguments.getInt(TRACK_INDEX);
                mSeekTo = arguments.getInt(SEEK_TO);
            }
        }

        if (playerIntent == null) {
            playerIntent = new Intent(getActivity(), MediaPlayerService.class);
            getActivity().bindService(playerIntent, mediaConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playerIntent);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(TRACK_INDEX)) {
            mPlayingIndex = savedInstanceState.getInt(TRACK_INDEX);
            if (savedInstanceState.containsKey(SEEK_TO)) {
                mSeekTo = savedInstanceState.getInt(SEEK_TO);
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        mArtistName = (TextView) rootView.findViewById(R.id.playing_artist_name);
        mAlbumName = (TextView) rootView.findViewById(R.id.playing_album_name);
        mAlbumArtWork = (ImageView) rootView.findViewById(R.id.playing_album_artwork);
        mTrackName = (TextView) rootView.findViewById(R.id.playing_track_name);
        seekBar = (SeekBar) rootView.findViewById(R.id.scrubBar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mPlayerService != null && mPlayerService.getPlayer() != null) {
                    int completedTime = Utilities.getCompletedTime(progress, mPlayerService.getPlayer().getDuration());
                    mPlayerService.seekDuration(completedTime);
                    updateProgressBar();
                }
            }
        });

        mTrackDuration = (TextView) rootView.findViewById(R.id.track_duration);
        mPlayPrevBtn = (ImageButton) rootView.findViewById(R.id.play_previous_btn);
        mPlayPrevBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                playPreviousTrack(arg0);
            }
        });
        mPlayPauseBtn = (ImageButton) rootView.findViewById(R.id.play_pause_btn);
        mPlayPauseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                togglePlayandPause(arg0);
            }
        });
        mPlayNextBtn = (ImageButton) rootView.findViewById(R.id.play_next_btn);
        mPlayNextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                playNextTrack(arg0);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_player, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mTracks != null && mNowPlaying != null) {
            mShareActionProvider.setShareIntent(createShareNowPlayingIntent());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareNowPlayingIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, SHARE_HASHTAG + mNowPlaying);
        return shareIntent;
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(playerIntent);
        mPlayerService = null;
        super.onDestroy();
    }

    /*
        Method which updates the Player with Track at currentIndex
     */
    public void updateTrackDetails() {
        Track track = mTracks.get(mPlayingIndex);
        if (track != null) {
            mArtistName.setText(track.getArtistName());
            mAlbumName.setText(track.getAlbumName());
            Picasso.with(getActivity()).load(track.getThumbnailImageUrl())
                    .into(mAlbumArtWork);
            mTrackName.setText(track.getTrackName());
            mTrackDuration.setText(Utilities.getFormatedTime(30000));
            mNowPlaying = "#NowPlaying " + track.getTrackUrl();
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareNowPlayingIntent());
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void updateUI() {
        //call updateTracks to update the player with current Index Track
        updateTrackDetails();
        //Disable Prev or Next or both button based on the current Index and number of tracks available
        if (mPlayingIndex == 0) {
            mPlayPrevBtn.setClickable(false);
        }
        if (mPlayingIndex == mTracks.size() - 1) {
            mPlayNextBtn.setClickable(false);
        }
        if (mPlayingIndex > 0 && mPlayingIndex < mTracks.size() - 1) {
            mPlayNextBtn.setClickable(true);
            mPlayPrevBtn.setClickable(true);
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 500);
    }

    public void startTrack() {
        seekBar.setProgress(Utilities.getProgressPercentage(mSeekTo, 30000));
        if (mPlayerService != null) {
            mPlayerService.setTrackIndex(mPlayingIndex);
            mHandler.removeCallbacks(mUpdateTimeTask);
            updateProgressBar();
            if (isPlaying) {
                mPlayerService.playTrack();
            }
            updateUI();
        }
    }

    public void showPlayer() {
        seekBar.setProgress(Utilities.getProgressPercentage(mSeekTo, 30000));
        if (mPlayerService != null) {
            updateProgressBar();
            updateUI();
        }
    }

    /*
       Method to be called to reduce the Playing Index by 1 so previous track could be played
    */
    public void playPreviousTrack(View view) {
        mPlayingIndex = mPlayingIndex - 1;
        startTrack();
    }

    /*
        Play/Pause the track and update the drawable icons
     */
    public void playPauseTrack() {
        //Switch the play/pause drawables
        if (isPlaying) {
            mPlayPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
            mPlayerService.resumeTrack();
        } else {
            mPlayPauseBtn.setImageResource(android.R.drawable.ic_media_play);
            mPlayerService.pauseTrack();
        }
    }

    /*
        Method to be called to Play/Pause the track playing now
     */
    public void togglePlayandPause(View view) {
        isPlaying = !isPlaying;
        playPauseTrack();
    }

    /*
        Method to be called to up the Playing Index so next track could be played
     */
    public void playNextTrack(View view) {
        mPlayingIndex = mPlayingIndex + 1;
        startTrack();
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == MediaPlayerService.TRACK_COMPLETED) {
            if (mPlayingIndex < mTracks.size() - 1) {
                playNextTrack(null);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPlayingIndex != -1) {
            outState.putInt(TRACK_INDEX, mPlayingIndex);
            if (mPlayerService != null && mPlayerService.getPlayer() != null) {
                outState.putInt(SEEK_TO, mPlayerService.getPlayer().getCurrentPosition());
            }
        }
        super.onSaveInstanceState(outState);
    }

}
