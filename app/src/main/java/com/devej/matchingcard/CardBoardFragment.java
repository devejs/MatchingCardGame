package com.devej.matchingcard;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.os.Handler;

import java.util.Random;



public class CardBoardFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Context attActivity;
    GridView gridview;
    GridBoardAdapter adapter;
    int[] cardImage;
    Boolean[] cardState;

    private final int cardPoint = 10;
    private final int addedStage = 1;

    Boolean stageClear = false;
    int stageNo, cardNo, colNum, stagePoint;
    int selectedCard;
    int tempSelPos;
    ImageView tempView;

    //맨 처음에 데이터 생성-> 그리드뷰 inflate
    //그리드뷰 inflate하자마자 전부 다 back으로 이미지 덮어야 함
    //클릭할 때마다 원래 이미지 보여주기

    //스테이지 클리어시 그리드뷰 포맷-> 데이터 재생성-> 그리드뷰 inflate(반복)
    //이건 activity 및 fragment 수명주기 공부 필요

    //프래그먼트 생명주기 순서
    @Override
    public void onAttach(Context context) {
        //프래그먼트가 액티비티와 연결될 때 호출
        //액티비티를 위해 설정해야 하는 정보들
        super.onAttach(context);
        Log.d("Fragment", "onAttach");
        attActivity = getActivity();
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            //액티비티 객체 참조-> 메서드 호출 가능
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //프래그먼트에 필요한 요소 초기화
        //액티비티 또한 생성중 -> 불안정
        //savedInstanceState: 프래그먼트가 재생성되기 이전의 상태를 저장하고 있는 변수
        //이 값을 참조해 이전 내용을 복구
        super.onCreate(savedInstanceState);
        Log.d("Fragment", "onCreate");
    }//사용할 예정 없음; 로그 확인용


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //프래그먼트에 쓰일 view들을 정의하고 초기화
        //프래그먼트는 자신의 레이아웃을 루트 뷰로 설정하고 이를 inflate
        //container 를 통해 프래그먼트가 액티비티의 어느 위치에 자리 잡아야 될지를 전달
        //프래그먼트상에 생성된 뷰들을 종속 된 액티비티의 뷰(container) 에 리턴해 줌으로써 화면에 표시

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_card_board,
                container, false);
        gridview = (GridView) rootView.findViewById(R.id.gridboard);
        Log.d("Fragment", "OnCreateView");
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //프래그먼트 구성요소 초기화
        //액티비티 완전히 생성된 이후-> 안정적
        super.onActivityCreated(savedInstanceState);
        selectedCard = 0;
        stageNo = ((PlayActivity) attActivity).playingstage; //stageNo 받아오기
        calCardNo(stageNo); //cardNo, colNum 초기화됨
        cardState = new Boolean[cardNo];
        for (int i = 0; i < cardNo; i++) {
            cardState[i] = false;
            //i번째 카드 오픈 안된 상태-> 클리어 체크
        }
        cardImage = CreateRandCard(cardNo, colNum);
        stagePoint = cardNo * 15;
        adapter=new GridBoardAdapter(attActivity, cardImage);
        for(int i=0; i<cardNo; i++){
            adapter.addItem(cardImage[i]);
        }
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(gridBoardOnItemClickListener);

    }

    @Override
    public void onResume() {
        //사용자와 상호작용
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        //액티비티와 연결을 끊기 바로 전에 호출
    }

    private GridView.OnItemClickListener gridBoardOnItemClickListener
            = new GridView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            ImageView curView = (ImageView) view;
            Log.d("Fragment", "Card Selected");
            Log.d("fragment", cardState + "");
//            if(curView.getDrawable()==R.drawable.back){
            switch (selectedCard) {
                case 0:
                    curView.setImageResource(cardImage[position]);
                    Log.d("fragment", position + " ");
                    tempSelPos = position;
                    tempView = curView;
                    selectedCard = 1;
                    break;
                case 1:
                    curView.setImageResource(cardImage[position]);
                    Log.d("fragment", position + " " + tempSelPos);
                    if (cardImage[tempSelPos] == cardImage[position]) {
                        //이미지 같을 경우
                        curView.setClickable(false);
                        cardState[position] = true;
                        cardState[tempSelPos] = true;
                        Log.d("fragment", cardState[position] + " after matching" + cardState[tempSelPos]);
                        selectedCard = 0;
                        passAddedScore(cardPoint);
                        if (checkStage(cardState)) {
                            Log.d("fragment", cardState[position] + " while thread");
                            passAddedScore(stagePoint);
                            passAddedStage(addedStage);
                            Log.d("fragment", "Next Stage!");
                            initData();
                        } else {
                            for (int i = 0; i < cardNo; i++) {
                                Log.d("fragment", "" + cardState[i]);
                            }
                        }
                    } else {
                        //이미지 다를 경우
                        curView.setImageResource(R.drawable.back);
                        tempView.setImageResource(R.drawable.back);
                        Log.d("fragment", cardState[position] + " after unmatching" + cardState[tempSelPos]);
                        //보니까 unmatching 하고 나서 getView 호출되던데 이렇게 되면 뭐가 달라진게 있나
                        for (int i = 0; i < cardNo; i++) {
                            Log.d("fragment", "" + cardState[i]);
                        }
                        selectedCard = 0;
                    }
                    break;
                default:
                    Log.d("ERROR", "뭔가 문제가 있어");
                    break;
            }
        }
