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

package com.jio.devicetracker.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.ExitRemovedGroupData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.database.pojo.SearchEventData;
import com.jio.devicetracker.database.pojo.request.SearchEventRequest;
import com.jio.devicetracker.database.pojo.response.SearchEventResponse;
import com.jio.devicetracker.network.ExitRemoveDeleteAPI;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.location.LocationActivity;
import com.jio.devicetracker.view.location.ShareLocationActivity;
import com.jio.devicetracker.view.menu.ActiveMemberActivity;

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

public class TrackingYouListAdapter extends RecyclerView.Adapter<TrackingYouListAdapter.ViewHolder> {

    private List<HomeActivityListData> mList;
    private Context mContext;
    private RelativeLayout trackingYouOprationLayout;
    private String userId;
    private DBManager mDbManager;
    private String groupId;
    private boolean gotoSharedLocation;
    private String memberName;
    private String deviceNumber;

    public TrackingYouListAdapter(List<HomeActivityListData> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        mDbManager = new DBManager(mContext);
        userId = mDbManager.getAdminLoginDetail().getUserId();
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage) {
        CustomAlertActivity alertActivity = new CustomAlertActivity(mContext);
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_you_list_apater, parent, false);
        return new TrackingYouListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeActivityListData trackingYou = mList.get(position);
        holder.trackingYouGroupOwnerName.setText(trackingYou.getGroupName());
        if (trackingYou.getConsentId() != null) {
            holder.trackingYouGroupMemberIcon.setImageResource(R.drawable.default_user);
        } else {
            holder.trackingYouGroupMemberIcon.setImageResource(R.drawable.ic_family_group);
        }
        if (mList != null && !mList.isEmpty() && trackingYou.getConsentsCount() <= 4) {
            switch (trackingYou.getConsentsCount()) {
                case 0:
                    holder.motherIcon.setVisibility(View.INVISIBLE);
                    holder.fatherIcon.setVisibility(View.INVISIBLE);
                    holder.kidIcon.setVisibility(View.INVISIBLE);
                    holder.dogIcon.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    holder.motherIcon.setVisibility(View.VISIBLE);
                    holder.fatherIcon.setVisibility(View.INVISIBLE);
                    holder.kidIcon.setVisibility(View.INVISIBLE);
                    holder.dogIcon.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    if (trackingYou.getConsentId() != null) {
                        holder.motherIcon.setVisibility(View.INVISIBLE);
                        holder.fatherIcon.setVisibility(View.INVISIBLE);
                        holder.kidIcon.setVisibility(View.INVISIBLE);
                        holder.dogIcon.setVisibility(View.INVISIBLE);
                    } else {
                        holder.motherIcon.setVisibility(View.VISIBLE);
                        holder.fatherIcon.setVisibility(View.VISIBLE);
                        holder.kidIcon.setVisibility(View.INVISIBLE);
                        holder.dogIcon.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 3:
                    holder.motherIcon.setVisibility(View.VISIBLE);
                    holder.fatherIcon.setVisibility(View.VISIBLE);
                    holder.kidIcon.setVisibility(View.VISIBLE);
                    holder.dogIcon.setVisibility(View.INVISIBLE);
                    break;
                case 4:
                    holder.motherIcon.setVisibility(View.VISIBLE);
                    holder.fatherIcon.setVisibility(View.VISIBLE);
                    holder.kidIcon.setVisibility(View.VISIBLE);
                    holder.dogIcon.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            holder.trackingYouNumberOfUsers.setText("");
        } else {
            holder.motherIcon.setVisibility(View.VISIBLE);
            holder.fatherIcon.setVisibility(View.VISIBLE);
            holder.kidIcon.setVisibility(View.VISIBLE);
            holder.dogIcon.setVisibility(View.VISIBLE);
            holder.trackingYouNumberOfUsers.setText("+ " + (trackingYou.getConsentsCount() - 4) + " invited");
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView trackingYouOperationStatus;
        private RelativeLayout trackingYouOprationLayout;
        private ImageView trackingYouclose;
        private TextView disableTracking;
        private TextView leaveGroup;
        public TextView trackingYouGroupOwnerName;
        private TextView reverseTrack;
        private TextView viewMembers;
        private ImageView trackingYouGroupMemberIcon;
        private View reverseTrackLine;
        private ImageView motherIcon;
        private ImageView fatherIcon;
        private ImageView kidIcon;
        private ImageView dogIcon;
        private TextView trackingYouNumberOfUsers;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackingYouOperationStatus = itemView.findViewById(R.id.trackingYouOperationStatus);
            trackingYouOperationStatus.setOnClickListener(this);
            trackingYouOprationLayout = itemView.findViewById(R.id.trackingYouOprationLayout);
            trackingYouclose = itemView.findViewById(R.id.trackingYouclose);
            trackingYouclose.setOnClickListener(this);
            disableTracking = itemView.findViewById(R.id.disableTracking);
            disableTracking.setOnClickListener(this);
            leaveGroup = itemView.findViewById(R.id.leaveGroup);
            leaveGroup.setOnClickListener(this);
            trackingYouGroupOwnerName = itemView.findViewById(R.id.trackingYouGroupOwnerName);
            reverseTrack = itemView.findViewById(R.id.reverseTrack);
            reverseTrack.setOnClickListener(this);
            viewMembers = itemView.findViewById(R.id.viewMembers);
            viewMembers.setOnClickListener(this);
            trackingYouGroupMemberIcon = itemView.findViewById(R.id.trackingYouGroupMemberIcon);
            reverseTrackLine = itemView.findViewById(R.id.reverseTrackLine);
            motherIcon = itemView.findViewById(R.id.motherIcon);
            fatherIcon = itemView.findViewById(R.id.fatherIcon);
            kidIcon = itemView.findViewById(R.id.kidIcon);
            dogIcon = itemView.findViewById(R.id.dogIcon);
            trackingYouNumberOfUsers = itemView.findViewById(R.id.trackingYou_numberOfUsers);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()) {
                case R.id.trackingYouOperationStatus:
                    if (mList.get(position).getConsentId() != null) {
                        viewMembers.setVisibility(View.GONE);
                        reverseTrack.setPadding(0, 0, 0, 16);
                        reverseTrackLine.setVisibility(View.GONE);
                    }
                    trackingYouOprationLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.trackingYouclose:
                    trackingYouOprationLayout.setVisibility(View.GONE);
                    break;
                case R.id.disableTracking:
                    // Todo
                    break;
                case R.id.leaveGroup:
                    TrackingYouListAdapter.this.trackingYouOprationLayout = trackingYouOprationLayout;
                    makeExitAPICall(mDbManager.getAdminLoginDetail().getPhoneNumber(), mList.get(position).getGroupId(), position);
                    break;
                case R.id.reverseTrack:
                    TrackingYouListAdapter.this.trackingYouOprationLayout = trackingYouOprationLayout;
                    HomeActivityListData mData = mList.get(position);
                    if(mData.getConsentId() != null) {
                        gotoSharedLocation = true;
                    }
                    memberName = mData.getName();
                    deviceNumber = mData.getNumber();
                    makeGetLocationAPICall(mData);
                    break;
                case R.id.viewMembers:
                    TrackingYouListAdapter.this.trackingYouOprationLayout = trackingYouOprationLayout;
                    getAllMembersOfGroup(mList.get(position));
                    break;
                default:
                    // Todo
                    break;
            }
        }
    }

