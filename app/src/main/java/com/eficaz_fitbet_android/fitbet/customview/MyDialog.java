package com.eficaz_fitbet_android.fitbet.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.eficaz_fitbet_android.fitbet.R;


public class MyDialog {
    private Dialog dialog;
    private TextView dialogButtonOk ,dialogButtonNo;
    private TextView title_lbl, subtitle_lbl;
    private View separator;
    private MyDialogClickListener myDialogClickListener;
    private boolean negativeExist;
    private static final String LOG_ERROR = "MyDialog_ERROR";
    private String flag;


    public MyDialog(Context context, MyDialogClickListener myDialogClickListener,String title, String subtitle,String positive,String negative,boolean cancelable,String flag) {
   this.myDialogClickListener=myDialogClickListener;
   this.flag=flag;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_two_buttons);
        if(dialog.getWindow()!=null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initViews();

        dialog.setCancelable(cancelable);

        initEvents();

        if(negative.equals(""))
            negativeExist=false;
        setTitle(title);
        setSubtitle(subtitle);
        setNegativeLabel(negative);
        setPositiveLabel(positive);

    }


    public void show(){
        if(!negativeExist){
            dialogButtonNo.setVisibility(View.GONE);
            separator.setVisibility(View.GONE);
        }
        dialog.show();
    }
    public boolean isShowing(){
        if(dialog!=null)
        return dialog.isShowing();
        else return false;
    }

    public void dismiss(){
        dialog.dismiss();
    }
    public void setTitle(String title){
        title_lbl.setText(title);
    }
    public void setSubtitle(String subtitle){
        subtitle_lbl.setText(subtitle);
    }
    private void setPositiveLabel(String positive){
        dialogButtonOk.setText(positive);
    }
    private void setNegativeLabel(String negative){
        dialogButtonNo.setText(negative);
    }
    private void setBoldPositiveLabel(boolean bold){
        if(bold)
            dialogButtonOk.setTypeface(null, Typeface.BOLD);
        else
            dialogButtonOk.setTypeface(null, Typeface.NORMAL);
    }
    private void setTypefaces(Typeface appleFont){
        if(appleFont!=null) {
            title_lbl.setTypeface(appleFont);
            subtitle_lbl.setTypeface(appleFont);
            dialogButtonOk.setTypeface(appleFont);
            dialogButtonNo.setTypeface(appleFont);
        }
    }


    private void initViews() {
        title_lbl = dialog.findViewById(R.id.title);
        subtitle_lbl =  dialog.findViewById(R.id.subtitle);
        dialogButtonOk =  dialog.findViewById(R.id.dialogButtonOK);
        dialogButtonNo =  dialog.findViewById(R.id.dialogButtonNO);
        separator =  dialog.findViewById(R.id.separator);
    }

    private void initEvents(){
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myDialogClickListener != null) {
                    myDialogClickListener.onClick(MyDialog.this,1,flag);
                }dismiss();

            }

        });
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myDialogClickListener != null) {
                    myDialogClickListener.onClick(MyDialog.this,0,flag);
                } dismiss();
            }
        });
    }

    public interface MyDialogClickListener{
        void onClick(MyDialog dialog,int type,String flag);
    }
}