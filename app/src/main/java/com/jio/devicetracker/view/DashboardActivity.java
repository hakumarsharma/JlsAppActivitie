// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.FMSAPIData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.database.pojo.TrackerdeviceData;
import com.jio.devicetracker.database.pojo.request.FMSTrackRequest;
import com.jio.devicetracker.database.pojo.request.TrackdeviceRequest;
import com.jio.devicetracker.database.pojo.response.LocationApiResponse;
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;
import com.jio.devicetracker.jiotoken.JiotokenHandler;
import com.jio.devicetracker.network.MQTTManager;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.network.SendSMSTask;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.TrackerDeviceListAdapter;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
    public static Map<String, Map<Double, Double>> namingMap = null;
    private Context context = null;
    private boolean flagValue;
    private String number;
    public static Map<Double, Double> fmsLatLngMap = null;
    public static Map<String, Map<Double, Double>> fmsNamingMap = null;
    public static String trackeeIMEI = null;
    private String trackeeName = "";
    private DashboardActivity dashboardActivity = null;
    private String adminEmail;

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
        intent = getIntent();
        mDbManager = new DBManager(this);
        //mAddData = (AddedDeviceData) intent.getSerializableExtra("AddDeviceData");
        number = intent.getStringExtra("phone");
        flagValue = intent.getBooleanExtra("flag",false);
        consentRequestBox(flagValue,number);
        trackBtn = toolbar.findViewById(R.id.track);
        trackBtn.setVisibility(View.VISIBLE);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(Constant.DASHBOARD_TITLE);
        trackBtn.setOnClickListener(this);
        mActionbtn.setOnClickListener(this);
        setSupportActionBar(toolbar);
        MessageReceiver.bindListener(DashboardActivity.this);
        selectedData = new ArrayList<>();
        addDeviceList = new ArrayList<>();
        latLngMap = new LinkedHashMap<>();
        consentListPhoneNumber = new LinkedList<>();
        namingMap = new HashMap<>();
        fmsNamingMap = new HashMap<>();
        context = getApplicationContext();
        fmsLatLngMap = new LinkedHashMap<>();

        isDevicePresent();

        String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS};
        if (!hasPermissions(getApplicationContext(), permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMIT_ALL);
        }

        if (RegistrationActivity.isFMSFlow == false) {
            getAdminDetail();
            showDatainList();
        } else {
            showDataFromFMS();
        }
        adapter.setOnItemClickPagerListener(new TrackerDeviceListAdapter.RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position, MultipleselectData data, boolean val) {
                if (val) {
                    selectedData.add(data);
                    checkConsentPublishMQTTMessage(data);
                    Log.d(TAG, "Value of data" + selectedData.size());
                } else {
                    for (int i = 0; i < selectedData.size(); i++) {
                        if (selectedData.get(i).getPhone() == data.getPhone()) {
                            selectedData.remove(i); // TODO include break after this if you want to stop loop once the device is deleted
                        }
                        //Log.d(TAG, "unselect the item from list");
                    }
                }
            }

            @Override
            public void recyclerviewEditList(String relation,String phoneNumber) {
                gotoEditScreen(relation,phoneNumber);
            }

            @Override
            public void recyclerviewDeleteList(String phoneNumber,int position) {

                alertDilogBoxWithCancelbtn(Constant.DELETC_DEVICE,Constant.ALERT_TITLE,phoneNumber,position);

            }

            @Override
            public void consetClick(String phoneNumber) {
                sendSMS(phoneNumber);
            }
        });
    }

    private void getAdminDetail(){
        AdminLoginData adminLoginData = new DBManager(this).getAdminLoginDetail();
        userToken = adminLoginData.getUser_token();
        adminEmail = adminLoginData.getEmail();
    }

    private void checkConsentPublishMQTTMessage(MultipleselectData multipleselectData) {
        Util util = Util.getInstance();
        MQTTManager mqttManager = new MQTTManager();
        List<AddedDeviceData> consentData = mDbManager.getAlldataFromFMS();
        for(int i = 0; i < consentData.size(); i++) {
            if((consentData.get(i).getPhoneNumber().equalsIgnoreCase(multipleselectData.getPhone())) && consentData.get(i).getConsentStaus().trim().equalsIgnoreCase("yes jiotracker")){
                String topic = "jioiot/svckm/jiophone/"+util.getIMEI(getApplicationContext())+"/uc/fwd/sesid";
                Log.d("Topic --> ", topic);
                trackeeIMEI = consentData.get(i).getImeiNumber();
                String message = "{\"reqId\":\"" +consentData.get(i).getImeiNumber()+ "\",\"trkId\":\"" +util.getIMEI(getApplicationContext())+ "\",\"sesId\":\"" + Util.getInstance().getSessionId()+ "\",\"trknr\":\""+ RegistrationActivity.phoneNumber.substring(2) +"\",\"reqnr\":\"" + consentData.get(i).getPhoneNumber() + "\"}";
                Log.d("Message --> ", message);
                mqttManager.publishMessage(topic, message);
            }
        }
    }

    public DashboardActivity getDashboardActivity() {
        if(dashboardActivity == null) {
            dashboardActivity = new DashboardActivity();
            return dashboardActivity;
        }
        return dashboardActivity;
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
        if (RegistrationActivity.isFMSFlow == false) {
            alldata = dbManager.getAlldata(adminEmail);
            if (alldata.size() == 0) {
                listView.setVisibility(View.INVISIBLE);
                devicePresent.setVisibility(View.VISIBLE);
            } else {
                devicePresent.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        } else {
            alldata = dbManager.getAlldataFromFMS();
            if (alldata.size() == 0) {
                listView.setVisibility(View.INVISIBLE);
                devicePresent.setVisibility(View.VISIBLE);
            } else {
                devicePresent.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getServicecall(String token) {
        if (RegistrationActivity.isFMSFlow == true) {
            gotoAddDeviceActivity();
        } else {
            showProgressBarDialog();
            TrackerdeviceData data = new TrackerdeviceData();
            TrackerdeviceData.StartsWith startsWith = data.new StartsWith();
            startsWith.setCurrentDat("jamnagar1tiq9s");
            data.setStartsWith(startsWith);
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
            //Toast.makeText(getApplicationContext(), "Token is null", Toast.LENGTH_SHORT).show();
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
        if (RegistrationActivity.isFMSFlow == false) {
            List<AddedDeviceData> consentData = mDbManager.getAlldata(adminEmail);
            if (selectedData.size() == 0) {
                Util.alertDilogBox(Constant.CHOOSE_DEVICE, Constant.ALERT_TITLE, this);
                return;
            }
            for (int i = 0; i < consentData.size(); i++) {
                if (selectedData.get(i).getPhone().equals(consentData.get(i).getPhoneNumber())) {
                    if (consentData.get(i).getConsentStaus().equalsIgnoreCase("Yes JioTracker")) {
                        latLngMap = mDbManager.getLatLongForMap(selectedData);
                        namingMap.put(selectedData.get(i).getName(), latLngMap);
                    } else {
                        Util.alertDilogBox(Constant.CONSENT_NOTAPPROVED + consentData.get(i).getPhoneNumber(), Constant.ALERT_TITLE, this);
                    }
                }
            }
            startTheScheduler();
            Intent map = new Intent(this, MapsActivity.class);
            startActivity(map);
        }
        else {
            showProgressBarDialog();
            List<String> imeiNumbers = new ArrayList<>();
            List<String> phoneNumbers = new ArrayList<>();
            List<AddedDeviceData> consentData = mDbManager.getAlldataFromFMS();
            if (selectedData.size() == 0) {
                Util.alertDilogBox("Please select the number for tracking", "Jio Alert", this);
                return;
            }
            for (int i = 0; i < consentData.size(); i++) {
                if (selectedData.get(i).getPhone().equals(consentData.get(i).getPhoneNumber())) {
                    if (consentData.get(i).getConsentStaus().toLowerCase().contains("yes jiotracker")) {
                        imeiNumbers.add(consentData.get(i).getImeiNumber());
                        phoneNumbers.add(consentData.get(i).getPhoneNumber());
                        trackeeName = consentData.get(i).getName();
                    } else {
                        Util.alertDilogBox("Consent is not apporoved for phone number " + consentData.get(i).getPhoneNumber(), "Jio Alert", this);
                    }
                }
            }

            FMSAPIData fmsapiData = new FMSAPIData();
            fmsapiData.setEvt(new String[]{"GPS"});
            fmsapiData.setDvt("SIM");
            fmsapiData.setImi(imeiNumbers);
            fmsapiData.setEfd(14546957247L);
            fmsapiData.setMob(phoneNumbers);
            fmsapiData.setTid("456");
            RequestHandler.getInstance(getApplicationContext()).handleFMSRequest(new FMSTrackRequest(new FMSSuccessListener(), new FMSErrorListener(), fmsapiData));
        }
    }

    private class FMSSuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            LocationApiResponse fmsAPIResponse = Util.getInstance().getPojoObject(String.valueOf(response), LocationApiResponse.class);
            fmsLatLngMap.put(Double.parseDouble(fmsAPIResponse.new Event().getLatitute()), Double.parseDouble(fmsAPIResponse.new Event().getLongnitute()));
            fmsNamingMap.put(trackeeName, fmsLatLngMap);
            progressDialog.dismiss();
            startActivity(new Intent(DashboardActivity.this, FMSMapActivity.class));
        }
    }

    private class FMSErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Map<Double, Double> hashMap = new HashMap<>();
            hashMap.put(12.976786, 77.593926);
            fmsNamingMap.put("JIO Location", hashMap);
            progressDialog.dismiss();
            startActivity(new Intent(DashboardActivity.this, FMSMapActivity.class));
            Toast.makeText(getApplicationContext(), Constant.FMS_SERVERISSUE, Toast.LENGTH_SHORT).show();
        }
    }

    public void startTheScheduler() {
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            Log.i("TAG", "Inside Scheduler");
            DashboardActivity.this.runOnUiThread(() -> {
                makeAPICall(Util.getUserToken());
                new MapsActivity().showMapOnTimeInterval();
            });
        }, 0, MapsActivity.refreshIntervalTime, TimeUnit.SECONDS);
    }

    public void makeAPICall(String token) {
        /*TrackerdeviceData data = new TrackerdeviceData();
        TrackerdeviceData.Sort sort = data.new Sort();
        sort.setLatestLocation(-1);
        TrackerdeviceData.Latlong latlong = data.new Latlong();
        latlong.setFrom(1542022465424L);
        latlong.setTo(1542108865424L);
        data.setmSort(sort);
        data.setLatlong(latlong);
        if(context != null) {
            RequestHandler.getInstance(context).handleRequest(new TrackdeviceRequest(new SuccessAPICall(), new UnSuccessfullAPICall(), token, data));
        }*/
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
            Toast.makeText(getApplicationContext(), Constant.TOKEN_NULL, Toast.LENGTH_SHORT).show();
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
       if(consentStatus.equalsIgnoreCase("Yes JioTracker"))
       {
           Util.alertDilogBox(Constant.CONSENT_APPROVED,Constant.ALERT_TITLE,this);
       } else {
           new SendSMSTask().execute(phoneNumber, userName+" From JIOTracker application wants to track your location, please reply in Yes JioTracker or No JioTracker !");
           Toast.makeText(DashboardActivity.this, "Consent sent", Toast.LENGTH_SHORT).show();
          /* if (JiotokenHandler.ssoToken == null || RegistrationActivity.isFMSFlow == false) {
               mDbManager.updatependingConsent(phoneNumber);
               showDatainList();
           } else { */
               mDbManager.updatependingConsentFMS(phoneNumber);
               showDataFromFMS();
           //}
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

    /*public class SendSMSAsyncTask extends AsyncTask<String, String, Void> {

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
    }*/

    @Override
    public void messageReceived(String message, String phoneNum) {
        Toast.makeText(getApplicationContext(), "Received message -> " + message + " from phone number -> " + phoneNum, Toast.LENGTH_SHORT).show();
        String phone = phoneNum.substring(3);
        /*if (JiotokenHandler.ssoToken == null || RegistrationActivity.isFMSFlow == false) {
            mDbManager.updateConsentInBors(phone,message);
            showDatainList();
        } else {
            mDbManager.updateConsentInFMS(phone,message);
            showDataFromFMS();
        }*/
        if(message.toLowerCase().contains("yes jiotracker")) {
            publishMQTTMessage(phone);
        }
        mDbManager.updateConsentInFMS(phone,message);
        showDataFromFMS();
    }

    private void publishMQTTMessage(String phoneNumber) {
        Util util = Util.getInstance();
        MQTTManager mqttManager = new MQTTManager();
        if(addDeviceList != null) {
            for(int i = 0; i < addDeviceList.size(); i ++) {
                if(addDeviceList.get(i).getPhoneNumber().equalsIgnoreCase(phoneNumber)) {
                    String topic = "jioiot/svckm/jiophone/"+util.getIMEI(getApplicationContext())+"/uc/fwd/sesid";
                    Log.d("Topic --> ", topic);
                    String message = "{\"reqId\":\"" +addDeviceList.get(i).getImeiNumber()+ "\",\"trkId\":\"" +util.getIMEI(getApplicationContext())+ "\",\"sesId\":\"" + Util.getInstance().getSessionId()+ "\",\"trknr\":\""+ RegistrationActivity.phoneNumber.substring(2) +"\",\"reqnr\":\"" + addDeviceList.get(i).getPhoneNumber() + "\"}";
                    Log.d("Message --> ", message);
                    mqttManager.publishMessage(topic, message);
                }
            }
        }
    }

    public void showDatainList() {
        addDeviceList = mDbManager.getAlldata(adminEmail);
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

    private void showProgressBarDialog() {
        progressDialog = ProgressDialog.show(DashboardActivity.this, "", Constant.LOADING_DATA, true);
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

              /*  if (JiotokenHandler.ssoToken == null || RegistrationActivity.isFMSFlow == false) {
                    mDbManager.deleteSelectedData(phoneNumber);
                } else {*/
                    mDbManager.deleteSelectedDataformFMS(phoneNumber);
             //   }
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

    public void consentRequestBox(boolean flag,String phoneNumber)
    {
        if(flag)
        {
            Util.alertDilogBox("Request consent for device "+phoneNumber,"Jio Alert",this);
            flag = false;
        }
    }
}