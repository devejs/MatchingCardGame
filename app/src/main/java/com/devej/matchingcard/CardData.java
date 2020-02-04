package com.devej.matchingcard;

import java.util.Random;

public class CardData {

    private Integer[] cardImage = { R.drawable.python_fixed,
            R.drawable.c_fixed, R.drawable.cpp_fixed,
            R.drawable.java_fixed, R.drawable.javascript_fixed,
            R.drawable.perl_fixed, R.drawable.ruby_fixed, R.drawable.swift_fixed};

    public int[] randomData;
    int cardNo;
    Random rand=new Random();

    public CardData(int cardNo) {
        this.cardNo = cardNo;
        randomData= new int[cardNo];
    }

    public int pickImage(){
        int selectImage;
        selectImage= cardImage[rand.nextInt(cardImage.length)];
        return selectImage;
    }

    public int[] makeData(){
        int[] randomImage= new int[cardNo];
        //cardNo은 항상 짝수이므로 성립
        for(int i=0; i<(cardNo/2); i++){
            randomImage[i]=pickImage();
            randomImage[cardNo-1-i]= randomImage[i];
            //카드는 무조건 두개씩
        }
        return ShuffleData(randomImage);
    }

    public int[] ShuffleData(int[] randomImage){
        Boolean[] shuf_check= new Boolean[cardNo];
        int length=0, rd;
        for(int i=0; i<cardNo; i++){
            shuf_check[i]=true;
        }//스위치 전부 true로 초기화
        while(length<cardNo){
            rd= rand.nextInt(cardNo);
            if(shuf_check[rd]) {
                shuf_check[rd] = false;
                randomData[length] = randomImage[rd];
                length++;
            }//true면 false 만들고 false면 length++ 없이 다시 while문
            //모든 배열이 다 차면, 즉 length가 cardNo과 같아지면 그때 종료 리턴
        }
        return randomData;
    }
}
