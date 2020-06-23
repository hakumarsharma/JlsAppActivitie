package com.jio.devicetracker.view.people;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.database.pojo.response.GetGroupInfoPerUserResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.BaseActivity;
import com.jio.devicetracker.view.adapter.ChooseGroupListAdapter;
import com.jio.devicetracker.view.group.CreateGroupActivity;

import java.util.ArrayList;
import java.util.List;

public class ChooseGroupFromPeopleFlow extends BaseActivity implements View.OnClickListener {
    private String userId;
    private DBManager mDbManager;
    private CardView cardViewGroup;
    private String groupId;
    private ChooseGroupListAdapter mAdapter;
    private String name;
    private String phoneNumber;
    private TextView groupText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_group_people_flow);
        initDataMember();
        initUI();
        makeGroupInfoPerUserRequestAPICall();
    }
    /**
     * Get All Group info per user API Call
     */
    protected void makeGroupInfoPerUserRequestAPICall() {
        GroupRequestHandler.getInstance(this).handleRequest(new GetGroupInfoPerUserRequest(new GetGroupInfoPerUserRequestSuccessListener(), new GetGroupInfoPerUserRequestErrorListener(), userId));
    }

    /**
     * Initialize data members
     */
    private void initDataMember() {
        mDbManager = new DBManager(this);
        userId = mDbManager.getAdminLoginDetail().getUserId();
    }

    /**
     * Initialize UI component
     */
    private void initUI() {
        cardViewGroup = findViewById(R.id.cardViewList);
        TextView title = findViewById(R.id.toolbar_title);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        ImageView add = findViewById(R.id.createGroup);
        add.setVisibility(View.VISIBLE);
        add.setOnClickListener(this);
        Intent intent = getIntent();
        groupText = findViewById(R.id.group_detail_text);
        name = intent.getStringExtra("TrackeeName");
        phoneNumber = intent.getStringExtra("TrackeeNumber");
        Button continueChooseGroup = findViewById(R.id.continueChooseGroup);
        continueChooseGroup.setOnClickListener(this);
        Button addLater = findViewById(R.id.addLater);
        addLater.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.createGroup:
                gotoCreateGroupActivity();
                break;
            case R.id.continueChooseGroup:
                addMemberToCreatedGroup(groupId);
                break;
            case R.id.addLater:
                createGroupAndAddContactDetails();
                break;
        }
    }

    private void gotoCreateGroupActivity() {
        Intent createGroupIntent = new Intent(this, CreateGroupActivity.class);
        createGroupIntent.putExtra("TrackeeName",name);
        createGroupIntent.putExtra("TrackeeNumber",phoneNumber);
        startActivity(createGroupIntent);
    }

    /**
     * GetGroupInfoPerUserRequest Success listener
     */
    private class GetGroupInfoPerUserRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GetGroupInfoPerUserResponse getGroupInfoPerUserResponse = Util.getInstance().getPojoObject(String.valueOf(response), GetGroupInfoPerUserResponse.class);
            parseResponseStoreInDatabase(getGroupInfoPerUserResponse);
            addDatainList();
            adapterEventListener();
        }
    }

    /**
     * GetGroupInfoPerUserRequest Error listener
     */
    private class GetGroupInfoPerUserRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 409) {
                Util.alertDilogBox(Constant.GET_GROUP_INFO_PER_USER_ERROR, Constant.ALERT_TITLE, ChooseGroupFromPeopleFlow.this);
            }
        }
    }

    /**
     * Parse the response and store in DB(Group Table and Member table)
     */
    public void parseResponseStoreInDatabase(GetGroupInfoPerUserResponse getGroupInfoPerUserResponse) {
        List<HomeActivityListData> groupList = new ArrayList<>();
        List<GroupMemberDataList> mGroupMemberDataLists = new ArrayList<>();
        for (GetGroupInfoPerUserResponse.Data data : getGroupInfoPerUserResponse.getData()) {
            HomeActivityListData homeActivityListData = new HomeActivityListData();
            homeActivityListData.setGroupName(data.getGroupName());
            homeActivityListData.setCreatedBy(data.getCreatedBy());
            homeActivityListData.setGroupId(data.getId());
            homeActivityListData.setStatus(data.getStatus());
            homeActivityListData.setUpdatedBy(data.getUpdatedBy());
            homeActivityListData.setFrom(data.getSession().getFrom());
            homeActivityListData.setTo(data.getSession().getTo());
            homeActivityListData.setGroupOwnerName(data.getGroupOwner().get(0).getName());
            homeActivityListData.setGroupOwnerPhoneNumber(data.getGroupOwner().get(0).getPhone());
            homeActivityListData.setGroupOwnerUserId(data.getGroupOwner().get(0).getUserId());
            groupList.add(homeActivityListData);
        }
        for (GetGroupInfoPerUserResponse.Data data : getGroupInfoPerUserResponse.getData()) {
            if (!data.getStatus().equalsIgnoreCase(Constant.CLOSED)) {
                for (GetGroupInfoPerUserResponse.Consents mConsents : data.getConsents()) {
                    GroupMemberDataList groupMemberDataList = new GroupMemberDataList();
                    groupMemberDataList.setConsentId(mConsents.getConsentId());
                    groupMemberDataList.setNumber(mConsents.getPhone());
                    groupMemberDataList.setGroupAdmin(mConsents.isGroupAdmin());
                    groupMemberDataList.setGroupId(data.getId());
                    groupMemberDataList.setConsentStatus(mConsents.getStatus());
                    groupMemberDataList.setName(mConsents.getName());
                    groupMemberDataList.setUserId(mConsents.getUserId());
                    mGroupMemberDataLists.add(groupMemberDataList);
                }
            }
        }
        mDbManager.insertAllDataIntoGroupTable(groupList);
        mDbManager.insertGroupMemberDataInListFormat(mGroupMemberDataLists);
    }

    /**
     * Adapter Listener
     */
    private void adapterEventListener() {
        if (mAdapter != null) {
            mAdapter.setOnItemClickPagerListener(new ChooseGroupListAdapter.RecyclerViewClickListener() {
                @Override
                public void groupButtonClicked(HomeActivityListData homeActivityListData) {
                    groupId = homeActivityListData.getGroupId();
                    //addMemberToCreatedGroup(homeActivityListData.getGroupId());
                }
            });
        }
    }

    /**
     * Displays created group in recycler view
     */
    private void addDatainList() {
        List<HomeActivityListData> groupDetailList = mDbManager.getAllGroupDetail();
        List<HomeActivityListData> chooseGroupDataList = new ArrayList<>();
        for (HomeActivityListData data : groupDetailList) {
            if (data.getCreatedBy() != null && data.getCreatedBy().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getUserId())) {
                if (!data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME) && !data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_DEVICE_GROUP_NAME) && (data.getStatus().equalsIgnoreCase("Active") || data.getStatus().equalsIgnoreCase("Scheduled"))) {
                    HomeActivityListData homeActivityListData = new HomeActivityListData();
                    homeActivityListData.setGroupName(data.getGroupName());
                    homeActivityListData.setGroupId(data.getGroupId());
                    homeActivityListData.setStatus(data.getStatus());
                    homeActivityListData.setCreatedBy(data.getCreatedBy());
                    homeActivityListData.setUpdatedBy(data.getUpdatedBy());
                    homeActivityListData.setFrom(data.getFrom());
                    homeActivityListData.setTo(data.getTo());
                    homeActivityListData.setProfileImage(R.drawable.ic_group_button);
                    chooseGroupDataList.add(homeActivityListData);
                }
            }
        }

        if(chooseGroupDataList.size() == 0) {
            groupText.setVisibility(View.VISIBLE);
            cardViewGroup.setVisibility(View.INVISIBLE);
        }
        GridLayoutManager mLayoutManager = new GridLayoutManager(this,4);
        RecyclerView mRecyclerView = findViewById(R.id.chooseGroupRecyclerViewWithInfo);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChooseGroupListAdapter(chooseGroupDataList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void addMemberToCreatedGroup(String groupId) {
        this.createdGroupId = groupId;
        this.memberName = name;
        this.memberNumber = phoneNumber;
        this.isFromCreateGroup = false;
        this.isGroupMember = false;
        this.isFromDevice = false;
        addMemberInGroupAPICall();
    }
    private void createGroupAndAddContactDetails() {
        this.memberName = name;
        this.memberNumber = phoneNumber;
        this.isFromCreateGroup = false;
        this.isGroupMember = false;
        this.isFromDevice = false;
        createGroupAndAddContactAPICall(Constant.INDIVIDUAL_DEVICE_GROUP_NAME);
    }
}
