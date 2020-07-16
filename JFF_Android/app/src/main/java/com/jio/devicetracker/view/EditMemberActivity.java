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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Callback;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.ExitRemovedGroupData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.request.GetGroupMemberRequest;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.network.ExitRemoveDeleteAPI;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.EditmemberListAdapter;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditMemberActivity extends Activity implements View.OnClickListener {
    private String groupId;
    private DBManager mDbManager;
    private String userId;
    private RecyclerView editMemberList;
    private EditmemberListAdapter editmemberListAdapter;
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member);
        TextView groupName = findViewById(R.id.group_name);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.EDIT_MEMBER_PROFILE_TITLE);
        title.setTypeface(Util.mTypeface(this, 5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        groupName.setTypeface(Util.mTypeface(this, 3));
        groupName.setText(getIntent().getStringExtra(Constant.GROUP_NAME));
        editMemberList = findViewById(R.id.editRecyclerList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        editMemberList.setLayoutManager(mLayoutManager);
        initData();
        makeGetGroupMemberAPICall();
    }

    private void initData() {
        groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        mDbManager = new DBManager(this);
        userId = mDbManager.getAdminLoginDetail().getUserId();
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage){
        CustomAlertActivity alertActivity = new CustomAlertActivity(this);
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    /**
     * Displays group members in a list
     */
    private void makeGetGroupMemberAPICall() {
        Util.getInstance().showProgressBarDialog(this);
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
            }
            showDataInList();
            adapterEventListener();
        }
    }

    /**
     * Adapter Listener
     */
    private void adapterEventListener() {
        if (editmemberListAdapter != null) {
            editmemberListAdapter.setOnItemClickPagerListener(new EditmemberListAdapter.RecyclerViewClickListener() {
                @Override
                public void clickonDeleteButton(GroupMemberDataList groupMemberDataList, int position) {
                    EditMemberActivity.this.position = position;
                    makeRemoveAPICall(groupMemberDataList);
                }
            });
        }
    }

    /**
     * Make a Remove API Call
     *
     * @param groupMemberDataList
     */
    private void makeRemoveAPICall(GroupMemberDataList groupMemberDataList) {
        DBManager mDbManager = new DBManager(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ExitRemoveDeleteAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExitRemoveDeleteAPI api = retrofit.create(ExitRemoveDeleteAPI.class);
        ExitRemovedGroupData exitRemovedGroupData = new ExitRemovedGroupData();
        ExitRemovedGroupData.Consent consent = new ExitRemovedGroupData().new Consent();
        consent.setPhone(groupMemberDataList.getNumber());
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
                    Toast.makeText(EditMemberActivity.this, Constant.EXIT_FROM_GROUP_SUCCESS, Toast.LENGTH_SHORT).show();
                    mDbManager.deleteSelectedDataFromGroupMember(groupMemberDataList.getConsentId());
                    editmemberListAdapter.removeItem(position);
                    makeGetGroupMemberAPICall();
                } else {
                    Util.progressDialog.dismiss();
                    showCustomAlertWithText(Constant.REMOVE_FROM_GROUP_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.progressDialog.dismiss();
                showCustomAlertWithText(Constant.REMOVE_FROM_GROUP_FAILURE);
            }
        });
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

    // Display Data in EditMemberActivity
    private void showDataInList() {
        List<GroupMemberDataList> mList = new ArrayList<>();
        List<GroupMemberDataList> listData = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupId);
        for (GroupMemberDataList groupMemberDataList : listData) {
            if ((groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.PENDING)
                    || groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.APPROVED)
                    || groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.EXPIRED))
                    && !groupMemberDataList.getNumber().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getPhoneNumber())) {
                GroupMemberDataList data = new GroupMemberDataList();
                data.setName(groupMemberDataList.getName());
                data.setNumber(groupMemberDataList.getNumber());
                data.setProfileImage(R.drawable.ic_tracee_list);
                data.setConsentStatus(groupMemberDataList.getConsentStatus());
                mList.add(data);
            }
        }
        editmemberListAdapter = new EditmemberListAdapter(mList, this);
        editMemberList.setAdapter(editmemberListAdapter);
    }

}
