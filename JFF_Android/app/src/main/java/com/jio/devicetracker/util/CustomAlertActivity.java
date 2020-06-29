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
package com.jio.devicetracker.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jio.devicetracker.R;

public class CustomAlertActivity extends Dialog implements View.OnClickListener {

    private Button laterBtn;
    private Button viewBtn;
    private Button okBtn;
    public Context mContext;
    public TextView message;

    public CustomAlertActivity(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_alert);
        initializeDataMember();
    }

    private void initializeDataMember() {
        TextView title = findViewById(R.id.dialogue_title);
        title.setTypeface(Util.mTypeface(mContext, 5));
        message = findViewById(R.id.dialogue_message);
        message.setText(Constant.Custom_Alert_Message);
        message.setTypeface(Util.mTypeface(mContext, 3));
        laterBtn = findViewById(R.id.btn_later);
        laterBtn.setTypeface(Util.mTypeface(mContext, 5));
        viewBtn = findViewById(R.id.btn_view);
        viewBtn.setTypeface(Util.mTypeface(mContext, 5));
        okBtn = findViewById(R.id.btn_ok);
        okBtn.setTypeface(Util.mTypeface(mContext, 5));
        okBtn.setOnClickListener(this);
    }

    // display alert with single button option
    public void alertWithOkButton(String alertMessage) {
        message.setText(alertMessage);
        okBtn.setVisibility(View.VISIBLE);
        laterBtn.setVisibility(View.INVISIBLE);
        viewBtn.setVisibility(View.INVISIBLE);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                dismiss();
                break;
            case R.id.btn_later:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

}