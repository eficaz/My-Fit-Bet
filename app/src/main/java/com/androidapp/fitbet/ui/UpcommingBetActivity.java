package com.androidapp.fitbet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.model.MyBets;
import com.androidapp.fitbet.utils.SLApplication;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UpcommingBetActivity extends BaseActivity {



    @Bind(R.id.searchview)
    EditText searchView;

    @Bind(R.id.group_list)
    RecyclerView group_list;

    ArrayList<MyBets> groupDetails;

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
        setContentView(R.layout.activity_upcomming_bet);
        ButterKnife.bind(this);



    }
}