    /**
     * find locations for group members
     *
     * @param trackingYou
     */
    private void makeGetLocationAPICall(HomeActivityListData trackingYou) {
        this.groupId = trackingYou.getGroupId();
        SearchEventData searchEventData = new SearchEventData();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.LOCATION);
        mList.add(Constant.SOS);
        searchEventData.setTypes(mList);
        Util.getInstance().showProgressBarDialog(mContext);
        GroupRequestHandler.getInstance(mContext).handleRequest(new SearchEventRequest(new SearchEventRequestSuccessListener(), new SearchEventRequestErrorListener(), searchEventData, userId, trackingYou.getGroupId(), Constant.GET_LOCATION_URL));
    }


    /**
     * Search Event Request API call Success Listener
     */
    private class SearchEventRequestSuccessListener implements com.android.volley.Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            trackingYouOprationLayout.setVisibility(View.GONE);
            SearchEventResponse searchEventResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchEventResponse.class);
            List<MapData> mapDataList = new ArrayList<>();
            List<GroupMemberDataList> grpMembersOfParticularGroupId = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupId);
            List<SearchEventResponse.Data> mList = searchEventResponse.getData();
            if (!mList.isEmpty()) {
                for (SearchEventResponse.Data data : mList) {
                    if (!data.getUserId().equalsIgnoreCase(userId)) {
                        for (GroupMemberDataList grpMembers : grpMembersOfParticularGroupId) {
                            if (grpMembers.getUserId().equalsIgnoreCase(data.getUserId())) {
                                MapData mapData = new MapData();
                                mapData.setLatitude(data.getLocation().getLat());
                                mapData.setLongitude(data.getLocation().getLng());
                                mapData.setName(grpMembers.getName());
                                mapData.setConsentId(grpMembers.getConsentId());
                                mapDataList.add(mapData);
                            }
                        }
                    }
                }
            }
            if(gotoSharedLocation) {
                gotoSharedLocation = false;
                Intent intent = new Intent(mContext, ShareLocationActivity.class);
                intent.putParcelableArrayListExtra(Constant.MAP_DATA, (ArrayList<? extends Parcelable>) mapDataList);
                intent.putExtra(Constant.GROUP_STATUS, Constant.ACTIVE);
                intent.putExtra(Constant.MEMBER_NAME, memberName);
                intent.putExtra(Constant.GROUP_ID, groupId);
                intent.putExtra(Constant.DEVICE_NUMBER,deviceNumber);
                intent.putExtra(Constant.CONSENT_STATUS, Constant.APPROVED);
                mContext.startActivity(intent);
            } else {
                goToMapActivity(mapDataList);
            }
        }
    }

    /**
     * Search Event Request API Call Error listener
     */
    private class SearchEventRequestErrorListener implements com.android.volley.Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            trackingYouOprationLayout.setVisibility(View.GONE);
            showCustomAlertWithText(Constant.FETCH_LOCATION_ERROR);
        }

    }

    /**
     * Navigates to the Map activity
     */
    private void goToMapActivity(List<MapData> mapDataList) {
        Intent intent = new Intent(mContext, LocationActivity.class);
        intent.putParcelableArrayListExtra(Constant.MAP_DATA, (ArrayList<? extends Parcelable>) mapDataList);
        intent.putExtra(Constant.GROUP_ID, groupId);
        intent.putExtra(Constant.GROUP_STATUS, Constant.ACTIVE);
        mContext.startActivity(intent);
    }

    /**
     * Make Exit from group API Call using retrofit
     *
     * @param phoneNumber
     * @param groupId
     */
    private void makeExitAPICall(String phoneNumber, String groupId, int position) {
        DBManager mDbManager = new DBManager(mContext);
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
        Util.getInstance().showProgressBarDialog(mContext);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Util.progressDialog.dismiss();
                    Toast.makeText(mContext, Constant.EXIT_FROM_GROUP_SUCCESS, Toast.LENGTH_SHORT).show();
                    mDbManager.deleteSelectedDataFromGroupMember(groupId);
                    removeItem(position);
                    trackingYouOprationLayout.setVisibility(View.GONE);
                } else {
                    Util.progressDialog.dismiss();
                    showCustomAlertWithText(Constant.REMOVE_FROM_GROUP_FAILURE);
                    trackingYouOprationLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.progressDialog.dismiss();
                showCustomAlertWithText(Constant.REMOVE_FROM_GROUP_FAILURE);
                trackingYouOprationLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getAllMembersOfGroup(HomeActivityListData homeActivityListData) {
        trackingYouOprationLayout.setVisibility(View.GONE);
        Intent intent = new Intent(mContext, ActiveMemberActivity.class);
        intent.putExtra(Constant.GROUP_NAME, homeActivityListData.getGroupName());
        intent.putExtra(Constant.GROUP_ID, homeActivityListData.getGroupId());
        intent.putExtra(Constant.ACTIVE_MEMBER_TITLE, Constant.ACTIVE_MEMBER_TITLE);
        mContext.startActivity(intent);
    }

    /**
     * Called when we remove device from active session screen
     *
     * @param adapterPosition
     */
    public void removeItem(int adapterPosition) {
        mList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }

}
