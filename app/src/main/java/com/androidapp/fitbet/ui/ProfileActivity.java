package com.androidapp.fitbet.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableRow;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.ui.fragments.UserProfileDashBoardFragment;
import com.androidapp.fitbet.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends BaseActivity {

    @Bind(R.id.content_main)
    FrameLayout content_main;

    @Bind(R.id.back_btn)
    TableRow back_btn;

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
