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

public class SettingsActivity extends AppCompatActivity {
    RecyclerView m_recyclerView;
    SettingsAdapter m_bleDetailsAdapter;
    List<SettingsDetails> m_bleDetails = new ArrayList<SettingsDetails>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(this, "PHONE AND DEVICE ALERT RESULT " + requestCode + "::"+resultCode, Toast.LENGTH_SHORT).show();
        if (resultCode == JioUtils.HOME_KEY) {
            Log.d("FEEDBACK","HOME KEY FROM FEEDBACK");
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_recycle_view);
        m_recyclerView = (RecyclerView) findViewById(R.id.settings_recycle);
        addSettingsItems();
        m_bleDetailsAdapter = new SettingsAdapter(m_bleDetails, this, m_recyclerView);
        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        m_recyclerView.setAdapter(m_bleDetailsAdapter);
        ImageButton attach_back = (ImageButton) findViewById(R.id.settings_back);
        attach_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        ImageButton device_home = (ImageButton) findViewById(R.id.settings_home);
        device_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }
    public void addSettingsItems(){
        SettingsDetails photo= new SettingsDetails(getResources().getString(R.string.settings_header_photocapture),getResources().getString(R.string.settings_photocapture_details));
        //SettingsDetails SOS= new SettingsDetails(getResources().getString(R.string.settings_header_sos),getResources().getString(R.string.settings_sos_details));
        SettingsDetails disconnection_alert= new SettingsDetails(getResources().getString(R.string.settings_header_disconnect),getResources().getString(R.string.settings_disconnect_details));
        SettingsDetails information_act= new SettingsDetails(getResources().getString(R.string.information_header),"");
        SettingsDetails how_toadd= new SettingsDetails(getResources().getString(R.string.how_header),"");
        SettingsDetails feedback_act= new SettingsDetails(getResources().getString(R.string.feedback_header),"");
        m_bleDetails.add(photo);
        //m_bleDetails.add(SOS);
        m_bleDetails.add(disconnection_alert);
        m_bleDetails.add(information_act);
        m_bleDetails.add(how_toadd);
        m_bleDetails.add(feedback_act);
    }
}
