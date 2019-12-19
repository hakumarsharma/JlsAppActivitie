// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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


public class LoginActivity extends AppCompatActivity {

    private String jioEmailIdText = null;
    private String jioPasswordText = null;
    private EditText jioEmailEditText = null;
    private EditText jioPasswordEditText = null;
    public static LogindetailResponse loginDetails = null;
    private ProgressDialog progressDialog = null;

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

        jioEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!jioEmailEditText.getText().toString().equals("") || !jioPasswordEditText.getText().toString().equals("")) {
                    loginButton.setBackground(getResources().getDrawable(R.drawable.login_selector));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailId = jioEmailEditText.getText().toString();
                if (emailId.equals("")) {
                    loginButton.setBackground(getResources().getDrawable(R.drawable.selector));
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                jioEmailIdText = jioEmailEditText.getText().toString().trim();
                jioPasswordText = jioPasswordEditText.getText().toString().trim();
                String emailIdRegEx = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4})(\\]?)$";
                if (jioEmailEditText.length() == 0) {
                    jioEmailEditText.setError("Email id cannot be left blank.");
                    return;
                }
                if (jioPasswordText.length() == 0) {
                    jioPasswordEditText.setError("Password cannot be left blank.");
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
                        jioEmailEditText.setError("Please provide the correct Email Id!");
                        return;
                    }
                }
                showProgressBarDialog();
            }
        });
    }

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
                Util.alertDilogBox("Please enter correct email and password", "Jio Alert", LoginActivity.this);
            } else if (error.networkResponse.statusCode == Constant.ACCOUNT_LOCK) {
                Util.alertDilogBox("Account is locked", "Jio Alert", LoginActivity.this);
            } else {
                Util.alertDilogBox("Please enter valid user", "Jio Alert", LoginActivity.this);
            }
            progressDialog.dismiss();
        }
    }

    private void showProgressBarDialog() {
        progressDialog = ProgressDialog.show(LoginActivity.this, "", "Please wait login...", true);
        progressDialog.setCancelable(true);
    }

}