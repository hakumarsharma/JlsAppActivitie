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
//    public static EditText phoneOTP = null;
    private String emailOtp = "";
    RegisterData registerData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borqs_otp);

        TextView title = findViewById(R.id.toolbar_title);
        title.setText("OTP Verification");
        emailOTP = findViewById(R.id.emailOTP);
       /* phoneOTP = findViewById(R.id.phoneOTP);
        phoneOTP.setText(DashboardActivity.otpNumber);*/
        verify = findViewById(R.id.verify);
        verify.setOnClickListener(this);
        getAdminDetail();

        emailOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused Method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!emailOTP.getText().toString().equals("")) {
                    verify.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.login_selector,null));
                    verify.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailId = emailOTP.getText().toString();
                if (emailId.isEmpty()) {
                    verify.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.selector,null));
                    verify.setTextColor(Color.WHITE);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verify:
                validation();
               // verifyPhoneOTP();
               // verifyEmailOTP();
                break;
            default:
                break;
        }
    }

    private void validation() {
        if(emailOTP.getText().toString().equals("")){
            emailOTP.setError("Please enter the email otp");
            return;
        }

       /* if(phoneOTP.getText().toString().equals(""))
        {
            phoneOTP.setError("Please enter the phone otp");
            return;
        }*/

        verifyPhoneOTP();
    }

    private void getAdminDetail() {
        DBManager dbManager = new DBManager(getApplicationContext());
        registerData = dbManager.getAdminRegistrationDetail();
    }

    private void verifyPhoneOTP() {
        /*String phoneOtp = phoneOTP.getText().toString();
        if (phoneOtp.equalsIgnoreCase(RegistrationDetailActivity.randomNumber)) {
            Log.d("TAG", "OTP is verified");*/

            verifyEmailOTP();
      /*  } else {
            Log.d("TAG", "OTP is not verified");
            Util.alertDilogBox("Please enter the correct OTP!", "OTP verification", this);
        }*/
    }

    private void verifyEmailOTP() {
        emailOtp = emailOTP.getText().toString();
        makeEmailVerifyAPICall(emailOtp);
    }

    private void makeEmailVerifyAPICall(String emailOTP) {
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
                Toast.makeText(getApplicationContext(), "Verification failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "Verification failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private class RegistrationTokenSuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            FMSRegistrationTokenResponse fmsRegistrationTokenResponse = Util.getInstance().getPojoObject(String.valueOf(response), FMSRegistrationTokenResponse.class);
            if(fmsRegistrationTokenResponse.getCode() == 200) {
                goToLoginActivity();
                Toast.makeText(getApplicationContext(), "Registration successfull!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RegistrationTokenErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "Registration is failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

}
