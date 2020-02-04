package com.devej.matchingcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RankActivity extends AppCompatActivity implements View.OnClickListener {

    DBHelper dbhelper;
    SQLiteDatabase database;
    RecyclerView rankView;
    GridLayoutManager layoutManager;
    RecyclerViewAdapter adapter;
    Button rankexit, btnfind;
    EditText etfindname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        rankexit=(Button)findViewById(R.id.rankexit);
        btnfind=(Button)findViewById(R.id.btnfind);
        etfindname=(EditText)findViewById(R.id.etfindname);

        rankexit.setOnClickListener(this);
        btnfind.setOnClickListener(this);

        dbhelper= new DBHelper(this, DBHelper.DATABASE_NAME,
                null, DBHelper.DATABASE_VERSION);
        database= dbhelper.getReadableDatabase();

        rankView=findViewById(R.id.ranklist);
        layoutManager= new GridLayoutManager(this, 2);
        rankView.setLayoutManager(layoutManager);
        //setLayoutManager 빼먹으면 화면이 안나옴
        adapter= new RecyclerViewAdapter();
        executeQuery();
        rankView.setAdapter(adapter);

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

    }

    public void executeQuery(){
        int id=0, score=0;
        String name=null;
        Cursor cursor = database.rawQuery("Select name, score from " +
                "scoreTBL Order by score desc limit 10", null);
        int recordCount= cursor.getCount();
        for(int i=1; i<recordCount+1; i++){
            cursor.moveToNext();
            id=i;
            name=cursor.getString(0);
            score=cursor.getInt(1);
            adapter.addItem(new Rank(id, name, score));
        }
        cursor.close();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rankexit:
                finish();
                break;
            case R.id.btnfind:
                break;
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
