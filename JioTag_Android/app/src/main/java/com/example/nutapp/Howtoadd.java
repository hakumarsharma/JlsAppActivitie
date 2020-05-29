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
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;



public class Howtoadd extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_toadd);
        setToolbarIconTitle();


    }

    public void setToolbarIconTitle()
    {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText("How to add");
        title.setTypeface(JioUtils.mTypeface(this,5));
        Button backIcn = findViewById(R.id.back);
        backIcn.setVisibility(View.VISIBLE);
        backIcn.setOnClickListener(this);
        Button homeicn = findViewById(R.id.home);
        homeicn.setVisibility(View.VISIBLE);
        homeicn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.back:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.home:
                Intent intentHome = new Intent();
                setResult(JioUtils.HOME_KEY, intentHome);
                finish();
                break;

            default:
                break;
        }
    }
}
