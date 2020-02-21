package com.androidapp.fitbet.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.utils.AppPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.REG_KEY;


public class BetUpdateService extends JobIntentService {
    public static final String TAG = "BetUpdateService";
    private AppPreference appPreference;
    private double positionLatitude = 0.0, positionLongitude = 0.0;

    static void enqueueWork(Context context, Intent work) {

        enqueueWork(context, BetUpdateService.class, 123, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appPreference = AppPreference.getPrefsHelper(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "onHandleWork");
        positionLatitude = intent.getDoubleExtra("positionLatitude", 0.0);
        positionLongitude = intent.getDoubleExtra("positionLongitude", 0.0);
        sendUpdates(positionLatitude, positionLongitude);

    }

    @Override
    public boolean onStopCurrentWork() {
        Log.d(TAG, "onStopCurrentWork: ");
        return super.onStopCurrentWork();
    }

    private void sendUpdates(double positionLatitude, double positionLongitude) {

        //  System.out.println("update params = Challenger "+appPreference.getSavedChallengerId()+"distance "+appPreference.getSavedDistance()+"lat "+positionLongitude+"lon "+positionLatitude+"regkey "+ appPreference.getPref(REG_KEY,"")+"bettype "+appPreference.getSavedBetType()+"route "+ appPreference.getSavedUserRoute());

        Call call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LiveDetailsUpdation(appPreference.getSavedChallengerId(), appPreference.getSavedDistance(), positionLongitude, positionLatitude, appPreference.getPref(REG_KEY, ""), appPreference.getSavedBetType(), appPreference.getSavedUserRoute());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                System.out.println(TAG + " Update response code =" + response.code());

                if (CustomProgress.getInstance().isShowing())
                    CustomProgress.getInstance().hideProgress();
                try {
                    /*run=false;*/
                    final String bodyString;
                    if (response.body() != null) {
                        bodyString = new String(response.body().bytes(), "UTF-8");

                        System.out.println(TAG + " BET updation ===" + bodyString);
                        JSONObject jsonObject;

                        jsonObject = new JSONObject(bodyString);
                        String data = jsonObject.getString("Status");
                        if (data.equals("Ok")) {

                            Intent intent = new Intent();
                            intent.putExtra("update_response", bodyString);
                            intent.setAction("bet_update");
                            LocalBroadcastManager.getInstance(BetUpdateService.this).sendBroadcast(intent);

                        }
                    } else {
                        System.out.println(TAG + " Null response");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (CustomProgress.getInstance().isShowing())
                    CustomProgress.getInstance().hideProgress();
            }
        });

    }

}
