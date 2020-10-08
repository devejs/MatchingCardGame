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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.os.Handler;

public class HelpActivity extends BaseActivity implements View.OnClickListener {

    TextView helpMessage, pageNo;
    ImageView helpImage;
    Button replay, next, before, exit;
    Thread storyThread, playThread;
    Handler handler=new Handler();
    ArrayList<String> arraylist = new ArrayList<String>();
    String sentence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // 디스플레이 화면 사이즈 구하기
        Display dp = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        // 화면 비율 설정
        int width = (int) (dp.getWidth() * 0.9);
        int height = (int) (dp.getHeight() * 0.9);
        // 현재 화면 적용
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        // 액티비티 바깥화면이 클릭되어도 종료되지 않게 설정
        this.setFinishOnTouchOutside(false);

        helpMessage = (TextView) findViewById(R.id.helptext);
        helpImage = (ImageView) findViewById(R.id.helpImage);
        replay = (Button) findViewById(R.id.btnreplay);
        next = (Button) findViewById(R.id.btnnext);
        before = (Button) findViewById(R.id.btnbefore);
        exit  = (Button) findViewById(R.id.btnexit3);
        pageNo= (TextView)findViewById(R.id.pageNo);

        replay.setOnClickListener(this);
        next.setOnClickListener(this);
        before.setOnClickListener(this);
        exit.setOnClickListener(this);

        arraylist= readtxt(R.raw.story);

//        storyThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                    arraylist= readtxt(R.raw.story);
//                    for(String line : arraylist){
//                        Log.d("message", line);
//                        if(line.contains("cls")){
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        helpMessage.append("cls");
//                                        Thread.sleep(2000);
//                                        helpMessage.setText("");
//                                        Thread.sleep(3000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                        } else{
//                            sentence=line;
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    helpMessage.append(sentence+"\n");
//                                }
//                            });
//                            Thread.sleep(1000);
//                        }
//                    }
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run(){
//                            helpMessage.append("\nPress NEXT to Continue...");
//                            replay.setVisibility(View.VISIBLE);
//                            next.setVisibility(View.VISIBLE);
//                        }
//                    });
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        Runnable storyRun = new StoryRun();
        storyThread=new Thread(storyRun);
        storyThread.start();
//        try{
//            storyThread.join();
//        }catch (InterruptedException e){
//        }


    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
//        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
//            return false;
//        }
//        return true;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnreplay:
                helpMessage.setText("");
                storyThread.start();
//                try{
//                    storyThread.join();
//                }catch (InterruptedException e){
//
//                }
                break;
            case R.id.btnbefore:
                break;
            case R.id.btnnext:
                exit.setVisibility(View.VISIBLE);
                before.setVisibility(View.VISIBLE);
//                Runnable playRun = new PlayRun();
//                playThread= new Thread(playRun);
//                playThread.start();
                helpMessage.setText("");
                break;
            case R.id.btnexit3:
                finish();
                break;
        }
    }

    public ArrayList<String> readtxt(int id) {
        InputStream inputData = getResources().openRawResource(id);

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputData, "UTF-8"));
            while (true) {
                String string = bufferedReader.readLine();

                if (string != null) {
                    arraylist.add(string);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arraylist;
    }

    class StoryRun implements Runnable{

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
//                arraylist= readtxt(R.raw.story);
                for(String line : arraylist){
//                    Log.d("message", line);
                    if(line.contains("cls")){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
//                                    helpMessage.append("cls");
//                                    Thread.sleep(2000);
//                                    helpMessage.setText("");
//                                    Thread.sleep(3000);
                                //아 이거 하고싶은데ㅜㅜㅜㅜㅜ
                                helpMessage.setText("");
                            }
                        });
                    } else{
                        sentence=line;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                helpMessage.append(sentence+"\n");
                            }
                        });
                        Thread.sleep(1000);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run(){
                        helpMessage.append("\nPress NEXT to Continue...");
                        replay.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    class PlayRun implements Runnable{
        @Override
        public void run() {

        }
    }
}