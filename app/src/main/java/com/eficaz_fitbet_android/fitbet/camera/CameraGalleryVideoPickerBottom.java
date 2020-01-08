package com.eficaz_fitbet_android.fitbet.camera;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.model.CommonUsage;
import com.eficaz_fitbet_android.fitbet.utils.CACHEDKEY;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.ButterKnife;


public class CameraGalleryVideoPickerBottom extends BottomSheetDialogFragment implements View.OnClickListener {


    CommonUsage commonUsage;

    public static CameraGalleryVideoPickerBottom newInstance() {
        return new CameraGalleryVideoPickerBottom();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        commonUsage = (CommonUsage) context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.customTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog().getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_gallery_video_selection_bottom, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.bt_camera).setOnClickListener(this);
        view.findViewById(R.id.bt_video).setOnClickListener(this);
        view.findViewById(R.id.bt_cancel).setOnClickListener(this);
        //view.findViewById(R.id.bt_gallery).setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_camera:
                dismiss();
                commonUsage.onSubmit(null, CACHEDKEY.CAMERA.ordinal());
                break;
            case R.id.bt_video:
                dismiss();
                commonUsage.onSubmit(null, CACHEDKEY.ACTION_VIDEO_CAPTURE.ordinal());
                break;
           /* case R.id.bt_gallery:
                dismiss();
                commonUsage.onSubmit(null, CACHEDKEY.GALLERY.ordinal());
                break;*/
            case R.id.bt_cancel:
                dismiss();
                commonUsage.onSubmit(null, CACHEDKEY.CANCEL.ordinal());
                break;
        }

    }


}

