package com.androidapp.fitbet.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import com.androidapp.fitbet.R;

public class RulesDialog {

    private Dialog dialog;

    private ImageView imgClose;

    public RulesDialog(Context context) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.rules_layout);

        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        dialog.setCancelable(true);
        imgClose = dialog.findViewById(R.id.img_close);
        dialog.show();

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }
}
