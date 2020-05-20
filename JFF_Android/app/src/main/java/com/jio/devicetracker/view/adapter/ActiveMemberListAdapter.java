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
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.util.Util;

import java.util.List;

/**
 * Display the member's list available inside group
 */
public class ActiveMemberListAdapter extends RecyclerView.Adapter<ActiveMemberListAdapter.ViewHolder> {
    private List<GroupMemberDataList> mList;
    private static RecyclerViewClickListener itemListener;

    /**
     * Constructor to add devices inside group
     *
     * @param mList
     */
    public ActiveMemberListAdapter(List<GroupMemberDataList> mList) {
        this.mList = mList;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_member_list, parent, false);

        return new ActiveMemberListAdapter.ViewHolder(itemView);
    }

    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ActiveMemberListAdapter.ViewHolder holder, int position) {
        if (mList.get(position).getGroupOwnerName() != null) {
            holder.phone.setText(mList.get(position).getGroupOwnerNumber());
            holder.name.setText(mList.get(position).getGroupOwnerName());
            holder.profile.setImageResource(mList.get(position).getProfileImage());
        } else {
            holder.phone.setText(mList.get(position).getNumber());
            holder.name.setText(mList.get(position).getName());
            holder.profile.setImageResource(mList.get(position).getProfileImage());
            holder.durationtime.setText(Util.getInstance().getTrackingExpirirationDuration(mList.get(position).getFrom(), mList.get(position).getTo()));
            holder.expirytime.setText(Util.getInstance().getTrackingExpirirationDuration(Util.getInstance().convertTimeToEpochtime(), mList.get(position).getTo()));
        }
        holder.activeMemberOptions.setOnClickListener(v -> itemListener.onPopupMenuClicked(holder.activeMemberOptions, position, mList.get(position)));
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
     * Interface to override methods in Dashboard to call those methods on particular item click
     */
    public interface RecyclerViewClickListener {
        void onPopupMenuClicked(View v, int position, GroupMemberDataList groupMemberDataList);
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
        public TextView activeMemberOptions;

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
            profile = itemView.findViewById(R.id.traceeImage);
            activeMemberOptions = itemView.findViewById(R.id.activeMemberOptions);
        }
    }

    /**
     * Called when we remove device from active member screen
     *
     * @param adapterPosition
     */
    public void removeItem(int adapterPosition) {
        mList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }

}

