package com.devej.matchingcard;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridBoardAdapter extends BaseAdapter {
    ArrayList<CardInfo> Cards= new ArrayList<CardInfo>();

    private Context Context;
    int CardNo;
    int[] cardImage;

    public GridBoardAdapter(Context context, int[] cardImage) {
        this.Context= context;
        this.CardNo = cardImage.length;
        this.cardImage= cardImage;
    }


    @Override
    public int getCount() {
        return CardNo;
        //뷰 개수 리턴-카드개수
    }

    @Override
    public Object getItem(int position) {
        return cardImage[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null){
            imageView= new ImageView(Context);  //사실 이거 이해 못함
            imageView.setLayoutParams(new GridView.LayoutParams(205,205));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(5,5,5,5);
        }else{
            imageView=(ImageView)convertView;
        }
        imageView.setImageResource(R.drawable.back);
        Log.d("fragmentAdapter", position+"view created");
        return imageView;
    }
}
