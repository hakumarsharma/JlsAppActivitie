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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GroupmemberListData;


import java.util.List;

/**
 * Display the group member inside the list
 */
public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.ViewHolder> {
    private List<GroupmemberListData> mList;

    public GroupMemberListAdapter(List<GroupmemberListData> mList) {
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_grpmember_list, parent, false);
        return new GroupMemberListAdapter.ViewHolder(itemView);
    }

    /**
     * A new ViewHolder that holds a View of the given view type
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull GroupMemberListAdapter.ViewHolder holder, int position) {
        holder.phone.setText(mList.get(position).getNumber());
        holder.name.setText(mList.get(position).getName());
        holder.profile.setImageResource(mList.get(position).getProfileImage());
    }

    /**
     * return The total number of items in this adapter
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
        public ImageView profile;

        /**
         * Constructor where we find element from .xml file
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.mobileNumber);
            name = itemView.findViewById(R.id.name);
            profile = itemView.findViewById(R.id.groupmemberImage);
        }
    }
}
