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

    private RecyclerView deviceList;
    List<ShareDeviceData> mList;
    private ShareDeviceDetailsAdapter adapter;
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
        deviceList = findViewById(R.id.shareDeviceList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        deviceList.setLayoutManager(linearLayoutManager);
        adapter = new ShareDeviceDetailsAdapter(mList);
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
                startActivity(intent);
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
