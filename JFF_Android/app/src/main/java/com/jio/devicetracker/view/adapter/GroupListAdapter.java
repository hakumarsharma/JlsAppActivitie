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
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.request.DeleteGroupRequest;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.EditMemberActivity;
import com.jio.devicetracker.view.dashboard.GroupsFragment;
import com.jio.devicetracker.view.device.AddDeviceActivity;
import com.jio.devicetracker.view.location.MapFragment;
import com.jio.devicetracker.view.location.MapPeopleListFragment;

import java.util.List;

/**
 * Display the group member inside the list
 */
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
    private List<HomeActivityListData> mList;
    private Context mContext;
    private static RecyclerViewClickListener itemListener;
    private String userId;
    private DBManager mDbManager;
    private String groupId;
    private int position;
    private RelativeLayout groupOptLayout;

    public GroupListAdapter(List<HomeActivityListData> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        mDbManager = new DBManager(mContext);
        userId = mDbManager.getAdminLoginDetail().getUserId();
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage){
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_group_list_adapter, parent, false);
        return new GroupListAdapter.ViewHolder(itemView);
    }

    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(@NonNull GroupListAdapter.ViewHolder holder, int position) {
        HomeActivityListData data = mList.get(position);
        holder.groupName.setTypeface(Util.mTypeface(mContext, 5));
        holder.groupName.setText(data.getGroupName());
        holder.timeLeft.setText(Util.getInstance().getTrackingExpirirationDuration(data.getFrom(),data.getTo()));
        if(data.getGroupIcon() != null && !data.getGroupIcon().isEmpty()) {
            Resources res = mContext.getResources();
            int iconId = res.getIdentifier(data.getGroupIcon(), Constant.DRAWABLE, mContext.getPackageName());
            Drawable drawable = ContextCompat.getDrawable(mContext, iconId);
            holder.groupListmemberIcon.setImageDrawable(drawable);
        }else {
            holder.groupListmemberIcon.setBackgroundResource(R.drawable.ic_family_group);
        }
        holder.mListlayout.setOnClickListener(v -> itemListener.clickonListLayout(data));
        if (mList != null && !mList.isEmpty() &&  data.getConsentsCount() <= 4){
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
        }else {
            holder.icon1.setVisibility(View.VISIBLE);
            holder.icon2.setVisibility(View.VISIBLE);
            holder.icon3.setVisibility(View.VISIBLE);
            holder.icon4.setVisibility(View.VISIBLE);
            holder.numberOfUsers.setText("+ "+ (data.getConsentsCount() - 4) + " invited");
        }
    }

    /**
     * Interface to override methods in Groups Fragment to call these methods on particular item click
     */
    public interface RecyclerViewClickListener {
        void clickonListLayout(HomeActivityListData homeActivityListData);
    }

    /**
     * Register the listener
     *
     * @param mItemClickListener
     */
    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
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

        private TextView groupName;
        private ImageView menuIcon;
        private RelativeLayout groupOptLayout;
        private ImageView close;
        private ImageView icon1;
        private ImageView icon2;
        private ImageView icon3;
        private ImageView icon4;
        private TextView editOpt;
        private TextView addNewMember;
        private TextView deleteOpt;
        public CardView mListlayout;
        private ImageView groupListmemberIcon;
        private TextView timeLeft;
        private TextView numberOfUsers;

        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupListmemberIcon = itemView.findViewById(R.id.groupListmemberIcon);
            groupName = itemView.findViewById(R.id.groupName);
            menuIcon = itemView.findViewById(R.id.operationStatus);
            close = itemView.findViewById(R.id.close);
            editOpt = itemView.findViewById(R.id.edit);
            addNewMember = itemView.findViewById(R.id.addNewMember);
            deleteOpt = itemView.findViewById(R.id.deleteGroup);
            groupOptLayout = itemView.findViewById(R.id.oprationLayout);
            mListlayout = itemView.findViewById(R.id.groupListLayout);
            numberOfUsers = itemView.findViewById(R.id.numberOfUsers);
            timeLeft = itemView.findViewById(R.id.group_timeLeft);
            icon1 = itemView.findViewById(R.id.group_motherIcon);
            icon2 = itemView.findViewById(R.id.group_fatherIcon);
            icon3 = itemView.findViewById(R.id.group_kidIcon);
            icon4 = itemView.findViewById(R.id.group_dogIcon);
            menuIcon.setOnClickListener(this);
            close.setOnClickListener(this);
            editOpt.setOnClickListener(this);
            addNewMember.setOnClickListener(this);
            deleteOpt.setOnClickListener(this);
            GroupListAdapter.this.groupOptLayout = groupOptLayout;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.edit:
                    if (mList.isEmpty()){
                        showCustomAlertWithText(Constant.ADD_DETAILS_TO_TRACK);
                        return;
                    }
                    gotoEditMemberActivity(mList.get(position));
                    break;
                case R.id.close:
                    groupOptLayout.setVisibility(View.GONE);
                    break;
                case R.id.operationStatus:
                    groupOptLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.deleteGroup:
                    position = getAdapterPosition();
                    GroupListAdapter.this.groupOptLayout = groupOptLayout;
                    deleteGroupAPICall(mList.get(position));
                    break;
                case R.id.addNewMember:
                    position = getAdapterPosition();
                    Intent intent = new Intent(mContext, AddDeviceActivity.class);
                    intent.putExtra(Constant.GROUP_ID, mList.get(position).getGroupId());
                    mContext.startActivity(intent);
            }
        }
    }

    private void gotoEditMemberActivity(HomeActivityListData homeActivityListData) {
        Intent intent = new Intent(mContext, EditMemberActivity.class);
        intent.putExtra(Constant.GROUP_ID, homeActivityListData.getGroupId());
        intent.putExtra(Constant.GROUP_NAME, homeActivityListData.getGroupName());
        mContext.startActivity(intent);
    }

    //    Delete the Group and update the database
    private void deleteGroupAPICall(HomeActivityListData homeActivityListData) {
        groupId = homeActivityListData.getGroupId();
        Util.getInstance().showProgressBarDialog(mContext);
        GroupRequestHandler.getInstance(mContext).handleRequest(new DeleteGroupRequest(new DeleteGroupRequestSuccessListener(), new DeleteGroupRequestErrorListener(), groupId, userId));
    }

    /**
     * Delete Group Request API Call Success Listener and create new group if Session time is completed and Request Consent button is clicked
     */
    private class DeleteGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            mDbManager.deleteSelectedDataFromGroup(groupId);
            mDbManager.deleteSelectedDataFromGroupMember(groupId);
            groupOptLayout.setVisibility(View.GONE);
            Util.progressDialog.dismiss();
            removeItem(position);
            GroupsFragment.checkIsGroupPresent();
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
     * Called when we delete group
     *
     * @param adapterPosition
     */
    public void removeItem(int adapterPosition) {
        mList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }
}