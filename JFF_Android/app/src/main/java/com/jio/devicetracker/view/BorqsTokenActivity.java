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

package com.jio.devicetracker.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.RegisterData;
import com.jio.devicetracker.database.pojo.VerifyTokenData;
import com.jio.devicetracker.database.pojo.request.TokenVerifyRequest;
import com.jio.devicetracker.database.pojo.response.GenerateTokenResponse;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

/**
 * Implementation of Registration Token screen to verify the Token.
 */
public class BorqsTokenActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {

    private Button verify = null;
    private static EditText phoneToken = null;
    private String token;
    private String countryCode;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borqs_otp);
        TextView title = findViewById(R.id.toolbar_title);
        Intent intent = getIntent();
        countryCode = intent.getStringExtra("countryCode");
        phoneNumber = intent.getStringExtra("phoneNumber");
        title.setText(Constant.BORQS_TOKEN_ACTIVITY_TITLE);
        phoneToken = findViewById(R.id.token);
        verify = findViewById(R.id.verify);
        verify.setOnClickListener(this);
        MessageListener messageListener = new BorqsTokenActivity();
        MessageReceiver.bindListener(messageListener);
        changeButtonColor();
    }

    /**
     * Change the Button color when we enter token in edittext
     */
    private void changeButtonColor() {
        phoneToken.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused Method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!phoneToken.getText().toString().equals("")) {
                    verify.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector,null));
                    verify.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String token = phoneToken.getText().toString();
                if (token.isEmpty()) {
                    verify.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector,null));
                    verify.setTextColor(Color.WHITE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(R.id.verify == v.getId()){
            validation();
        }
    }

    /**
     * Validation of token and make verify Token API call
     */
    private void validation() {
        if(phoneToken.getText().toString().equals("")){
            phoneToken.setError(Constant.INVALID_TOKEN_MSG);
            return;
        }
        token = phoneToken.getText().toString();
        makeVerifyTokenAPICall(token);
    }

    /**
     * Verify Token API call
     * @param token
     */
    private void makeVerifyTokenAPICall(String token) {
        VerifyTokenData verifyTokenData = new VerifyTokenData();
        verifyTokenData.setToken(token);
        verifyTokenData.setType(Constant.REGISTRATION);
        verifyTokenData.setPhone(phoneNumber);
        verifyTokenData.setPhoneCountryCode(countryCode);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new TokenVerifyRequest(new SuccessListener(), new ErrorListener(), verifyTokenData));
    }

    /**
     * Verify Token API Success Listener
     */
    private class SuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GenerateTokenResponse generateTokenResponse = Util.getInstance().getPojoObject(String.valueOf(response), GenerateTokenResponse.class);
            if(generateTokenResponse.getCode() == Constant.SUCCESS_CODE_200 && generateTokenResponse.getMessage().equalsIgnoreCase(Constant.TOKEN_VERIFIED_SUCCESS)) {
                Toast.makeText(BorqsTokenActivity.this, Constant.TOKEN_VERIFIED_SUCCESS, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BorqsTokenActivity.this, RegistrationActivity.class);
                intent.putExtra("countryCode", countryCode);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        }
    }

    /**
     * Verify Token API Error Listener
     */
    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), Constant.TOKEN_VERIFICATION_FAILED, Toast.LENGTH_SHORT).show();
            goToLoginActivity();
        }
    }

    private void goToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * To receive token
     * @param message
     * @param phoneNum
     */
    public void messageReceived(String message, String phoneNum) {
        if(message.contains(Constant.TOKEN_SMS) && phoneToken != null) {
            String[] splitMessage = message.split(":");
            phoneToken.setText(splitMessage[1].substring(1, 6));
        }
    }
}
