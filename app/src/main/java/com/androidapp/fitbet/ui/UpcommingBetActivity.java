package com.androidapp.fitbet.ui;

import android.os.Bundle;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.model.MyBets;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcomming_bet);
        ButterKnife.bind(this);



    }
}
