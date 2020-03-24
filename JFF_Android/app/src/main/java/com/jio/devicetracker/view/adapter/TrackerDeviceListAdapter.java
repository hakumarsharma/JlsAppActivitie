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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.util.Constant;


import java.util.List;

/**
 * Implementation of adapter for tracee list.
 */
public class TrackerDeviceListAdapter extends RecyclerView.Adapter<TrackerDeviceListAdapter.ViewHolder> {

    private Context mContext;
    private List<HomeActivityListData> mData;
    private static RecyclerViewClickListener itemListener;
    private MultipleselectData mSelectData;

    public TrackerDeviceListAdapter(Context mContext, List<HomeActivityListData> mData) {
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
        HomeActivityListData data = mData.get(position);
        holder.phone.setText(mData.get(position).getPhoneNumber());
        holder.name.setText(mData.get(position).getName());
        if (mData.get(position).isGroupMember() == true) {
            holder.mIconImage.setImageResource(R.drawable.ic_group_button);
        } else {
            holder.mIconImage.setImageResource(R.drawable.ic_user);
        }
        // holder.mDelete.setTransformationMethod(null);
        //holder.mEdit.setTransformationMethod(null);
        holder.mConsentStatus.setTransformationMethod(null);
        if (mData.get(position).getConsentStaus() != null && mData.get(position).getConsentStaus().trim().equalsIgnoreCase(Constant.CONSENT_STATUS_MSG)) {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentApproved));
            holder.mConsentStatus.setText(Constant.CONSENT_APPROVED_STATUS);
//            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_approved,0,0,0);
            holder.mConsentStatus.setEnabled(false);
        } else if (mData.get(position).getConsentStaus() != null && mData.get(position).getConsentStaus().trim().equals(Constant.PENDING)) {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentPending));
            holder.mConsentStatus.setText(Constant.CONSENT_PENDING);
//            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pending,0,0,0);
            holder.mConsentStatus.setEnabled(false);

        } else {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentNotSent));
            holder.mConsentStatus.setText(Constant.REQUEST_CONSENT);
//            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notsent,0,0,0);
        }

        if (!data.isGroupMember()) {
            holder.mConsentStatus.setOnClickListener(v -> {
                itemListener.consetClick(mData.get(position).getPhoneNumber());
            });
        } else {
            holder.mConsentStatus.setOnClickListener(v -> {
                itemListener.consentClickForGroup(mData.get(position).getName());
            });
        }
        holder.mConsent.setOnClickListener(v -> {
            data.setSelected(!data.isSelected());
            if (data.isGroupMember() && data.isSelected()) {
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkmark);
                mSelectData = new MultipleselectData();
                mSelectData.setName(mData.get(position).getName());
                itemListener.recyclerViewListClicked(v, position, mSelectData, 1);
            } else if (data.isSelected()) {
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkmark);
                mSelectData = new MultipleselectData();
                mSelectData.setPhone(mData.get(position).getPhoneNumber());
                mSelectData.setLat(mData.get(position).getLat());
                mSelectData.setLng(mData.get(position).getLng());
                mSelectData.setName(mData.get(position).getName());
                mSelectData.setImeiNumber(mData.get(position).getImeiNumber());
                itemListener.recyclerViewListClicked(v, position, mSelectData, 2);
            } else {
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkboxempty);
                itemListener.recyclerViewListClicked(v, position, mSelectData, 3);
            }
        });

        // holder.mEdit.setOnClickListener(v -> itemListener.recyclerviewEditList(mData.get(position).getRelation(),mData.get(position).getPhoneNumber()));
        // holder.mDelete.setOnClickListener(v -> itemListener.recyclerviewDeleteList(mData.get(position).getPhoneNumber(),position));

        holder.viewOptionMenu.setOnClickListener(v -> itemListener.onPopupMenuClicked(holder.viewOptionMenu, position, mData.get(position).getName(), mData.get(position).getPhoneNumber()));

        holder.mListlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        if (data.isGroupMember()) {
            holder.mListlayout.setOnClickListener(v -> {
                itemListener.clickonListLayout(mData.get(position).getName());
                return;
            });
        }
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
        //        public Button mDelete;
//        public Button mEdit;
        public Button mConsent;
        public Button mConsentStatus;
        public TextView viewOptionMenu;
        public ImageView mIconImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.phoneNumber);
            name = itemView.findViewById(R.id.name);
            mListlayout = itemView.findViewById(R.id.listLayout);
            mConsent = itemView.findViewById(R.id.consent);
            mConsentStatus = itemView.findViewById(R.id.consentstatus);
            viewOptionMenu = itemView.findViewById(R.id.textViewOptions);
            status = itemView.findViewById(R.id.statusView);
            mIconImage = itemView.findViewById(R.id.contactImage);


        }
    }

    public interface RecyclerViewClickListener {
        void recyclerViewListClicked(View v, int position, MultipleselectData data, int val);

        // void recyclerviewEditList(String relation,String phoneNumber);
        // void recyclerviewDeleteList(String phoneNuber,int position);
        void clickonListLayout(String selectedGroupName);

        void consetClick(String phoneNumber);

        void consentClickForGroup(String selectedGroupName);

        void onPopupMenuClicked(View v, int position, String name, String number);
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
