package com.devej.matchingcard;

import android.content.Context;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class CardBoardView extends SurfaceView implements SurfaceHolder.Callback {

    Context context;
    int stageNo;

    public CardBoardView(Context context) {
        super(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
