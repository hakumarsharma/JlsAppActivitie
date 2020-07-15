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
package com.example.nutapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutapp.R;
import com.example.nutapp.pojo.ShareDeviceData;

import java.util.List;

public class ShareDeviceDetailsAdapter extends RecyclerView.Adapter<ShareDeviceDetailsAdapter.ViewHolder> {
    List<ShareDeviceData> mDeviceData;

    public ShareDeviceDetailsAdapter(List<ShareDeviceData> mData) {
        this.mDeviceData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_device_detail_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.deviceName.setText(mDeviceData.get(position).getDeviceName());
        holder.deviceSharedetail.setText(mDeviceData.get(position).getDeviceShareDetail());
        holder.deviceSharedate.setText(mDeviceData.get(position).getDeviceShareDate());
    }

    @Override
    public int getItemCount() {
        return mDeviceData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView deviceName;
        public TextView deviceSharedetail;
        public TextView deviceSharedate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceSharedetail = itemView.findViewById(R.id.sharedDetails);
            deviceSharedate = itemView.findViewById(R.id.shareDate);
        }
    }
}
