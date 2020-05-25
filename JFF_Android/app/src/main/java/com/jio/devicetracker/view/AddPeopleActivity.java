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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.AddPersonListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddPeopleActivity extends AppCompatActivity implements View.OnClickListener {

    private static RecyclerView contactsListView;
    private static AddPersonListAdapter mAdapter;
    private List<String> listOfContacts;
    private Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpeople);
        initializeData();
    }

    private void initializeData() {

        toolbar = findViewById(R.id.addPeopleToolbar);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.Add_People);

        toolbar.setBackgroundColor(getResources().getColor(R.color.cardviewlayout_device_background_color));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);

        contactsListView = findViewById(R.id.contactsListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        contactsListView.setLayoutManager(linearLayoutManager);
        listOfContacts = new ArrayList();
        listOfContacts.add("Sree");
        mAdapter = new AddPersonListAdapter(listOfContacts);
        contactsListView.setAdapter(mAdapter);

        TextView peopleText = findViewById(R.id.people_text);
        peopleText.setTypeface(Util.mTypeface(this,3));

        EditText nameTxt = findViewById(R.id.memberName);
        nameTxt.setTypeface(Util.mTypeface(this,5));

        EditText numberTxt = findViewById(R.id.deviceNumber);
        numberTxt.setTypeface(Util.mTypeface(this,5));

        ImageView contactBtn = toolbar.findViewById(R.id.contactAdd);
        contactBtn.setVisibility(View.VISIBLE);
        contactBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
