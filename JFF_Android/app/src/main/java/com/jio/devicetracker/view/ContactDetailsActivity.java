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
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

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

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for adding new devices(It can add from QR code, Contact list or manually)
 */
public class ContactDetailsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private String name;
    private String number;
    private EditText mName;
    private EditText mNumber;
    private DBManager mDbManager;
    private String deviceType;
    private static String numberComingFromContactList = null;
    private static String nameComingFromContactList = null;
    private Button addContactInGroupButon = null;
    private Toolbar toolbar = null;
    private static String groupId;
    private static String userId;
    private boolean isComingFromAddContact;
    private boolean isComingFromAddDevice;
    private boolean isComingFromGroupList;
    private boolean isComingFromContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        toolbar = findViewById(R.id.loginToolbar);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.CONTACT_DEVICE_TITLE);
        addContactInGroupButon = findViewById(R.id.addContactInGroup);
        addContactInGroupButon.setOnClickListener(this);
        mName = findViewById(R.id.memberName);
        mNumber = findViewById(R.id.deviceNumberInContactDetails);
        Spinner deviceTypeSpinner = findViewById(R.id.deviceTypeSpinner);
        deviceTypeSpinner.setOnItemSelectedListener(this);
        mDbManager = new DBManager(this);
        Intent intent = getIntent();
        String qrValue = intent.getStringExtra(Constant.QR_CODE_VALUE);
        isComingFromContactList = intent.getBooleanExtra(Constant.IS_COMING_FROM_CONTACT_LIST, false);
        isComingFromAddDevice = intent.getBooleanExtra(Constant.IS_COMING_FROM_ADD_DEVICE, false);
        isComingFromAddContact = intent.getBooleanExtra(Constant.IS_COMING_FROM_ADD_CONTACT, false);
        isComingFromGroupList = intent.getBooleanExtra(Constant.IS_COMING_FROM_GROUP_LIST, false);
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        userId = intent.getStringExtra(Constant.USER_ID);
        setNameNumberImei(qrValue);
        changeButtonColorOnDataEntry();
        checkValidationOfFieldEntry();
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
        savedInstanceState.putString(Constant.USER_ID, userId);
        savedInstanceState.putBoolean(Constant.IS_COMING_FROM_ADD_CONTACT, isComingFromAddContact);
        savedInstanceState.putBoolean(Constant.IS_COMING_FROM_ADD_DEVICE, isComingFromAddDevice);
        savedInstanceState.putBoolean(Constant.IS_COMING_FROM_GROUP_LIST, isComingFromGroupList);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        groupId = savedInstanceState.getString(Constant.GROUP_ID);
        userId = savedInstanceState.getString(Constant.USER_ID);
        isComingFromAddDevice = savedInstanceState.getBoolean(Constant.IS_COMING_FROM_ADD_DEVICE, false);
        isComingFromAddContact = savedInstanceState.getBoolean(Constant.IS_COMING_FROM_ADD_CONTACT, false);
        isComingFromGroupList = savedInstanceState.getBoolean(Constant.IS_COMING_FROM_GROUP_LIST, false);
    }

    /**
     * If coming to this activity after clicking on Add Contact or Create group from floating button, then display the contact icon on toolbar
     * or else display the QR code on toolbar when Add device is clicked
     */
    private void checkValidationOfFieldEntry() {
        if (isComingFromAddContact == true || isComingFromGroupList || isComingFromContactList) {
            ImageView contactBtn = toolbar.findViewById(R.id.contactAdd);
            contactBtn.setVisibility(View.VISIBLE);
            contactBtn.setOnClickListener(this);
        } else if (isComingFromAddDevice == true) {
            ImageView scanner = toolbar.findViewById(R.id.qrScanner);
            scanner.setVisibility(View.VISIBLE);
            scanner.setOnClickListener(this);
        }

        if (numberComingFromContactList != null && nameComingFromContactList != null) {
            mName.setText(nameComingFromContactList);
            mNumber.setText(numberComingFromContactList);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, DashboardActivity.class));
    }

    /**
     * Change the button color when data is field
     */
    private void changeButtonColorOnDataEntry() {
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*addContactInGroupButon.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                addContactInGroupButon.setTextColor(Color.WHITE);*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = mName.getText().toString();
                if (!"".equalsIgnoreCase(name)) {
                    addContactInGroupButon.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                    addContactInGroupButon.setTextColor(Color.WHITE);
                }
            }
        });

    }

    /**
     * Fetch the name and number after scanning from QR Scanner
     *
     * @param qrValue
     */
    private void setNameNumberImei(String qrValue) {
        if (qrValue != null) {
            String[] splitString = qrValue.split("\n");
            name = splitString[0];
            number = splitString[1];
            mName.setText(name);
            mNumber.setText(number);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contactAdd) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, 1);
        } else if (v.getId() == R.id.addContactInGroup) {
            if (mName.getText().toString().equalsIgnoreCase("")) {
                mName.setError(Constant.NAME_EMPTY);
                return;
            }
            name = mName.getText().toString().trim();
            number = mNumber.getText().toString().trim();
            if (deviceType.equalsIgnoreCase(Constant.PEOPLE_TRACKER_DEVICE_TYPE)) {
                if (Util.isValidMobileNumberForPet(number)) {
                    mNumber.setError(Constant.PEOPLE_NUMBER_VALIDATION_PET_NUMBER_ENTERED);
                    return;
                } else if (!Util.isValidMobileNumber(number)) {
                    mNumber.setError(Constant.MOBILENUMBER_VALIDATION);
                    return;
                }
            } else if (deviceType.equalsIgnoreCase(Constant.PET_TRACKER_DEVICE_TYPE)) {
                if (Util.isValidMobileNumber(number)) {
                    mNumber.setError(Constant.PET_TRACKER_VALIDATION_PEOPLE_NUMBER_ENTERE);
                    return;
                } else if (!Util.isValidMobileNumberForPet(number)) {
                    mNumber.setError(Constant.PET_NUMBER_VALIDATION);
                    return;
                }
            }

            // If user is coming from Add device/Add Contact then create group called individual_user and add member in the group else directly add member in the group.
            if (isComingFromAddDevice || isComingFromAddContact) {
                createGroupAndAddContactAPICall();
            } else {
                addMemberInGroupAPICall();
            }
        }

        if (v.getId() == R.id.qrScanner) {
            gotoQRScannerScreen();
            nameComingFromContactList = null;
            numberComingFromContactList = null;
            isComingFromAddDevice = true;
        }
    }

    /**
     * Adds individual contact in Dashboard, but it adds as a member of group.
     * Group Name is hardcoded as a Individual_User
     */
    private void createGroupAndAddContactAPICall() {
        CreateGroupData createGroupData = new CreateGroupData();
        createGroupData.setName(Constant.INDIVIDUAL_USER_GROUP_NAME);
        createGroupData.setType(Constant.ONE_TO_ONE);
        CreateGroupData.Session session = new CreateGroupData().new Session();
        session.setFrom(Util.getInstance().getTimeEpochFormatAfterCertainTime(1));
        session.setTo(Util.getInstance().getTimeEpochFormatAfterCertainTime(2));
        createGroupData.setSession(session);
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(getApplicationContext()).handleRequest(new CreateGroupRequest(new CreateGroupSuccessListener(), new CreateGroupErrorListener(), createGroupData, userId));
    }

    /**
     * Create Group Success Listener
     */
    private class CreateGroupSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            CreateGroupResponse createGroupResponse = Util.getInstance().getPojoObject(String.valueOf(response), CreateGroupResponse.class);
            if (createGroupResponse.getCode() == 200) {
                mDbManager.insertIntoGroupTable(createGroupResponse);
                groupId = createGroupResponse.getData().getId();
                addIndividualUserInGroupAPICall();
            }
        }
    }

    /**
     * Create Group error Listener
     */
    private class CreateGroupErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                Util.alertDilogBox(Constant.ADDING_INDIVIDUAL_USER_FAILED, Constant.ALERT_TITLE, ContactDetailsActivity.this);
            }
        }
    }

    /**
     * Adds individual member inside Group Called Individual_User
     */
    private void addIndividualUserInGroupAPICall() {
        AddMemberInGroupData addMemberInGroupData = new AddMemberInGroupData();
        AddMemberInGroupData.Consents consents = new AddMemberInGroupData().new Consents();
        List<AddMemberInGroupData.Consents> consentList = new ArrayList<>();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.EVENTS);
        consents.setEntities(mList);
        consents.setPhone(number);
        consents.setName(name);
        consentList.add(consents);
        addMemberInGroupData.setConsents(consentList);
        GroupRequestHandler.getInstance(this).handleRequest(new AddMemberInGroupRequest(new AddMemberInGroupRequestSuccessListener(), new AddMemberInGroupRequestErrorListener(), addMemberInGroupData, groupId, userId));
    }

    /**
     * Add Members in Group API Call, member will be part of group
     */
    public void addMemberInGroupAPICall() {
        AddMemberInGroupData addMemberInGroupData = new AddMemberInGroupData();
        AddMemberInGroupData.Consents consents = new AddMemberInGroupData().new Consents();
        List<AddMemberInGroupData.Consents> consentList = new ArrayList<>();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.EVENTS);
        consents.setEntities(mList);
        consents.setPhone(number);
        consents.setName(name);
        consentList.add(consents);
        addMemberInGroupData.setConsents(consentList);
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new AddMemberInGroupRequest(new AddMemberInGroupRequestSuccessListener(), new AddMemberInGroupRequestErrorListener(), addMemberInGroupData, groupId, userId));
    }

    /**
     * Add Member in group Success Listener
     */
    private class AddMemberInGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GroupMemberResponse groupMemberResponse = Util.getInstance().getPojoObject(String.valueOf(response), GroupMemberResponse.class);
            if (groupMemberResponse.getCode() == Constant.SUCCESS_CODE_200) {
                mDbManager.insertGroupMemberDataInTable(groupMemberResponse);
                getAllForOneGroupAPICall();
            }
        }
    }

    /**
     * Add Member in Group Error Listener
     */
    private class AddMemberInGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.GROUP_MEMBER_ADDITION_FAILURE, Constant.ALERT_TITLE, ContactDetailsActivity.this);
            } else if (error.networkResponse.statusCode == Constant.STATUS_CODE_404) {
                // Make Verify and Assign call
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.DEVICE_NOT_FOUND, Constant.ALERT_TITLE, ContactDetailsActivity.this);
            } else {
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.GROUP_MEMBER_ADDITION_FAILURE, Constant.ALERT_TITLE, ContactDetailsActivity.this);
            }
        }
    }

    /**
     * Get all members of a particular group and update the database
     */
    public void getAllForOneGroupAPICall() {
        GroupRequestHandler.getInstance(this).handleRequest(new GetGroupMemberRequest(new GetGroupMemberRequestSuccessListener(), new GetGroupMemberRequestErrorListener(), groupId, userId));
    }

    /**
     * Get all members of a particular group success listener
     */
    public class GetGroupMemberRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            GroupMemberResponse groupMemberResponse = Util.getInstance().getPojoObject(String.valueOf(response), GroupMemberResponse.class);
            if (groupMemberResponse.getCode() == Constant.SUCCESS_CODE_200) {
                mDbManager.insertGroupMemberDataInTable(groupMemberResponse);
                if (isComingFromAddContact || isComingFromAddDevice) {
                    gotoDashboardActivity();
                } else {
                    gotoGroupListActivity();
                }
            }
        }
    }

    /**
     * Get all members of a particular group error listener
     */
    private class GetGroupMemberRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
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
                    numberComingFromContactList = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    nameComingFromContactList = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                }
                Intent intent = new Intent(this, ContactDetailsActivity.class);
                intent.putExtra(Constant.IS_COMING_FROM_CONTACT_LIST, true);
                intent.putExtra(Constant.IS_COMING_FROM_ADD_CONTACT, isComingFromAddContact);
                intent.putExtra(Constant.IS_COMING_FROM_ADD_DEVICE, isComingFromAddDevice);
                intent.putExtra(Constant.IS_COMING_FROM_GROUP_LIST, isComingFromGroupList);
                intent.putExtra(Constant.GROUP_ID, groupId);
                intent.putExtra(Constant.USER_ID, userId);
                startActivity(intent);
            }
        }
    }

    private void gotoGroupListActivity() {
        Intent intent = new Intent(this, GroupListActivity.class);
        intent.putExtra(Constant.GROUP_ID, groupId);
        intent.putExtra(Constant.USER_ID, userId);
        startActivity(intent);
    }

    private void gotoDashboardActivity() {
        startActivity(new Intent(this, DashboardActivity.class));
    }

    private void gotoQRScannerScreen() {
        Intent intent = new Intent(this, QRCodeReaderActivity.class);
        intent.putExtra(Constant.IS_COMING_FROM_CONTACT_LIST, true);
        intent.putExtra(Constant.IS_COMING_FROM_ADD_CONTACT, isComingFromAddContact);
        intent.putExtra(Constant.IS_COMING_FROM_ADD_DEVICE, isComingFromAddDevice);
        intent.putExtra(Constant.IS_COMING_FROM_GROUP_LIST, isComingFromGroupList);
        intent.putExtra(Constant.GROUP_ID, groupId);
        intent.putExtra(Constant.USER_ID, userId);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        deviceType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //To-Do
    }

}
