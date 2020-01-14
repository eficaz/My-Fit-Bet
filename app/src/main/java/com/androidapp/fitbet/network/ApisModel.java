package com.androidapp.fitbet.network;

public class ApisModel {
    private static ApisModel instance;
    public String BASE_URL = "";


    public String PLACE_BASE_URL = "";
    public String MAP_BASE_URL = "";

    public static final Boolean isProduction = true;
    public static final Boolean isTest = true;

    public ApisModel() {
        init();
    }

    public static ApisModel getInstance() {
        if (null == instance)
            instance = new ApisModel();
        return instance;
    }

    public void clear() {
        instance = null;
        getInstance();
    }

    public void setProductionServerPreference() {
        instance = null;
        getInstance();
    }

    public ApisModel init() {
       /* if (BuildConfig.isProduction==true){
            setProductionServer();
        } else{
            setLocalServer();
        }*/
        setProductionServer();
        setCommonUrl();
        return this;
    }

    private void setLocalServer() {
        BASE_URL = "https://eficaztechsol.com/fitbet/api/";
        MAP_BASE_URL="https://maps.googleapis.com/maps/api/directions/";
        PLACE_BASE_URL="https://maps.googleapis.com/maps/api/place/details/";
    }
    private void setProductionServer() {
        BASE_URL = "https://fitbet.com.au/api/";//Test Estel Server OLD
        MAP_BASE_URL="https://maps.googleapis.com/maps/api/directions/";
        PLACE_BASE_URL="https://maps.googleapis.com/maps/api/place/details/";
    }
    private void setTestServer() {
        BASE_URL = "";//Test Estel Server OLD
        MAP_BASE_URL="https://maps.googleapis.com/maps/api/directions/";
        PLACE_BASE_URL="https://maps.googleapis.com/maps/api/place/details/";
    }
    void setCommonUrl() {
    }
}
