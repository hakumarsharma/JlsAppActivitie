package com.jio.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jio.jiotoken.JioutilsToken;
import com.jio.util.Util;

public class RegistrationActivity extends Activity implements View.OnClickListener {

    Button mRegistration,mBorqs;
    EditText mJionmber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText("Registration");

        mJionmber = findViewById(R.id.jioNumber);
        mRegistration = findViewById(R.id.registration);
        mBorqs = findViewById(R.id.borqs);

        mRegistration.setOnClickListener(this);
        mBorqs.setOnClickListener(this);

        mJionmber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRegistration.setBackground(getResources().getDrawable(R.drawable.login_selector));
            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = mJionmber.getText().toString();
                if(number.equals(""))
                {
                    mRegistration.setBackground(getResources().getDrawable(R.drawable.selector));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.registration:
                validateNumber();
                break;

            case R.id.borqs:
                gotoLoginScreen();
                break;
        }

    }

    private void validateNumber() {

        String number = mJionmber.getText().toString();
        if(number.equals("") && number.length()<10)
        {
            Util.AlertDilogBox("Please enter a valid phone number","Jio Alert",this);
        }else
        {
            getssoToken();
        }
    }

    private void getssoToken() {

        boolean isAvailable = Util.isMobileNetworkAvailable(RegistrationActivity.this);
        if(isAvailable)
        {
            JioutilsToken.getSSOIdmaToken(RegistrationActivity.this);
            Toast.makeText(this,"SSO token is generated",Toast.LENGTH_SHORT).show();

        }
        else{
            Util.AlertDilogBox("Please use your mobile data","Jio Alert",this);
        }
    }

    private void gotoLoginScreen() {

        Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}