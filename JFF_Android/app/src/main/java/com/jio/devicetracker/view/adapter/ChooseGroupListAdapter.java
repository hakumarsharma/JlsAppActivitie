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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Util;

import java.util.List;

public class ChooseGroupListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<List<HomeActivityListData>> mData;
    private Context mContext;
    private RecyclerViewClickListener itemListener;

    public ChooseGroupListAdapter(List<List<HomeActivityListData>> mList, Context mContext) {
        mData = mList;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_choose_group_list_adapter, parent, false);
                viewHolder = new ViewHolderWithInfo(view);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_choose_device_list_adapter, parent, false);
                viewHolder = new ViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                List<HomeActivityListData> data = mData.get(position);
                ViewHolderWithInfo viewHolderWithInfo = (ViewHolderWithInfo) holder;
                for (int i = 0; i < data.size(); i++) {
                    if (i == 0) {
                        viewHolderWithInfo.firstTextViewWithInfo.setText(data.get(i).getGroupName());
                        viewHolderWithInfo.firstTextViewWithInfo.setTypeface(Util.mTypeface(mContext, 3));
                        viewHolderWithInfo.firstTextViewWithInfo.setVisibility(View.VISIBLE);
                        viewHolderWithInfo.firstButtonWithInfo.setVisibility(View.VISIBLE);
                        int finalI = i;
                        viewHolderWithInfo.firstButtonWithInfo.setOnClickListener(v -> itemListener.groupButtonClicked(data.get(finalI)));
                    } else if (i == 1) {
                        viewHolderWithInfo.secondButtonTextViewWithInfo.setText(data.get(i).getGroupName());
                        viewHolderWithInfo.secondButtonTextViewWithInfo.setTypeface(Util.mTypeface(mContext, 3));
                        viewHolderWithInfo.secondButtonTextViewWithInfo.setVisibility(View.VISIBLE);
                        viewHolderWithInfo.secondButtonWithInfo.setVisibility(View.VISIBLE);
                        int finalI1 = i;
                        viewHolderWithInfo.secondButtonWithInfo.setOnClickListener(v -> itemListener.groupButtonClicked(data.get(finalI1)));
                    } else if (i == 2) {
                        viewHolderWithInfo.thirdButtonTextViewWithInfo.setText(data.get(i).getGroupName());
                        viewHolderWithInfo.thirdButtonTextViewWithInfo.setTypeface(Util.mTypeface(mContext, 3));
                        viewHolderWithInfo.thirdButtonTextViewWithInfo.setVisibility(View.VISIBLE);
                        viewHolderWithInfo.thirdButtonWithInfo.setVisibility(View.VISIBLE);
                        int finalI2 = i;
                        viewHolderWithInfo.thirdButtonWithInfo.setOnClickListener(v -> itemListener.groupButtonClicked(data.get(finalI2)));
                    } else if (i == 3) {
                        viewHolderWithInfo.fourthButtonTextViewWithInfo.setText(data.get(i).getGroupName());
                        viewHolderWithInfo.fourthButtonTextViewWithInfo.setTypeface(Util.mTypeface(mContext, 3));
                        viewHolderWithInfo.fourthButtonTextViewWithInfo.setVisibility(View.VISIBLE);
                        viewHolderWithInfo.fourthButtonWithInfo.setVisibility(View.VISIBLE);
                        int finalI3 = i;
                        viewHolderWithInfo.fourthButtonWithInfo.setOnClickListener(v -> itemListener.groupButtonClicked(data.get(finalI3)));
                    }
                }
                break;
            case 1:
                List<HomeActivityListData> homeActivityListData = mData.get(position);
                ViewHolder viewHolder = (ViewHolder) holder;
                for (int i = 0; i < homeActivityListData.size(); i++) {
                    if (i == 0) {
                        viewHolder.firstButtonTextView.setText(homeActivityListData.get(i).getGroupName());
                        viewHolder.firstButtonTextView.setTypeface(Util.mTypeface(mContext, 3));
                        viewHolder.firstButtonTextView.setVisibility(View.VISIBLE);
                        viewHolder.firstButton.setVisibility(View.VISIBLE);
                        int finalI = i;
                        viewHolder.firstButton.setOnClickListener(v -> itemListener.groupButtonClicked(homeActivityListData.get(finalI)));
                    } else if (i == 1) {
                        viewHolder.secondButtonTextView.setText(homeActivityListData.get(i).getGroupName());
                        viewHolder.secondButtonTextView.setTypeface(Util.mTypeface(mContext, 3));
                        viewHolder.secondButtonTextView.setVisibility(View.VISIBLE);
                        viewHolder.secondButton.setVisibility(View.VISIBLE);
                        int finalI1 = i;
                        viewHolder.secondButton.setOnClickListener(v -> itemListener.groupButtonClicked(homeActivityListData.get(finalI1)));
                    } else if (i == 2) {
                        viewHolder.thirdButtonTextView.setText(homeActivityListData.get(i).getGroupName());
                        viewHolder.thirdButtonTextView.setTypeface(Util.mTypeface(mContext, 3));
                        viewHolder.thirdButtonTextView.setVisibility(View.VISIBLE);
                        viewHolder.thirdButton.setVisibility(View.VISIBLE);
                        int finalI2 = i;
                        viewHolder.thirdButton.setOnClickListener(v -> itemListener.groupButtonClicked(homeActivityListData.get(finalI2)));
                    } else if (i == 3) {
                        viewHolder.fourthButtonTextView.setText(homeActivityListData.get(i).getGroupName());
                        viewHolder.fourthButtonTextView.setTypeface(Util.mTypeface(mContext, 3));
                        viewHolder.fourthButtonTextView.setVisibility(View.VISIBLE);
                        viewHolder.fourthButton.setVisibility(View.VISIBLE);
                        int finalI3 = i;
                        viewHolder.fourthButton.setOnClickListener(v -> itemListener.groupButtonClicked(homeActivityListData.get(finalI3)));
                    }
                }
                break;
        }
    }

    /**
     * First row in Choose Group
     */
    public static class ViewHolderWithInfo extends RecyclerView.ViewHolder {
        private Button firstButtonWithInfo;
        private TextView firstTextViewWithInfo;
        private Button secondButtonWithInfo;
        private TextView secondButtonTextViewWithInfo;
        private Button thirdButtonWithInfo;
        private TextView thirdButtonTextViewWithInfo;
        private Button fourthButtonWithInfo;
        private TextView fourthButtonTextViewWithInfo;

        public ViewHolderWithInfo(View itemView) {
            super(itemView);
            firstButtonWithInfo = itemView.findViewById(R.id.firstButtonWithInfo);
            firstTextViewWithInfo = itemView.findViewById(R.id.firstTextViewWithInfo);
            secondButtonWithInfo = itemView.findViewById(R.id.secondButtonWithInfo);
            secondButtonTextViewWithInfo = itemView.findViewById(R.id.secondButtonTextViewWithInfo);
            thirdButtonWithInfo = itemView.findViewById(R.id.thirdButtonWithInfo);
            thirdButtonTextViewWithInfo = itemView.findViewById(R.id.thirdButtonTextViewWithInfo);
            fourthButtonWithInfo = itemView.findViewById(R.id.fourthButtonWithInfo);
            fourthButtonTextViewWithInfo = itemView.findViewById(R.id.fourthButtonTextViewWithInfo);
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

    /**
     * Interface to override methods in ChooseGroup Activity to call particular methods on button click
     */
    public interface RecyclerViewClickListener {
        void groupButtonClicked(HomeActivityListData homeActivityListData);
    }

    /**
     * Register the listener
     * @param mItemClickListener
     */
    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
    }

}