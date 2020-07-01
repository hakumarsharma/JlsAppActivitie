package com.jio.devicetracker.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HomeActivityListData;

import java.util.List;

public class AlertsFragmentAdapter extends RecyclerView.Adapter<AlertsFragmentAdapter.ViewHolder>{

    private List<HomeActivityListData> homeActivityListData;

    public AlertsFragmentAdapter(List<HomeActivityListData> homeActivityListData) {
        this.homeActivityListData = homeActivityListData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.alerts_fragment_adapter, parent, false);
        return new AlertsFragmentAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertsFragmentAdapter.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return homeActivityListData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
