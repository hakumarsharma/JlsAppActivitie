package com.jio.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.database.pojo.AddedDeviceData;
import com.jio.database.pojo.MultipleselectData;
import com.jio.view.R;

import java.util.List;

public class TrackerDeviceListAdapter extends RecyclerView.Adapter<TrackerDeviceListAdapter.ViewHolder> {

    private Context mContext;
    private List<AddedDeviceData> mData;
    private static RecyclerViewClickListener itemListener;
    private int count;
    private boolean isSelect;
    private MultipleselectData mSelectData;

    public TrackerDeviceListAdapter(Context mContext, List<AddedDeviceData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_devicetracker_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //count=position;
        holder.phone.setText(mData.get(position).getPhoneNumber());
        holder.relation.setText(mData.get(position).getRelation());

        holder.mListlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelect) {
                    holder.mListlayout.setBackgroundColor(Color.CYAN);
                    mSelectData = new MultipleselectData();
                    mSelectData.setPhone(mData.get(position).getPhoneNumber());
                    mSelectData.setLat(mData.get(position).getLat());
                    mSelectData.setLng(mData.get(position).getLng());
                    itemListener.recyclerViewListClicked(v, position, mSelectData);
                } else {
                    holder.mListlayout.setBackgroundColor(Color.WHITE);
                }
                isSelect = !isSelect;
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView phone, relation, title, status;
        public CardView mListlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.phoneNumber);
            relation = itemView.findViewById(R.id.relation);
            mListlayout = itemView.findViewById(R.id.listLayout);

        }
    }

    public interface RecyclerViewClickListener {
        public void recyclerViewListClicked(View v, int position, MultipleselectData data);
    }

    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
    }
}
