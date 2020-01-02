package com.jio.devicetracker.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.RegisterData;
import com.jio.devicetracker.database.pojo.request.FMSRegistrationToken;
import com.jio.devicetracker.database.pojo.request.FMSRegistrationToken.Token;
import com.jio.devicetracker.database.pojo.request.FMSRegistrationTokenRequest;
import com.jio.devicetracker.database.pojo.request.FMSVerifyToken;
import com.jio.devicetracker.database.pojo.request.FMSVerifyTokenRequest;
import com.jio.devicetracker.database.pojo.response.FMSRegistrationTokenResponse;
import com.jio.devicetracker.database.pojo.response.FMSVerifyTokenResponse;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Util;

public class BorqsOTPActivity extends AppCompatActivity implements View.OnClickListener {

    private Button verify = null;
    private EditText emailOTP = null;
    private EditText phoneOTP = null;
    private Util util = null;
    private String emailOtp = "";
    RegisterData registerData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borqs_otp);
        emailOTP = findViewById(R.id.emailOTP);
        phoneOTP = findViewById(R.id.phoneOTP);
        verify = findViewById(R.id.verify);
        verify.setOnClickListener(this);
        util = Util.getInstance();
        getAdminDetail();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verify:
                verifyPhoneOTP();
                verifyEmailOTP();
                break;
            default:
                break;
        }
    }

    private void getAdminDetail() {
        DBManager dbManager = new DBManager(getApplicationContext());
        registerData = dbManager.getAdminRegistrationDetail();
    }

    private void verifyPhoneOTP() {
        String phoneOtp = phoneOTP.getText().toString();
        if (phoneOtp.equalsIgnoreCase(RegistrationDetailActivity.randomNumber)) {
            Log.d("TAG", "OTP is verified");

        } else {
            Log.d("TAG", "OTP is not verified");
            Util.alertDilogBox("Please enter the correct OTP!", "OTP verification", getApplicationContext());
        }
    }

    private void verifyEmailOTP() {
        emailOtp = emailOTP.getText().toString();
        makeEmailVerifyAPICall(emailOtp);
    }

    private void makeEmailVerifyAPICall(String emailOTP) {
        util.showProgressBarDialog(this, "Please wait loading data...");
        FMSVerifyToken fmsVerifyToken = new FMSVerifyToken();
        fmsVerifyToken.setEmail(registerData.getEmail().trim());
        fmsVerifyToken.setToken(emailOTP.trim());
        fmsVerifyToken.setType("registration");
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new FMSVerifyTokenRequest(new BorqsOTPActivity.SuccessListener(), new BorqsOTPActivity.ErrorListener(), fmsVerifyToken));
    }

    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            FMSVerifyTokenResponse fmsVerifyTokenResponse = Util.getInstance().getPojoObject(String.valueOf(response), FMSVerifyTokenResponse.class);
            if (fmsVerifyTokenResponse.getMessage().equalsIgnoreCase("Token successfully verified.")) {
                FMSRegistrationToken fmsRegistrationToken = new FMSRegistrationToken();
                fmsRegistrationToken.setEmail(registerData.getEmail().trim());
                fmsRegistrationToken.setDob(registerData.getDob());
                fmsRegistrationToken.setName(registerData.getName().trim());
                fmsRegistrationToken.setPassword(registerData.getPassword());
                fmsRegistrationToken.setPhone(registerData.getPhoneNumber().trim());
                Token token = new FMSRegistrationToken().new Token();
                token.setValue(emailOtp);
                fmsRegistrationToken.setToken(token);
                RequestHandler.getInstance(getApplicationContext()).handleRequest(new FMSRegistrationTokenRequest(new BorqsOTPActivity.RegistrationTokenSuccessListener(), new BorqsOTPActivity.RegistrationTokenErrorListener(), fmsRegistrationToken));
            }else {
                util.dismissProgressBarDialog();
                Toast.makeText(getApplicationContext(), "Verification failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            util.dismissProgressBarDialog();
            Toast.makeText(getApplicationContext(), "Verification failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private class RegistrationTokenSuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            FMSRegistrationTokenResponse fmsRegistrationTokenResponse = Util.getInstance().getPojoObject(String.valueOf(response), FMSRegistrationTokenResponse.class);
            util.dismissProgressBarDialog();
            if(fmsRegistrationTokenResponse.getCode() == 200) {
                //updateTheDatabase();
            }
            goToLoginActivity();
        }
    }

    private class RegistrationTokenErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            util.dismissProgressBarDialog();
            Toast.makeText(getApplicationContext(), "Registration is failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

}
