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
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Util;

import java.util.List;

public class ChooseGroupListAdapter extends RecyclerView.Adapter<ChooseGroupListAdapter.ViewHolder> {

    private List<List<HomeActivityListData>> mData;
    private Context mContext;

    public ChooseGroupListAdapter(List<List<HomeActivityListData>> mList, Context mContext) {
        mData = mList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_choose_device_list_adapter, parent, false);
        ViewHolder evh = new ViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List<HomeActivityListData> data = mData.get(position);
        for(int i = 0; i < data.size(); i ++) {
            if(i == 0) {
                holder.firstButtonTextView.setText(data.get(i).getGroupName());
                holder.firstButtonTextView.setTypeface(Util.mTypeface(mContext, 3));
                holder.firstButtonTextView.setVisibility(View.VISIBLE);
                holder.firstButton.setVisibility(View.VISIBLE);
            }
            else if(i == 1) {
                holder.secondButtonTextView.setText(data.get(i).getGroupName());
                holder.secondButtonTextView.setTypeface(Util.mTypeface(mContext, 3));
                holder.secondButtonTextView.setVisibility(View.VISIBLE);
                holder.secondButton.setVisibility(View.VISIBLE);
            }
            else if(i == 2) {
                holder.thirdButtonTextView.setText(data.get(i).getGroupName());
                holder.thirdButtonTextView.setTypeface(Util.mTypeface(mContext, 3));
                holder.thirdButtonTextView.setVisibility(View.VISIBLE);
                holder.thirdButton.setVisibility(View.VISIBLE);
            }
            else if(i == 3) {
                holder.fourthButtonTextView.setText(data.get(i).getGroupName());
                holder.fourthButtonTextView.setTypeface(Util.mTypeface(mContext, 3));
                holder.fourthButtonTextView.setVisibility(View.VISIBLE);
                holder.fourthButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView firstButtonTextView;
        public TextView secondButtonTextView;
        public TextView thirdButtonTextView;
        public TextView fourthButtonTextView;
        public Button firstButton;
        public Button secondButton;
        public Button thirdButton;
        public Button fourthButton;

        public ViewHolder(View itemView) {
            super(itemView);
            firstButtonTextView = itemView.findViewById(R.id.firstButtonTextView);
            secondButtonTextView = itemView.findViewById(R.id.secondButtonTextView);
            thirdButtonTextView = itemView.findViewById(R.id.thirdButtonTextView);
            fourthButtonTextView = itemView.findViewById(R.id.fourthButtonTextView);
            firstButton = itemView.findViewById(R.id.firstButton);
            secondButton = itemView.findViewById(R.id.secondButton);
            thirdButton = itemView.findViewById(R.id.thirdButton);
            fourthButton = itemView.findViewById(R.id.fourthButton);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}