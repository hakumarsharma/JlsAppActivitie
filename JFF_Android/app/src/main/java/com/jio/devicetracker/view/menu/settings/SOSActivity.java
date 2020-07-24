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

package com.jio.devicetracker.view.menu.settings;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.SOSContactData;
import com.jio.devicetracker.database.pojo.SOSData;
import com.jio.devicetracker.database.pojo.request.CreateSOSContactRequest;
import com.jio.devicetracker.database.pojo.request.GetAllSOSDetailRequest;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SOSActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Spinner sosSpinner;
    private Spinner sosSpinner2;
    private Spinner sosSpinner3;
    private ImageView contact1ImageView;
    private ImageView contact2ImageView;
    private ImageView contact3ImageView;
    private EditText sos1ContactNumber;
    private EditText sos2ContactNumber;
    private EditText sos3ContactNumber;
    private boolean isContact1Selected;
    private boolean isContact2Selected;
    private boolean isContact3Selected;
    private Button sosSaveButton;
    private int apiCount;
    private List<SOSContactData> mList;
    private String userToken;
    private DBManager mDbManager;
    private String phoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        initiateUI();
    }

    // Initiate all UI components
    private void initiateUI() {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.SOS_MODE);
        title.setTypeface(Util.mTypeface(this, 5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        sosSpinner = findViewById(R.id.sosSpinner);
        sosSpinner2 = findViewById(R.id.sosSpinner2);
        sosSpinner3 = findViewById(R.id.sosSpinner3);
        sosSpinner.setOnItemSelectedListener(this);
        sosSpinner2.setOnItemSelectedListener(this);
        sosSpinner3.setOnItemSelectedListener(this);
        String[] items = {"Mobile", "Landline"};
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
        aa.setDropDownViewResource(R.layout.sos_spinner);
        sosSpinner.setAdapter(aa);
        sosSpinner2.setAdapter(aa);
        sosSpinner3.setAdapter(aa);
        contact1ImageView = findViewById(R.id.contact1ImageView);
        contact1ImageView.setOnClickListener(this);
        contact2ImageView = findViewById(R.id.contact2ImageView);
        contact2ImageView.setOnClickListener(this);
        contact3ImageView = findViewById(R.id.contact3ImageView);
        contact3ImageView.setOnClickListener(this);
        sos1ContactNumber = findViewById(R.id.sos1ContactNumber);
        sos2ContactNumber = findViewById(R.id.sos2ContactNumber);
        sos3ContactNumber = findViewById(R.id.sos3ContactNumber);
        sosSaveButton = findViewById(R.id.sosSaveButton);
        sosSaveButton.setOnClickListener(this);
        mList = new ArrayList<>();
        mDbManager = new DBManager(this);
        userToken = mDbManager.getAdminLoginDetail().getUserToken();
        getAllSOSDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.contact1ImageView:
                isContact1Selected = true;
                isContact2Selected = false;
                isContact3Selected = false;
                openContactList();
                break;
            case R.id.contact2ImageView:
                isContact2Selected = true;
                isContact1Selected = false;
                isContact3Selected = false;
                openContactList();
                break;
            case R.id.contact3ImageView:
                isContact3Selected = true;
                isContact1Selected = false;
                isContact2Selected = false;
                openContactList();
                break;
            case R.id.sosSaveButton:
                saveSOSDetails();
                break;
            default:
                // Todo
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Todo
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Todo
    }

    // Open Contact list of phone
    private void openContactList() {
        PackageManager pm = getPackageManager();
        int hasPerm = pm.checkPermission(
                Manifest.permission.READ_CONTACTS,
                getPackageName());
        if (hasPerm == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, 1);
        } else {
            showCustomAlertWithText(Constant.CONTACTS_PERMISSION);
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
                    if (isContact1Selected) {
                        sos1ContactNumber.setText(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", Constant.EMPTY_STRING));
                    } else if (isContact2Selected) {
                        sos2ContactNumber.setText(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", Constant.EMPTY_STRING));
                    } else if (isContact3Selected) {
                        sos3ContactNumber.setText(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", Constant.EMPTY_STRING));
                    }
                }
            }
        }
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage) {
        CustomAlertActivity alertActivity = new CustomAlertActivity(this);
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    // check if number is valid then make an API call
    private void saveSOSDetails() {
        String contact1 = sos1ContactNumber.getText().toString().trim();
        String contact2 = sos2ContactNumber.getText().toString().trim();
        String contact3 = sos3ContactNumber.getText().toString().trim();
        mList.clear();
        if (!contact1.equalsIgnoreCase(Constant.EMPTY_STRING) && Util.getInstance().isValidMobileNumber(contact1)) {
            SOSContactData sosContactData = new SOSContactData();
            sosContactData.setNumber(contact1);
            sosContactData.setPriority(1);
            mList.add(sosContactData);
        }
        if (!contact2.equalsIgnoreCase(Constant.EMPTY_STRING) && Util.getInstance().isValidMobileNumber(contact2)) {
            SOSContactData sosContactData = new SOSContactData();
            sosContactData.setNumber(contact2);
            sosContactData.setPriority(2);
            mList.add(sosContactData);
        }
        if (!contact3.equalsIgnoreCase(Constant.EMPTY_STRING) && Util.getInstance().isValidMobileNumber(contact3)) {
            SOSContactData sosContactData = new SOSContactData();
            sosContactData.setNumber(contact3);
            sosContactData.setPriority(3);
            mList.add(sosContactData);
        }
        if (mList.isEmpty()) {
            showCustomAlertWithText(Constant.SOS_WARNINGS);
            return;
        }
        createSOSContactAPICall(mList);
    }

    // Make SOS Contact API call
    private void createSOSContactAPICall(List<SOSContactData> sosDetailsList) {
        if (apiCount == sosDetailsList.size()) {
            Util.progressDialog.dismiss();
            Toast.makeText(this, Constant.SOS_CREATION_SUCCESS_MSG, Toast.LENGTH_SHORT).show();
            apiCount = 0;
            startActivity(new Intent(this, SOSDetailActivity.class));
        } else {
            phoneNumber = sosDetailsList.get(apiCount).getNumber();
            SOSData sosData = new SOSData();
            SOSData.Desired desired = sosData.new Desired();
            SOSData.Desired.Phonebook phonebook = desired.new Phonebook();
            phonebook.setName(Constant.EMERGENCY);
            phonebook.setType(Constant.EMERGENCY);
            phonebook.setNumber(phoneNumber);
            phonebook.setPriority(sosDetailsList.get(apiCount).getPriority());
            desired.setPhonebook(phonebook);
            sosData.setDesired(desired);
            apiCount++;
            String deviceId = Constant.EMPTY_STRING;
            List<GroupMemberDataList> groupMemberDataLists = mDbManager.getAllGroupMemberData();
            for (GroupMemberDataList data : groupMemberDataLists) {
                if (data.getNumber().equalsIgnoreCase(phoneNumber)) {
                    deviceId = data.getDeviceId();
                    break;
                }
            }
            Util.getInstance().showProgressBarDialog(this);
            RequestHandler.getInstance(this).handleRequest(new CreateSOSContactRequest(new CreateSOSSuccessListener(), new CreateSOSErrorListener(), sosData, userToken, deviceId));
        }
    }

    /**
     * Success Listener of Create SOS request
     */
    public class CreateSOSSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            createSOSContactAPICall(mList);
        }
    }

    /**
     * Error Listener of Create SOS request
     */
    private class CreateSOSErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            showCustomAlertWithText(Constant.SOS_CREATION_ERROR + phoneNumber + Constant.PLEASE_TRY_AGAIN);
        }
    }

    // Get all SOS details API calls
    private void getAllSOSDetails() {
        Util.getInstance().showProgressBarDialog(this);
        RequestHandler.getInstance(this).handleRequest(new GetAllSOSDetailRequest(new GetAllSOSDetailSuccessListener(), new GetAllSOSDetailErrorListener(), userToken));
    }

    /**
     * Success Listener of Create SOS request
     */
    public class GetAllSOSDetailSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            // Todo
            // Insert into the DB
        }
    }

    /**
     * Error Listener of Create SOS request
     */
    private class GetAllSOSDetailErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            // Todo
        }
    }

}
