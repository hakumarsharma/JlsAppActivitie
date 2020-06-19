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
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Util;

import java.util.List;

public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.ViewHolder> {
    private List<GroupMemberDataList> mList;
    private Context mContext;

    public PeopleListAdapter(List<GroupMemberDataList> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
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
    public PeopleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_people_list_adapter, parent, false);
        return new PeopleListAdapter.ViewHolder(itemView);
    }



    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(@NonNull PeopleListAdapter.ViewHolder holder, int position) {
        GroupMemberDataList data = mList.get(position);
        holder.memberName.setTypeface(Util.mTypeface(mContext, 5));
        holder.memberName.setText(data.getName());
        holder.memberAddress.setText(data.getAddress());
        holder.memberAddress.setTypeface(Util.mTypeface(mContext, 3));
        holder.memberStatus.setText(data.getConsentStatus());
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

        private TextView memberName;
        private ImageView memberIcon;
        private TextView memberStatus;
        private TextView memberAddress;
        private ImageView menuIcon;
        private ImageView closeBtn;
        private TextView editText;
        private TextView removeFromGroupText;
        private TextView shareInvite;
        private RelativeLayout layoutOps;
        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.mapMemberName);
            memberIcon = itemView.findViewById(R.id.mapMemberIcon);
            memberStatus = itemView.findViewById(R.id.mapMemberStatus);
            memberAddress = itemView.findViewById(R.id.memberAddress);
            menuIcon = itemView.findViewById(R.id.consentStatus);
            closeBtn = itemView.findViewById(R.id.close);
            editText = itemView.findViewById(R.id.edit);
            layoutOps = itemView.findViewById(R.id.oprationLayout);
            removeFromGroupText = itemView.findViewById(R.id.remove_from_group);
            shareInvite = itemView.findViewById(R.id.share_invite);
            editText.setOnClickListener(this);
            removeFromGroupText.setOnClickListener(this);
            shareInvite.setOnClickListener(this);
            menuIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){

                case R.id.close:
                    layoutOps.setVisibility(View.GONE);
                    break;
                case R.id.consentStatus:
                    layoutOps.setVisibility(View.VISIBLE);
                    break;
                case R.id.edit:
                    break;
                case R.id.remove_from_group:
                    break;
                case R.id.share_invite:
                    break;

            }

        }
    }
}
