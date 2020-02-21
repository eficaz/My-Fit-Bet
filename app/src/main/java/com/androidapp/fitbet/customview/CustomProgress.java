package com.androidapp.fitbet.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.androidapp.fitbet.R;

import lal.adhish.gifprogressbar.GifView;


public class CustomProgress {
    public static CustomProgress customProgress = null;
    private Dialog mDialog;
    private GifView mProgressBar;

    public static CustomProgress getInstance() {
        if (customProgress == null) {
            customProgress = new CustomProgress();
        }
        return customProgress;
    }

    public void showProgress(Context context, String message, boolean cancelable) {
        mDialog = new Dialog(context, android.R.style.Theme_Black);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.progress_bar_dialog);
        mDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        mProgressBar = (GifView) mDialog.findViewById(R.id.progress_bar);
        //mProgressBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);
        mProgressBar.setImageResource(R.drawable.prograss_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        //mProgressBar.setIndeterminate(true);
        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(cancelable);
        mDialog.show();
    }

    public void hideProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public boolean isShowing() {
        if (mDialog != null)
            return mDialog.isShowing();
        else return false;
    }
}