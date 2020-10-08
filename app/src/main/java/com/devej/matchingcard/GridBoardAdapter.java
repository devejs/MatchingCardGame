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
        return Cards.get(position);
    }

    public void addItem(int cardPic){
        Cards.add(new CardInfo(cardPic, true));
        //처음에 보여줘야 하므로 true로 설정
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void modifyItem(int position, Boolean cardOpened){
        Cards.get(position).setCardOpened(cardOpened);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null){
            imageView= new ImageView(Context);
            imageView.setLayoutParams(new GridView.LayoutParams(205,205));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(5,5,5,5);
        }else{
            imageView=(ImageView)convertView;
        }
        //뷰 객체 재활용; 여기서는 필요 없는 코드
        //imageView.setImageResource(R.drawable.back);
        CardInfo card= (CardInfo) getItem(position);
        if(card.getCardOpened()){
            imageView.setImageResource(card.frontImage);
        }else{
            imageView.setImageResource(R.drawable.back);
        }
        Log.d("fragmentAdapter", position+"view created");
        return imageView;
    }
}
