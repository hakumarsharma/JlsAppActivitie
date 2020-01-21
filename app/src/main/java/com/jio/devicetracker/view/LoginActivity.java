// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.Userdata;
import com.jio.devicetracker.database.pojo.request.LoginDataRequest;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

/**
 * Implementation of Splash Screen.This class creates splash screen for JFF application
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {

    private EditText jioEmailEditText = null;
    private EditText jioPasswordEditText = null;
    public static LogindetailResponse logindetailResponse = null;
    private ProgressDialog progressDialog = null;
    private static final int PERMIT_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText("Login");

        Button loginButton = findViewById(R.id.login);
        jioEmailEditText = findViewById(R.id.jioEmailId);
        //jioEmailEditText.setText("shivakumar.jagalur@ril.com");
        jioPasswordEditText = findViewById(R.id.jioPassword);
        //jioPasswordEditText.setText("Jio@1234");
        TextView registerText = findViewById(R.id.registedHere);
        TextView forgetPass = findViewById(R.id.clickForget);
        MessageReceiver.bindListener(LoginActivity.this);
        registerText.setOnClickListener(this);
        forgetPass.setOnClickListener(this);

        String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS};
        if (!hasPermissions(LoginActivity.this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMIT_ALL);
        }

        jioEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!jioEmailEditText.getText().toString().equals("") || !jioPasswordEditText.getText().toString().equals("")) {
                    loginButton.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.login_selector,null));
                    loginButton.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailId = jioEmailEditText.getText().toString();
                if (emailId.isEmpty()) {
                    loginButton.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.selector,null));
                    loginButton.setTextColor(Color.WHITE);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String jioEmailIdText = jioEmailEditText.getText().toString().trim();
                String jioPasswordText = jioPasswordEditText.getText().toString().trim();
                if (jioEmailEditText.length() == 0) {
                    jioEmailEditText.setError(Constant.EMAILID_VALIDATION);
                    return;
                }
                if (jioPasswordText.length() == 0) {
                    jioPasswordEditText.setError(Constant.PASSWORD_VALIDATION);
                    return;
                }

                if (jioEmailEditText.length() != 0) {
                    if (Util.isValidEmailId(jioEmailIdText)) {
                        Userdata data = new Userdata();
                        data.setEmailId(jioEmailIdText);
                        data.setPassword(jioPasswordText);
                        data.setType("supervisor");
                        RequestHandler.getInstance(getApplicationContext()).handleRequest(new LoginDataRequest(new SuccessListener(), new ErrorListener(), data));
                    } else {
                        jioEmailEditText.setError(Constant.EMAIL_VALIDATION);
                        return;
                    }
                }
                showProgressBarDialog();
            }
        });
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

    @Override
    public void messageReceived(String message, String phoneNum) {
       // Toast.makeText(LoginActivity.this, "Received message -> " + message + " from phone number -> " + phoneNum, Toast.LENGTH_SHORT).show();
        Log.d("Received Message --> ", message);
//        String phone = phoneNum.substring(3);
        /*if (RegistrationActivity.isFMSFlow == false) {
            if(RegistrationDetailActivity.phoneNumber != null && message.length() == 4) {
                BorqsOTPActivity.phoneOTP.setText(message);
            }
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registedHere:
                gotoRegisterScreen();
                break;
            case R.id.clickForget:
                gotoForgetPassTokenScreen();
                break;
            default:
                break;
        }
    }

    private void gotoForgetPassTokenScreen() {

        Intent intent = new Intent(LoginActivity.this,ForgetpassTokenActivity.class);
        intent.putExtra("Email",jioEmailEditText.getText().toString().trim());
        startActivity(intent);
    }

   /* private void forgetpassTokenApi() {

        if(jioEmailEditText.getText().length()!= 0 && Util.isValidEmailId(jioEmailEditText.getText().toString().trim()))
        {
            data = new ForgetPassToken();
            data.setEmail(jioEmailEditText.getText().toString().trim());
           RequestHandler.getInstance(getApplicationContext()).handleRequest(new ForgetpasswordTokenRequest(new SuccessForgetToken(), new ErrorToken(),data));
        } else {
            jioEmailEditText.setError(Constant.EMAIL_VALIDATION);
            return;
        }

    }

    private class SuccessForgetToken implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Toast.makeText(LoginActivity.this,"Token is sent to entered email",Toast.LENGTH_SHORT).show();
            gotoForgetPassScreen();
        }
    }

    private class ErrorToken implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }*/

    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            logindetailResponse = Util.getInstance().getPojoObject(String.valueOf(response), LogindetailResponse.class);
            if (logindetailResponse.getUgsToken() != null) {
                new DBManager(LoginActivity.this).insertLoginData(logindetailResponse);
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
            }
            progressDialog.dismiss();
        }
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.INVALID_USER) {
                Util.alertDilogBox(Constant.LOGIN_VALIDATION, Constant.ALERT_TITLE, LoginActivity.this);
            } else if (error.networkResponse.statusCode == Constant.ACCOUNT_LOCK) {
                Util.alertDilogBox(Constant.EMAIL_LOCKED, Constant.ALERT_TITLE, LoginActivity.this);
            } else {
                Util.alertDilogBox(Constant.VALID_USER, Constant.ALERT_TITLE, LoginActivity.this);
            }
            progressDialog.dismiss();
        }
    }

    private void showProgressBarDialog() {
        progressDialog = ProgressDialog.show(LoginActivity.this, "", Constant.WAIT_LOADER, true);
        progressDialog.setCancelable(true);
    }

    private void gotoRegisterScreen() {
        Intent intent = new Intent(LoginActivity.this,RegistrationDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}