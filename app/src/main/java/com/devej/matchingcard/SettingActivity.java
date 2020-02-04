package com.devej.matchingcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    Button exit, conmusic, setname, noname, save;
    Boolean musicState;
    EditText defaultName;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Display dp = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        int width = (int) (dp.getWidth() * 0.6);
        int height = (int) (dp.getHeight() * 0.8);
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        this.setFinishOnTouchOutside(false);

        exit=(Button)findViewById(R.id.btnexitset);
        conmusic=(Button)findViewById(R.id.btnmusic);
        setname=(Button)findViewById(R.id.btnnameset);
        noname=(Button)findViewById(R.id.btnnoname);
        save=(Button)findViewById(R.id.btnnamesave);
        defaultName=(EditText)findViewById(R.id.defaultName);

        exit.setOnClickListener(this);
        conmusic.setOnClickListener(this);
        setname.setOnClickListener(this);
        noname.setOnClickListener(this);
        save.setOnClickListener(this);

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);
        musicState=true;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnexitset:
                finish();
                break;
            case R.id.btnmusic:
                if(musicState){
                    Log.d("setting", "Touched");
                    //(구현 필요) turning off the music
                    mServ.stopMusic();
                    mServ=null;
                    conmusic.setText("> TURN ON THE MUSIC");
                    musicState=false;
                }else{
                    //(구현 필요) turning on the music
                    doBindService();
                    Intent music = new Intent();
                    music.setClass(this, MusicService.class);
                    startService(music);
                    conmusic.setText("> TURN OFF THE MUSIC");
                    musicState=true;
                }
                break;
            case R.id.btnnameset:
                if(defaultName.getVisibility()==View.VISIBLE){
                    save.setVisibility(View.VISIBLE);
                    defaultName.setFocusable(true);
                }else{
                    defaultName.setVisibility(View.VISIBLE);
                    save.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btnnoname:
                defaultName.setText("");
                defaultName.setVisibility(View.INVISIBLE);
                save.setVisibility(View.INVISIBLE);
                break;
            case R.id.btnnamesave:
                if(defaultName.getText().toString().equals("")){
                    break;
                }else{
                    defaultName.setFocusable(false);
                    save.setVisibility(View.INVISIBLE);
                    //name 데이터 sharepreference나 어디든 보내기
                }

        }

    }

    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mServ != null) {
            mServ.pauseMusic();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);
    }
}
