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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.nutapp.util.JioConstant;


public class ShareTagActivity extends Activity implements View.OnClickListener {
    private EditText contactNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_tag);
        contactNumber = findViewById(R.id.addcontact);
        setToolbarIconTitle();
        ImageButton addContactnumber = findViewById(R.id.addContactIcon);
        Button shareBtn = findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(this);
        addContactnumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addContactIcon:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
                //addContactNumber();
                break;

            case R.id.share_btn:
                 gotoSharetagSuccessActivity();
                break;
            case R.id.back:
                Intent intentBack = new Intent();
                setResult(RESULT_CANCELED, intentBack);
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

    private void gotoSharetagSuccessActivity() {


        if(!contactNumber.getText().toString().isEmpty()) {
            Intent intent = new Intent(this, ShareTagSuccessActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please enter the contact number", Toast.LENGTH_SHORT).show();
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
                    contactNumber.setText(number);
                }
                }
            }
        }

        public void setToolbarIconTitle()
        {
            TextView title = findViewById(R.id.toolbar_title);
            title.setText(JioConstant.SHARE_TAG_TITLE);
            title.setTypeface(JioUtils.mTypeface(this,5));
            Button backIcn = findViewById(R.id.back);
            backIcn.setVisibility(View.VISIBLE);
            backIcn.setOnClickListener(this);
            Button homeIcn = findViewById(R.id.home);
            homeIcn.setVisibility(View.VISIBLE);
            homeIcn.setOnClickListener(this);
        }
    }
