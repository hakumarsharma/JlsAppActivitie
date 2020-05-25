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

package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Util;

public class CreateGroupActivity extends AppCompatActivity {

    private Button createGroupNameButton;
    private EditText trackeeNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initUI();
        changeButtonColorOnDataEntry();
    }

    /**
     * Initialize UI component
     */
    private void initUI() {
        TextView chooseGroupTextView = findViewById(R.id.createGroupTextView);
        chooseGroupTextView.setTypeface(Util.mTypeface(this, 5));
        trackeeNameEditText = findViewById(R.id.createGroupEditText);
        trackeeNameEditText.setTypeface(Util.mTypeface(this, 5));
        createGroupNameButton = findViewById(R.id.createGroupName);
        createGroupNameButton.setTypeface(Util.mTypeface(this, 5));
    }

    /**
     * Change the button color when user enter trackee name
     */
    private void changeButtonColorOnDataEntry() {
        trackeeNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createGroupNameButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                createGroupNameButton.setTextColor(Color.WHITE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                createGroupNameButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                createGroupNameButton.setTextColor(Color.WHITE);
            }
        });
    }
}
