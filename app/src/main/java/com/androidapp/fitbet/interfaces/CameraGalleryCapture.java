package com.androidapp.fitbet.interfaces;

import java.io.File;

public interface CameraGalleryCapture {

    <T> void requestFailure(int requestCode, T data);

    <T> File requestSuccess(File imageFile);
}
