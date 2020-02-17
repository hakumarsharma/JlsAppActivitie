// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.view;

import android.app.Activity;
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

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.ChangePassworddata;
import com.jio.devicetracker.database.pojo.request.ChangepasswordRequest;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

/**
 * Implementation of ChangePassword Screen to reset the admin's password.
 */
public class ForgotPasswordActivity extends Activity implements View.OnClickListener {

    private EditText mEmail;
    private EditText mPass;
    private EditText mConfirmpass;
    private EditText mToken;
    private Button mSubmit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        mEmail = findViewById(R.id.email);
        mPass = findViewById(R.id.newPass);
        mConfirmpass = findViewById(R.id.confirmpass);
        mToken = findViewById(R.id.emailToken);
        mSubmit = findViewById(R.id.submit);
        mSubmit.setOnClickListener(this);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.RESET_TITLE);

        Intent intent = getIntent();
        String email = intent.getStringExtra(Constant.FORGOT_EMAIL);
        mEmail.setText(email);
        mEmail.setEnabled(false);

        mToken.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mToken.getText().toString().equals("")) {
                    mSubmit.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.login_selector,null));
                    mSubmit.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String token = mToken.getText().toString();
                if (token.isEmpty()) {
                    mSubmit.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.selector,null));
                    mSubmit.setTextColor(Color.WHITE);
                }
            }
        });



    }

    @Override
    public void onClick(View v) {
        validation();

    }


    private void validation() {

        if (mEmail.getText().toString().length() == 0) {
            mEmail.setError(Constant.VALID_EMAIL_ID);
            return;
        }

        if (mToken.getText().toString().length() == 0) {
            mToken.setError(Constant.ENTER_TOKEN);
            return;
        }

        if (mPass.getText().toString().length() == 0) {
            mPass.setError(Constant.PASSWORD_EMPTY);
            return;
        }
        if (mConfirmpass.getText().toString().length() == 0
                || !mConfirmpass.getText().toString().equals(mPass.getText().toString())) {
            mConfirmpass.setError(Constant.PASSWORD_NOT_MATCHED);
            return;
        }


        if(mPass.getText().toString().length() != 0 && !Util.isValidPassword(mPass.getText().toString()))
        {
            mPass.setError(Constant.PASSWORD_VALIDATION2);
            return;
        }

        Util.getInstance().showProgressBarDialog(this);
        changepasswordApicall();

    }
    private void changepasswordApicall() {
        ChangePassworddata data = new ChangePassworddata();
        ChangePassworddata.EmailToken token = data.new EmailToken();
        token.setTokenValue(mToken.getText().toString().trim());
        data.setmToken(token);
        data.setEmialId(mEmail.getText().toString().trim());
        data.setNewPass(mPass.getText().toString().trim());

        RequestHandler.getInstance(getApplicationContext()).handleRequest(new ChangepasswordRequest(new Success(), new Error(),data));
    }



    private class Success implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Toast.makeText(ForgotPasswordActivity.this,Constant.PASSWORD_RESET_SUCCESS_MSG, Toast.LENGTH_SHORT).show();
            Util.progressDialog.dismiss();
            gotoLoginScreen();
        }
    }

    private void gotoLoginScreen() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private class Error implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if(error.networkResponse.statusCode == Constant.STATUS_CODE_404){

                Util.alertDilogBox(Constant.NOT_REGISTERED_MSG,Constant.ALERT_TITLE,ForgotPasswordActivity.this);
            } else if(error.networkResponse.statusCode == Constant.STATUS_CODE_417){
                Util.alertDilogBox(Constant.INVALID_TOKEN_MSG,Constant.ALERT_TITLE,ForgotPasswordActivity.this);
            } else {
                Toast.makeText(ForgotPasswordActivity.this,Constant.RESET_PASSWORD_FAILED,Toast.LENGTH_SHORT).show();
            }
            Util.progressDialog.dismiss();
        }
    }


}
