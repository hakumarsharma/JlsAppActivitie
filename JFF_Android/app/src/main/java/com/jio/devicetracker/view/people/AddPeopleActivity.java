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
package com.jio.devicetracker.view.people;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.ExitRemovedGroupData;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.network.ExitRemoveDeleteAPI;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.BaseActivity;
import com.jio.devicetracker.view.dashboard.DashboardActivity;
import com.jio.devicetracker.view.dashboard.DashboardMainActivity;
import com.jio.devicetracker.view.adapter.AddPersonListAdapter;
import com.jio.devicetracker.view.menu.ActiveSessionActivity;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddPeopleActivity extends BaseActivity implements View.OnClickListener {

    private static RecyclerView contactsListView;
    private static AddPersonListAdapter mAdapter;
    private static List<GroupMemberResponse.Data> listOfContacts;
    private String groupId;
    private EditText contactName;
    private EditText contactNumber;
    private Button addContact;
    private static Button addContact_Continue;
    private static Context context;
    private static DBManager mPeopleDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpeople);
        initializeData();
    }

    private void initializeData() {
        Toolbar toolbar = findViewById(R.id.addPeopleToolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(Constant.Add_People);
        toolbar.setBackgroundColor(getResources().getColor(R.color.cardviewlayout_device_background_color));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        context = this;
        mPeopleDbManager = new DBManager(this);
        contactsListView = findViewById(R.id.contactsListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        contactsListView.setLayoutManager(linearLayoutManager);
        getDataFromIntent(title);

        TextView peopleText = findViewById(R.id.people_text);
        peopleText.setTypeface(Util.mTypeface(this, 3));

        contactName = findViewById(R.id.memberName);
        contactName.setTypeface(Util.mTypeface(this, 5));

        contactNumber = findViewById(R.id.deviceNumber);
        contactNumber.setTypeface(Util.mTypeface(this, 5));

        addContact = findViewById(R.id.addContactDetail);
        if (groupId != null) {
            addContact.setVisibility(View.VISIBLE);
            addContact.setTypeface(Util.mTypeface(this, 5));
            addContact.setOnClickListener(this);
        }

        addContact_Continue = findViewById(R.id.addContact_Continue);
        addContact_Continue.setOnClickListener(this);

        ImageView contactBtn = toolbar.findViewById(R.id.contactAdd);
        contactBtn.setVisibility(View.VISIBLE);
        contactBtn.setOnClickListener(this);

        changeButtonColorOnDataEntry();

    }

    // Intent data to check whether use has come from group or individual
    private void getDataFromIntent(TextView title) {
        Intent intent = getIntent();
        String qrValue = intent.getStringExtra(Constant.QR_CODE_VALUE);
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        String groupName = intent.getStringExtra(Constant.GROUP_NAME);

        if (groupId != null && !groupId.isEmpty()) {
            this.createdGroupId = groupId;
            this.isFromCreateGroup = false;
            this.isGroupMember = true;
            getAllForOneGroupAPICall();
        }

        // set group name as title
        if (groupName != null && !groupName.isEmpty()) {
            title.setText(groupName);
        }
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
                String name = contactName.getText().toString();
                String number = contactNumber.getText().toString();
                setEditTextBottomLineColor(contactName);

                // Displaying add button and continue based on groupid as groupid exists if user is coming from create group
                if ((groupId == null || groupId.isEmpty()) && !"".equalsIgnoreCase(name) && !"".equalsIgnoreCase(number)) {
                    setButtonBackground(addContact_Continue, true);
                } else if ((groupId != null && !groupId.isEmpty()) && !"".equalsIgnoreCase(name) && !"".equalsIgnoreCase(number)) {
                    setButtonBackground(addContact, true);
                    setButtonBackground(addContact_Continue, false);
                } else {
                    setButtonBackground(addContact_Continue, false);
                    setButtonBackground(addContact, false);
                }
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

                // Displaying add button and continue based on groupid as groupid exists if user is coming from create group
                if ((groupId == null || groupId.isEmpty()) && !"".equalsIgnoreCase(name) && !"".equalsIgnoreCase(number)) {
                    setButtonBackground(addContact_Continue, true);
                } else if ((groupId != null && !groupId.isEmpty()) && !"".equalsIgnoreCase(name) && !"".equalsIgnoreCase(number)) {
                    setButtonBackground(addContact, true);
                    setButtonBackground(addContact_Continue, false);
                } else {
                    setButtonBackground(addContact_Continue, false);
                    setButtonBackground(addContact, false);
                }
            }
        });

    }

    // Background color of button based text entered
    private void setButtonBackground(Button btn, Boolean selectorChange) {
        if (selectorChange) {
            btn.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.login_selector, null));
            btn.setTextColor(Color.WHITE);
        } else {
            btn.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.selector, null));
            btn.setTextColor(Color.WHITE);
        }
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
        } else if (v.getId() == R.id.addContactDetail) {
            validationCheck();
        } else if (v.getId() == R.id.back) {
            finish();
        } else if (v.getId() == R.id.addContact_Continue) {
            if (groupId == null || groupId.isEmpty()) {
                validationCheck();
            } else {
                if (listOfContacts != null && !listOfContacts.isEmpty()) {
                    startActivity(new Intent(this, DashboardMainActivity.class));
                } else {
                    Toast.makeText(this, Constant.ADD_CONTACT_WARNING, Toast.LENGTH_SHORT);
                }
            }
        }

    }

    private void validationCheck() {
        if (contactName.getText().length() == 0) {
            contactName.setError(Constant.ENTER_NAME);
            return;
        }
        if (!Util.isValidMobileNumber(contactNumber.getText().toString())) {
            contactNumber.setError(Constant.MOBILENUMBER_VALIDATION);
            return;
        }
        // if groupid exists it means it coming from created group
        if (groupId == null || groupId.isEmpty()) {
            createGroupAndAddContactDetails();
        } else {
            addMemberToCreatedGroup();
        }
    }

    private void createGroupAndAddContactDetails() {
        this.memberName = contactName.getText().toString();
        this.memberNumber = contactNumber.getText().toString();
        this.isFromCreateGroup = false;
        this.isGroupMember = false;
        setEditTextValues();
        createGroupAndAddContactAPICall(Constant.INDIVIDUAL_USER_GROUP_NAME);
    }

    private void addMemberToCreatedGroup() {
        this.createdGroupId = groupId;
        this.memberName = contactName.getText().toString();
        this.memberNumber = contactNumber.getText().toString();
        this.isFromCreateGroup = false;
        this.isGroupMember = true;
        setEditTextValues();
        addMemberInGroupAPICall();
    }

    public void getAllMembers(List<GroupMemberResponse.Data> memberList) {
        if (memberList != null) {
            listOfContacts = new ArrayList();
            for(GroupMemberResponse.Data data : memberList) {
                if(!data.getStatus().equalsIgnoreCase(Constant.REMOVED)) {
                    listOfContacts.add(data);
                }
            }
            mAdapter = new AddPersonListAdapter(listOfContacts);
            contactsListView.setAdapter(mAdapter);
            if (!listOfContacts.isEmpty() && addContact_Continue != null) {
                setButtonBackground(addContact_Continue, true);
            }
            adapterEventListener();
        }
    }

    private void setEditTextValues() {
        if (addContact != null) {
            setButtonBackground(addContact, false);
        }
        setEditTextBottomLineColor(contactName);
        setEditTextBottomLineColor(contactNumber);
        contactName.getText().clear();
        contactNumber.getText().clear();

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
                    contactName.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                    contactNumber.setText((phones.getString((phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))).replace(" ", "")));
                }
            }
        }
    }

    private void adapterEventListener() {
        mAdapter.setOnItemClickPagerListener(new AddPersonListAdapter.RecyclerViewClickListener() {
            @Override
            public void onDeleteMemberClicked(View v, int position, String groupId, GroupMemberResponse.Data data) {
                AddPeopleActivity.this.makeRemoveAPICall(data.getPhone(), groupId, position);
            }
        });
    }

    /**
     * Make Remove from Group API Call using retrofit
     *
     * @param phoneNumber
     * @param groupId
     */
    private void makeRemoveAPICall(String phoneNumber, String groupId, int position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ExitRemoveDeleteAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExitRemoveDeleteAPI api = retrofit.create(ExitRemoveDeleteAPI.class);
        ExitRemovedGroupData exitRemovedGroupData = new ExitRemovedGroupData();
        ExitRemovedGroupData.Consent consent = new ExitRemovedGroupData().new Consent();
        consent.setPhone(phoneNumber);
        consent.setStatus(Constant.REMOVED);
        exitRemovedGroupData.setConsent(consent);
        RequestBody body = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), new Gson().toJson(exitRemovedGroupData));
        Util.getInstance().showProgressBarDialog(context);
        Call<ResponseBody> call = api.deleteGroupDetails(Constant.BEARER + mPeopleDbManager.getAdminLoginDetail().getUserToken(),
                Constant.APPLICATION_JSON, mPeopleDbManager.getAdminLoginDetail().getUserId(), Constant.SESSION_GROUPS, groupId, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Util.progressDialog.dismiss();
                    Toast.makeText(context, Constant.REMOVE_FROM_GROUP_SUCCESS, Toast.LENGTH_SHORT).show();
                    mPeopleDbManager.deleteSelectedDataFromGroup(groupId);
                    mPeopleDbManager.deleteSelectedDataFromGroupMember(groupId);
                    mAdapter.removeItem(position);
                    getAllForOneGroupAPICall();
                } else {
                    Util.progressDialog.dismiss();
                    Util.alertDilogBox(Constant.REMOVE_FROM_GROUP_FAILURE, Constant.ALERT_TITLE, context);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.REMOVE_FROM_GROUP_FAILURE, Constant.ALERT_TITLE, context);
            }
        });
    }
}
