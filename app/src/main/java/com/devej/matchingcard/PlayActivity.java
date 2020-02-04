package com.devej.matchingcard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener,
        CardBoardFragment.OnFragmentInteractionListener {

    CardBoardFragment board;
    TextView stageNo, curScore, timer, beforeplaying, hintani;
    Button hint, pause, setting, exit;
    PlayExitDialog dialogExit;
    PauseDialog dialogPause;
    int hintNo=2;
    public int playingstage=1 , playingscore=0 ;
    int pauseTimeSec=0;
    Thread timerThread;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        stageNo=(TextView)findViewById(R.id.stageno);
        curScore=(TextView)findViewById(R.id.curscore);
        timer=(TextView)findViewById(R.id.timer);
        beforeplaying=(TextView)findViewById(R.id.beforeplaying);
        hint=(Button)findViewById(R.id.btnhint);
        pause=(Button)findViewById(R.id.btnpause);
        setting=(Button)findViewById(R.id.btnsetting2);
        exit=(Button)findViewById(R.id.btnexit2);
        hintani=(TextView)findViewById(R.id.hintani);

        hint.setOnClickListener(this);
        pause.setOnClickListener(this);
        setting.setOnClickListener(this);
        exit.setOnClickListener(this);

        board=(CardBoardFragment) getSupportFragmentManager()
                .findFragmentById(R.id.cardboard);
        // 프래그먼트 내부 세부 컴포넌트 접근
        // 프래그먼트 매니저 선언
        fragmentManager = getSupportFragmentManager();

        clearData();

        beforeplaying.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(timer.getText().toString().contains("0:00")){
                    beforeplaying.setVisibility(View.INVISIBLE);
                    //(구현필요) 스레드 시작
                    TimerRun t= new TimerRun();
                    timerThread= new Thread(t);
                    timerThread.start();
                }else{  //pause or exit 됐다가 돌아옴을 의미(시간이 흐른 상태)
                    beforeplaying.setVisibility(View.INVISIBLE);
                    //(구현필요) 스레드 재시작(runnable 쓰든 새로 만들든 어쨌든 재시작)
                    TimerRun t= new TimerRun();
                    timerThread= new Thread(t);
                    timerThread.start();
                }
                return false;
            }
        });
        //OnPause OnResume으로 재구현해야될듯
        //액티비티 수명주기 추가 공부 필요!!

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnhint:
                if(hintNo==0){ //힌트 개수가 0개
                    hint.setText("NO MORE HINT");
                    hint.setClickable(false);
                }else{
                    hintani.setText(board.HintFunc());
                    hintani.startAnimation(AnimationUtils.loadAnimation
                            (getApplicationContext(), R.anim.move));
                    hintNo--;
                    hint.setText("> HINT #"+hintNo);
                }
                break;
            case R.id.btnpause:
                //Dialogue -> RESUME/EXIT
                //(구현필요) timer 정지
                //OnPause 공부
                timerThread.interrupt();
                Log.d("ActivityLC", "PlayPauseButton");
                dialogPause= new PauseDialog(
                        PlayActivity.this, R.layout.pause_dialog,
                        R.id.btnresume, R.id.btnptoexit);
                dialogPause.createDialog();
                break;
            case R.id.btnsetting2:
                timerThread.interrupt();
                Intent intent=new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                beforeplaying.setVisibility(View.VISIBLE);
                break;
            case R.id.btnexit2:
                //Dialogue -> input name or cancle
                //(구현필요) timer 정지
                timerThread.interrupt();
                ExitDialog();
                break;
        }
    }

    @Override
    public void onReceivedAddedScore(int addedScore) {
        playingscore+=addedScore;
        curScore.setText(playingscore+"");
    }

    @Override
    public void onReceivedAddedStage(int addedStage) {
        playingstage+=addedStage;
        Log.d("activity", ""+playingstage);
        stageNo.setText(playingstage+"");
        //fragment view 초기화
//        fragmentTransaction = fragmentManager.beginTransaction();
//        Log.d("activity","Transaction begins");
//        fragmentTransaction.detach(board);
//        fragmentTransaction.attach(board);
//        fragmentTransaction.commit();
        //필요 없어짐
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ActivityLC", "PlayOnPause");
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
        Log.d("ActivityLC", "Play Destroy- Timer interrupted");
        timerThread.interrupt();

//        doUnbindService();
//        Intent music = new Intent();
//        music.setClass(this,MusicService.class);
//        stopService(music);
    }


    private void saveState() {
        SharedPreferences pref= getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putInt("playingStage", playingstage);
        editor.putInt("playingScore", playingscore);
        editor.commit();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d("activityLife", "Resume");
////        restoreState();
//    }

    private void restoreState() {
        SharedPreferences pref=getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if((pref !=null)&& (pref.contains("playingStage"))){
            playingscore=pref.getInt("playingScore", 0);
            playingstage=pref.getInt("playingStage", 0);
            //TextView에 set하는 코드 추가ㄴ
        }
    }

    private void clearData(){
        playingstage=1;
        playingscore=0;
        hintNo=2;
        pauseTimeSec=0;
    }

    public void ExitDialog(){
        dialogExit= new PlayExitDialog
                (PlayActivity.this, R.layout.play_exit_dialog,
                        R.id.btnexitsave, R.id.btncancle, R.id.btnnosave);
        dialogExit.createDialog();
    }

    class PlayExitDialog extends CustomDialog{

        public PlayExitDialog(Context Context, int LayoutId, int btn1Id,
                              int btn2Id, int btn3Id) {
            super(Context, LayoutId, btn1Id, btn2Id, btn3Id);
        }

        @Override
        void setCustombtn1(Context Context, AlertDialog alertDialog, Button btn1) {
            Log.d("Button", "SAVE SELECTED");
            // SAVE
            EditText name=(EditText)alertDialog.findViewById(R.id.etname);
            TextView score=(TextView)findViewById(R.id.curscore);
            int intscore=Integer.parseInt(score.getText().toString());
            String strName=name.getText().toString();
            Intent returnIntent= new Intent();
            Log.d("message", "저장됨: "+strName+intscore);
            returnIntent.putExtra("name",strName);
            returnIntent.putExtra("score",intscore);
            ((Activity)Context).setResult(PlayActivity.RESULT_OK,returnIntent);
            alertDialog.dismiss();
            ((Activity)Context).finish();
        }

        @Override
        void setCustombtn2(Context Context, AlertDialog alertDialog, Button btn2) {
            Log.d("Button", "CANCLE SELECTED");
            //CANCLE
            alertDialog.dismiss();
            //시작화면(터치시 시작/재개)
            TextView screen=(TextView) ((Activity)Context).findViewById(R.id.beforeplaying);
            screen.setVisibility(View.VISIBLE);
            //여기서는 타이머 재개할 필요x (screen visible되고 나면 터치해야 시작되기 때문)
        }

        @Override
        void setCustombtn3(Context Context, AlertDialog alertDialog, Button btn3) {
            //EXIT
            Log.d("Button", "EXIT SELECTED");
            Log.d("message", "저장 안됨: ");
            Intent returnIntent= new Intent();
            ((Activity)Context).setResult(PlayActivity.RESULT_CANCELED, returnIntent);
            alertDialog.dismiss();
            ((Activity)Context).finish();
        }
    }

    class PauseDialog extends CustomDialog{

        public PauseDialog(Context Context, int LayoutId, int btn1Id, int btn2Id) {
            super(Context, LayoutId, btn1Id, btn2Id);
        }

        @Override
        void setCustombtn1(Context Context, AlertDialog alertDialog, Button btn1) {
            // RESUME
            Log.d("Button", "RESUME SELECTED");
            alertDialog.dismiss();
            TextView screen=(TextView)findViewById(R.id.beforeplaying);
            screen.setVisibility(View.VISIBLE);
        }

        @Override
        void setCustombtn2(Context Context, AlertDialog alertDialog, Button btn2) {
            // EXIT
            //(구현필요) timer 정지
            // 현재 Dialog 캔슬하고 EXIT Dialog 생성
            Log.d("Button", "Pause->EXIT SELECTED");
            ExitDialog();
            alertDialog.dismiss();
        }

        @Override
        void setCustombtn3(Context Context, AlertDialog alertDialog, Button btn3) {

        }
    }

    class TimerRun implements Runnable{

        int timeSec=pauseTimeSec;
        int leftSec=180-pauseTimeSec;
        Handler timeHd= new Handler();

        @Override
        public void run() {
            for(int i=0; i<leftSec; i++){
                try {
                    Thread.sleep(1000);
                    timeSec+=1;
                    timeHd.post(new Runnable() {
                        @Override
                        public void run() {
                            if(timeSec<10){
                                timer.setText("0:0"+timeSec);
                            }else if(timeSec<60){
                                timer.setText("0:"+timeSec);
                            }else if(timeSec<70){
                                timer.setText("1:0"+(timeSec-60));
                            }else if(timeSec<120){
                                timer.setText("1:"+(timeSec-60));
                            }else if(timeSec<130){
                                timer.setText("2:0"+(timeSec-120));
                            }else if(timeSec==180){
                                timer.setText("3:00");
                            }
                            else{
                                timer.setText("2:"+(timeSec-120));
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    pauseTimeSec=timeSec;
                    Log.d("timer", "Interrupt! 진행된 초: "+pauseTimeSec);
                    return;
                }
            }
            Log.d("timer", "Timer 종료");
        }
    }

}

