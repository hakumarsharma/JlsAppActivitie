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
import com.jio.devicetracker.database.pojo.TrackingYou;

import java.util.List;

public class TrackingYouListAdapter extends RecyclerView.Adapter<TrackingYouListAdapter.ViewHolder> {

    private List<TrackingYou> mList;

    public TrackingYouListAdapter(List<TrackingYou> mList) {
        this.mList = mList;
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
        holder.trackingYouGroupName.setText(trackingYou.getGroupOwnerName());
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
        public TextView trackingYouGroupName;

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
            trackingYouGroupName = itemView.findViewById(R.id.trackingYouGroupName);
        }

        @Override
        public void onClick(View v) {
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
                    // Todo
                    break;

            }
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

}
