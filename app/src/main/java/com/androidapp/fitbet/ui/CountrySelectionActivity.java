package com.androidapp.fitbet.ui;

import android.content.Intent;
import android.os.Bundle;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.utils.SLApplication;
import com.juanpabloprado.countrypicker.CountryPicker;
import com.juanpabloprado.countrypicker.CountryPickerListener;

import static com.androidapp.fitbet.utils.Contents.COUNTRY_NAME;

public class CountrySelectionActivity extends BaseActivity {
    @Override
    protected void onMessageReceived(String message) {
        super.onMessageReceived(message);
        SLApplication.isCountDownRunning=true;
        startActivity(new Intent(this,DashBoardActivity.class));
        finish();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_selection);
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
}
