package com.devej.matchingcard;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;

public class MusicPlayer extends Thread{

    int music;
    MediaPlayer mPlayer;
    Context context;

    public MusicPlayer(Context context, int music) {
        this.context=context;
        this.music = music;
    }

    public void OnMusic(){

    }
    public void OffMusic(){

    }
    @Override
    public void run() {
        super.run();
        mPlayer = MediaPlayer.create(context, R.raw.bgm);
        if (mPlayer != null) {
            mPlayer.setLooping(true);
            mPlayer.setVolume(100, 100);
        }
        if(mPlayer.isPlaying()){

        }
    }

}
