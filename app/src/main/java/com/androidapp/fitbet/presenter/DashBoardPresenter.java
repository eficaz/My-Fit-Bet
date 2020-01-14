package com.androidapp.fitbet.presenter;

import com.androidapp.fitbet.interfaces.DashBoardInterFace;
import com.androidapp.fitbet.model.DashBoardModel;
import com.androidapp.fitbet.ui.DashBoardActivity;

public class DashBoardPresenter implements DashBoardInterFace {
    DashBoardModel dashBoardModel;


    public DashBoardPresenter(DashBoardActivity dashBoardActivity) {

    }

    @Override
    public void BetOnClick(int posction) {
        if(posction==1){
         dashBoardModel.BetOnClick(posction);
        }

    }
}
