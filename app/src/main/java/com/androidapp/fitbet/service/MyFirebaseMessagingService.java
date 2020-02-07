package com.androidapp.fitbet.service;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.ui.DashBoardActivity;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    private LocalBroadcastManager broadcaster;
    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());





        Log.d("msg", "onMessageReceived betid: " + remoteMessage.getData().get("bet_date"));
        System.out.println("Notification content "+remoteMessage.getData().toString());
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "com.eficaz_android.fitbet";
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.notification_icon);
            builder.setContentTitle(remoteMessage.getNotification().getTitle());
            builder.setSound(alarmSound);
            builder.setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);
            //builder.setColor(getResources().getColor(R.color.transparent));
        } else {
            builder.setSmallIcon(R.drawable.notification_icon);
            builder.setContentTitle(remoteMessage.getNotification().getTitle());
            builder.setSound(alarmSound);
            builder.setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
if(!TextUtils.isEmpty(remoteMessage.getData().get("betid"))&& AppPreference.getPrefsHelper().getPref(Contents.BET_START_STATUS,"").equals("false")) {

    AppPreference.getPrefsHelper().saveBetId(remoteMessage.getData().get("betid"));
    AppPreference.getPrefsHelper().saveBetDate(remoteMessage.getData().get("bet_date"));
    AppPreference.getPrefsHelper().saveChallengerId(remoteMessage.getData().get("challenger_id"));
    calculateCountDownTime(remoteMessage.getData().get("bet_date"));

    sendMessageToReceiver(remoteMessage.getNotification().getBody());

}

    }

    private void sendMessageToReceiver(String message) {
System.out.println("sendMessageToReceiver "+message);
Intent intent = new Intent();
intent.putExtra("message",message);
intent.setAction("count_down");
broadcaster.sendBroadcast(intent);

    }

    private  void calculateCountDownTime(String betDate){
        try {


            Calendar calendar = Calendar.getInstance();
            Date time = calendar.getTime();
            SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            outputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            String currentDateTime = outputFmt.format(time);

            Date currentDate=outputFmt.parse(currentDateTime);



        Date betStartDate=outputFmt.parse(betDate);

            long diffInMs = betStartDate.getTime() - currentDate.getTime();

            int count= (int) (diffInMs/1000);

            System.out.println("calculateCountDownTime "+count);

            AppPreference.getPrefsHelper().saveCountDownTime(count);



        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}