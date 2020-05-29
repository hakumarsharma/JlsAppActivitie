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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.nutapp.util.JioConstant;

public class ShareViewTagActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_invite);

        Button viewTagBtn = findViewById(R.id.viewtag_btn);
        viewTagBtn.setOnClickListener(this);
        setToolbarIconTitle();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,CardDetails.class);
        intent.putExtra("ShareFlag",true);
        startActivity(intent);
    }
    public void setToolbarIconTitle()
    {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(JioConstant.SHARE_INVITE_TITLE);
        title.setTypeface(JioUtils.mTypeface(this,5));
        Button backIcn = findViewById(R.id.back);
        backIcn.setVisibility(View.VISIBLE);
        Button homeicn = findViewById(R.id.home);
        homeicn.setVisibility(View.VISIBLE);
    }
}
