/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.

 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.

 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SkipLogin extends AppCompatActivity {
    boolean m_isChecked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skip_login);

        final CheckBox loginCheck = (CheckBox) findViewById(R.id.skip_login_check_box);
        loginCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m_isChecked=isChecked;
            }
        });

        Button skipLoginBtnSendOtp = (Button) findViewById(R.id.skip_login_btn_send_otp);
        skipLoginBtnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(m_isChecked){
                   Intent i = new Intent(v.getContext(), JioAddFinder.class);
                   startActivity(i);
               }else{
                   Intent i = new Intent(v.getContext(), OtpRequest.class);
                   startActivity(i);
               }
            }
        });
    }
}
