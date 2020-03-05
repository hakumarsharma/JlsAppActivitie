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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HomeActivityListData;

import java.util.List;

/**
 * Implementation of adapter for tracee list.
 */
public class HomeActivityListAdapter extends RecyclerView.Adapter<HomeActivityListAdapter.ViewHolder> {

    private List<HomeActivityListData> mData;
    private static RecyclerViewClickListener itemListener;

    public HomeActivityListAdapter(List<HomeActivityListData> mData) {
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_home_list_view, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.relationWithGrpMembers.setText(mData.get(position).getNumber());
        holder.name.setText(mData.get(position).getName());
        if (mData.get(position).isGroupMember() == true) {
            holder.imageView.setImageResource(R.drawable.ic_user);
        } else if (mData.get(position).isGroupMember() == false) {
            holder.imageView.setImageResource(R.drawable.ic_group_button);
        }

        holder.viewOptionMenu.setOnClickListener(v -> itemListener.onPopupMenuClicked(holder.viewOptionMenu, position, mData.get(position).getName(), mData.get(position).getNumber()));

        holder.mListlayout.setOnClickListener(v -> itemListener.onRecyclerViewItemClick(v, position, mData.get(position).getName()));
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView relationWithGrpMembers;
        public TextView name;
        public TextView status;
        public CardView mListlayout;
        public Button mConsentStatus;
        public TextView viewOptionMenu;
        public ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relationWithGrpMembers = itemView.findViewById(R.id.relationWithGrpMembers);
            name = itemView.findViewById(R.id.name);
            mListlayout = itemView.findViewById(R.id.listLayout);
            mConsentStatus = itemView.findViewById(R.id.consentstatus);
            status = itemView.findViewById(R.id.statusView);
            viewOptionMenu = itemView.findViewById(R.id.textViewOptions);
            imageView = itemView.findViewById(R.id.contactImage);
        }
    }

    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
    }

    public interface RecyclerViewClickListener {
        //         void recyclerViewListClicked(View v, int position, MultipleselectData data, boolean val);
//        void recyclerviewEditList(String relation, String phoneNumber);
         /*void recyclerviewDeleteList(String phoneNuber, int position);
         void consetClick(String phoneNumber);*/
        void onPopupMenuClicked(View v, int position, String name, String number);
        void onRecyclerViewItemClick(View v, int position, String name);
    }
}
