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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.NotificationData;
import com.jio.devicetracker.util.Constant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationFragmentAdapter extends RecyclerView.Adapter<NotificationFragmentAdapter.ViewHolder> {
    private List<NotificationData> mNotificationsHistoryData;
    private String currentDateValues = Constant.EMPTY_STRING;

    public NotificationFragmentAdapter(List<NotificationData> mAlertHistoryData) {
        this.mNotificationsHistoryData = mAlertHistoryData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_fragment_adapter, parent, false);
        return new NotificationFragmentAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationFragmentAdapter.ViewHolder holder, int position) {
        try {
            NotificationData mNotificationHistoryData = mNotificationsHistoryData.get(position);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
            Long oldDateLong = simpleDateFormat.parse(mNotificationHistoryData.getNotificationDate()).getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            Long todayDateLong = sdf.parse(new Date().toString()).getTime();
            long diffInMillies = todayDateLong - oldDateLong;
            int diffDays = (int) (diffInMillies / (24 * 60 * 60 * 1000));
            if (diffDays == 0) {
                if(!currentDateValues.equalsIgnoreCase(Constant.TODAY)) {
                    holder.notificationDateTextView.setText(Constant.TODAY);
                    currentDateValues = Constant.TODAY;
                }
            } else if (diffDays == 1) {
                if(!currentDateValues.equalsIgnoreCase(Constant.YESTERDAY)) {
                    holder.notificationDateTextView.setText(Constant.YESTERDAY);
                    currentDateValues = Constant.YESTERDAY;
                }
            } else {
                holder.notificationDateTextView.setText(mNotificationHistoryData.getNotificationDate());
            }
            holder.notificationTime.setText(mNotificationHistoryData.getNotificationDate());
            holder.notificationTitle.setText(mNotificationHistoryData.getNotificationTitle());
            holder.notificationMessage.setText(mNotificationHistoryData.getNotificationMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mNotificationsHistoryData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView notificationTitle;
        private TextView notificationMessage;
        private TextView notificationTime;
        private TextView notificationDateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
            notificationTime = itemView.findViewById(R.id.notificationTime);
            notificationDateTextView = itemView.findViewById(R.id.notificationDateTextView);
        }
    }
}
