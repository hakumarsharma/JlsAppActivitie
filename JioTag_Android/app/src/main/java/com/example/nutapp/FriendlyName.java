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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FriendlyName extends AppCompatActivity {
    String dev_address;
    String defVal = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendly_name);

        Intent receiveIntent = this.getIntent();
        dev_address = receiveIntent.getStringExtra("ADDR_DEV");
        Log.d("INTENTADDR",dev_address);

        final EditText filledNameValue = (EditText) findViewById(R.id.filled_name);
        Button friendlyNameKey = (Button) findViewById(R.id.button_key);
        friendlyNameKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filledNameValue.setText(getResources().getString(R.string.friendly_key));
            }
        });
        Button friendlyNameWallet = (Button) findViewById(R.id.button_wallet);
        friendlyNameWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filledNameValue.setText(getResources().getString(R.string.friendly_wallet));
            }
        });
        Button friendlyNameLaptop = (Button) findViewById(R.id.button_laptops);
        friendlyNameLaptop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filledNameValue.setText(getResources().getString(R.string.friendly_laptops));
            }
        });
        Button friendlyNameSuitcase = (Button) findViewById(R.id.button_suitcase);
        friendlyNameSuitcase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filledNameValue.setText(getResources().getString(R.string.friendly_suitcase));
            }
        });
        Button friendlyNameSatchel = (Button) findViewById(R.id.button_satchel);
        friendlyNameSatchel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filledNameValue.setText(getResources().getString(R.string.friendly_satchel));
            }
        });
        Button friendlyNameOthers = (Button) findViewById(R.id.button_others);
        friendlyNameOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filledNameValue.setText(getResources().getString(R.string.friendly_others));
            }
        });

        Button dialogCancel = (Button) findViewById(R.id.button_cancel);
        dialogCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button dialogFinish = (Button) findViewById(R.id.button_finish);
        dialogFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("ADDR_DEV", dev_address);
                intent.putExtra("ADDR_DEV_NAME", filledNameValue.getText().toString());
                Log.d("FRADD",dev_address);
                Log.d("FRNAME",filledNameValue.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
