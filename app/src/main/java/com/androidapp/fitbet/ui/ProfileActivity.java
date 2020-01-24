package com.androidapp.fitbet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableRow;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.ui.fragments.UserProfileDashBoardFragment;
import com.androidapp.fitbet.utils.SLApplication;
import com.androidapp.fitbet.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends BaseActivity {

    @Bind(R.id.content_main1)
    FrameLayout content_main;

    @Bind(R.id.back_btn)
    TableRow back_btn;

    @Override
    protected void onMessageReceived(String message) {
        super.onMessageReceived(message);
        SLApplication.isCountDownRunning=true;
        startActivity(new Intent(this,DashBoardActivity.class));
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new UserProfileDashBoardFragment(), Utils.BetScreenName).commitAllowingStateLoss();
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
