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
import android.os.ResultReceiver;
import android.util.Log;

import com.santhoshn.spotifystreamer.track.Track;

import java.util.ArrayList;

/**
 * Created by santhosh on 21/08/15.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    private final IBinder mediaBinder = new MediaPlayerBinder();

    MediaPlayer mMediaPlayer = null;
    ResultReceiver mReceiver = null;
    ArrayList<Track> mTraks;

    int mTrackIndex;
    boolean trackLoaded = false;

    public static final int TRACK_COMPLETED = 0;


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
        mMediaPlayer.setOnSeekCompleteListener(this);
    }

    public void setTracks(ArrayList<Track> tracks) {
        mTraks = tracks;
    }

    public void setTrackIndex(int trackIndex) {
        mTrackIndex = trackIndex;
    }

    public void setReceiver(ResultReceiver rec) {
        mReceiver = rec;
    }

    public int getTrackIndex() {
        return mTrackIndex;
    }

    public MediaPlayer getPlayer(){
        return mMediaPlayer;
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
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (mMediaPlayer != null && mReceiver != null) {
            mReceiver.send(TRACK_COMPLETED, null);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
       if(trackLoaded) {
           mReceiver.send(TRACK_COMPLETED, null);
           trackLoaded = false;
       } else {
           trackLoaded = true;
       }
    }

    public void playTrack() {
        mMediaPlayer.reset();
        mMediaPlayer.seekTo(0);
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

    public void seekDuration(int untilMilli) {
        mMediaPlayer.seekTo(untilMilli);
    }

    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }
}