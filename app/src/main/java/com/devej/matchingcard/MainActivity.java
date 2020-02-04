package com.devej.matchingcard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.os.Handler;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button play, rank, help, setting, exit;
    TextView title;
    EditText maintext;
    Handler handler= new Handler();
    String message="Press play to start...";
    Intent i;
    private static final int REQUEST_CODE_SCORE=1996;

    DBHelper dbhelper;
    SQLiteDatabase database;
    BGMService bgm;

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
                Scon,Context.BIND_AUTO_CREATE);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ActivityLC", "MainOnCreate");
        setContentView(R.layout.activity_main);
        Log.d("ActivityLC", "MainSetView");
        play=(Button)findViewById(R.id.btnplay);
        rank=(Button)findViewById(R.id.btnrank);
        help=(Button)findViewById(R.id.btnhelp);
        setting=(Button)findViewById(R.id.btnsetting);
        exit=(Button)findViewById(R.id.btnexit);
        title=(TextView)findViewById(R.id.title);
        maintext=(EditText)findViewById(R.id.maintext);

        play.setOnClickListener(this);
        rank.setOnClickListener(this);
        help.setOnClickListener(this);
        setting.setOnClickListener(this);
        exit.setOnClickListener(this);

        StartThread thread = new StartThread();
        thread.start();
//        bgm.onPrepared();
        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnplay:
                i= new Intent(getApplicationContext(), PlayActivity.class);
                startActivityForResult(i, REQUEST_CODE_SCORE);
                break;
            case R.id.btnrank:
                i= new Intent(getApplicationContext(), RankActivity.class);
                startActivity(i);
                break;
            case R.id.btnhelp:
                i= new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(i);
                break;
            case R.id.btnsetting:
                i= new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(i);
                break;
            case R.id.btnexit:
                //Alert Dialogue
                ExitDialog dialog= new ExitDialog(MainActivity.this, R.layout.exit_dialog,
                        R.id.btnexitfinal, R.id.btncanclefinal);
                dialog.createDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE_SCORE){
            if(resultCode==RESULT_OK){
                Log.d("message", "데이터 도착");
                int score=data.getIntExtra("score", 10);
                String name=data.getStringExtra("name");
                Log.d("message",data.toString());
                Log.d("message", "점수 "+score+"이름"+name);
                addScore(score, name);
            }
        }
    }

    private void addScore(int score, String name){
        dbhelper= new DBHelper(this, DBHelper.DATABASE_NAME,
               null, DBHelper.DATABASE_VERSION);
        database= dbhelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("score", score);
        values.put("name", name);
        long newRowId = database.insert("scoreTBL", null, values);
        if(newRowId== -1){
            Toast.makeText(this, "저장 실패", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ActivityLC", "MainOnPause");

        if (mServ != null) {
            mServ.pauseMusic();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ActivityLC", "MainOnStart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ActivityLC", "MainOnResume");

        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ActivityLC", "MainOnStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ActivityLC", "MainOnDestroy");

        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);
    }

    class StartThread extends Thread{

        public void run(){
            try{
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        maintext.append("\n\n"+message);
                    }
                },1000);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    class ExitDialog extends CustomDialog{

        public ExitDialog(Context Context, int LayoutId, int Btn1Id, int Btn2Id) {
            super(Context, LayoutId, Btn1Id, Btn2Id);
        }

        @Override
        void setCustombtn1(Context Context, AlertDialog alertDialog, Button btn1) {
            //EXIT
            Log.d("Button", "EXIT SELECTED");
            alertDialog.dismiss();
            MainActivity.this.finish();
        }

        @Override
        void setCustombtn2(Context Context, AlertDialog alertDialog, Button btn2) {
            //CANCLE
            Log.d("Button", "CANCLE SELECTED");
            alertDialog.dismiss();
        }

        @Override
        void setCustombtn3(Context Context, AlertDialog alertDialog, Button btn3) {

        }
    }
}
