package com.devej.matchingcard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.os.Handler;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    Button play, rank, help, setting, exit;
    TextView title, threadtext;
    EditText maintext;
    Handler handler= new Handler();
    String message="Press play to start...";
    Intent i;
    private static final int REQUEST_CODE_SCORE=1996;

    public static Context context_main;
    Thread musicT;

    DBHelper dbhelper;
    SQLiteDatabase database;
    MediaPlayer mPlayer;

//    private boolean mIsBound = false;
//    private MusicService mServ;
//    private ServiceConnection Scon =new ServiceConnection(){
//
//        public void onServiceConnected(ComponentName name, IBinder
//                binder) {
//            mServ = ((MusicService.ServiceBinder)binder).getService();
//            Log.d("Service", "Service conntected "+getApplicationContext());
//        }
//
//        public void onServiceDisconnected(ComponentName name) {
//            mServ = null;
//            Log.d("Service", "Service unconntected "+getApplicationContext());
//        }
//    };

//    void doBindService(){
//        bindService(new Intent(this,MusicService.class),
//                Scon,Context.BIND_AUTO_CREATE);
//        Log.d("Service", "Service bound "+getApplicationContext());
//        mIsBound = true;
//    }
//
//    void doUnbindService()
//    {
//        if(mIsBound)
//        {
//            unbindService(Scon);
//            Log.d("Service", "Service unbound "+getApplicationContext());
//            mIsBound = false;
//        }
//    }

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
        threadtext=(TextView)findViewById(R.id.threadtext);

        play.setOnClickListener(this);
        rank.setOnClickListener(this);
        help.setOnClickListener(this);
        setting.setOnClickListener(this);
        exit.setOnClickListener(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        StartThread thread = new StartThread();
        thread.start();

//        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);
        Log.d("Service", "Main Service started");


        //백그라운드 스레드 테스트
//        musicT= new Thread(new Runnable() {
//            @Override
//            public void run() {
//            mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bgm);
//            if (mPlayer != null) {
//                mPlayer.setLooping(true);
//                mPlayer.setVolume(100, 100);
//          }
//            mPlayer.start();
//            }
//        });
//        musicT.start();
//        context_main=this;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnplay:
                i= new Intent(getApplicationContext(), PlayActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                startActivityForResult(i, REQUEST_CODE_SCORE);
                break;
            case R.id.btnrank:
                i= new Intent(getApplicationContext(), RankActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                startActivity(i);
                break;
            case R.id.btnhelp:
                i= new Intent(getApplicationContext(), HelpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                startActivity(i);
                break;
            case R.id.btnsetting:
                i= new Intent(getApplicationContext(), SettingActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
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

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return super.onKeyDown(keyCode, event);
//        if (keyCode == KeyEvent.KEYCODE_HOME) {
//            Log.d("ActivityLC", "Home Button Clicked");
//            mServ.pauseMusic();
//            return true;
//        }else{
//            return false;
//        }
//    } //key event를 포기한 이유는 home key가 인식 안되는 기기도 있기 때문
        // -> 대신 onUserLeaveHint() 사용
        // 간단하게 말해서 액티비티가 사용자 이벤트에 의해 백그라운드로 갈 때 콜백됨(홈키같은거)

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.d("ActivityLC", "Home Button");
        super.getmServ().pauseMusic();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("ActivityLC", "MainOnRestart");
        super.getmServ().resumeMusic();
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

//        if (mServ != null) {
//            mServ.pauseMusic();
//        }
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

//        if (mServ != null) {
//            mServ.resumeMusic();
//        }
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
                        threadtext.setText("\n\n"+message);
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
