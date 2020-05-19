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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;

import com.example.nutapp.util.JioConstant;

import org.w3c.dom.Text;

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
        }
    }

    private void gotoSharetagSuccessActivity() {

        Intent intent = new Intent(this,ShareTagSuccessActivity.class);
        startActivity(intent);
    }

    private void addContactNumber() {
        //Uri contactData = data.getData();
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
        String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        if ("1".equalsIgnoreCase(hasPhone)) {
            Cursor phones = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                    null, null);
            phones.moveToFirst();
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactNumber.setText(number);
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
            Button homeicn = findViewById(R.id.home);
            homeicn.setVisibility(View.VISIBLE);
        }
    }
