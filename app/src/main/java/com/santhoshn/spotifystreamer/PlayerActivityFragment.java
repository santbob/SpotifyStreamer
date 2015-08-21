package com.santhoshn.spotifystreamer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.santhoshn.spotifystreamer.track.Track;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends DialogFragment {

    public static final String TRACK_INDEX = "trackIndex";
    public static final String PLAY_LIST = "playList";

    private View mRootView;
    private TextView mArtistName;
    private TextView mAlbumName;
    private ImageView mAlbumArtWork;
    private TextView mTrackName;
    private TextView mTrackDuration;
    private SeekBar seekBarView;
    private TextView finalDuration;
    private ImageButton mPlayPrevBtn;
    private ImageButton mPlayPauseBtn;
    private ImageButton mPlayNextBtn;
    private ArrayList<Track> mTracks;
    private int mPlayingIndex;
    private boolean isPlaying = true;
    public PlayerActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        //return inflater.inflate(R.layout.fragment_player, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTracks = arguments.getParcelableArrayList(PLAY_LIST);
            mPlayingIndex = arguments.getInt(TRACK_INDEX);
        }

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        mArtistName = (TextView) rootView.findViewById(R.id.playing_artist_name);
        mAlbumName = (TextView) rootView.findViewById(R.id.playing_album_name);
        mAlbumArtWork = (ImageView) rootView.findViewById(R.id.playing_album_artwork);
        mTrackName = (TextView) rootView.findViewById(R.id.playing_track_name);
        seekBarView = (SeekBar) rootView.findViewById(R.id.scrubBar);
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
        updateUI();
        return  rootView;
    }

    /*
        Method which updates the Player with Track at currentIndex
     */
    public void updateTrackDetails(){
        Track track = mTracks.get(mPlayingIndex);
        if(track != null) {
            mArtistName.setText(track.getArtistName());
            mAlbumName.setText(track.getAlbumName());
            Picasso.with(getActivity()).load(track.getThumbnailImageUrl())
                    .into(mAlbumArtWork);
            mTrackName.setText(track.getTrackName());
            mTrackDuration.setText(getFormatedTime(track.getTrackDuration()));
        }

    }
    /*
        Format the Duration to mm:ss from given milliseconds
     */
    private String getFormatedTime(long milliSeconds) {
        return String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
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
        if(mPlayingIndex == 0) {
            mPlayPrevBtn.setClickable(false);
        }
        if (mPlayingIndex == mTracks.size() -1) {
            mPlayNextBtn.setClickable(false);
        }
        if (mPlayingIndex > 0 && mPlayingIndex < mTracks.size() -1){
            mPlayNextBtn.setClickable(true);
            mPlayPrevBtn.setClickable(true);
        }
        //Switch the play/pause drawables
        if (isPlaying) {
            mPlayPauseBtn.setImageResource(android.R.drawable.ic_media_play);
        } else {
            mPlayPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    /*
       Method to be called to reduce the Playing Index by 1 so previous track could be played
    */
    public void playPreviousTrack(View view) {
        mPlayingIndex = mPlayingIndex - 1;
        updateUI();
    }

    /*
        Method to be called to Play/Pause the track playing now
     */
    public void togglePlayandPause(View view) {
        isPlaying = !isPlaying;
        updateUI();
    }

    /*
        Method to be called to up the Playing Index so next track could be played
     */
    public void playNextTrack(View view) {
        mPlayingIndex = mPlayingIndex + 1;
        updateUI();
    }
}
