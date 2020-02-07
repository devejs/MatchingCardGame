package com.devej.matchingcard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private boolean mIsBound = false;
    private MusicService mServ;
    private Intent music;

    public Intent getMusic() {
        return music;
    }

    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
            Log.d("Service- call Component", "Service conntected ");
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
            Log.d("Service- call Component", "Service unconntected ");
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        Log.d("Service- call Component", "bindService() ");
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            Log.d("Service- call Component", "unbindService() ");
            mIsBound = false;
        }
    }

    public MusicService getmServ() {
       //Log.d("Service", mServ+"서비스객체");
        return this.mServ;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doBindService();
        music = new Intent();
        music.setClass(this, MusicService.class);
        //Log.d("Service", "onCreate activity service"+this.getLocalClassName());
        //여기서 this를 걸어버리면 액티비티마다 본인 context 가져오네
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.d("ActivityLC", "Home Button");
        mServ.pauseMusic();
       // Log.d("Service", mServ+"");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
      //  Log.d("ActivityLC", "Music Resume");
        mServ.resumeMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();
//        music.setClass(this,MusicService.class
//        Intent music = new Intent(););
    }
}
