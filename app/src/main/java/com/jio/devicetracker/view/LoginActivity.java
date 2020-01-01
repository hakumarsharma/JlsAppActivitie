// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.Userdata;
import com.jio.devicetracker.database.pojo.request.LoginDataRequest;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String jioEmailIdText = null;
    private String jioPasswordText = null;
    private EditText jioEmailEditText = null;
    private EditText jioPasswordEditText = null;
    public static LogindetailResponse loginDetails = null;
    private ProgressDialog progressDialog = null;
    private TextView mRegisterText;
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
        mRegisterText = findViewById(R.id.registedHere);
        mRegisterText.setOnClickListener(this);

        jioEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
                if (emailId.equals("")) {
                    loginButton.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.selector,null));
                    loginButton.setTextColor(Color.WHITE);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                jioEmailIdText = jioEmailEditText.getText().toString().trim();
                jioPasswordText = jioPasswordEditText.getText().toString().trim();
                if (jioEmailEditText.length() == 0) {
                    jioEmailEditText.setError(Constant.EMAILID_VALIDATION);
                    return;
                }
                if (jioPasswordText.length() == 0) {
                    jioPasswordEditText.setError(Constant.PASSWORD_VALIDATION);
                    return;
                }

                if (jioEmailEditText.length() != 0) {
                    if (isValidEmailId(jioEmailIdText)) {
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
    // TODO move this to Util class
    public static boolean isValidEmailId(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    @Override
    public void onClick(View v) {

        gotoRegisterScreen();
    }

    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            loginDetails = Util.getInstance().getPojoObject(String.valueOf(response), LogindetailResponse.class);
            if (loginDetails.getLogindata().getUgsToken() != null) {
                Util.getInstance().setUserToken(getApplicationContext(), loginDetails.getLogindata().getUgsToken().toString());
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra("UserToken", loginDetails.getLogindata().getUgsToken());
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

    private void gotoRegisterScreen()
    {
        Intent intent = new Intent(LoginActivity.this,RegistrationDetailActivity.class);
        startActivity(intent);
    }

}