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

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddMemberInGroupData;
import com.jio.devicetracker.database.pojo.CreateGroupData;
import com.jio.devicetracker.database.pojo.request.AddMemberInGroupRequest;
import com.jio.devicetracker.database.pojo.request.CreateGroupRequest;
import com.jio.devicetracker.database.pojo.request.GetGroupMemberRequest;
import com.jio.devicetracker.database.pojo.response.CreateGroupResponse;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.AddPersonListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddPeopleActivity extends BaseActivity implements View.OnClickListener {

    private static RecyclerView contactsListView;
    private static AddPersonListAdapter mAdapter;
    private List<GroupMemberResponse.Data> listOfContacts;
    private Toolbar toolbar = null;
    private boolean isComingFromAddContact;
    private boolean isComingFromAddDevice;
    private boolean isComingFromGroupList;
    private boolean isComingFromContactList;
    private static String groupId;
    private EditText contactName;
    private EditText contactNumber;
    private Button   addContact;
    private DBManager mDbManager;

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

        mDbManager = new DBManager(this);

        contactsListView = findViewById(R.id.contactsListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        contactsListView.setLayoutManager(linearLayoutManager);

        TextView peopleText = findViewById(R.id.people_text);
        peopleText.setTypeface(Util.mTypeface(this,3));

        contactName = findViewById(R.id.memberName);
        contactName.setTypeface(Util.mTypeface(this,5));

        contactNumber = findViewById(R.id.deviceNumber);
        contactNumber.setTypeface(Util.mTypeface(this,5));

        addContact = findViewById(R.id.add_contact);
        addContact.setTypeface(Util.mTypeface(this,5));
        addContact.setOnClickListener(this);

        ImageView contactBtn = toolbar.findViewById(R.id.contactAdd);
        contactBtn.setVisibility(View.VISIBLE);
        contactBtn.setOnClickListener(this);

        changeButtonColorOnDataEntry();

        getDataFromIntent(title);
    }

    // Intent data to check whether use has come from group or individual
    private void getDataFromIntent(TextView title){
        Intent intent = getIntent();
        String qrValue = intent.getStringExtra(Constant.QR_CODE_VALUE);
        isComingFromContactList = intent.getBooleanExtra(Constant.IS_COMING_FROM_CONTACT_LIST, false);
        isComingFromAddDevice = intent.getBooleanExtra(Constant.IS_COMING_FROM_ADD_DEVICE, false);
        isComingFromAddContact = intent.getBooleanExtra(Constant.IS_COMING_FROM_ADD_CONTACT, false);
        isComingFromGroupList = intent.getBooleanExtra(Constant.IS_COMING_FROM_GROUP_LIST, false);
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        String groupName = intent.getStringExtra(Constant.GROUP_NAME);

        if(groupId != null && !groupId.isEmpty()){
            this.createdGroupId = groupId;
            this.isFromCreateGroup = false;
            this.isGroupMember = true;
            getAllForOneGroupAPICall();
        }

        // set group name as title
        if(groupName != null && !groupName.isEmpty()){
            title.setText(groupName);
        }

    }

    /**
     * To save data before going into the other activity
     *
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Constant.GROUP_ID, groupId);
        savedInstanceState.putString(Constant.USER_ID, Util.userId);
        savedInstanceState.putBoolean(Constant.IS_COMING_FROM_ADD_CONTACT, isComingFromAddContact);
        savedInstanceState.putBoolean(Constant.IS_COMING_FROM_ADD_DEVICE, isComingFromAddDevice);
        savedInstanceState.putBoolean(Constant.IS_COMING_FROM_GROUP_LIST, isComingFromGroupList);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        groupId = savedInstanceState.getString(Constant.GROUP_ID);
        isComingFromAddDevice = savedInstanceState.getBoolean(Constant.IS_COMING_FROM_ADD_DEVICE, false);
        isComingFromAddContact = savedInstanceState.getBoolean(Constant.IS_COMING_FROM_ADD_CONTACT, false);
        isComingFromGroupList = savedInstanceState.getBoolean(Constant.IS_COMING_FROM_GROUP_LIST, false);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, DashboardActivity.class));
    }


    // Change the button color when data is field
    private void changeButtonColorOnDataEntry() {
        contactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                setEditTextBottomLineColor(contactName);
            }
        });

        contactNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String name = contactName.getText().toString();
                String number = contactNumber.getText().toString();
                setEditTextBottomLineColor(contactNumber);
                if (!"".equalsIgnoreCase(name) && !"".equalsIgnoreCase(number)) {
                    addContact.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                    addContact.setTextColor(Color.WHITE);
                }else {
                    addContact.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
                    addContact.setTextColor(Color.WHITE);
                }
            }
        });

    }
    // change edit text bottom line color based on text entry
    private void setEditTextBottomLineColor(EditText contactEditText) {
        if (!"".equalsIgnoreCase(contactEditText.getText().toString())) {
            contactEditText.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
        } else {
            contactEditText.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.hintColor));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contactAdd) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, 1);
        }else if (v.getId() == R.id.add_contact){
            if (!Util.isValidMobileNumber(contactNumber.getText().toString())) {
                contactNumber.setError(Constant.MOBILENUMBER_VALIDATION);
                return;
            }
            if(groupId == null || groupId.isEmpty()) {
                this.memberName = contactName.getText().toString();
                this.memberNumber = contactNumber.getText().toString();
                this.isFromCreateGroup = false;
                createGroupAndAddContactAPICall(Constant.INDIVIDUAL_USER_GROUP_NAME);
            }else {
                this.createdGroupId = groupId;
                this.memberName = contactName.getText().toString();
                this.memberNumber = contactNumber.getText().toString();
                this.isFromCreateGroup = false;
                this.isGroupMember = true;
                addMemberInGroupAPICall();
            }
        }

    }

    public void getAllMembers(List<GroupMemberResponse.Data> memberList){
//        if(contactName != null){
//            hideKeyboard();
//            setEditTextValues();
//        }
        if(memberList != null) {
            listOfContacts = new ArrayList();
            listOfContacts.addAll(memberList);
            mAdapter = new AddPersonListAdapter(listOfContacts);
            contactsListView.setAdapter(mAdapter);
        }
    }

    private void setEditTextValues(){
        contactName.getText().clear();
        contactNumber.getText().clear();
        addContact.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
        addContact.setTextColor(Color.WHITE);
        contactNumber.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.hintColor));
        contactName.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.hintColor));
    }
    // To dismiss Keyboard
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Call back method of QR scanner button available on toolbar
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                ContentResolver resolver = getContentResolver();
                Cursor cursor = resolver.query(contactData, null, null, null, null);
                cursor.moveToFirst();
                String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if ("1".equalsIgnoreCase(hasPhone)) {
                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, null);
                    phones.moveToFirst();
                    contactNumber.setText(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    contactName.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                }
                Intent intent = new Intent(this, AddPeopleActivity.class);
                intent.putExtra(Constant.IS_COMING_FROM_CONTACT_LIST, true);
                intent.putExtra(Constant.IS_COMING_FROM_ADD_CONTACT, isComingFromAddContact);
                intent.putExtra(Constant.IS_COMING_FROM_ADD_DEVICE, isComingFromAddDevice);
                intent.putExtra(Constant.IS_COMING_FROM_GROUP_LIST, isComingFromGroupList);
                intent.putExtra(Constant.GROUP_ID, groupId);
                intent.putExtra(Constant.USER_ID, Util.userId);
                startActivity(intent);
            }
        }
    }

    private void gotoDashboardActivity() {
        startActivity(new Intent(this, DashboardActivity.class));
    }
}
