package com.jio.devicetracker.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.ForgetPassToken;
import com.jio.devicetracker.database.pojo.request.ForgetpasswordTokenRequest;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class ForgetpassTokenActivity extends Activity implements View.OnClickListener {
    private Button mVerify;
    private EditText mEmail;
    private ForgetPassToken data;
    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_email_forgetpass);
        mVerify = findViewById(R.id.tokenVerify);
        mEmail = findViewById(R.id.email);
        mVerify.setOnClickListener(this);

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mEmail.getText().toString().equals("")) {
                    mVerify.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.login_selector,null));
                    mVerify.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailId = mEmail.getText().toString();
                if (emailId.equals("")) {
                    mVerify.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.selector,null));
                    mVerify.setTextColor(Color.WHITE);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        validation();
    }

    private void validation() {

        if(mEmail.getText().length()!= 0 && Util.isValidEmailId(mEmail.getText().toString().trim()))
        {
            data = new ForgetPassToken();
            data.setEmail(mEmail.getText().toString().trim());
            showProgressBarDialog();
            RequestHandler.getInstance(getApplicationContext()).handleRequest(new ForgetpasswordTokenRequest(new SuccessForgetToken(), new ErrorToken(),data));
        } else {
            mEmail.setError(Constant.EMAIL_VALIDATION);
            return;
        }
    }

    private class SuccessForgetToken implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            progressDialog.dismiss();
            Toast.makeText(ForgetpassTokenActivity.this,"Token is sent to entered email",Toast.LENGTH_SHORT).show();
            gotoForgetPassScreen();
        }
    }

    private class ErrorToken implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

            Toast.makeText(ForgetpassTokenActivity.this,"Token api failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void gotoForgetPassScreen() {

        Intent intent = new Intent(ForgetpassTokenActivity.this,ForgotPasswordActivity.class);
        intent.putExtra("Email",mEmail.getText().toString().trim());
        startActivity(intent);
    }

    private void showProgressBarDialog() {
        progressDialog = ProgressDialog.show(ForgetpassTokenActivity.this, "", Constant.WAIT_LOADER, true);
        progressDialog.setCancelable(true);
    }
}
