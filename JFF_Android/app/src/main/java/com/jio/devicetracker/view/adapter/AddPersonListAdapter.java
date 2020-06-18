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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;

import java.util.List;

public class AddPersonListAdapter extends RecyclerView.Adapter<AddPersonListAdapter.ViewHolder> {

    private List<GroupMemberResponse.Data> mData;
    private static RecyclerViewClickListener itemListener;

    /**
     * Constructor to add devices in home screen
     *
     * @param mData
     */
    public AddPersonListAdapter(List mData) {
        this.mData = mData;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_addperson, parent, false);

        return new ViewHolder(itemView);
    }

    /**
     * Interface to override methods in ActiveSessionActivity to call this methods on particular item click
     */
    public interface RecyclerViewClickListener {
        void onDeleteMemberClicked(View v,int position,String groupId, GroupMemberResponse.Data data);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupMemberResponse.Data data = mData.get(position);
        holder.contactName.setText(data.getName());
        holder.deleteButton.setOnClickListener(v -> itemListener.onDeleteMemberClicked(holder.deleteButton, position, data.getGroupId(), data));
    }

    /**
     * Register the listener
     * @param mItemClickListener
     */
    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView contactImage;
        public TextView contactName;
        public Button deleteButton;
        public CardView mListLayout;

        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contactImage);
            contactName = itemView.findViewById(R.id.contactName);
            mListLayout = itemView.findViewById(R.id.listLayout);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }

    /**
     * Called when we remove device from active session screen
     * @param adapterPosition
     */
    public void removeItem(int adapterPosition) {
        mData.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }


}