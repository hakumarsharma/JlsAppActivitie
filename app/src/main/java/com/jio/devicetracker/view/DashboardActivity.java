// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jio.devicetracker.R;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.FMSAPIData;
import com.jio.devicetracker.database.pojo.FMSHeader;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.database.pojo.TrackerdeviceData;
import com.jio.devicetracker.database.pojo.request.FMSTrackRequest;
import com.jio.devicetracker.database.pojo.request.TrackdeviceRequest;
import com.jio.devicetracker.database.pojo.response.LocationApiResponse;
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;
import com.jio.devicetracker.jiotoken.JiotokenHandler;
import com.jio.devicetracker.network.MQTTHandler;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.RecyclerviewSwipeController;
import com.jio.devicetracker.view.adapter.TrackerDeviceListAdapter;
import com.jio.mqttclient.JiotMqttClient;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DashboardActivity extends AppCompatActivity implements MessageListener, View.OnClickListener {

    Toolbar toolbar;
    private RecyclerView listView;
    private String TAG = "DashboardActivity";
    private TrackerdeviceResponse mtrackerresponse;
    Intent intent = null;
    private String userToken = null;
    List<MultipleselectData> selectedData;
    private Button removeBtn, addButton, trackBtn, consent;
    public static List<TrackerdeviceResponse.Data> data;
    private AddedDeviceData mAddData;
    private TrackerDeviceListAdapter adapter;
    private static List<AddedDeviceData> addDeviceList;
    private List<AddedDeviceData> existList;
    private static final int PERMIT_ALL = 1;
    public static List<String> consentListPhoneNumber = null;
    public static Map<Double, Double> latLngMap = null;
    private DBManager mDbManager;
    private TextView devicePresent = null;
    private ProgressDialog progressDialog = null;
    private FloatingActionButton mActionbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = findViewById(R.id.customToolbar);
        listView = findViewById(R.id.listView);
        mActionbtn = findViewById(R.id.fab);
       // consent = findViewById(R.id.consent);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(linearLayoutManager);
        toolbar.setTitle("Dashboard");
        intent = getIntent();
        userToken = Util.getUserToken();
        mDbManager = new DBManager(this);
        mAddData = (AddedDeviceData) intent.getSerializableExtra("AddDeviceData");
        trackBtn = toolbar.findViewById(R.id.track);
        trackBtn.setVisibility(View.VISIBLE);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Home");
        trackBtn.setOnClickListener(this);
        mActionbtn.setOnClickListener(this);
        setSupportActionBar(toolbar);
        MessageReceiver.bindListener(DashboardActivity.this);
        selectedData = new ArrayList<>();
        addDeviceList = new ArrayList<AddedDeviceData>();
        latLngMap = new LinkedHashMap<>();
        consentListPhoneNumber = new LinkedList<>();
        isDevicePresent();

        String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS};
        if (!hasPermissions(getApplicationContext(), permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMIT_ALL);
        }

        if (JiotokenHandler.ssoToken == null && RegistrationActivity.isFMSFlow == false) {
            showDatainList();
        } else {
            showDataFromFMS();
        }
        adapter.setOnItemClickPagerListener(new TrackerDeviceListAdapter.RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position, MultipleselectData data, boolean val) {
                if (val) {
                    selectedData.add(data);
                    Log.d(TAG, "Value of data" + selectedData.size());
                } else {
                    for (int i = 0; i < selectedData.size(); i++) {
                        if (selectedData.get(i).getPhone() == data.getPhone()) {
                            selectedData.remove(i);
                        }
                        Log.d(TAG, "unselect the item from list");
                    }
                }
            }

            @Override
            public void recyclerviewEditList(String relation,String phoneNumber) {
                gotoEditScreen(relation,phoneNumber);
            }

            @Override
            public void recyclerviewDeleteList(String phoneNumber,int position) {

                alertDilogBoxWithCancelbtn("Are you want to delete ?","Jio Alert",phoneNumber,position);

            }

            @Override
            public void consetClick(String phoneNumber) {
                sendSMS(phoneNumber);
            }
        });
        /*ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerviewSwipeController(0, ItemTouchHelper.LEFT, this, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(listView);*/
    }

    private void gotoEditScreen(String relation,String phoneNumber) {
        Intent intent = new Intent(DashboardActivity.this,EditActivity.class);
        intent.putExtra("number",phoneNumber);
        intent.putExtra("Relation",relation);
        startActivity(intent);
    }
    private void isDevicePresent() {
        DBManager dbManager = new DBManager(getApplicationContext());
        devicePresent = findViewById(R.id.devicePresent);
        List<AddedDeviceData> alldata = null;
        if (JiotokenHandler.ssoToken == null && RegistrationActivity.isFMSFlow == false) {
            alldata = dbManager.getAlldata();
            RecyclerView recyclerView = findViewById(R.id.listView);

            if (alldata.size() == 0) {
                recyclerView.setVisibility(View.INVISIBLE);
                devicePresent.setVisibility(View.VISIBLE);
            } else {
                devicePresent.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            alldata = dbManager.getAlldataFromFMS();
            RecyclerView recyclerView = findViewById(R.id.listView);
            if (alldata.size() == 0) {
                recyclerView.setVisibility(View.INVISIBLE);
                devicePresent.setVisibility(View.VISIBLE);
            } else {
                devicePresent.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getServicecall(String token) {
        if (JiotokenHandler.ssoToken != null && RegistrationActivity.isFMSFlow == false) {
            gotoAddDeviceActivity();
        } else {
            showProgressBarDialog();
            TrackerdeviceData data = new TrackerdeviceData();
            TrackerdeviceData.Sort sort = data.new Sort();
            sort.setLatestLocation(-1);
            TrackerdeviceData.Latlong latlong = data.new Latlong();
            latlong.setFrom(1542022465424L);
            latlong.setTo(1542108865424L);
            data.setmSort(sort);
            data.setLatlong(latlong);
            devicePresent.setVisibility(View.GONE);
            RequestHandler.getInstance(getApplicationContext()).handleRequest(new TrackdeviceRequest(new SuccessListener(), new ErrorListener(), token, data));
        }
    }


    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            mtrackerresponse = Util.getInstance().getPojoObject(String.valueOf(response), TrackerdeviceResponse.class);
            data = mtrackerresponse.getmData();
            Log.d(TAG, "Response print" + response);
            gotoAddDeviceScreen(data);
            progressDialog.dismiss();
        }
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Token is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                getServicecall(userToken);
                break;
            case R.id.track:
                trackDevice();
                break;
        }
    }


    private void trackDevice() {
        if (JiotokenHandler.ssoToken == null && RegistrationActivity.isFMSFlow == false) {
            List<AddedDeviceData> consentData = mDbManager.getAlldata();
            if (selectedData.size() == 0) {
                Util.alertDilogBox("Please select the number for tracking", "Jio Alert", this);
                return;
            }
            for (int i = 0; i < consentData.size(); i++) {
                if (selectedData.get(i).getPhone().equals(consentData.get(i).getPhoneNumber())) {
                    if (consentData.get(i).getConsentStaus().equals("Yes")) {
                        latLngMap = mDbManager.getLatLongForMap(selectedData);
                        startTheScheduler();
                        Intent map = new Intent(this, MapsActivity.class);
                        startActivity(map);
                    } else {
                        Util.alertDilogBox("Consent is not apporoved for phone number " + consentData.get(i).getPhoneNumber(), "Jio Alert", this);
                    }
                }
            }
        }
        else {
            showProgressBarDialog();
            MQTTHandler mqttHandler = new MQTTHandler();
            mqttHandler.getSessionId();
            mqttHandler.getMQTTClient(getApplicationContext());
            mqttHandler.connetMQTT();
            FMSAPIData fmsapiData = new FMSAPIData();
            fmsapiData.setEvt(new String[]{"GPS"});
            fmsapiData.setDvt("JIOPHONE");
            fmsapiData.setImi(new String[]{"40903"});
            fmsapiData.setEfd(14546957247L);
            fmsapiData.setMob(new String[]{"8618799136"});
            fmsapiData.setTid("456");
            RequestHandler.getInstance(getApplicationContext()).handleFMSRequest(new FMSTrackRequest(new FMSSuccessListener(), new FMSErrorListener(), fmsapiData));
        }
    }

    private class FMSSuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            LocationApiResponse fmsAPIResponse = Util.getInstance().getPojoObject(String.valueOf(response), LocationApiResponse.class);
            latLngMap.put(Double.parseDouble(fmsAPIResponse.new Event().getLatitute()), Double.parseDouble(fmsAPIResponse.new Event().getLongnitute()));
            progressDialog.dismiss();
        }
    }

    private class FMSErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "FMS server is down please call back after some time", Toast.LENGTH_SHORT).show();
        }
    }

    public void startTheScheduler() {
        final Runnable beeper = new Runnable() {
            public void run() {
                Log.i("TAG", "Inside Scheduler");
                DashboardActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        makeAPICall(Util.getUserToken());
                        new MapsActivity().fetchLocationDetail();
                    }
                });
            }
        };
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(beeper, 0, 300, TimeUnit.SECONDS);
    }

    public void makeAPICall(String token) {
        TrackerdeviceData data = new TrackerdeviceData();
        TrackerdeviceData.Sort sort = data.new Sort();
        sort.setLatestLocation(-1);
        TrackerdeviceData.Latlong latlong = data.new Latlong();
        latlong.setFrom(1542022465424L);
        latlong.setTo(1542108865424L);
        data.setmSort(sort);
        data.setLatlong(latlong);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new TrackdeviceRequest(new SuccessAPICall(), new UnSuccessfullAPICall(), token, data));
    }

    private class SuccessAPICall implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            mtrackerresponse = Util.getInstance().getPojoObject(String.valueOf(response), TrackerdeviceResponse.class);
            data = mtrackerresponse.getmData();
            Log.d(TAG, "Response print" + response);
        }
    }

    private class UnSuccessfullAPICall implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "Token is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void gotoAddDeviceActivity() {
        startActivity(new Intent(getApplicationContext(), NewDeviceActivity.class));
    }

    private void gotoAddDeviceScreen(List<TrackerdeviceResponse.Data> mData) {
        Intent intent = new Intent(getApplicationContext(), NewDeviceActivity.class);
        intent.putExtra("DeviceData", (Serializable) mData);
        startActivity(intent);
    }


    private void sendSMS(String phoneNumber) {

       String userName= mDbManager.getAdminDetail();
       String consentStatus = mDbManager.getConsentStatusBorqs(phoneNumber);
       if(consentStatus.equals("Yes"))
       {
           Util.alertDilogBox("Consent status is already approved for this number","Jio Alert",this);
       } else {
           new SendSMSAsyncTask().execute(phoneNumber, "Do you want to be tracked, please reply in Yes or No !\n"+userName);
           if (JiotokenHandler.ssoToken == null && RegistrationActivity.isFMSFlow == false) {
               mDbManager.updatependingConsent(phoneNumber);
               showDatainList();
           } else {
               mDbManager.updatependingConsentFMS(phoneNumber);
               showDataFromFMS();
           }
       }
       
    }


    public boolean hasPermissions(Context context, String[] permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public class SendSMSAsyncTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(strings[0], null, strings[1], null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(DashboardActivity.this, "Consent sent", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void messageReceived(String message, String phoneNum) {
        Toast.makeText(getApplicationContext(), "Received message -> " + message + " from phone number -> " + phoneNum, Toast.LENGTH_SHORT).show();
        String phone = phoneNum.substring(3,phoneNum.length());
        if (JiotokenHandler.ssoToken == null && RegistrationActivity.isFMSFlow == false) {
            mDbManager.updateConsentInBors(phone,message);
            showDatainList();
        } else {
            mDbManager.updateConsentInFMS(phone,message);
            showDataFromFMS();
        }

    }

    public void showDatainList() {
        addDeviceList = mDbManager.getAlldata();
        if (addDeviceList != null) {
            adapter = new TrackerDeviceListAdapter(this, addDeviceList);
            listView.setAdapter(adapter);
        }
    }

    private void showDataFromFMS() {
        addDeviceList = mDbManager.getAlldataFromFMS();
        if (addDeviceList != null) {
            adapter = new TrackerDeviceListAdapter(this, addDeviceList);
            listView.setAdapter(adapter);
        }
    }

  /*  @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof TrackerDeviceListAdapter.ViewHolder) {
            String phoneNumber = ((TrackerDeviceListAdapter.ViewHolder) viewHolder).phone.getText().toString();
            if (JiotokenHandler.ssoToken == null && RegistrationActivity.isFMSFlow == false) {
                mDbManager.deleteSelectedData(phoneNumber);
            } else {
                mDbManager.deleteSelectedDataformFMS(phoneNumber);
            }
            adapter.removeItem(viewHolder.getAdapterPosition());
            Toast.makeText(DashboardActivity.this, "Phone number is deleted", Toast.LENGTH_SHORT).show();
        }
        isDevicePresent();
    }
*/
    private void showProgressBarDialog() {
        progressDialog = ProgressDialog.show(DashboardActivity.this, "", "Please wait loading data...", true);
        progressDialog.setCancelable(true);
    }

    public void alertDilogBoxWithCancelbtn(String message, String title,String phoneNumber,int position) {

        AlertDialog.Builder adb = new AlertDialog.Builder(DashboardActivity.this);
        //adb.setView(alertDialogView);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (JiotokenHandler.ssoToken == null && RegistrationActivity.isFMSFlow == false) {
                    mDbManager.deleteSelectedData(phoneNumber);
                } else {
                    mDbManager.deleteSelectedDataformFMS(phoneNumber);
                }
                //mDbManager.deleteSelectedData(phoneNumber);
                adapter.removeItem(position);

                isDevicePresent();
            }
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        adb.show();
    }
}