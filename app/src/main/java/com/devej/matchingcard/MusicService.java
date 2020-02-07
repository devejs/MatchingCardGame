package com.devej.matchingcard;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MusicService extends Service  implements MediaPlayer.OnErrorListener {

    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer;
    private int length = 0;
    BroadcastReceiver serviceReceiver;

    public MusicService() {
    }

    public class ServiceBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        //Service 인터페이스
        Log.d("Service", "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("Service", "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service", "Service onCreate");
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bgm);
        mPlayer.setOnErrorListener(this);

        if (mPlayer != null) {
            mPlayer.setLooping(true);
            mPlayer.setVolume(100, 100);
        }


        mPlayer.setOnErrorListener(new OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int
                    extra) {

                onError(mPlayer, what, extra);
                return true;
            }
        });

        BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
            public static final String PAUSE_M="com.devej.matchingcard.ACTION_PAUSE";
            public static final String RESUME_M="com.devej.matchingcard.ACTION_RESUME";
            @Override
            public void onReceive(Context context, Intent intent) {
                String action= intent.getAction();

                if(action.equals(PAUSE_M)){
                    pauseMusic();
                    Log.d("Service", "pause from broadcast");
                }else if(action.equals(RESUME_M)){
                    resumeMusic();
                    Log.d("Service", "resume from broadcast");
                }
            }
        };
        IntentFilter filter= new IntentFilter();
        filter.addAction("com.devej.matchingcard.ACTION_PAUSE");
        filter.addAction("com.devej.matchingcard.ACTION_RESUME");
        //registerReceiver(serviceReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "Service onStart");
        mPlayer.start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for(int i=0; i<30; i++){
//
//                }
//                stopServiceItself();
//            }
//        }).start();
        return START_STICKY;
    }

    public void pauseMusic() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            Log.d("Service", "music pause");
            length = mPlayer.getCurrentPosition();

        }
    }

    public void resumeMusic() {
        if (mPlayer.isPlaying() == false) {
            mPlayer.seekTo(length);
            mPlayer.start();
            Log.d("Service", "music resume");
        }
    }

    public void stopMusic() {
        if(mPlayer.isPlaying()){
            mPlayer.stop();
        }
    }

    public void restartMusic(){
        if(!mPlayer.isPlaying()){
            mPlayer.start();
            Log.d("Service", "music restart");
        }
    }

    public void stopServiceItself(){
        stopSelf();
        Log.d("Service", "stopSelf()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service", "Service onDestroy");
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
        //unregisterReceiver(serviceReceiver);
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
        return false;
    }


}