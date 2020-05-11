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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.ActiveSessionData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Constant;

import java.util.List;

public class ActiveSessionListAdapter extends RecyclerView.Adapter<ActiveSessionListAdapter.ViewHolder> {
    private List mList;
    private static RecyclerViewClickListener itemListener;

    /**
     * Constructor to display the active session devices list
     *
     * @param mList
     */
    public ActiveSessionListAdapter(List mList) {
        this.mList = mList;
    }

    /**
     * Binds the given View to the position
     * @param parent
     * @param viewType
     * @return View Holder object
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_active_session_list, parent, false);
        return new ActiveSessionListAdapter.ViewHolder(itemView);
    }

    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mList.get(position).getClass().getName().equalsIgnoreCase(Constant.GROUP_NAME_CLASS_NAME)) {
            HomeActivityListData data = (HomeActivityListData) mList.get(position);
            holder.profile.setImageResource(R.drawable.ic_group_button);
            holder.name.setText(data.getGroupName());
            holder.relativeLayout.setOnClickListener(v -> {
                itemListener.clickOnListLayout(data.getProfileImage(), data.getGroupName(), data.getGroupId()
                        , data.getCreatedBy());
                return;
            });
            holder.activeSessionOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onPopupMenuClickedForGroup(holder.activeSessionOptions, position, data.getCreatedBy(), data.getGroupId());
                }
            });
        } else if (mList.get(position).getClass().getName().equalsIgnoreCase(Constant.GROUP_MEMBER_CLASS_NAME)) {
            GroupMemberDataList data = (GroupMemberDataList) mList.get(position);
            holder.profile.setImageResource(R.drawable.ic_user);
            holder.name.setText(data.getName());
            holder.phone.setText(data.getNumber());
            holder.relativeLayout.setOnClickListener(v -> {
                itemListener.clickOnListLayout(data.getProfileImage(), data.getName(), data.getConsentId(), "");
                return;
            });
            holder.activeSessionOptions.setOnClickListener(v -> itemListener.onPopupMenuClickedForMember(holder.activeSessionOptions, position, data.getNumber(), data.getGroupId(), data.getConsentId()));
        }
        /*holder.durationtime.setText(mList.get(position).getDurationTime());
        holder.expirytime.setText(mList.get(position).getExpiryTime());*/
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
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView phone;
        public TextView name;
        public TextView durationtime;
        public TextView expirytime;
        public ImageView profile;
        public RelativeLayout relativeLayout;
        public TextView activeSessionOptions;

        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.mobileNumber);
            name = itemView.findViewById(R.id.name);
            durationtime = itemView.findViewById(R.id.durationTime);
            expirytime = itemView.findViewById(R.id.expiryTime);
            profile = itemView.findViewById(R.id.activeSessionImage);
            relativeLayout = itemView.findViewById(R.id.activeSessionLayout);
            activeSessionOptions = itemView.findViewById(R.id.activeSessionOptions);
        }
    }

    /**
     * Interface to override methods in ActiveSessionActivity to call this methods on particular item click
     */
    public interface RecyclerViewClickListener {
        void clickOnListLayout(int selectedGroupName, String name, String groupId, String createdBy);
        void onPopupMenuClickedForMember(View v, int position, String phoneNumber, String groupId, String consentId);
        void onPopupMenuClickedForGroup(View v, int position, String createdBy, String groupId);
    }

    /**
     * Register the listener
     * @param mItemClickListener
     */
    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
    }

    /**
     * Called when we remove device from active session screen
     * @param adapterPosition
     */
    public void removeItem(int adapterPosition) {
        mList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }

}
