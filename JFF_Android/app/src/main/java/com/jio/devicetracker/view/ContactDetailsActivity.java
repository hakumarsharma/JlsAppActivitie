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
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GroupData;
import com.jio.devicetracker.database.pojo.ListOnHomeScreen;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class ContactDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mName;
    private EditText mNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        Toolbar toolbar = findViewById(R.id.loginToolbar);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.CONTACT_DEVICE_TITLE);
        Button addContactInGroup = findViewById(R.id.addContactInGroup);
        addContactInGroup.setOnClickListener(this);
        mName = findViewById(R.id.memberName);
        mNumber = findViewById(R.id.deviceName);
        ImageView contactBtn = toolbar.findViewById(R.id.contactAdd);
        contactBtn.setVisibility(View.VISIBLE);
        contactBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contactAdd) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, 1);
        } else if (v.getId() == R.id.addContactInGroup) {
            if (!Util.isValidMobileNumber(mNumber.getText().toString().trim())) {
                mNumber.setError(Constant.MOBILENUMBER_VALIDATION);
                return;
            } else {
                if (DashboardActivity.isComingFromGroupList) {
                    setGroupData(DashboardActivity.groupName, mName.getText().toString(), mNumber.getText().toString());
                    gotoGroupListActivity();
                } else {
                    if (DashboardActivity.isAddIndividual == true) {
                        setListDataOnHomeScreen(mName.getText().toString().trim(), mNumber.getText().toString().trim(), true);
                    } else {
                        setListDataOnHomeScreen(mName.getText().toString().trim(), mNumber.getText().toString().trim(), false);
                    }
                    gotoDashboardActivity();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String number = null;

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
                    number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }

                if(DashboardActivity.isComingFromGroupList) {
                    setGroupData(DashboardActivity.groupName, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)), number);
                    gotoGroupListActivity();
                } else {
                    if (DashboardActivity.isAddIndividual == true) {
                        setListDataOnHomeScreen(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).trim(), number.trim(), true);
                    } else {
                        setListDataOnHomeScreen(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).trim(), number.trim(), false);
                    }
                    gotoDashboardActivity();
                }
            }
        }
    }

    private void setGroupData(String groupName, String name, String number) {
        GroupData groupData = new GroupData();
        groupData.setGroupName(groupName.trim());
        groupData.setName(name.trim());
        groupData.setNumber(number.trim());
        DashboardActivity.specificGroupMemberData.add(groupData);
    }

    private void setListDataOnHomeScreen(String name, String relationWithGroupMember, boolean isGroupMember) {
        ListOnHomeScreen listOnHomeScreen = new ListOnHomeScreen();
        listOnHomeScreen.setName(name);
        listOnHomeScreen.setRelationWithName(relationWithGroupMember);
        listOnHomeScreen.setGroupMember(isGroupMember);
        DashboardActivity.listOnHomeScreens.add(listOnHomeScreen);
    }

    private void gotoGroupListActivity() {
        startActivity(new Intent(this, GroupListActivity.class));
    }

    private void gotoDashboardActivity() {
        startActivity(new Intent(this, DashboardActivity.class));
    }
}
