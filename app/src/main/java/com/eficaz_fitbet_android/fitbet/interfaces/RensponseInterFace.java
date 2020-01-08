package com.eficaz_fitbet_android.fitbet.interfaces;

public interface RensponseInterFace {
    <T> void requestSuccess(int requestCode, T data);

    <T> void requestFailure(int requestCode, T data);

    void BetOnClick(int posction);
}
