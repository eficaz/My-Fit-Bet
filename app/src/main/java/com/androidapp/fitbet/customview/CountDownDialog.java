package com.androidapp.fitbet.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.androidapp.fitbet.R;

public class CountDownDialog {

    private Dialog dialog;

private int counter;
   private  TextView txtCountDown;

    public CountDownDialog(Context context,CountDownStopListener countDownStopListener,int count) {
this.counter=count;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.count_down_layout);

        if(dialog.getWindow()!=null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        dialog.setCancelable(false);
        txtCountDown=dialog.findViewById(R.id.txt_count_down);
        if(CustomProgress.getInstance().isShowing())
            CustomProgress.getInstance().hideProgress();
        dialog.show();

        new CountDownTimer(counter*1000, 1000){
            public void onTick(long millisUntilFinished){

                txtCountDown.setText(String.valueOf(counter));
                counter--;
            }
            public  void onFinish(){

                txtCountDown.setText("0");
                countDownStopListener.onCountDownStopped(dialog);


            }
        }.start();

    }

    public interface CountDownStopListener{

         void onCountDownStopped(Dialog dialog);
    }
}
