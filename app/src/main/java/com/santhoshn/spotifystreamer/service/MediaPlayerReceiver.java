package com.santhoshn.spotifystreamer.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by santhosh on 22/08/15.
 */
public class MediaPlayerReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public MediaPlayerReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
