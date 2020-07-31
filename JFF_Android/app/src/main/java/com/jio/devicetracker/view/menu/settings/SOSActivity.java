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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.SOSContactData;
import com.jio.devicetracker.database.pojo.SOSData;
import com.jio.devicetracker.database.pojo.request.CreateSOSContactRequest;
import com.jio.devicetracker.database.pojo.request.DeleteSOSContactRequest;
import com.jio.devicetracker.database.pojo.request.GetAllSOSDetailRequest;
import com.jio.devicetracker.database.pojo.request.UpdateSOSContactRequest;
import com.jio.devicetracker.database.pojo.response.GetAllSOSDetailsResponse;
import com.jio.devicetracker.database.pojo.response.UpdateSOSContactResponse;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SOSActivity extends Activity implements View.OnClickListener {
    private EditText sos1ContactNumber;
    private EditText sos2ContactNumber;
    private EditText sos3ContactNumber;
    private boolean isContact1Selected;
    private boolean isContact2Selected;
    private boolean isContact3Selected;
    private int apiCount;
    private List<SOSContactData> mList;
    private String userToken;
    private DBManager mDbManager;
    private String phoneNumber;
    private String deviceId;
    private List<SOSContactData> mSosDetailList;
    private String phonebookId;
    private List<String> phonebookIdList;
    private int deletePhonebookCount;
    private List<SOSContactData> updateList;
    private int updateAPICount;
    private List<SOSContactData> updateDataList;
    private boolean isProgressbarDialog = true;
    private static boolean isStartActivityRequired = true;

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
        String[] items = {"Mobile", "Landline"};
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
        aa.setDropDownViewResource(R.layout.sos_spinner);
        ImageView contact1ImageView = findViewById(R.id.contact1ImageView);
        contact1ImageView.setOnClickListener(this);
        ImageView contact2ImageView = findViewById(R.id.contact2ImageView);
        contact2ImageView.setOnClickListener(this);
        ImageView contact3ImageView = findViewById(R.id.contact3ImageView);
        contact3ImageView.setOnClickListener(this);
        sos1ContactNumber = findViewById(R.id.sos1ContactNumber);
        sos2ContactNumber = findViewById(R.id.sos2ContactNumber);
        sos3ContactNumber = findViewById(R.id.sos3ContactNumber);
        Button sosSaveButton = findViewById(R.id.sosSaveButton);
        sosSaveButton.setOnClickListener(this);
        mList = new ArrayList<>(3);
        phonebookIdList = new ArrayList<>();
        updateList = new ArrayList<>();
        updateDataList = new ArrayList<>();
        mDbManager = new DBManager(this);
        userToken = mDbManager.getAdminLoginDetail().getUserToken();
        List<GroupMemberDataList> groupMemberDataLists = mDbManager.getAllGroupMemberData();
        for (GroupMemberDataList data : groupMemberDataLists) {
            if (data.getNumber().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getPhoneNumber())) {
                deviceId = data.getDeviceId();
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllSOSDetails();
        isStartActivityRequired = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                startActivity(new Intent(this, SettingsActivity.class));
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
        updateList.clear();

        if (contact1.equalsIgnoreCase(Constant.EMPTY_STRING)) {
            showCustomAlertWithText(Constant.PRIMARY_SOS_MANDATORY);
            return;
        } else if (!Util.getInstance().isValidMobileNumber(contact1)) {
            showCustomAlertWithText(Constant.CONTACT1_NUMBER_VALIDATION);
            return;
        } else if (!Util.getInstance().isValidMobileNumber(contact2) && !contact2.equalsIgnoreCase(Constant.EMPTY_STRING)) {
            showCustomAlertWithText(Constant.CONTACT2_NUMBER_VALIDATION);
            return;
        } else if (!Util.getInstance().isValidMobileNumber(contact3) && !contact3.equalsIgnoreCase(Constant.EMPTY_STRING)) {
            showCustomAlertWithText(Constant.CONTACT3_NUMBER_VALIDATION);
            return;
        }

        if (!contact1.equalsIgnoreCase(Constant.EMPTY_STRING) && Util.getInstance().isValidMobileNumber(contact1)) {
            if (mSosDetailList.isEmpty()) { // For first time it is create SOS
                SOSContactData sosContactData = new SOSContactData();
                sosContactData.setNumber(contact1);
                sosContactData.setPriority(1);
                mList.add(sosContactData);
            } else if (!mSosDetailList.isEmpty()) {  // If it is not empty and number is not the previous number then update
                boolean isUpdate = false;
                for (SOSContactData data : mSosDetailList) {
                    if (data.getPriority() == 1 && !data.getNumber().equalsIgnoreCase(contact1)) {
                        SOSContactData sosContactData = new SOSContactData();
                        sosContactData.setNumber(contact1);
                        sosContactData.setPriority(1);
                        sosContactData.setPhonebookId(data.getPhonebookId());
                        updateList.add(sosContactData);
                        isUpdate = true;
                    }
                }
                if (!isUpdate) {
                    boolean isMatched = false;
                    for (SOSContactData data : mSosDetailList) {
                        if (data.getPriority() == 1 && data.getNumber().equalsIgnoreCase(contact1)) {
                            isMatched = true;
                        }
                    }
                    if (!isMatched) { // If data is already present then do nothing
                        SOSContactData sosContactData = new SOSContactData();
                        sosContactData.setNumber(contact1);
                        sosContactData.setPriority(1);
                        mList.add(sosContactData);
                    }
                }
            }
        } else if (contact1.equalsIgnoreCase(Constant.EMPTY_STRING)) {
            for (SOSContactData data : mSosDetailList) {
                if (data.getPriority() == 1) {
                    phonebookIdList.add(data.getPhonebookId());
                }
            }
        }
        if (!contact2.equalsIgnoreCase(Constant.EMPTY_STRING) && Util.getInstance().isValidMobileNumber(contact2)) {
            if (mSosDetailList.isEmpty()) {
                SOSContactData sosContactData = new SOSContactData();
                sosContactData.setNumber(contact2);
                sosContactData.setPriority(2);
                mList.add(sosContactData);
            } else if (!mSosDetailList.isEmpty()) {
                boolean isUpdate = false;
                for (SOSContactData data : mSosDetailList) {
                    if (data.getPriority() == 2 && !data.getNumber().equalsIgnoreCase(contact2)) {
                        SOSContactData sosContactData = new SOSContactData();
                        sosContactData.setNumber(contact2);
                        sosContactData.setPriority(2);
                        sosContactData.setPhonebookId(data.getPhonebookId());
                        updateList.add(sosContactData);
                        isUpdate = true;
                    }
                }
                if (!isUpdate) {
                    boolean isMatched = false;
                    for (SOSContactData data : mSosDetailList) {
                        if (data.getPriority() == 2 && data.getNumber().equalsIgnoreCase(contact2)) {
                            isMatched = true;
                        }
                    }
                    if (!isMatched) { // If data is already present then do nothing
                        SOSContactData sosContactData = new SOSContactData();
                        sosContactData.setNumber(contact2);
                        sosContactData.setPriority(2);
                        mList.add(sosContactData);
                    }
                }
            }
        } else if (contact2.equalsIgnoreCase(Constant.EMPTY_STRING)) {
            for (SOSContactData data : mSosDetailList) {
                if (data.getPriority() == 2) {
                    phonebookIdList.add(data.getPhonebookId());
                }
            }
        }
        if (!contact3.equalsIgnoreCase(Constant.EMPTY_STRING) && Util.getInstance().isValidMobileNumber(contact3)) {
            if (mSosDetailList.isEmpty()) {
                SOSContactData sosContactData = new SOSContactData();
                sosContactData.setNumber(contact3);
                sosContactData.setPriority(3);
                mList.add(sosContactData);
            } else if (!mSosDetailList.isEmpty()) {
                boolean isUpdate = false;
                for (SOSContactData data : mSosDetailList) {
                    if (data.getPriority() == 3 && !data.getNumber().equalsIgnoreCase(contact3)) {
                        SOSContactData sosContactData = new SOSContactData();
                        sosContactData.setNumber(contact3);
                        sosContactData.setPriority(3);
                        sosContactData.setPhonebookId(data.getPhonebookId());
                        updateList.add(sosContactData);
                        isUpdate = true;
                    }
                }
                if (!isUpdate) {
                    boolean isMatched = false;
                    for (SOSContactData data : mSosDetailList) {
                        if (data.getPriority() == 3 && data.getNumber().equalsIgnoreCase(contact3)) {
                            isMatched = true;
                        }
                    }
                    if (!isMatched) { // If data is already present then do nothing
                        SOSContactData sosContactData = new SOSContactData();
                        sosContactData.setNumber(contact3);
                        sosContactData.setPriority(3);
                        mList.add(sosContactData);
                    }
                }
            }
        } else if (contact3.equalsIgnoreCase(Constant.EMPTY_STRING)) {
            for (SOSContactData data : mSosDetailList) {
                if (data.getPriority() == 3) {
                    phonebookIdList.add(data.getPhonebookId());
                }
            }
        }
        if (!mList.isEmpty()) {
            createSOSContactAPICall(mList);
        }

        if (!phonebookIdList.isEmpty()) {
            deleteSOSContact(phonebookIdList);
        }
        // If number is already added as a SOS Contact then display the alert
        if (!updateList.isEmpty()) {
            boolean isUpdateRequired = false;
            for (SOSContactData mData : updateList) {
                for (SOSContactData data : mSosDetailList) {
                    if (mData.getNumber().equalsIgnoreCase(data.getNumber())) {
                        showCustomAlertWithText(Constant.ALREADY_SOS_EXIST);
                        isUpdateRequired = true;
                        return;
                    }
                }
            }
            if (!isUpdateRequired) {
                if (!phonebookIdList.isEmpty()) {
                    isProgressbarDialog = false;
                }
                callUpdateAPI(updateList);
            }
        }
    }

    // Make SOS Contact API call
    private void createSOSContactAPICall(List<SOSContactData> sosDetailsList) {
        if (apiCount == sosDetailsList.size()) {
            Util.progressDialog.dismiss();
            Toast.makeText(this, Constant.SOS_CREATION_SUCCESS_MSG, Toast.LENGTH_SHORT).show();
            apiCount = 0;
            if (isStartActivityRequired) {
                isStartActivityRequired = false;
                startActivity(new Intent(this, SOSDetailActivity.class));
            }
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
            apiCount++;
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
        if (isProgressbarDialog) {
            Util.getInstance().showProgressBarDialog(this);
        }
        AdminLoginData adminLoginData = mDbManager.getAdminLoginDetail();
        List<GroupMemberDataList> mList = mDbManager.getAllGroupMemberData();
        String devideId = Constant.EMPTY_STRING;
        for (GroupMemberDataList groupMemberDataList : mList) {
            if (adminLoginData.getPhoneNumber().equalsIgnoreCase(groupMemberDataList.getNumber())) {
                devideId = groupMemberDataList.getDeviceId();
                break;
            }
        }
        RequestHandler.getInstance(this).handleRequest(new GetAllSOSDetailRequest(new GetAllSOSDetailSuccessListener(), new GetAllSOSDetailErrorListener(), devideId, userToken));
    }

    /**
     * Success Listener of Create SOS request
     */
    public class GetAllSOSDetailSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GetAllSOSDetailsResponse getAllSOSDetailsResponse = Util.getInstance().getPojoObject(String.valueOf(response), GetAllSOSDetailsResponse.class);
            List<GetAllSOSDetailsResponse.Data.Desired.Phonebooks> phonebooks = getAllSOSDetailsResponse.getData().getDesired().getmList();
            getAllSOSDetailsResponse.getData().getDesired().getmList();
            List<SOSContactData> mList = new ArrayList<>();
            if (getAllSOSDetailsResponse.getCode() == 200 && !phonebooks.isEmpty()) {
                for (GetAllSOSDetailsResponse.Data.Desired.Phonebooks mPhoneBooks : phonebooks) {
                    SOSContactData sosContactData = new SOSContactData();
                    sosContactData.setPriority(mPhoneBooks.getPriority());
                    sosContactData.setNumber(mPhoneBooks.getNumber());
                    sosContactData.setPhonebookId(mPhoneBooks.getId());
                    mList.add(sosContactData);
                }
            }
            mDbManager.insertIntoSOSTable(mList);
            if (isProgressbarDialog) {
                Util.progressDialog.dismiss();
            }
            displayDataInSOSActivity();
        }
    }

    /**
     * Error Listener of Create SOS request
     */
    private class GetAllSOSDetailErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (isProgressbarDialog) {
                Util.progressDialog.dismiss();
            }
        }
    }

    // Display data in SOS activity if data is already present
    private void displayDataInSOSActivity() {
        mSosDetailList = mDbManager.getAllSOStableData();
        for (SOSContactData sosContactData : mSosDetailList) {
            if (sosContactData.getPriority() == 1) {
                sos1ContactNumber.setText(sosContactData.getNumber());
            } else if (sosContactData.getPriority() == 2) {
                sos2ContactNumber.setText(sosContactData.getNumber());
            } else {
                sos3ContactNumber.setText(sosContactData.getNumber());
            }
        }
    }

    // Delete SOS contact API call
    private void deleteSOSContact(List<String> phonebookIdList) {
        if (deletePhonebookCount != phonebookIdList.size()) {
            phonebookId = phonebookIdList.get(deletePhonebookCount);
            Util.getInstance().showProgressBarDialog(this);
            RequestHandler.getInstance(this).handleRequest(new DeleteSOSContactRequest(new DeleteSOSContactSuccessListener(), new DeleteSOSContactErrorListener(), phonebookId, deviceId, userToken));
        } else if (deletePhonebookCount == phonebookIdList.size()) {
            Util.progressDialog.dismiss();
            displayDataInSOSActivity();
            phonebookIdList.clear();
            deletePhonebookCount = 0;
            Toast.makeText(this, Constant.DELETE_SOS_SUCCESS_MSG, Toast.LENGTH_SHORT).show();
            if (isStartActivityRequired) {
                isStartActivityRequired = false;
                startActivity(new Intent(this, SOSDetailActivity.class));
            }
        }
    }

    /**
     * Success Listener of Create SOS request
     */
    public class DeleteSOSContactSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            mDbManager.deleteSOSDetail(phonebookId);
            deletePhonebookCount++;
            Util.progressDialog.dismiss();
            deleteSOSContact(phonebookIdList);
        }
    }

    /**
     * Error Listener of Create SOS request
     */
    private class DeleteSOSContactErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println(Constant.DELETE_SOS_ERROR);
            Util.progressDialog.dismiss();
        }
    }

    /**
     * Make an update API call
     *
     * @param mList
     */
    private void callUpdateAPI(List<SOSContactData> mList) {
        if (updateAPICount != mList.size()) {
            SOSData sosData = new SOSData();
            SOSData.Desired desired = sosData.new Desired();
            SOSData.Desired.Phonebook phonebook = desired.new Phonebook();
            phonebook.setName(Constant.EMERGENCY);
            phonebook.setType(Constant.EMERGENCY);
            phonebook.setNumber(mList.get(updateAPICount).getNumber());
            phonebook.setPriority(mList.get(updateAPICount).getPriority());
            desired.setPhonebook(phonebook);
            sosData.setDesired(desired);
            if (isProgressbarDialog) {
                Util.getInstance().showProgressBarDialog(this);
            }
            RequestHandler.getInstance(this).handleRequest(new UpdateSOSContactRequest(new UpdateSOSSuccessListener(), new UpdateSOSErrorListener(), sosData, mList.get(updateAPICount).getPhonebookId(), userToken, deviceId));
        } else if (updateAPICount == mList.size()) {
            if (isProgressbarDialog) {
                Util.progressDialog.dismiss();
            }
            mDbManager.updateSOSDatabase(updateDataList);
            Toast.makeText(this, Constant.DELETE_SOS_SUCCESS_MSG, Toast.LENGTH_SHORT).show();
            updateAPICount = 0;
            getAllSOSDetails();
            if (isStartActivityRequired) {
                isStartActivityRequired = false;
                startActivity(new Intent(this, SOSDetailActivity.class));
            }
        }
    }

    /**
     * Success Listener of Create SOS request
     */
    public class UpdateSOSSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            UpdateSOSContactResponse updateSOSContactResponse = Util.getInstance().getPojoObject(String.valueOf(response), UpdateSOSContactResponse.class);
            SOSContactData sosContactData = new SOSContactData();
            sosContactData.setPhonebookId(updateSOSContactResponse.getData().getId());
            sosContactData.setPriority(updateSOSContactResponse.getData().getPriority());
            sosContactData.setNumber(updateSOSContactResponse.getData().getNumber());
            updateDataList.add(sosContactData);
            updateAPICount++;
            if (isProgressbarDialog) {
                Util.progressDialog.dismiss();
            }
            callUpdateAPI(updateList);
        }
    }

    /**
     * Error Listener of Create SOS request
     */
    private class UpdateSOSErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            showCustomAlertWithText(Constant.UPDATE_SOS_ERROR);
            if (isProgressbarDialog) {
                Util.progressDialog.dismiss();
            }
        }
    }

}
