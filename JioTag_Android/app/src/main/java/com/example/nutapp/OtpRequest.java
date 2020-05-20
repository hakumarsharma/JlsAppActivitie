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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("PMD")
public class OtpRequest extends AppCompatActivity {

    static boolean m_login_check = false;
    Button otp_send;
    String m_url = "https://stg.borqs.io/accounts/api/users/tokens";
    String m_publickey = "";
    EditText login_edit_number;


    int OTP_MIN = 100000;
    int OTP_MAX = 999999;
    int m_self_otp = 100000;
    String sms_otp = "Dear Customer,your JioTags OTP is : " + m_self_otp + " .Use this password to validate your login.";

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    public boolean isPermissionAlreadyGranted() {
        int sendSms= ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int writeExternalStorage=ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExternalStorage=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int coarseLocation=ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int readPhoneState=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int readPhoneNumbers=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS);
        int readSms=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int receiveSms=ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int readContact=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if(sendSms != PackageManager.PERMISSION_GRANTED|| fineLocation !=PackageManager.PERMISSION_GRANTED || camera !=PackageManager.PERMISSION_GRANTED ||
        writeExternalStorage !=PackageManager.PERMISSION_GRANTED || readExternalStorage !=PackageManager.PERMISSION_GRANTED ||
        coarseLocation !=PackageManager.PERMISSION_GRANTED || readPhoneState != PackageManager.PERMISSION_GRANTED ||
        readPhoneNumbers !=PackageManager.PERMISSION_GRANTED || readSms !=PackageManager.PERMISSION_GRANTED ||
        receiveSms != PackageManager.PERMISSION_GRANTED || readContact != PackageManager.PERMISSION_GRANTED ){
            return false;
        }else{
            return true;
        }

    }

    private void checkLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionAlreadyGranted() == false) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS ,Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                Log.d("PERMISSIONS", "GRANTED ALREADY NO CHECK");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        Log.d("onRequest", "onRequestPermissionsResult");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                Log.d("INRESULT", "MY_PERMISSIONS_REQUEST_LOCATION" + permissions.toString());
                // If request is cancelled, the result arrays are empty.
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        Log.d("NOPERM", "NOT Granted");
                        Toast.makeText(this.getApplicationContext(), "You have to accept all the permissions else the app will close", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                break;
            }
            default:
                break;

        }
    }


    public int generateSelfOtp() {
        Random rand = new Random();
        m_self_otp = rand.nextInt((OTP_MAX - OTP_MIN) + 1) + OTP_MIN;
        Log.d("SELFOTP", m_self_otp + "");
        return m_self_otp;
    }

    public void sendSelfOtp() {
        generateSelfOtp();
        sms_otp = "Dear Customer,your JioTags OTP is : " + m_self_otp + " .Use this password to validate your login.";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(m_publickey, null, sms_otp, null, null);
        Intent startMain = new Intent(getApplicationContext(), OtpWaitScreen.class);
        startMain.putExtra("PHNUM", m_publickey);
        startMain.putExtra("SELFOTP", Integer.toString(m_self_otp));
        startActivity(startMain);
    }

    public boolean isNetworkEnabled() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_view);

        TextView changeRmn = findViewById(R.id.email_login_option);
        final CheckBox loginCheck = (CheckBox) findViewById(R.id.login_check_box);
        loginCheck.setButtonDrawable(R.drawable.uncheckbox);
        loginCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m_login_check = isChecked;
                Log.d("CHECK::", isChecked + "");
                if (m_login_check) {
                    loginCheck.setButtonDrawable(R.drawable.filled);
                    otp_send.setEnabled(true);
                    otp_send.setBackground(getResources().getDrawable(R.drawable.button_frame_blue));
                    Log.d("BUTTON::", "ENABLED" + "");
                } else {
                    loginCheck.setButtonDrawable(R.drawable.uncheckbox);
                    otp_send.setEnabled(false);
                    otp_send.setBackground(getResources().getDrawable(R.drawable.disabled_button));
                    Log.d("BUTTON::", "DISABLED" + "");
                }
            }
        });

        otp_send = (Button) findViewById(R.id.login_btn_send_otp);
        otp_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (true) {//isNetworkEnabled()) {
                    String phNumber = login_edit_number.getText().toString();
                    if (phNumber.isEmpty() || phNumber.length() < 10) {
                        Toast.makeText(v.getContext(), "Please Enter 10 digit valid mobile number of this Phone!!!! ", Toast.LENGTH_SHORT).show();
                    } else {
                        m_publickey = phNumber;
                        //sendOtpReuest(m_url, m_publickey);
                        sendSelfOtp();
                    }
                } else {
                    Toast.makeText(v.getContext(), "Please Enable Network to to get OTP!!!! ", Toast.LENGTH_SHORT).show();
                }
                //Intent startMain = new Intent(getApplicationContext(), OtpWaitScreen.class);
                //startActivity(startMain);
            }
        });
        otp_send.setEnabled(false);
        otp_send.setBackground(getResources().getDrawable(R.drawable.disabled_button));

        login_edit_number = (EditText) findViewById(R.id.login_edit_number);
        login_edit_number.setTypeface(JioUtils.mTypeface(this, 5));

        EditText prefixText = (EditText) findViewById(R.id.prefix_text);
        prefixText.setTypeface(JioUtils.mTypeface(this, 5));

        //TextView loginTerms = (TextView) findViewById(R.id.login_terms);
        //login_terms.setTypeface(JioUtils.mTypeface(this, 2));
        TextView loginJioTagsEnterNumber = (TextView) findViewById(R.id.login_jioTags_enter_number);
        loginJioTagsEnterNumber.setTypeface(JioUtils.mTypeface(this, 3));
        TextView loginJioTagsRegisteredNumber = (TextView) findViewById(R.id.login_jioTags_registered_number);
        loginJioTagsRegisteredNumber.setTypeface(JioUtils.mTypeface(this, 2));

        ///Skip login View///////
        Button loginSkipBtnSendOtp = (Button) findViewById(R.id.login_skip_btn_send_otp);
        loginSkipBtnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(getApplicationContext(), JioAddFinder.class);
                startActivity(startMain);
            }
        });
        //////////////////
        checkLocationPermission();

        changeRmn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailOption = new Intent(getApplicationContext(), AuthLoginActivity.class);
                startActivity(emailOption);
            }
        });
    }


    public void sendOtpReuest(final String url, String publickey) {
        Log.d("SERVER OTP", "Request OTP From server::" + publickey);

        try {
            JSONObject jsonMainBody = new JSONObject();
            JSONObject jsonCode = new JSONObject();
            jsonCode.put("code", "student");
            jsonMainBody.put("role", jsonCode);
            jsonMainBody.put("phone", publickey);
            jsonMainBody.put("type", "registration");

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonMainBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("MSGFROMSERVER", "SUCCESS" + response.toString());
                    try {
                        Log.d("MSGFROMSERVER", "SUCCESS" + response.get("code"));
                        Toast.makeText(getApplicationContext(), "Sent OTP Successfully!!!! ", Toast.LENGTH_SHORT).show();
                        Intent startMain = new Intent(getApplicationContext(), OtpWaitScreen.class);
                        startMain.putExtra("PHNUM", m_publickey);
                        //startMain.putExtra("SELFOTP",m_self_otp);
                        startActivity(startMain);
                    } catch (Exception e) {
                        Log.d("EXCEPTION", "exce");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("MSGFROMSERVER", "FAILURE");
                    Toast.makeText(getApplicationContext(), "Send OTP Failed!!!! ", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap headers = new HashMap();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            queue.add(req);
        } catch (Exception e) {
            Log.d("onRequest", "Error"+e);
        }
    }
}
