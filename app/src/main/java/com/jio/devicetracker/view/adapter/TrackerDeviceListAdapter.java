// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.MultipleselectData;


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
        AddedDeviceData data = mData.get(position);
        holder.phone.setText(mData.get(position).getPhoneNumber());
        holder.name.setText(mData.get(position).getName());
        holder.mDelete.setTransformationMethod(null);
        holder.mEdit.setTransformationMethod(null);
        holder.mConsentStatus.setTransformationMethod(null);
        holder.mConsentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.consetClick(mData.get(position).getPhoneNumber());
            }
        });
        if(mData.get(position).getConsentStaus().trim().equalsIgnoreCase("Yes JioTracker"))
        {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentApproved));
            holder.mConsentStatus.setText("Consent Approved");
            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_approved,0,0,0);
            holder.mConsentStatus.setEnabled(false);
        } else if(mData.get(position).getConsentStaus().trim().equals("Pending"))
        {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentPending));
            holder.mConsentStatus.setText("Consent Pending");
            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pending,0,0,0);

        } else {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentNotSent));
        }
        holder.mConsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //itemListener.consetClick(mData.get(position).getPhoneNumber());
                data.setSelected(!data.isSelected());
                if (data.isSelected()) {
                    holder.mConsent.setBackgroundResource(R.drawable.ic_checkmark);
                    mSelectData = new MultipleselectData();
                    mSelectData.setPhone(mData.get(position).getPhoneNumber());
                    mSelectData.setLat(mData.get(position).getLat());
                    mSelectData.setLng(mData.get(position).getLng());
                    mSelectData.setName(mData.get(position).getName());
                    itemListener.recyclerViewListClicked(v, position, mSelectData,true);
                } else {
                    holder.mConsent.setBackgroundResource(R.drawable.ic_checkboxempty);
                    itemListener.recyclerViewListClicked(v, position, mSelectData,false);
                }
            }
        });

        holder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.recyclerviewEditList(mData.get(position).getRelation(),mData.get(position).getPhoneNumber());
            }
        });
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.recyclerviewDeleteList(mData.get(position).getPhoneNumber(),position);
            }
        });


        holder.mListlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

              /*  data.setSelected(!data.isSelected());
                if (data.isSelected()) {
                    holder.mListlayout.setBackground(mContext.getResources().getDrawable(R.drawable.selector));
                    mSelectData = new MultipleselectData();
                    mSelectData.setPhone(mData.get(position).getPhoneNumber());
                    mSelectData.setLat(mData.get(position).getLat());
                    mSelectData.setLng(mData.get(position).getLng());
                    mSelectData.setName(mData.get(position).getName());
                    itemListener.recyclerViewListClicked(v, position, mSelectData,true);
                } else {
                    holder.mListlayout.setBackground(mContext.getResources().getDrawable(R.drawable.unselect_list));
                    itemListener.recyclerViewListClicked(v, position, mSelectData,false);
                }*/
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView phone,name, relation,status;
        public CardView mListlayout;
        public Button mDelete,mEdit,mConsent,mConsentStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.phoneNumber);
            name = itemView.findViewById(R.id.name);
            mListlayout = itemView.findViewById(R.id.listLayout);
            mDelete = itemView.findViewById(R.id.delete);
            mEdit = itemView.findViewById(R.id.edit);
            mConsent = itemView.findViewById(R.id.consent);
            mConsentStatus = itemView.findViewById(R.id.consentstatus);
            status = itemView.findViewById(R.id.statusView);

        }
    }

    public interface RecyclerViewClickListener {
        public void recyclerViewListClicked(View v, int position, MultipleselectData data,boolean val);
        public void recyclerviewEditList(String relation,String phoneNumber);
        public void recyclerviewDeleteList(String phoneNuber,int position);
        public void consetClick(String phoneNumber);
    }

    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
    }

    public void removeItem(int adapterPosition) {

        mData.remove(adapterPosition);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }
}
