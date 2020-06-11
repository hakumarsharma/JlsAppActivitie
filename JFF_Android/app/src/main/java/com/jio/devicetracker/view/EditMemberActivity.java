package com.jio.devicetracker.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.request.GetGroupMemberRequest;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.EditmemberListAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class EditMemberActivity extends Activity implements View.OnClickListener {
    private List<GroupMemberDataList> dataList;
    private String groupId;
    private DBManager mDbManager;
    private String userId;
    private RecyclerView editMemberList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member);
        TextView groupName = findViewById(R.id.group_name);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.EDIT_MEMBER_PROFILE_TITLE);
        title.setTypeface(Util.mTypeface(this,5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        groupName.setTypeface(Util.mTypeface(this,3));
        groupName.setText(getIntent().getStringExtra(Constant.GROUP_NAME));
        editMemberList = findViewById(R.id.editRecyclerList);
        dataList = new ArrayList<>();
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

    // Display Data in EditMemberActivity
    private void showDataInList() {
        List<GroupMemberDataList> mList = new ArrayList<>();
        List<GroupMemberDataList> listData = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupId);
        for (GroupMemberDataList groupMemberDataList : listData) {
            if (groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.PENDING) || groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.APPROVED)
                    || groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.EXITED)) {
                GroupMemberDataList data = new GroupMemberDataList();
                data.setName(groupMemberDataList.getName());
                data.setNumber(groupMemberDataList.getNumber());
                data.setProfileImage(R.drawable.ic_tracee_list);
                data.setConsentStatus(groupMemberDataList.getConsentStatus());
                mList.add(data);
            }
        }
        EditmemberListAdapter adapterData = new EditmemberListAdapter(mList,this);
        editMemberList.setAdapter(adapterData);
    }

}
