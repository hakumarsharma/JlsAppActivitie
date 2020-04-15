package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Information extends AppCompatActivity {
    RecyclerView m_recyclerView;
    InfoAdapter m_bleDetailsAdapter;
    List<SettingsDetails> m_bleDetails = new ArrayList<SettingsDetails>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_recycle_view);
        m_recyclerView = (RecyclerView) findViewById(R.id.info_recycle);
        addSettingsItems();
        m_bleDetailsAdapter = new InfoAdapter(m_bleDetails, this, m_recyclerView);
        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        m_recyclerView.setAdapter(m_bleDetailsAdapter);
        ImageButton info_back = (ImageButton) findViewById(R.id.info_back);
        info_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        ImageButton info_home = (ImageButton) findViewById(R.id.info_home);
        info_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(JioUtils.HOME_KEY, intent);
                finish();
            }
        });
    }

    public void addSettingsItems() {
        SettingsDetails info_about = new SettingsDetails(getResources().getString(R.string.info_aboutjio_header), getResources().getString(R.string.info_aboutjio_details));
        SettingsDetails info_feature = new SettingsDetails(getResources().getString(R.string.info_features_header), "");
        SettingsDetails info_conn = new SettingsDetails(getResources().getString(R.string.info_connectionsetup_header), getResources().getString(R.string.info_connectionsetup_details));
        SettingsDetails info_photo = new SettingsDetails(getResources().getString(R.string.info_photoclick_header), getResources().getString(R.string.info_photoclick_details));
        m_bleDetails.add(info_about);
        m_bleDetails.add(info_feature);
        m_bleDetails.add(info_conn);
        m_bleDetails.add(info_photo);
    }

}
