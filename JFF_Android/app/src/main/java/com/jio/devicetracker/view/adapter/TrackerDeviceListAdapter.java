// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.util.Constant;


import java.util.List;

/**
 * Implementation of adapter for tracee list.
 */
public class TrackerDeviceListAdapter extends RecyclerView.Adapter<TrackerDeviceListAdapter.ViewHolder> {

    private Context mContext;
    private List<AddedDeviceData> mData;
    private static RecyclerViewClickListener itemListener;
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
        AddedDeviceData data = mData.get(position);
        holder.phone.setText(mData.get(position).getPhoneNumber());
        holder.name.setText(mData.get(position).getName());
        holder.mDelete.setTransformationMethod(null);
        holder.mEdit.setTransformationMethod(null);
        holder.mConsentStatus.setTransformationMethod(null);
        holder.mConsentStatus.setOnClickListener(v -> itemListener.consetClick(mData.get(position).getPhoneNumber()));
        if(mData.get(position).getConsentStaus() != null && mData.get(position).getConsentStaus().trim().equalsIgnoreCase(Constant.CONSENT_STATUS_MSG))
        {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentApproved));
            holder.mConsentStatus.setText(Constant.CONSENT_APPROVED_STATUS);
            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_approved,0,0,0);
            holder.mConsentStatus.setEnabled(false);
        } else if(mData.get(position).getConsentStaus() != null && mData.get(position).getConsentStaus().trim().equals(Constant.PENDING))
        {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentPending));
            holder.mConsentStatus.setText(Constant.CONSENT_PENDING);
            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pending,0,0,0);

        } else {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentNotSent));
            holder.mConsentStatus.setText(Constant.REQUEST_CONSENT);
            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notsent,0,0,0);
        }
        holder.mConsent.setOnClickListener(v -> {
            data.setSelected(!data.isSelected());
            if (data.isSelected()) {
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkmark);
                mSelectData = new MultipleselectData();
                mSelectData.setPhone(mData.get(position).getPhoneNumber());
                mSelectData.setLat(mData.get(position).getLat());
                mSelectData.setLng(mData.get(position).getLng());
                mSelectData.setName(mData.get(position).getName());
                mSelectData.setImeiNumber(mData.get(position).getImeiNumber());
                itemListener.recyclerViewListClicked(v, position, mSelectData,true);
            } else {
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkboxempty);
                itemListener.recyclerViewListClicked(v, position, mSelectData,false);
            }
        });

        holder.mEdit.setOnClickListener(v -> itemListener.recyclerviewEditList(mData.get(position).getRelation(),mData.get(position).getPhoneNumber()));
        holder.mDelete.setOnClickListener(v -> itemListener.recyclerviewDeleteList(mData.get(position).getPhoneNumber(),position));


        holder.mListlayout.setOnLongClickListener(v -> true);
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView phone;
        public TextView name;
        public TextView relation;
        public TextView status;
        public CardView mListlayout;
        public Button mDelete;
        public Button mEdit;
        public Button mConsent;
        public Button mConsentStatus;


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
        void recyclerViewListClicked(View v, int position, MultipleselectData data,boolean val);
        void recyclerviewEditList(String relation,String phoneNumber);
        void recyclerviewDeleteList(String phoneNuber,int position);
        void consetClick(String phoneNumber);
    }

    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
    }

    public void removeItem(int adapterPosition) {
        mData.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }
}
