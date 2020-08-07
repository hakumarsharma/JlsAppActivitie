/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.
 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.
 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.view.geofence;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.GeofenceViewAdapter;

public class GeoFenceMapAndListViewActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mapTab;
    private TextView listTab;
    private ViewPager viewPager;
    private String deviceNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_geofence_area);
        Toolbar toolbar = findViewById(R.id.locationToolbar);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.GEOFENCE_AREA);
        title.setTypeface(Util.mTypeface(this, 5));
        ImageView createGeofence = findViewById(R.id.createGroup);
        createGeofence.setVisibility(View.VISIBLE);
        createGeofence.setOnClickListener(this);
        mapTab = findViewById(R.id.map_view);
        listTab = findViewById(R.id.list_view);
        listTab.setOnClickListener(this);
        mapTab.setOnClickListener(this);
        deviceNumber = getIntent().getStringExtra(Constant.DEVICE_NUMBER);
        mapTab.setTypeface(Util.mTypeface(this, 5));
        listTab.setTypeface(Util.mTypeface(this, 5));
        mapTab.setText(Constant.MAP_TAB + Html.fromHtml(getResources().getString(R.string.white_indicater)));
        setSupportActionBar(toolbar);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        initUI();
    }

    private void initUI() {
        viewPager = findViewById(R.id.geofence_view_pager);
        GeofenceViewAdapter locationAdapter = new GeofenceViewAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(locationAdapter);
        onTabClicked();
    }

    private void onTabClicked() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Todo
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    setMapTab();
                } else if (position == 1) {
                    setListTab();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Todo
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        } else if(v.getId() == R.id.map_view) {
            viewPager.setCurrentItem(0);
            setMapTab();
            return;
        } else if(v.getId() == R.id.list_view) {
            viewPager.setCurrentItem(1);
            setListTab();
            return;
        } else if(v.getId() == R.id.createGroup); {
            Intent intent = new Intent(this, CreateGeofenceActivity.class);
            intent.putExtra(Constant.DEVICE_NUMBER,deviceNumber);
            intent.putExtra(Constant.CREATE_GEOFENCE,true);
            startActivityForResult(intent,180);

        }
    }

    // Sets the Map tab data
    private void setMapTab() {
        mapTab.setText(Constant.MAP_TAB + Html.fromHtml(getResources().getString(R.string.white_indicater)));
        mapTab.setTextColor(Color.WHITE);
        listTab.setText(Constant.LIST_TAB_WITHOUT_NEXTLINE);
        listTab.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
    }

    // Sets the List tab data
    private void setListTab() {
        listTab.setText(Constant.LIST_TAB + Html.fromHtml(getResources().getString(R.string.white_indicater)));
        listTab.setTextColor(Color.WHITE);
        mapTab.setText(Constant.MAP_TAB_WITHOUT_NEXTLINE);
        mapTab.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 110 || requestCode == 180){
            finish();
        }
    }
}

