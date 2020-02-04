package com.devej.matchingcard;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter
        <RecyclerViewAdapter.ViewHolder>{
    ArrayList<Rank> rankitems= new ArrayList<Rank>();


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //뷰홀더 객체 만들어질 때
        //아이템을 위해 정의한 xml 레이아웃-> 뷰 객체 생성
        //뷰 객체는 새로 만든 뷰 홀더 객체에 담아 반환
        //뷰 타입 파라미터-> 다른 xml 파일 보여줄 수 있음
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View itemView= inflater.inflate(R.layout.ranklist_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //뷰홀더 객체 재사용될 때
        //뷰 객체는 그대로 사용하고 데이터만 바꿔줌
        Rank rankitem= rankitems.get(position);
        holder.setItem(rankitem);
    }

    @Override
    public int getItemCount() {
        //어댑터에서 관리하는 아이템의 개수 반환
        return rankitems.size();
    }

    public void addItem(Rank rankitem){
        rankitems.add(rankitem);
        Log.d("adapter", rankitem.getName()+rankitem.getScore());
    }

    public void setItems(ArrayList<Rank> rankitems){
        this.rankitems= rankitems;
    }

    public Rank getItem(int position){
        return rankitems.get(position);
    }

    public void setItem(int position, Rank rankitem){
        rankitems.set(position, rankitem);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView noText, nameText, scoreText;

        public ViewHolder(View itemView){
            //뷰 객체가 전달됨
            super(itemView);

            noText= itemView.findViewById(R.id.rankno);
            nameText= itemView.findViewById(R.id.rankname);
            scoreText= itemView.findViewById(R.id.rankscore);
            //setItem 메서드에서 참조 가능
        }

        public void setItem(Rank rankitem){
            // 뷰홀더에 든 뷰 객체의 데이터를 다른 것으로 보이도록 함
            //원래는 class파일에서 getName 식으로 가져오는데 SQLite니까 잠깐 냅둠
            noText.setText(rankitem.getRank()+"");
            nameText.setText(rankitem.getName());
            scoreText.setText(rankitem.getScore()+"");
            Log.d("adapter", "set to textview");
        }
    }
}
