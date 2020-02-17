package com.jio.devicetracker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.TraceeListData;
import com.jio.devicetracker.database.pojo.TrackerListData;

import java.util.List;

public class TrackerListAdapter extends RecyclerView.Adapter<TrackerListAdapter.ViewHolder> {
    private List<TrackerListData> mList;
    private Context mContext;

    public TrackerListAdapter(Context mContext, List<TrackerListData> mList){
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_tracker_list, parent, false);

        return new TrackerListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackerListAdapter.ViewHolder holder, int position) {

        holder.phone.setText(mList.get(position).getNumber());
        holder.name.setText(mList.get(position).getName());
        holder.durationtime.setText(mList.get(position).getDurationTime());
        holder.expirytime.setText(mList.get(position).getExpiryTime());
        holder.profile.setImageResource(mList.get(position).getProfileImage());
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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.mobileNumber);
            name = itemView.findViewById(R.id.name);
            durationtime = itemView.findViewById(R.id.durationTime);
            expirytime = itemView.findViewById(R.id.expiryTime);
            profile = itemView.findViewById(R.id.traceeImage);
        }
    }
}

