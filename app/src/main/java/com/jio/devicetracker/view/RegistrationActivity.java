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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.jiotoken.JioUtilsToken;
import com.jio.devicetracker.jiotoken.JiotokenHandler;
import com.jio.devicetracker.util.AESEncrypt;
import com.jio.devicetracker.util.Util;

import java.util.List;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class RegistrationActivity extends Activity implements View.OnClickListener {

    private Button mRegistration, mBorqs;
    private EditText mJionmber, mName;
    private AESEncrypt mAesEncryption = null;
    private List<SubscriptionInfo> subscriptionInfos;
    private DBManager mDbManager;
    public static boolean isFMSFlow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText("Registration");

        mJionmber = findViewById(R.id.jioNumber);
        mName = findViewById(R.id.name);
        mRegistration = findViewById(R.id.registration);
        mBorqs = findViewById(R.id.borqs);
        mDbManager = new DBManager(this);
        mRegistration.setOnClickListener(this);
        mBorqs.setOnClickListener(this);
        mJionmber.setOnClickListener(this);
        try {
            mAesEncryption = new AESEncrypt(Util.getProperty("key", this));
        } catch (Exception e) {
            e.printStackTrace();
        }




        mJionmber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRegistration.setBackground(getResources().getDrawable(R.drawable.login_selector));
                mRegistration.setTextColor(Color.WHITE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = mJionmber.getText().toString();
                if (number.equals("")) {
                    mRegistration.setBackground(getResources().getDrawable(R.drawable.selector));
                    mRegistration.setTextColor(Color.WHITE);
                } else {
                    mJionmber.setError(null);
                }
            }
        });

        requestPermission();

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                checkJioSIMSlot1();
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.registration:
                validateNumber();
                break;

            case R.id.borqs:
                gotoLoginScreen();
                break;

          /*  case R.id.jioNumber:
                showDialog(subscriptionInfos);*/

        }

    }

    private void validateNumber() {
        if (mName.getText().toString().equals("")) {
            mName.setError("Name can't left empty");
        } else if (mJionmber.getText().toString().equals("")) {
            mJionmber.setError("Phone number cannot be left empty!");
        } else {
            isFMSFlow = true;
            getssoToken();
        }
    }

    private void getssoToken() {

        boolean isAvailable = Util.isMobileNetworkAvailable(RegistrationActivity.this);
        if (isAvailable) {
            checkJiooperator();

        } else {
            Util.alertDilogBox("Please use your mobile data", "Jio Alert", this);
        }
    }

    private void checkJiooperator() {
        String phoneNumber = mJionmber.getText().toString();
        String carierName = subscriptionInfos.get(0).getCarrierName().toString();
        String number = subscriptionInfos.get(0).getNumber();
        if(number != null && number.equals(phoneNumber) || number !=null && number.equals("91"+ phoneNumber))
        {
            if (carierName.contains("Jio"))
            {
                JioUtilsToken.getSSOIdmaToken(RegistrationActivity.this);
                mDbManager.insertAdminData(mName.getText().toString(), mJionmber.getText().toString());
                gotoDashBoardActivity();
            } else {
                Util.alertDilogBox("Please use Jio number", "Jio Alert", this);
            }
        } else {
            Util.alertDilogBox("Entered phone number should be in SIM slot 1","Jio Alert",this);
        }

        /*for (int i = 0; i < subscriptionInfos.size(); i++) {
            String carrierName = subscriptionInfos.get(i).getCarrierName().toString();
            if (subscriptionInfos.get(i).getNumber() != null && subscriptionInfos.get(i).getNumber().equals(phoneNumber)) {
                if (carrierName.contains("Jio")) {
                    JioUtilsToken.getSSOIdmaToken(RegistrationActivity.this);
                    mDbManager.insertAdminData(mName.getText().toString(), mJionmber.getText().toString());
                    gotoDashBoardActivity();
                    return;
                } else {
                    Util.alertDilogBox("Please use Jio number", "Jio Alert", this);
                }
            }
        }*/
    }

    private void gotoLoginScreen() {
        JiotokenHandler.ssoToken = null;
        isFMSFlow = false;
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void gotoDashBoardActivity() {
        Intent intent = new Intent(RegistrationActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

    public void showDialog(List<SubscriptionInfo> list) {
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
                mJionmber.setFocusable(false);
                dialog.dismiss();
            }
        });

        sim2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJionmber.setText(sim2.getText().toString());
                mJionmber.setFocusable(false);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void checkJioSIMSlot1()
    {
        if(subscriptionInfos != null) {
            String carrierNameSlot1 = subscriptionInfos.get(0).getCarrierName().toString();
            if (!carrierNameSlot1.contains("Jio")) {
                Util.alertDilogBox("Please use Jio number in SIM slot 1", "Jio Alert", this);
            } else {
                mJionmber.setText(subscriptionInfos.get(0).getNumber().toString());
            }
        }
    }

}