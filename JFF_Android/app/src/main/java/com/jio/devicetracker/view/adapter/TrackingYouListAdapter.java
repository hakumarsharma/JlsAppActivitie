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
import com.jio.devicetracker.database.pojo.TrackingYou;
import com.jio.devicetracker.database.pojo.request.SearchEventRequest;
import com.jio.devicetracker.database.pojo.response.SearchEventResponse;
import com.jio.devicetracker.network.ExitRemoveDeleteAPI;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.location.LocationActivity;

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

    private List<TrackingYou> mList;
    private Context mContext;
    private RelativeLayout trackingYouOprationLayout;
    private String userId;
    private DBManager mDbManager;
    private String groupId;
    private String name;

    public TrackingYouListAdapter(List<TrackingYou> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        mDbManager = new DBManager(mContext);
        userId = mDbManager.getAdminLoginDetail().getUserId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_you_list_apater, parent, false);
        return new TrackingYouListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrackingYou trackingYou = mList.get(position);
        holder.trackingYouGroupOwnerName.setText(trackingYou.getGroupOwnerName());
        if (trackingYou.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_DEVICE_GROUP_NAME)
                || trackingYou.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)) {
            holder.trackingYouGroupName.setText(Constant.INDIVIDUAL_MEMBER);
        } else {
            holder.trackingYouGroupName.setText(trackingYou.getGroupName());
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
        private TextView trackingYouGroupName;
        private TextView reverseTrack;

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
            trackingYouGroupName = itemView.findViewById(R.id.trackingYouGroupName);
            reverseTrack = itemView.findViewById(R.id.reverseTrack);
            reverseTrack.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()) {
                case R.id.trackingYouOperationStatus:
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
                    makeGetLocationAPICall(mList.get(position));
                    break;
            }
        }
    }

    /**
     * find locations for group members
     *
     * @param trackingYou
     */
    private void makeGetLocationAPICall(TrackingYou trackingYou) {
        this.groupId = trackingYou.getGroupId();
        name = trackingYou.getGroupOwnerName();
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
            List<SearchEventResponse.Data> mList = searchEventResponse.getData();
            if (!mList.isEmpty()) {
                for (SearchEventResponse.Data data : mList) {
                        MapData mapData = new MapData();
                        mapData.setLatitude(data.getLocation().getLat());
                        mapData.setLongitude(data.getLocation().getLng());
                        mapData.setName(name);
                        mapDataList.add(mapData);
                }
            }
            goToMapActivity(mapDataList);
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
            Util.alertDilogBox(Constant.FETCH_LOCATION_ERROR, Constant.ALERT_TITLE, mContext);
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
                    Util.alertDilogBox(Constant.REMOVE_FROM_GROUP_FAILURE, Constant.ALERT_TITLE, mContext);
                    trackingYouOprationLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.REMOVE_FROM_GROUP_FAILURE, Constant.ALERT_TITLE, mContext);
                trackingYouOprationLayout.setVisibility(View.GONE);
            }
        });
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
