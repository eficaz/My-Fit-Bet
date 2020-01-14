package com.androidapp.fitbet.interfaces;

import java.io.File;

public interface CameraGalaryCaputer {

    <T> void requestFailure(int requestCode, T data);
    <T> File requestSuccess(File imageFile);
}
