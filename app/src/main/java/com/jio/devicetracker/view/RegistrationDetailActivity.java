package com.jio.devicetracker.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.RegisterData;
import com.jio.devicetracker.database.pojo.RegisterRequestData;
import com.jio.devicetracker.database.pojo.request.LoginDataRequest;
import com.jio.devicetracker.database.pojo.request.RegistrationTokenrequest;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.Calendar;

public class RegistrationDetailActivity extends Activity implements View.OnClickListener {

    private EditText mName,mEmail,mPhone,mDob,mPass,mRepass;
    private Button mRegister;
    private int month,year,day;
    private int DATE_PICKER_ID = 100;
    private RegisterRequestData data = null;
    private DBManager mDbmanager;

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



    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.register:
                validate();
                break;

            case R.id.dob:
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
        if (mRepass.getText().toString().length() == 0 || (! mRepass.getText().toString().equals(mPass.getText().toString()))) {
            mRepass.setError("Password cannot be left blank.");
            return;
        }

        getServicecall();

    }

    private void getServicecall() {
        data = new RegisterRequestData();
        data.setEmail(mEmail.getText().toString());
        data.setType("registration");
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new RegistrationTokenrequest(new SuccessListener(), new RegistrationDetailActivity.ErrorListener(), data));
    }

    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {

            RegisterData data = new RegisterData();
            data.setName(mName.getText().toString());
            data.setEmail(mEmail.getText().toString());
            data.setPhoneNumber(mPhone.getText().toString());
            data.setDob(mDob.getText().toString());
            data.setPassword(mPass.getText().toString());
            mDbmanager.insertAdminData(data);

        }
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }


    private void SelectDate() {

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog   mDatePicker =new DatePickerDialog(RegistrationDetailActivity.this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
            {
                mDob.setText(new StringBuilder().append(selectedyear).append("-").append(selectedmonth+1).append("-").append(selectedday));
                int month_k=selectedmonth+1;

            }
        },year, month, day);
        mDatePicker.setTitle("Please select date");
        // TODO Hide Future Date Here
        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

        // TODO Hide Past Date Here
        //  mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        mDatePicker.show();
    }

}
