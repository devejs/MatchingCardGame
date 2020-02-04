package com.devej.matchingcard;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BGMService extends Service implements MediaPlayer.OnPreparedListener{

    private static final String ACTION_PLAY = "com.devej.matchingcard.PLAY";
        MediaPlayer mediaPlayer = null;

        public int onStartCommand(Intent intent, int flags, int startId) {
            if (intent.getAction().equals(ACTION_PLAY)) {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bgm);
                mediaPlayer.setLooping(true);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.prepareAsync(); // prepare async to not block main thread
            }
            return flags;
        }

        /** Called when MediaPlayer is ready */
        public void onPrepared(MediaPlayer player) {
            player.start();
        }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