//        }
    };

    public void passAddedScore(int addedScore) {
        if (mListener != null) {
            mListener.onReceivedAddedScore(addedScore);
        }
    }

    public void passAddedStage(int addedStage) {
        if (mListener != null) {
            mListener.onReceivedAddedStage(addedStage);
        }
    }

    public interface OnFragmentInteractionListener {
        void onReceivedAddedScore(int addedScore);

        void onReceivedAddedStage(int addedStage);
        //메서드 바꾸면 됨
        //액티비티쪽으로 데이터 전달; 액티비티에 메서드 정의한 후 호출
        //액티비티마다 메서드 이름이 다르면 해당 액티비티 확인 필요 -> 인터페이스 정의
        //액티비티 접근-> getActivity
        //외부 액티비티, 프래그먼트 접근-> 이벤트 콜백
    }

    public Boolean checkStage(final Boolean[] cardState) {
        for (int i = 0; i < cardNo; i++) {
            if (!cardState[i]) {
                //cardState[i]값이 false면 자동으로 break; for문 탈출
                break;
            } else {
                //cardState[i]값이 true면 계속 체크-> 마지막 열까지 확인
                Log.d("fragment", "checking array thread");
                if (i == (cardNo - 1)) {
                    stageClear = true;
                }
            }
        }
        Log.d("fragment", stageClear + "");
        return stageClear;
    }

    public void initData() {
        selectedCard = 0;
        stageClear = false;
        stageNo = ((PlayActivity) attActivity).playingstage;
        Log.d("FragmentInit", "스테이지" + stageNo);
        calCardNo(stageNo); //cardNo, colNum 초기화됨
        Log.d("FragmentInit", cardNo + "->" + colNum);
        cardState = new Boolean[cardNo];
        for (int i = 0; i < cardNo; i++) {
            cardState[i] = false;
            //i번째 카드 오픈 안된 상태-> 클리어 체크
        }
        Log.d("FragmentInit", "불린 길이" + cardState.length);
        cardImage = CreateRandCard(cardNo, colNum);
        Log.d("FragmentInit", "카드 배열 길이" + cardImage.length);
        stagePoint = cardNo * 15;
        adapter=new GridBoardAdapter(attActivity, cardImage);
        for(int i=0; i<cardNo; i++){
            adapter.addItem(cardImage[i]);
        }
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(gridBoardOnItemClickListener);
        Log.d("FragmentInit", "어댑터 데이터 적용");
    }

    public int[] CreateRandCard(int cardNo, int colNum) {
        gridview.setNumColumns(colNum);
        CardData card = new CardData(cardNo);
        return card.makeData();
    }

    public void calCardNo(int stageNo) {
        switch (stageNo) {
            case 1:
                cardNo = 4;
                colNum = 2;
                break;
            case 2:
                cardNo = 8;
                colNum = 4;
                break;
            case 3:
                cardNo = 16;
                colNum = 4;
                break;
            default:
                cardNo = 32;
                colNum = 8;
                break;
        }
    }

    public String HintFunc() {
        // 랜덤으로 수를 뽑아서 해당 수 인덱스의 cardState가 false인지 확인
        // true면 다시 랜덤->반복
        // 해당 카드 이미지와 같은 이미지인 카드들 찾고 cardState false인지 확인
        // 확인하면 바로 break;
        int pos1, pos2=1002;
        //pos2 임의로 초기화; 로그에 제대로 뜨나 확인 위함
        Random rand = new Random();

        while (true) {
            pos1 = rand.nextInt(cardNo);
            if (cardState[pos1]) {
                continue;
            } else {
                for(int j=0; j<cardNo; j++){
                    // 1. cardState에서 false인 것을 찾아서 이미지를 비교
                    // 2. cardImage에서 이미지가 같은 것을 찾아서 false인지 확인
                    // 보통 힌트를 쓰는 상황은 false인게 많을 때니까 2번이 더 나을 듯
                    if((cardImage[pos1]==cardImage[j]) && (pos1!=j)){
                        if(cardState[j]){
                            continue;
                        }else{
                            pos2=j;
                            break;
                        }
                    }
                }
                Log.d("fragment", "힌트 카드"+pos1+" "+pos2);
                Log.d("fragment", "cardState 확인"+cardState[pos1]+cardState[pos2]);
                break;
            }
        }
        //로그로 배열을 찍어보면 배열 값이 아니라 주소?가 나왔던거 같음 이거 포인터 관련해서 공부하기
        return "Select "+findCardPos(pos1+1)+"and "+findCardPos(pos2+1);
    }

    public String findCardPos(int pos){
        //pos는 index+1;
        int row, col;
        if(pos<=colNum){
            row=1;
            col=pos;
        }else if(pos<=colNum*2){
            row=2;
            col=pos-colNum;
        }else if(pos<=colNum*3){
            row=3;
            col=pos-colNum*2;
        }else{
            row=4;
            col=pos-colNum*3;
        }
        return "("+row+","+col+")";
    }

}
