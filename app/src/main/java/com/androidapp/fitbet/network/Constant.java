package com.androidapp.fitbet.network;

public interface Constant {
    String BASE_APP_IMAGE__PATH = ApisModel.getInstance().BASE_URL + "uploadimages/profilepic/";

    String BASE_APP_WINNER_IMAGE__PATH = ApisModel.getInstance().BASE_URL + "uploadvideo/winner/";

    String BASE_APP_GROUP_IMAGE__PATH = ApisModel.getInstance().BASE_URL + "uploadimages/grouppic/";

    String BASE_APP_URL = ApisModel.getInstance().BASE_URL;

    String DRAW_MAP_BASE_URL = ApisModel.getInstance().MAP_BASE_URL;

    String PLACE_BASE_URL = ApisModel.getInstance().PLACE_BASE_URL;
}
