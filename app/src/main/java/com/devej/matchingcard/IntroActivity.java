package com.devej.matchingcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class IntroActivity extends AppCompatActivity {

    EditText text;
    Handler handler1 = new Handler();
    String start = "Starting MC-Game...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ActivityLC", "IntroOnCreate");
        setContentView(R.layout.activity_intro);
        Log.d("ActivityLC", "IntroSetView");


        text = (EditText) findViewById(R.id.text);

        final BackThread introthread = new BackThread();
        introthread.start();
        Log.d("ActivityLC", "Intro Thread Start");

        LinearLayout screen= (LinearLayout) findViewById(R.id.introscreen);
        screen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                introthread.interrupt();
                if(introthread.isInterrupted()){
                    Log.d("message", "interrupt occured");
                }
                return false;
            }
        });
    }

    class BackThread extends Thread {

        String chStr;

        public void run() {
                for(int i=1; i<start.length()+1; i++){
                    chStr=start.split("")[i];
                    //split 주의! index 0은 ""
                    handler1.post(new Runnable(){
                        @Override
                        public void run() {
                            text.append(chStr);
                            text.setSelection(text.getText().length()); //커서 이동
                        }
                    });
                    try {
                        if(text.getText().toString().contains(start)){
                            Thread.sleep(1000);
                            Log.d("message", "1sec");
//                        Handler handler2= new Handler();
//                        handler2.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.d("message","sleep");
//                                Intent intent= new Intent(getApplicationContext(), MainActivity.class);
//                                Log.d("message", "create intent");
//                                startActivity(intent);
//                                Log.d("message","start activity");
//                                finish();
//                                Log.d("message", "finish");
//                            }
//                        }, 1000);
                            //equals 왜 안되는지 더 공부해보기

                        }else{
                            Thread.sleep(300);
//                    Log.d("message", text.getText().toString());
//                    Log.d("message", "0.3sec");
//                            if(this.isInterrupted()){
//
//                            }
                        }
                    } catch (Exception InterruptedException) {
                        Log.d("message", this+"");
                        Log.d("message", "Interrupt detected");
                        Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                }
                Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                Log.d("ActivityLC", "finish 호출 전");
                finish();
                Log.d("MoveActivity","인트로 종료");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ActivityLC", "IntroOnStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ActivityLC", "IntroOnResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ActivityLC", "IntroOnPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ActivityLC", "IntroOnStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ActivityLC", "IntroOnDestroy");
    }
}
