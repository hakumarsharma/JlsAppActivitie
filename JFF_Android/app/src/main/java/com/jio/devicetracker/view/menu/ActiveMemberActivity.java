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


package com.jio.devicetracker.view.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.ExitRemovedGroupData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.network.ExitRemoveDeleteAPI;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.ActiveMemberListAdapter;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActiveMemberActivity extends AppCompatActivity implements View.OnClickListener{

    private String groupId;
    private DBManager mDbManager;
    private ActiveMemberListAdapter mAdapter;
    private String consentId;
    private int position;
    private String errorMessage;
    private String createdBy;
    private String userId;
    private RecyclerView mRecyclerList;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_member_list);
        mDbManager = new DBManager(this);
        userId = mDbManager.getAdminLoginDetail().getUserId();
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        Intent intent = getIntent();
        toolbarTitle.setText(Constant.EDIT_MEMBER_TITLE);
        createdBy = intent.getStringExtra(Constant.CREATED_BY);
        TextView groupNameTitle = findViewById(R.id.groupNameTitle);
        groupNameTitle.setText(intent.getStringExtra(Constant.GROUP_NAME));
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        mRecyclerList = findViewById(R.id.trackerList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerList.setLayoutManager(linearLayoutManager);
        mAdapter = new ActiveMemberListAdapter(addDataInList(), this);
        mRecyclerList.setAdapter(mAdapter);
        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(this);
        backButton.setVisibility(View.VISIBLE);
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage){
        CustomAlertActivity alertActivity = new CustomAlertActivity(this);
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    /**
     * make an Exit API Call
     */
    private void makeExitAPICall(String phoneNumber) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ExitRemoveDeleteAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExitRemoveDeleteAPI api = retrofit.create(ExitRemoveDeleteAPI.class);
        ExitRemovedGroupData exitRemovedGroupData = new ExitRemovedGroupData();
        ExitRemovedGroupData.Consent consent = new ExitRemovedGroupData().new Consent();
        consent.setPhone(phoneNumber);
        consent.setStatus(Constant.EXITED);
        exitRemovedGroupData.setConsent(consent);
        RequestBody body = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), new Gson().toJson(exitRemovedGroupData));
        Call<ResponseBody> call = api.deleteGroupDetails(Constant.BEARER + mDbManager.getAdminLoginDetail().getUserToken(),
                Constant.APPLICATION_JSON, mDbManager.getAdminLoginDetail().getUserId(), Constant.SESSION_GROUPS, groupId, body);
        Util.getInstance().showProgressBarDialog(this);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Util.progressDialog.dismiss();
                    Toast.makeText(ActiveMemberActivity.this, Constant.EXIT_FROM_GROUP_SUCCESS, Toast.LENGTH_SHORT).show();
                    mDbManager.deleteSelectedDataFromGroupMember(groupId);
                    mAdapter.removeItem(position);
                    addDataInList();
                } else {
                    Util.progressDialog.dismiss();
                    showCustomAlertWithText(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.progressDialog.dismiss();
                showCustomAlertWithText(errorMessage);
            }
        });
    }

    /**
     * Make a Remove API Call
     *
     * @param phoneNumber
     */
    private void makeRemoveAPICall(String phoneNumber) {
        DBManager mDbManager = new DBManager(this);
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
        Call<ResponseBody> call = api.deleteGroupDetails(Constant.BEARER + mDbManager.getAdminLoginDetail().getUserToken(),
                Constant.APPLICATION_JSON, mDbManager.getAdminLoginDetail().getUserId(), Constant.SESSION_GROUPS, groupId, body);
        Util.getInstance().showProgressBarDialog(this);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Util.progressDialog.dismiss();
                    Toast.makeText(ActiveMemberActivity.this, Constant.EXIT_FROM_GROUP_SUCCESS, Toast.LENGTH_SHORT).show();
                    mDbManager.deleteSelectedDataFromGroupMember(groupId);
                    mAdapter.removeItem(position);
                    addDataInList();
                } else {
                    Util.progressDialog.dismiss();
                    showCustomAlertWithText(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.progressDialog.dismiss();
                showCustomAlertWithText(errorMessage);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    /**
     * Exit API Call Success Listener
     */
    private class ExitFromGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            mDbManager.deleteSelectedDataFromGroup(consentId);
            mAdapter.removeItem(position);
            addDataInList();
        }
    }

    /**
     * Exit API Call Error Listener
     */
    private class ExitFromGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            showCustomAlertWithText(errorMessage);
        }
    }

    /**
     * Displays group member data in list
     *
     * @return List<GroupMemberDataList>
     */
    private List<GroupMemberDataList> addDataInList() {
        List<GroupMemberDataList> mList = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupId);
        List<GroupMemberDataList> memberList = new ArrayList<>();
        for (GroupMemberDataList data : mList) {
            if (!data.getConsentStatus().equalsIgnoreCase(Constant.EXITED) && !data.getConsentStatus().equalsIgnoreCase(Constant.REMOVED)) {
                GroupMemberDataList groupMemberDataList = new GroupMemberDataList();
                groupMemberDataList.setName(data.getName());
                groupMemberDataList.setNumber(data.getNumber());
                groupMemberDataList.setConsentStatus(data.getConsentStatus());
                groupMemberDataList.setGroupId(data.getGroupId());
                groupMemberDataList.setGroupAdmin(data.isGroupAdmin());
                groupMemberDataList.setProfileImage(R.drawable.ic_tracee_list);
                groupMemberDataList.setFrom(mDbManager.getGroupDetail(data.getGroupId()).getFrom());
                groupMemberDataList.setTo(mDbManager.getGroupDetail(data.getGroupId()).getTo());
                memberList.add(groupMemberDataList);
            }
        }
        return memberList;
    }
}
