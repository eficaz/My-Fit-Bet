package com.eficaz_fitbet_android.fitbet.interfaces;

import java.io.File;

public interface CameraGalaryCaputer {

    <T> void requestFailure(int requestCode, T data);
    <T> File requestSuccess(File imageFile);
}
