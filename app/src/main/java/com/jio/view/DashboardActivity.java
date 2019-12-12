// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.database.pojo.AddedDeviceData;
import com.jio.database.pojo.MultipleselectData;
import com.jio.database.pojo.TrackerdeviceData;
import com.jio.database.pojo.request.TrackdeviceRequest;
import com.jio.database.pojo.response.LogindetailResponse;
import com.jio.database.pojo.response.TrackerdeviceResponse;
import com.jio.network.MessageListener;
import com.jio.network.MessageReceiver;
import com.jio.network.RequestHandler;
import com.jio.util.Util;
import com.jio.view.adapter.TrackerDeviceListAdapter;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity implements MessageListener, View.OnClickListener {

    Toolbar toolbar;
    private RecyclerView listView;
    private String TAG = "DashboardActivity";
    private Button consent = null;
    private TrackerdeviceResponse mtrackerresponse;
    Intent intent = null;
    private String userToken = null;
    List<MultipleselectData> selectedData;
    private Button addButton;
    private Button track;
    private List<TrackerdeviceResponse.Data> data;
    private AddedDeviceData mAddData;
    private TrackerDeviceListAdapter adapter;
    private static List<AddedDeviceData> addDeviceList;
    private List<AddedDeviceData> existList;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    public static List<String> consentListPhoneNumber = null;
    public static Map<Double, Double> latLngMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = findViewById(R.id.customToolbar);
        listView = findViewById(R.id.listView);
        consent = findViewById(R.id.consent);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(linearLayoutManager);
        toolbar.setTitle("Dashboard");
        intent = getIntent();
        userToken = Util.getUserToken();
        mAddData = (AddedDeviceData) intent.getSerializableExtra("AddDeviceData");
        addButton = toolbar.findViewById(R.id.addbtn);
        track = findViewById(R.id.track);
        consent.setOnClickListener(this);
        addButton.setVisibility(View.VISIBLE);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Home");
        addButton.setOnClickListener(this);
        track.setOnClickListener(this);
        setSupportActionBar(toolbar);
        MessageReceiver.bindListener(DashboardActivity.this);
        selectedData = new ArrayList<MultipleselectData>();
        addDeviceList = new ArrayList<AddedDeviceData>();
        latLngMap = new LinkedHashMap<>();
        consentListPhoneNumber = new LinkedList<>();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }

        if (mAddData != null && !mAddData.getPhoneNumber().equals("")) {
            existList = Util.getAddedDevicelist();
            if ((existList == null) || (existList.size() == 0)) {
                addDeviceList.add(mAddData);
                Util.setListvalue(addDeviceList);
                adapter = new TrackerDeviceListAdapter(this, addDeviceList);
                listView.setAdapter(adapter);
            } else {
                existList.add(existList.size(), mAddData);
                adapter = new TrackerDeviceListAdapter(this, existList);
                listView.setAdapter(adapter);
            }

            adapter.setOnItemClickPagerListener(new TrackerDeviceListAdapter.RecyclerViewClickListener() {
                @Override
                public void recyclerViewListClicked(View v, int position, MultipleselectData data) {
                    selectedData.add(data);
                    Log.d("DashBoard", "Value of data" + selectedData);
                }
            });
        }

    }

    private void getServicecall(String token) {
        TrackerdeviceData data = new TrackerdeviceData();
        TrackerdeviceData.Sort sort = data.new Sort();
        sort.setLatestLocation(-1);
        TrackerdeviceData.Latlong latlong = data.new Latlong();
        latlong.setFrom(1542022465424L);
        latlong.setTo(1542108865424L);
        data.setmSort(sort);
        data.setLatlong(latlong);

        RequestHandler.getInstance(getApplicationContext()).handleRequest(new TrackdeviceRequest(new SuccessListener(), new ErrorListener(), token, data));
    }

    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            mtrackerresponse = Util.getInstance().getPojoObject(String.valueOf(response), TrackerdeviceResponse.class);
            data = mtrackerresponse.getmData();
            Log.d(TAG, "Response print" + response);
            gotoAddDeviceScreen(data);
        }
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "Token is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addbtn:
                getServicecall(userToken);
                break;
            case R.id.consent:
                sendSMS();
                break;
            case R.id.track:
                trackDevice();
                break;
        }
    }

    private void trackDevice() {
        for (int i = 0; i < selectedData.size(); i++) {
            if (consentListPhoneNumber != null && consentListPhoneNumber.contains("+91" + selectedData.get(i).getPhone())) {
                latLngMap.put(Double.parseDouble(selectedData.get(i).getLat()), Double.parseDouble(selectedData.get(i).getLng()));
            }
        }

        if (existList != null) {
            adapter = new TrackerDeviceListAdapter(this, existList);
            listView.setAdapter(adapter);
        }
        Intent map = new Intent(this, MapsActivity.class);
        startActivity(map);
        selectedData.clear();
    }

    private void gotoAddDeviceScreen(List<TrackerdeviceResponse.Data> mData) {
        Intent intent = new Intent(getApplicationContext(), NewDeviceActivity.class);
        intent.putExtra("DeviceData", (Serializable) mData);
        startActivity(intent);
    }


    private void sendSMS() {
        for (int i = 0; i < selectedData.size(); i++) {
            new SendSMSAsyncTask().execute(selectedData.get(i).getPhone(), "Do you want to be tracked, please reply in Yes or No !");
        }

        if (existList != null) {
            adapter = new TrackerDeviceListAdapter(this, existList);
            listView.setAdapter(adapter);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted !", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("Permission not granted");
                }
            }
        }
    }

    public class SendSMSAsyncTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(strings[0], null, strings[1], null, null);
            System.out.println("SMS Sent");
            return null;
        }
    }

    @Override
    public void messageReceived(String message, String phoneNum) {
        Toast.makeText(getApplicationContext(), "Received message -> " + message + " from phone number -> " + phoneNum, Toast.LENGTH_SHORT).show();
        for (int i = 0; i < selectedData.size(); i++) {
            if (phoneNum.trim().equals("+91" + selectedData.get(i).getPhone().trim())) {
                consentListPhoneNumber.add(phoneNum.trim());
            }
        }
    }
}