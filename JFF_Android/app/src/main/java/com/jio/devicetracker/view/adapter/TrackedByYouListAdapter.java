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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.request.DeleteGroupRequest;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.menu.ActiveMemberActivity;

import java.util.List;

public class TrackedByYouListAdapter extends RecyclerView.Adapter<TrackedByYouListAdapter.ViewHolder> {
    private List<HomeActivityListData> mList;
    private Context mContext;
    private RelativeLayout trackedByYouOprationLayout;
    private DBManager mDbManager;
    private String groupId;
    private int position;

    /**
     * Constructor to display the active session devices list
     *
     * @param mList
     */
    public TrackedByYouListAdapter(List<HomeActivityListData> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        mDbManager = new DBManager(mContext);
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage) {
        CustomAlertActivity alertActivity = new CustomAlertActivity(mContext);
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    /**
     * Binds the given View to the position
     *
     * @param parent
     * @param viewType
     * @return View Holder object
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_by_you_list_apater, parent, false);
        return new TrackedByYouListAdapter.ViewHolder(itemView);
    }

    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeActivityListData data = mList.get(position);
        if (data.getConsentId() != null) {
            holder.name.setText(data.getName());
            holder.profile.setImageResource(R.drawable.mother);
        } else {
            holder.profile.setImageResource(R.drawable.ic_family_group);
            holder.name.setText(data.getGroupName());
        }
        if (mList != null && !mList.isEmpty() &&  data.getConsentsCount() <= 4) {
            switch (data.getConsentsCount()) {
                case 1:
                    holder.icon1.setVisibility(View.VISIBLE);
                    holder.icon2.setVisibility(View.INVISIBLE);
                    holder.icon3.setVisibility(View.INVISIBLE);
                    holder.icon4.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    holder.icon1.setVisibility(View.VISIBLE);
                    holder.icon2.setVisibility(View.VISIBLE);
                    holder.icon3.setVisibility(View.INVISIBLE);
                    holder.icon4.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    holder.icon1.setVisibility(View.VISIBLE);
                    holder.icon2.setVisibility(View.VISIBLE);
                    holder.icon3.setVisibility(View.VISIBLE);
                    holder.icon4.setVisibility(View.INVISIBLE);
                    break;
                case 4:
                    holder.icon1.setVisibility(View.VISIBLE);
                    holder.icon2.setVisibility(View.VISIBLE);
                    holder.icon3.setVisibility(View.VISIBLE);
                    holder.icon4.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            holder.numberOfUsers.setText("");
        }   else {
            holder.icon1.setVisibility(View.VISIBLE);
            holder.icon2.setVisibility(View.VISIBLE);
            holder.icon3.setVisibility(View.VISIBLE);
            holder.icon4.setVisibility(View.VISIBLE);
            holder.numberOfUsers.setText("+ "+ (data.getConsentsCount() - 4) + " invited");
        }
    }

    /**
     * return The total number of items in this adapter
     *
     * @return size
     */
    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public ImageView icon1;
        public ImageView icon2;
        public ImageView icon3;
        public ImageView icon4;
        public ImageView profile;
        public RelativeLayout trackedByYouOprationLayout;
        public ImageView trackedByYouOperationStatus;
        public ImageView trackedByYouClose;
        private TextView trackedByYouEdit;
        private TextView deleteAllMembers;
        private TextView numberOfUsers;

        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.groupName);
            profile = itemView.findViewById(R.id.groupmemberIcon);
            trackedByYouOprationLayout = itemView.findViewById(R.id.trackedByYouOprationLayout);
            trackedByYouClose = itemView.findViewById(R.id.trackedByYouClose);
            trackedByYouClose.setOnClickListener(this);
            trackedByYouOperationStatus = itemView.findViewById(R.id.trackedByYouOperationStatus);
            trackedByYouOperationStatus.setOnClickListener(this);
            icon1 = itemView.findViewById(R.id.session_motherIcon);
            icon2 = itemView.findViewById(R.id.session_fatherIcon);
            icon3 = itemView.findViewById(R.id.session_kidIcon);
            icon4 = itemView.findViewById(R.id.session_dogIcon);
            numberOfUsers = itemView.findViewById(R.id.session_numberOfUsers);
            trackedByYouEdit = itemView.findViewById(R.id.trackedByYouEdit);
            trackedByYouEdit.setOnClickListener(this);
            deleteAllMembers = itemView.findViewById(R.id.deleteAllMembers);
            deleteAllMembers.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.trackedByYouOperationStatus:
                    if (mList.get(getAdapterPosition()).getConsentStaus() != null) {
                        deleteAllMembers.setText("Delete member");
                    }
                    trackedByYouOprationLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.trackedByYouClose:
                    trackedByYouOprationLayout.setVisibility(View.GONE);
                    break;
                case R.id.trackedByYouEdit:
                    TrackedByYouListAdapter.this.trackedByYouOprationLayout = trackedByYouOprationLayout;
                    gotoActiveMemberActivity(mList.get(getAdapterPosition()).getGroupName(), mList.get(getAdapterPosition()).getGroupId());
                    break;
                case R.id.deleteAllMembers:
                    position = getAdapterPosition();
                    groupId = mList.get(position).getGroupId();
                    TrackedByYouListAdapter.this.trackedByYouOprationLayout = trackedByYouOprationLayout;
                    makeDeleteGroupAPICall(groupId);
                    break;
            }
        }
    }

    /**
     * Delete the Group and update the database
     */
    private void makeDeleteGroupAPICall(String groupId) {
        trackedByYouOprationLayout.setVisibility(View.GONE);
        Util.getInstance().showProgressBarDialog(mContext);
        GroupRequestHandler.getInstance(mContext).handleRequest(new DeleteGroupRequest(new DeleteGroupRequestSuccessListener(), new DeleteGroupRequestErrorListener(), groupId, mDbManager.getAdminLoginDetail().getUserId()));
    }

    /**
     * Delete Group Request API Call Success Listener and create new group if Session time is completed and Request Consent button is clicked
     */
    private class DeleteGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            mDbManager.deleteSelectedDataFromGroup(groupId);
            mDbManager.deleteSelectedDataFromGroupMember(groupId);
            removeItem(position);
        }
    }

    /**
     * Delete Group Request API Call Error Listener
     */
    private class DeleteGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            showCustomAlertWithText(Constant.GROUP_DELETION_FAILURE);
        }
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

    private void gotoActiveMemberActivity(String groupName, String groupId) {
        trackedByYouOprationLayout.setVisibility(View.GONE);
        Intent intent = new Intent(mContext, ActiveMemberActivity.class);
        intent.putExtra(Constant.GROUP_NAME, groupName);
        intent.putExtra(Constant.GROUP_ID, groupId);
        mContext.startActivity(intent);
    }

}
