// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.jio.devicetracker.R;
import com.jio.devicetracker.jiotoken.JioUtilToken;
import com.jio.devicetracker.network.MQTTManager;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.mqttclient.JiotMqttClient;

import java.util.List;

public class RegistrationActivity extends Activity implements View.OnClickListener {

    private Button mRegistration;
    private EditText mJionmber;
    private EditText mName;
    private List<SubscriptionInfo> subscriptionInfos;
    public static boolean isFMSFlow = false;
    public static String phoneNumber = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.REGISTRATION_TITLE);

        mJionmber = findViewById(R.id.jioNumber);
        mName = findViewById(R.id.name);
        mRegistration = findViewById(R.id.registration);
        mRegistration.setOnClickListener(this);
        mJionmber.setOnClickListener(this);
        mJionmber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRegistration.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.login_selector,null));
                mRegistration.setTextColor(Color.WHITE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = mJionmber.getText().toString();
                if (number.isEmpty()) {
                    mRegistration.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.selector,null));
                    mRegistration.setTextColor(Color.WHITE);
                } else {
                    mJionmber.setError(null);
                }
            }
        });

        requestPermission();

      /*  mJionmber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideSoftKeyboard(RegistrationActivity.this);
            }
        });
*/
    }

    // Request for SMS and Phone Permissions
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.READ_PHONE_STATE}, 100);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (100 == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
            checkJioSIMSlot1();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.registration:
                validateNumber();
                makeMQTTConnection();
                break;

            /*case R.id.borqs:
                gotoLoginScreen();
                break;*/

          /*  case R.id.jioNumber:
                showDialog(subscriptionInfos);*/
          default:
              break;

        }
    }

    private void makeMQTTConnection() {
        MQTTManager mqttManager = new MQTTManager();
        JiotMqttClient jiotMqttClient = mqttManager.getMQTTClient(getApplicationContext());
        jiotMqttClient.connect();
    }

    private void validateNumber() {
        if (mName.getText().toString().equals("")) {
            mName.setError(Constant.NAME_VALIDATION);
        } else if (mJionmber.getText().toString().equals("")) {
            mJionmber.setError(Constant.PHONE_VALIDATION);
        } else {
            //isFMSFlow = true;
            getssoToken();
        }
    }

    private void getssoToken() {
        boolean isAvailable = Util.isMobileNetworkAvailable(RegistrationActivity.this);
        if (isAvailable) {
            checkJiooperator();

        } else {
            Util.alertDilogBox(Constant.MOBILE_NETWORKCHECK, Constant.ALERT_TITLE, this);
        }
    }

    private void checkJiooperator() {
        phoneNumber = mJionmber.getText().toString();
        String carierName = subscriptionInfos.get(0).getCarrierName().toString();
        String number = subscriptionInfos.get(0).getNumber();

        if(number != null
                && (number.equals(phoneNumber) || number.equals("91" + phoneNumber))) {
            if (carierName.contains("Jio"))
            {
                JioUtilToken.getSSOIdmaToken(RegistrationActivity.this);
                //mDbManager.insertAdminData(mName.getText().toString(), mJionmber.getText().toString());
                gotoDashBoardActivity();
            } else {
                Util.alertDilogBox(Constant.NUMBER_VALIDATION, Constant.ALERT_TITLE, this);
            }
        } else if(subscriptionInfos.size()==2 && subscriptionInfos.get(1).getNumber().toString() != null ){
             if(subscriptionInfos.get(1).getNumber().toString().equals("91"+phoneNumber)|| subscriptionInfos.get(1).getNumber().toString().equals(phoneNumber)) {
                 Util.alertDilogBox(Constant.NUMBER_VALIDATION, Constant.ALERT_TITLE, this);
             } else {
                 Util.alertDilogBox(Constant.DEVICE_JIONUMBER,Constant.ALERT_TITLE,this);
             }
        } else {
            Util.alertDilogBox(Constant.DEVICE_JIONUMBER,Constant.ALERT_TITLE,this);

        }

    }

    private void gotoDashBoardActivity() {
        Intent intent = new Intent(RegistrationActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

   /* public void showDialog(List<SubscriptionInfo> list) {
        final Dialog dialog = new Dialog(RegistrationActivity.this);
        dialog.setContentView(R.layout.number_display_dialog);
        dialog.setTitle("Title...");
        dialog.getWindow().setLayout(1000, 400);


        // set the custom dialog components - text, image and button
        final TextView sim1 = (TextView) dialog.findViewById(R.id.sim1Number);
        TextView sim2 = (TextView) dialog.findViewById(R.id.sim1Number);

        if (list.size() == 2) {
            sim1.setText(list.get(0).getNumber());

            sim2.setText(list.get(1).getNumber());
        } else {
            sim1.setText(list.get(0).getNumber());
        }


        sim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJionmber.setText(sim1.getText().toString());

                dialog.dismiss();
            }
        });

        sim2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJionmber.setText(sim2.getText().toString());

                dialog.dismiss();
            }
        });

        dialog.show();

    }
*/
    public void checkJioSIMSlot1()
    {
        if(subscriptionInfos != null) {
            String carrierNameSlot1 = subscriptionInfos.get(0).getCarrierName().toString();
            if (!carrierNameSlot1.contains("Jio")) {
                Util.alertDilogBox(Constant.SIM_VALIDATION, Constant.ALERT_TITLE, this);
            } else {
                mJionmber.setText(subscriptionInfos.get(0).getNumber().toString());
            }
        }
    }

}