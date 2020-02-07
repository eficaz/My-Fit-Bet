package com.androidapp.fitbet.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.utils.SLApplication;
import com.juanpabloprado.countrypicker.CountryPicker;
import com.juanpabloprado.countrypicker.CountryPickerListener;

import static com.androidapp.fitbet.utils.Contents.COUNTRY_NAME;

public class CountrySelectionActivity extends BaseActivity {


    private IntentFilter filter=new IntentFilter("count_down");
    private boolean firstConnect=true;
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null) {
                if (firstConnect) {
                    firstConnect = false;

                    String message = intent.getStringExtra("message");
                    onMessageReceived(message);

                }
            }else{
                firstConnect=true;
            }

        }
    };
    @Override
    public void onMessageReceived(String message) {

        SLApplication.isCountDownRunning=true;
        startActivity(new Intent(this,DashBoardActivity.class));
        finish();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_selection);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,
                        CountryPicker.getInstance(new CountryPickerListener() {
                            @Override public void onSelectCountry(String name, String code) {
                                Intent intent=new Intent();
                                intent.putExtra(COUNTRY_NAME,name);
                                setResult(123,intent);
                                finish();//finishing activity
                                //Toast.makeText(CountrySelectionActivity.this, "Name: " + name, Toast.LENGTH_SHORT).show();
                            }
                        }))
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }
}
