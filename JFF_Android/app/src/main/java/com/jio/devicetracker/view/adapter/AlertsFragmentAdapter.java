package com.jio.devicetracker.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.AlertHistoryData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;

import java.util.List;

public class AlertsFragmentAdapter extends RecyclerView.Adapter<AlertsFragmentAdapter.ViewHolder>{

    private List<AlertHistoryData> mAlertHistoryData;
    private String dateStatus;

    public AlertsFragmentAdapter(List<AlertHistoryData> mAlertHistoryData) {
        this.mAlertHistoryData = mAlertHistoryData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.alerts_fragment_adapter, parent, false);
        return new AlertsFragmentAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertsFragmentAdapter.ViewHolder holder, int position) {
        holder.dateTextView.setText(mAlertHistoryData.get(position).getDate());
        holder.geofenceAddress.setText(mAlertHistoryData.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return mAlertHistoryData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView dateTextView;
        private TextView geofenceAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            geofenceAddress = itemView.findViewById(R.id.geofenceAddress);
        }
    }
}
