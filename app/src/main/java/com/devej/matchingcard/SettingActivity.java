package com.devej.matchingcard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    Button exit, conmusic, setname, noname, save;
    Boolean musicState;
    EditText defaultName;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Log.d("ActivityLC", "Setting OnCreate");

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


        //doBindService();
//        Intent music = new Intent();
//        music.setClass(this, MusicService.class);

        restoreState();
        //Log.d("Service", "Setting restore state"+musicState);
        if(musicState){
            //state true-> music playing
            conmusic.setText("> TURN OFF THE MUSIC");
        }else{
            //state false-> music is not playing
            conmusic.setText("> TURN ON THE MUSIC");
        }


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
                    //        ((MainActivity)MainActivity.context_main).musicT.interrupt();
//                    super.getmServ().pauseMusic();
                    //super.getmServ().stopMusic();
                    Intent i=new Intent("com.devej.matchingcard.ACTION_PAUSE");
                    this.sendBroadcast(i);
                    Log.d("Broadcast", "broadcast to pause");
                    conmusic.setText("> TURN ON THE MUSIC");
                    musicState=false;
                    Log.d("Service", "Setting restore state"+musicState);
                }else{
                    //(구현 필요) turning on the music
//                    super.getmServ().resumeMusic();
                    //super.getmServ().restartMusic();
                    Intent i=new Intent("com.devej.matchingcard.ACTION_RESUME");
                    this.sendBroadcast(i);
                    Log.d("Broadcast", "broadcast to resume");
                    conmusic.setText("> TURN OFF THE MUSIC");
                    musicState=true;
                    Log.d("Service", "Setting restore state"+musicState);
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

//    @Override
//    protected void onUserLeaveHint() {
//        super.onUserLeaveHint();
//        Log.d("ActivityLC", "Home Button");
//        mServ.pauseMusic();
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.d("ActivityLC", "MainOnRestart");
//        mServ.resumeMusic();
//    }

    private void saveState() {
        SharedPreferences pref= getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putBoolean("musicState", musicState);
        editor.commit();
    }

    private void restoreState() {
        SharedPreferences pref=getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if((pref !=null)&& (pref.contains("musicState"))){
            musicState=pref.getBoolean("musicState", true);
        }else{
            musicState=true;
        }
    }

//    private boolean mIsBound = false;
//    private MusicService mServ;
//    private ServiceConnection Scon =new ServiceConnection(){
//
//        public void onServiceConnected(ComponentName name, IBinder
//                binder) {
//            mServ = ((MusicService.ServiceBinder)binder).getService();
//        }
//
//        public void onServiceDisconnected(ComponentName name) {
//            mServ = null;
//        }
//    };
//
//    void doBindService(){
//        bindService(new Intent(this,MusicService.class),
//                Scon, Context.BIND_AUTO_CREATE);
//        mIsBound = true;
//    }
//
//    void doUnbindService()
//    {
//        if(mIsBound)
//        {
//            unbindService(Scon);
//            mIsBound = false;
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();

//        if (mServ != null) {
//            mServ.pauseMusic();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (mServ != null) {
//            mServ.resumeMusic();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ActivityLC", "Setting OnDestroy");
        //doUnbindService();
    }
}
