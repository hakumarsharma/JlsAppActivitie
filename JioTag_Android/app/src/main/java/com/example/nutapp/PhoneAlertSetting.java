package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PhoneAlertSetting extends AppCompatActivity {

    RecyclerView m_recyclerView;
    PhoneAdapter m_phoneAlertAdapter;
    List<SettingsDetails> m_bleDetails = new ArrayList<SettingsDetails>();
    public static String HEXADDRRESS;


    public String getHexAddress(){
        return HEXADDRRESS;
    }

    public void addPhoneAlertItems() {
        SettingsDetails mainAlert = new SettingsDetails(getResources().getString(R.string.phonealert_main_header),"");
        SettingsDetails duration = new SettingsDetails(getResources().getString(R.string.phonealert_header_duration), getResources().getString(R.string.phonealert_header_duration_details));
        SettingsDetails repeat = new SettingsDetails(getResources().getString(R.string.phonealert_header_repeat), getResources().getString(R.string.phonealert_header_repeat_details));
        m_bleDetails.add(mainAlert);
        m_bleDetails.add(duration);
        m_bleDetails.add(repeat);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("ONCREATE","PHONEALERT");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_alert_recycle);
        Intent receiveIntent = this.getIntent();
        HEXADDRRESS = receiveIntent.getStringExtra("HEXADDR");

        m_recyclerView = (RecyclerView) findViewById(R.id.phonealert_recycle);
        addPhoneAlertItems();
        m_phoneAlertAdapter = new PhoneAdapter(m_bleDetails, this, m_recyclerView);
        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        m_recyclerView.setAdapter(m_phoneAlertAdapter);

        ImageButton attach_back = (ImageButton) findViewById(R.id.phonealert_back);
        attach_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        ImageButton device_home = (ImageButton) findViewById(R.id.phonealert_home);
        device_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(JioUtils.HOME_KEY, intent);
                finish();
            }
        });
    }
}
