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

package com.example.nutapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutapp.adapter.ShareDeviceDetailsAdapter;
import com.example.nutapp.pojo.ShareDeviceData;
import com.example.nutapp.util.JioConstant;

import java.util.ArrayList;
import java.util.List;

public class UserDetailNavigationActivity extends Activity implements View.OnClickListener {

    List<ShareDeviceData> mList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_userdetail);
        CardView userDetail = findViewById(R.id.cardUserdetail);
        userDetail.setOnClickListener(this);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(JioConstant.USER_DETAIL);
        title.setTypeface(JioUtils.mTypeface(this,5));
        TextView shareDeviceText = findViewById(R.id.shareDevice);
        shareDeviceText.setTypeface(JioUtils.mTypeface(this,3));
        mList = new ArrayList<>();
        addDeviceData();
        RecyclerView deviceList = findViewById(R.id.shareDeviceList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        deviceList.setLayoutManager(linearLayoutManager);
        ShareDeviceDetailsAdapter adapter = new ShareDeviceDetailsAdapter(mList);
        deviceList.setAdapter(adapter);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        Button homeBtn = findViewById(R.id.home);
        homeBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);

    }

    public void addDeviceData()
    {
        for(int i=0;i<5;i++){
            ShareDeviceData data = new ShareDeviceData();
            data.setDeviceName("Name of the Tag");
            data.setDeviceShareDetail("Shared with :10 people");
            data.setDeviceShareDate("Shared on DD-MM-YY");
            mList.add(data);
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.cardUserdetail:
                Intent intent = new Intent(this,UserProfileUpdateActivity.class);
                startActivityForResult(intent,JioUtils.HOME_KEY);
                break;
            case R.id.back:
                Intent intentCancel = new Intent();
                setResult(RESULT_CANCELED, intentCancel);
                finish();
                break;
            case R.id.home:
                Intent intentHome = new Intent();
                setResult(JioUtils.HOME_KEY, intentHome);
                finish();
                break;
            default:
                break;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(this, "PHONE AND DEVICE ALERT RESULT " + requestCode + "::"+resultCode, Toast.LENGTH_SHORT).show();
        if (resultCode == JioUtils.HOME_KEY) {
            Log.d("FEEDBACK","HOME KEY FROM FEEDBACK");
            finish();
        }
    }

}
