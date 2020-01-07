package com.jio.devicetracker.view;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.RegisterData;
import com.jio.devicetracker.database.pojo.RegisterRequestData;
import com.jio.devicetracker.database.pojo.request.LoginDataRequest;
import com.jio.devicetracker.database.pojo.request.RegistrationTokenrequest;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.jiotoken.JioUtilsToken;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.network.SendSMSTask;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

import java.util.Calendar;
import java.util.List;

public class RegistrationDetailActivity extends Activity implements View.OnClickListener {

    private EditText mName, mEmail, mPhone, mDob, mPass, mRepass;
    private Button mRegister;
    private int month, year, day;
    private int DATE_PICKER_ID = 100;
    private RegisterRequestData data = null;
    private DBManager mDbmanager;
    private Util util = null;
    public static String randomNumber = "";
    private List<SubscriptionInfo> subscriptionInfos;
    public static String phoneNumber = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration_detail);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText("Registration");
        mName = findViewById(R.id.memberName);
        mEmail = findViewById(R.id.email);
        mPhone = findViewById(R.id.deviceNumber);
        mDob = findViewById(R.id.dob);
        mPass = findViewById(R.id.password);
        mRepass = findViewById(R.id.repassword);
        mRegister = findViewById(R.id.register);
        mDbmanager = new DBManager(this);
        mRegister.setOnClickListener(this);
        mDob.setOnClickListener(this);
        util = Util.getInstance();

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mName.getText().toString().equals("")) {
                    mRegister.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.login_selector,null));
                    mRegister.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailId = mName.getText().toString();
                if (emailId.equals("")) {
                    mRegister.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.selector,null));
                    mRegister.setTextColor(Color.WHITE);
                }
            }
        });

        requestPermission();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.register:
                validate();
                break;

            case R.id.dob:
                hideKeyboard(mDob);
                SelectDate();
                break;

        }

    }

    private void validate() {

        if (mName.getText().toString().length() == 0) {
            mName.setError("Name cannot be left blank.");
            return;
        }
        if (mEmail.getText().toString().length() == 0) {
            mEmail.setError("Email cannot be left blank.");
            return;
        }

        if (mPhone.getText().toString().length() == 0) {
            mPhone.setError("Mobile number cannot be left blank.");
            return;
        }
        if (mDob.getText().toString().length() == 0) {
            mDob.setError("Date of birth cannot be left blank.");
            return;
        }
        if (mPass.getText().toString().length() == 0) {
            mPass.setError("Password cannot be left blank.");
            return;
        }
        if (mRepass.getText().toString().length() == 0 || (!mRepass.getText().toString().equals(mPass.getText().toString()))) {
            mRepass.setError("Password did not match, please try again");
            return;
        }

        if(mPhone.getText().toString().length() < 10)
        {
            mPhone.setError("Enter the valid phone number");
        }
        if (mEmail.getText().toString().length() != 0 && !Util.isValidEmailId(mEmail.getText().toString().trim())) {
            mEmail.setError(Constant.EMAIL_VALIDATION);
            return;
        }

        if(mPass.getText().toString().length() != 0 && !Util.isValidPassword(mPass.getText().toString()))
        {
            mPass.setError(Constant.PASSWORD_VALIDATION2);
            return;
        }
        boolean jioCheck = getssoToken();
        if(jioCheck)
        {
            getServicecall();
        }



    }

    private void getServicecall() {
        data = new RegisterRequestData();
        data.setEmail(mEmail.getText().toString().trim());
        data.setType("registration");
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new RegistrationTokenrequest(new SuccessListener(), new RegistrationDetailActivity.ErrorListener(), data));
    }

    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {

            RegisterData data = new RegisterData();
            data.setName(mName.getText().toString().trim());
            data.setEmail(mEmail.getText().toString().trim());
            data.setPhoneNumber(mPhone.getText().toString().trim());
            data.setDob(mDob.getText().toString().trim());
            data.setPassword(mPass.getText().toString());
            mDbmanager.insertAdminData(data);
            //sendOTP();
            goToBorqsOTPActivity();
        }
    }

    private void goToBorqsOTPActivity() {
        startActivity(new Intent(this, BorqsOTPActivity.class));
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

            if(error.networkResponse.statusCode == 409)
            {
                Util.alertDilogBox("User is already registered","Jio Alert",RegistrationDetailActivity.this);
            } else {
                Util.alertDilogBox("Register failed ,Please contact your admin","Jio Alert",RegistrationDetailActivity.this);
            }
        }
    }


    private void SelectDate() {

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog mDatePicker = new DatePickerDialog(RegistrationDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                String monthselect, dayselect;
                selectedmonth = selectedmonth + 1;
                if (selectedmonth < 10) {
                    monthselect = ("0" + selectedmonth);
                } else {
                    monthselect = String.valueOf(selectedmonth);
                }
                if (selectedday < 10) {
                    dayselect = ("0" + selectedday);
                } else {
                    dayselect = String.valueOf(selectedday);
                }
                mDob.setText(new StringBuilder().append(selectedyear).append("-").append(monthselect).append("-").append(dayselect));

            }
        }, year, month, day);
        mDatePicker.setTitle("Please select date");
        // TODO Hide Future Date Here
        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

        // TODO Hide Past Date Here
        //  mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        mDatePicker.show();
    }

    /*private void sendOTP() {
        randomNumber = util.getFourDigitRandomNumber();
        new SendSMSTask().execute(mPhone.getText().toString(), randomNumber);
    }*/

    private boolean getssoToken() {
        boolean isAvailable = Util.isMobileNetworkAvailable(RegistrationDetailActivity.this);
        if (isAvailable) {
            boolean flag = checkJiooperator();
            return flag;

        } else {
            Util.alertDilogBox(Constant.MOBILE_NETWORKCHECK, Constant.ALERT_TITLE, this);
            return false;
        }
    }

    private boolean checkJiooperator() {
        phoneNumber = mPhone.getText().toString();
        String carierName = subscriptionInfos.get(0).getCarrierName().toString();
        String number = subscriptionInfos.get(0).getNumber();

        if ((number != null && number.equals(phoneNumber)) || (number != null && number.equals("91" + phoneNumber))) {
            if (carierName.contains("Jio")) {
                JioUtilsToken.getSSOIdmaToken(RegistrationDetailActivity.this);
                //mDbManager.insertAdminData(mName.getText().toString(), mJionmber.getText().toString());
                //gotoDashBoardActivity();
                return true;
            } else {
                Util.alertDilogBox(Constant.NUMBER_VALIDATION, Constant.ALERT_TITLE, this);
                return false;
            }
        } else if (subscriptionInfos.size() == 2 && subscriptionInfos.get(1).getNumber() != null) {
            if (subscriptionInfos.get(1).getNumber().equals("91" + phoneNumber) || subscriptionInfos.get(1).getNumber().equals(phoneNumber)) {
                Util.alertDilogBox(Constant.NUMBER_VALIDATION, Constant.ALERT_TITLE, this);
                return false;
            } else {
                Util.alertDilogBox(Constant.DEVICE_JIONUMBER, Constant.ALERT_TITLE, this);
                return false;
            }
        } else {
            Util.alertDilogBox(Constant.DEVICE_JIONUMBER, Constant.ALERT_TITLE, this);
            return false;

        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // TODO change switch to if condition, if you are not handlig multiple cases
        switch (requestCode) {
            case 100:

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                //checkJioSIMSlot1();
                break;
        }
    }

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
