package com.eficaz_fitbet_android.fitbet.presenter;

import com.eficaz_fitbet_android.fitbet.interfaces.DashBoardInterFace;
import com.eficaz_fitbet_android.fitbet.model.DashBoardModel;
import com.eficaz_fitbet_android.fitbet.ui.DashBoardActivity;

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
