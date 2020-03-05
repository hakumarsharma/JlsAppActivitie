// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.view;

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

    private void gotoLoginScreen() {

        Util.setTermconditionFlag(this,true);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void checkBoxStatuscheck(){

        if(mCheckbox.isChecked()){
            gotoLoginScreen();
        } else {
            Toast.makeText(this,Constant.TERM_AND_CONDITION_ALERT,Toast.LENGTH_SHORT).show();
        }
    }
}
