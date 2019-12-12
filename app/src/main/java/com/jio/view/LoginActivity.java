// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.database.pojo.Userdata;
import com.jio.database.pojo.request.LoginDataRequest;
import com.jio.database.pojo.response.LogindetailResponse;
import com.jio.jiotoken.JioutilsToken;
import com.jio.network.RequestHandler;
import com.jio.util.Util;

public class LoginActivity extends AppCompatActivity {

    private String jioEmailIdText = null;
    private String jioPasswordText = null;
    private EditText jioEmailEditText = null;
    private EditText jioPasswordEditText = null;
    public static LogindetailResponse loginDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText("Login");

        Button loginButton = findViewById(R.id.login);
        jioEmailEditText = findViewById(R.id.jioEmailId);
        jioEmailEditText.setText("shivakumar.jagalur@ril.com");
        jioPasswordEditText = findViewById(R.id.jioPassword);
        jioPasswordEditText.setText("Jio@1234");

        jioEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              loginButton.setBackground(getResources().getDrawable(R.drawable.login_selector));
            }

            @Override
            public void afterTextChanged(Editable s) {
               String emailId=jioEmailEditText.getText().toString();
               if(emailId.equals(""))
               {
                   loginButton.setBackground(getResources().getDrawable(R.drawable.selector));
               }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                jioEmailIdText = jioEmailEditText.getText().toString().trim();
                jioPasswordText = jioPasswordEditText.getText().toString().trim();
                if (jioEmailEditText.length() == 0) {
                    jioEmailEditText.setError("Email id cannot be left blank.");
                    return;
                }
                if (jioPasswordText.length() == 0) {
                    jioPasswordEditText.setError("Password cannot be left blank.");
                    return;
                }
                Userdata data = new Userdata();
                data.setEmailId(jioEmailIdText);
                data.setPassword(jioPasswordText);
                data.setType("supervisor");
                RequestHandler.getInstance(getApplicationContext()).handleRequest(new LoginDataRequest(new SuccessListener(), new ErrorListener(), data));
            }
        });
    }

    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            loginDetails = Util.getInstance().getPojoObject(String.valueOf(response), LogindetailResponse.class);
            if(loginDetails.getLogindata().getUgs_token() != null) {
                Util.getInstance().setUserToken(getApplicationContext(),loginDetails.getLogindata().getUgs_token().toString());
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra("UserToken",loginDetails.getLogindata().getUgs_token() );
                startActivity(intent);
            }
        }
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(),"Response is not coming",Toast.LENGTH_SHORT);
        }
    }
}