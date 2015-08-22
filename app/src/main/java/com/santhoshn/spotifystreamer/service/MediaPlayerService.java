package com.santhoshn.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.santhoshn.spotifystreamer.track.Track;

import java.util.ArrayList;

/**
 * Created by santhosh on 21/08/15.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private final IBinder mediaBinder = new MediaPlayerBinder();

    MediaPlayer mMediaPlayer = null;
    ArrayList<Track> mTraks;
    int mTrackIndex;


    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        mTrackIndex = 0;
        //create player
        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    public void setTracks(ArrayList<Track> tracks) {
        mTraks = tracks;
    }

    public void setTrackIndex(int trackIndex) {
        mTrackIndex = trackIndex;
    }

    /**
     * Called when MediaPlayer is ready
     */
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mediaBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    public void playTrack() {
        mMediaPlayer.reset();
        Track track = mTraks.get(mTrackIndex);
        try{
            mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(track.getTrackUrl()));
        } catch(Exception e){
            Log.e("MediaPlayerService", "Error setting data source", e);
        }
        mMediaPlayer.prepareAsync();
    }

    public void pauseTrack() {
        if(mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void resumeTrack() {
        if(!mMediaPlayer.isPlaying()) {
            playTrack();
        }
    }

    public void seekDuration(int untilDuration) {

    }

    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }
}