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
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.ChooseGroupData;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.ChooseGroupActivity;

import org.w3c.dom.Text;

import java.util.List;

public class ChooseGroupListAdapter extends RecyclerView.Adapter<ChooseGroupListAdapter.ExampleViewHolder> {

    private List<ChooseGroupData> mData;
    private Context mContext;

    public ChooseGroupListAdapter(List<ChooseGroupData> mList, Context mContext) {
        mData = mList;
        this.mContext = mContext;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView cardTextView;
        public TextView homeTextView;
        public TextView familyTextView;
        public TextView friendsTextView;
        public TextView petTextView;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            cardTextView = itemView.findViewById(R.id.cardTextOnChooseGroup);
            homeTextView = itemView.findViewById(R.id.homeTextView);
            familyTextView = itemView.findViewById(R.id.familyTextView);
            friendsTextView = itemView.findViewById(R.id.friendsTextView);
            petTextView = itemView.findViewById(R.id.petTextView);
        }
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_choose_device_list_adapter, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        holder.cardTextView.setText(R.string.chooseGroupText);
        holder.cardTextView.setTypeface(Util.mTypeface(mContext, 3));
        holder.homeTextView.setTypeface(Util.mTypeface(mContext, 3));
        holder.familyTextView.setTypeface(Util.mTypeface(mContext, 3));
        holder.friendsTextView.setTypeface(Util.mTypeface(mContext, 3));
        holder.petTextView.setTypeface(Util.mTypeface(mContext, 3));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}