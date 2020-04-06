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
import java.util.List;

public class ActiveSessionListAdapter extends RecyclerView.Adapter<ActiveSessionListAdapter.ViewHolder> {
    private List<ActiveSessionData> mList;
    private static RecyclerViewClickListener itemListener;

    public ActiveSessionListAdapter(List<ActiveSessionData> mList){
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_active_session_list, parent, false);
        return new ActiveSessionListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.phone.setText(mList.get(position).getNumber());
        holder.name.setText(mList.get(position).getName());
        holder.durationtime.setText(mList.get(position).getDurationTime());
        holder.expirytime.setText(mList.get(position).getExpiryTime());
        holder.profile.setImageResource(mList.get(position).getProfileImage());
        holder.relativeLayout.setOnClickListener(v -> {
            itemListener.clickOnListLayout(mList.get(position).getProfileImage(), mList.get(position).getName());
            return;
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView phone;
        public TextView name;
        public TextView durationtime;
        public TextView expirytime;
        public ImageView profile;
        public RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.mobileNumber);
            name = itemView.findViewById(R.id.name);
            durationtime = itemView.findViewById(R.id.durationTime);
            expirytime = itemView.findViewById(R.id.expiryTime);
            profile = itemView.findViewById(R.id.activeSessionImage);
            relativeLayout = itemView.findViewById(R.id.activeSessionLayout);
        }
    }

    public interface RecyclerViewClickListener {
        void clickOnListLayout(int selectedGroupName, String name);
    }

    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
    }

}
