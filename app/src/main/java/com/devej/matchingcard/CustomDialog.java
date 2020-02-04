package com.devej.matchingcard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

public abstract class CustomDialog implements DialogInterface {

    Context context;
    int layoutId;
    int btn1Id, btn2Id, btn3Id;
    Button btn1, btn2, btn3;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    public CustomDialog(Context Context, int LayoutId, final int Btn1Id, final int Btn2Id) {
        this.context=Context;
        this.layoutId=LayoutId;
        this.btn1Id=Btn1Id;
        this.btn2Id=Btn2Id;
        this.btn3Id=-1;
    }
    public CustomDialog(Context Context, int LayoutId, int Btn1Id,
                        int Btn2Id, int Btn3Id) {
        this.context=Context;
        this.layoutId=LayoutId;
        this.btn1Id=Btn1Id;
        this.btn2Id=Btn2Id;
        this.btn3Id=Btn3Id;
    }

    public void createDialog(){
        AlertDialog.Builder alertDialogBuilder
                = new AlertDialog.Builder(context, R.style.CustomDialogTheme);

        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView= inflater.inflate(layoutId, null);

        alertDialogBuilder
                .setCancelable(false)
                .setView(dialogView);
//                .setPositiveButton(btnposId, new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        setCustomPos(context, btnposId);
//                    }
//                }).setNegativeButton(btnnegId, new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                setCustomNeg(context, btnnegId);
//            }
//        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        btn1= (Button) alertDialog.findViewById(btn1Id);
        btn2= (Button) alertDialog.findViewById(btn2Id);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCustombtn1(context, alertDialog, btn1);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setCustombtn2(context, alertDialog, btn2);
            }
        });
        if(btn3Id!=-1){
            btn3= (Button) alertDialog.findViewById(btn3Id);
            btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCustombtn3(context, alertDialog, btn3);
                }
            });
        }
    }
//    public void setBtnView(final int btn1Id, final int btn2Id){
//        btn1=(Button) ((Activity)context).findViewById(btn1Id);
//        btn2=(Button) ((Activity)context).findViewById(btn2Id);
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setCustombtn1(context, btn1);
//            }
//        });
//        btn2.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                setCustombtn2(context, btn2);
//            }
//        });
//    }

    abstract void setCustombtn1(Context Context, AlertDialog alertDialog, Button btn1);
    abstract void setCustombtn2(Context Context, AlertDialog alertDialog, Button btn2);
    abstract void setCustombtn3(Context Context, AlertDialog alertDialog, Button btn3);

    @Override
    public void cancel() {

    }

    @Override
    public void dismiss() {

    }
}
