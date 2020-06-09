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

package com.jio.devicetracker.view.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.signinsignup.LoginActivity;
import com.jio.devicetracker.view.signinsignup.SigninSignupActivity;

/**
 * Implementation of Term and Condition Screen to show the term and condition of application.
 */
public class PrivacyPolicyActivity extends Activity implements View.OnClickListener {
    private CheckBox mCheckbox;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termcondition);
        mCheckbox = findViewById(R.id.checkbox);
        Button mAccept = findViewById(R.id.accept);
        Button mDecline = findViewById(R.id.decline);
        TextView mPolicyContent = findViewById(R.id.termConditioncontent);
        mPolicyContent.setText(Html.fromHtml(getString(R.string.privacy_policy)));
        mAccept.setOnClickListener(this);
        mDecline.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accept:
                checkBoxStatuscheck();
                break;
            case R.id.decline:
                Util.alertDilogBox(Constant.TERM_AND_CONDITION_ALERT,Constant.ALERT_TITLE,this);
                break;
            default :
                break;
        }
    }

    /**
     * Navigates to the Login activity
     */
    private void gotoLoginScreen() {
        Util.setTermconditionFlag(this,true);
        Intent intent = new Intent(this, SigninSignupActivity.class);
        startActivity(intent);
    }

    /**
     * If accepted the term and condition go the Login screen else display the Alert dialog
     */
    private void checkBoxStatuscheck(){
        if(mCheckbox.isChecked()){
            gotoLoginScreen();
        } else {
            Toast.makeText(this,Constant.TERM_AND_CONDITION_ALERT,Toast.LENGTH_SHORT).show();
        }
    }
}
