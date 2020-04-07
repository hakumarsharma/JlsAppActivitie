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

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.List;

public class ContactDetailsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private String name;
    private String number;
    private EditText mName;
    private EditText mNumber;
    private EditText mIMEINumber;
    private DBManager mDbManager;
    private String deviceType;
    private boolean isDataMatched = false;
    private static String numberComingFromContactList = null;
    private static String nameComingFromContactList = null;
    private Button addContactInGroupButon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        Toolbar toolbar = findViewById(R.id.loginToolbar);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.CONTACT_DEVICE_TITLE);
        addContactInGroupButon = findViewById(R.id.addContactInGroup);
        addContactInGroupButon.setOnClickListener(this);
        mName = findViewById(R.id.memberName);
        mNumber = findViewById(R.id.deviceNumberInContactDetails);
        mIMEINumber = findViewById(R.id.contactDetailIMEI);
        Spinner deviceTypeSpinner = findViewById(R.id.deviceTypeSpinner);
        deviceTypeSpinner.setOnItemSelectedListener(this);
        mDbManager = new DBManager(this);
        Intent intent = getIntent();
        String qrValue = intent.getStringExtra("QRCodeValue");
        setNameNumberImei(qrValue);
        changeButtonColorOnDataEntry();

        if (DashboardActivity.isComingFromGroupList || DashboardActivity.isAddIndividual) {
            ImageView contactBtn = toolbar.findViewById(R.id.contactAdd);
            contactBtn.setVisibility(View.VISIBLE);
            contactBtn.setOnClickListener(this);
        } else {
            ImageView scanner = toolbar.findViewById(R.id.qrScanner);
            scanner.setVisibility(View.VISIBLE);
            scanner.setOnClickListener(this);
        }

        if ("".equals(numberComingFromContactList) && "".equals(nameComingFromContactList)) {
            mName.setText(nameComingFromContactList);
            mNumber.setText(numberComingFromContactList);
        }
    }

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

        mIMEINumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addContactInGroupButon.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                addContactInGroupButon.setTextColor(Color.WHITE);
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
            String mPhoneNumber = mNumber.getText().toString().trim();
            if (deviceType.equalsIgnoreCase(Constant.PEOPLE_TRACKER_DEVICE_TYPE)) {
                if (Util.isValidMobileNumberForPet(mPhoneNumber)) {
                    mNumber.setError(Constant.PEOPLE_NUMBER_VALIDATION_PET_NUMBER_ENTERED);
                    return;
                } else if (!Util.isValidMobileNumber(mPhoneNumber)) {
                    mNumber.setError(Constant.MOBILENUMBER_VALIDATION);
                    return;
                }
            } else if (deviceType.equalsIgnoreCase(Constant.PET_TRACKER_DEVICE_TYPE)) {
                if (Util.isValidMobileNumber(mPhoneNumber)) {
                    mNumber.setError(Constant.PET_TRACKER_VALIDATION_PEOPLE_NUMBER_ENTERE);
                    return;
                } else if (!Util.isValidMobileNumberForPet(mPhoneNumber)) {
                    mNumber.setError(Constant.PET_NUMBER_VALIDATION);
                    return;
                }
            }

            if (!Util.isValidIMEINumber(mIMEINumber.getText().toString().trim())) {
                mIMEINumber.setError(Constant.IMEI_VALIDATION);
                return;
            } else {
                checkValidationOfUser(mName.getText().toString().trim(), mNumber.getText().toString().trim(), mIMEINumber.getText().toString().trim());
                if (isDataMatched && DashboardActivity.isComingFromGroupList) {
                    mDbManager.updateIsGroupMember(1, mIMEINumber.getText().toString().trim(), DashboardActivity.groupName);
                    gotoGroupListActivity();
                } else if (isDataMatched && DashboardActivity.isComingFromGroupList == false) {
                    mDbManager.updateIsGroupMember(0, mIMEINumber.getText().toString().trim(), mName.getText().toString());
                    gotoDashboardActivity();
                }
            }
            mNumber.setError(null);
            mIMEINumber.setError(number);
        }

        if (v.getId() == R.id.qrScanner) {
            gotoQRScannerScreen();
        }
    }

    private void checkValidationOfUser(String mName, String mNumber, String mIMEINumber) {
        List<HomeActivityListData> homeListData = mDbManager.getAllBorqsData(Util.adminEmail);
        for (HomeActivityListData homeActivityListData : homeListData) {
            if (homeActivityListData.getImeiNumber() != null && homeActivityListData.getImeiNumber().equalsIgnoreCase(mIMEINumber) && homeActivityListData.getPhoneNumber().equalsIgnoreCase(mNumber)) {
                mDbManager.updateDeviceTypeAndGroupName(deviceType, homeActivityListData.getGroupName(), homeActivityListData.getImeiNumber(), mName, 1);
                isDataMatched = true;
            }
        }
        if (!isDataMatched) {
            Util.alertDilogBox(Constant.DEVICE_DETAIL_VALIDATION, Constant.ALERT_TITLE, this);
            return;
        }
    }

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
                startActivity(new Intent(this, ContactDetailsActivity.class));
                /*if (DashboardActivity.isComingFromGroupList) {
                    setGroupData(DashboardActivity.groupName, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)), number);
                    gotoGroupListActivity();
                } else {
                    setListDataOnHomeScreen(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).trim(), number.trim(), false);
                    if (insertRowid > 0) {
                        gotoDashboardActivity();
                    } else {
                        Util.alertDilogBox(Constant.DUPLICATE_NUMBER, Constant.ALERT_TITLE, this);
                    }
                }*/
            }
        }
    }

    /*private void setGroupData(String groupName, String name, String number) {
        GroupData groupData = new GroupData();
        groupData.setGroupName(groupName.trim());
        groupData.setName(name.trim());
        groupData.setNumber(number.trim());
        groupData.setLat("12.9050641");
        groupData.setLng("77.6310009");
        DashboardActivity.specificGroupMemberData.add(groupData);
    }

    private void setListDataOnHomeScreen(String name, String number, boolean isGroupMember) {
        HomeActivityListData listOnHomeScreen = new HomeActivityListData();
//        listOnHomeScreen.setRelationWithName(relationWithGroupMember);
        listOnHomeScreen.setGroupMember(isGroupMember);
        List<HomeActivityListData> homeListData = mDbManager.getAlldata(adminData.getEmail());

        mDbManager.insertInBorqsDeviceDB(listOnHomeScreen, adminData.getEmail());
    }*/

    private void gotoGroupListActivity() {
        startActivity(new Intent(this, GroupListActivity.class));
    }

    private void gotoDashboardActivity() {
        startActivity(new Intent(this, DashboardActivity.class));
    }

    private void gotoQRScannerScreen() {
        startActivity(new Intent(this, QRCodeReaderActivity.class));
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
