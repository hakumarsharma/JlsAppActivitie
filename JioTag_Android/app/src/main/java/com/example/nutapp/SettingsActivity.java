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
        ImageButton attachBack = (ImageButton) findViewById(R.id.settings_back);
        attachBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        ImageButton deviceHome = (ImageButton) findViewById(R.id.settings_home);
        deviceHome.setOnClickListener(new View.OnClickListener() {
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
        SettingsDetails disconnectionAlert= new SettingsDetails(getResources().getString(R.string.settings_header_disconnect),getResources().getString(R.string.settings_disconnect_details));
        SettingsDetails informationAct= new SettingsDetails(getResources().getString(R.string.information_header),"");
        SettingsDetails howToadd= new SettingsDetails(getResources().getString(R.string.how_header),"");
        SettingsDetails feedbackAct= new SettingsDetails(getResources().getString(R.string.feedback_header),"");
        m_bleDetails.add(photo);
        //m_bleDetails.add(SOS);
        m_bleDetails.add(disconnectionAlert);
        m_bleDetails.add(informationAct);
        m_bleDetails.add(howToadd);
        m_bleDetails.add(feedbackAct);
    }
}
