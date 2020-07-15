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
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Constant;

import java.util.List;

public class ChooseGroupListAdapter extends RecyclerView.Adapter<ChooseGroupListAdapter.ViewHolder> {

    private List<HomeActivityListData> mData;
    private Context mContext;
    private static RecyclerViewClickListener itemListener;

    public ChooseGroupListAdapter(List<HomeActivityListData> mList, Context mContext) {
        mData = mList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_choose_group_list_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeActivityListData homeActivityListData = mData.get(position);
        if(homeActivityListData.getGroupIcon() != null && homeActivityListData.getGroupIcon().equalsIgnoreCase(Constant.GROUP_SELECTED)) {
            holder.groupIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.groupselected));
        } else if (mData.get(position).getGroupIcon() != null) {
            Resources res = mContext.getResources();
            int iconId = res.getIdentifier(homeActivityListData.getGroupIcon(), Constant.DRAWABLE, mContext.getPackageName());
            Drawable drawable = ContextCompat.getDrawable(mContext, iconId);
            holder.groupIcon.setImageDrawable(drawable);
        } else {
            holder.groupIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_creategroup));
        }
        holder.groupName.setText(mData.get(position).getGroupName());
        holder.groupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.groupButtonClicked(homeActivityListData, mData.get(position).getGroupIcon());
            }
        });
       /* holder.groupIcon.setOnClickListener(v -> {
            itemListener.groupButtonClicked(homeActivityListData, mData.get(position).getGroupIcon());
        });*/

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView groupName;
        public ImageView groupIcon;


        public ViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            groupIcon = itemView.findViewById(R.id.group_icon);
        }
    }

    /**
     * Interface to override methods in ChooseGroup Activity to call particular methods on button click
     */
    public interface RecyclerViewClickListener {
        void groupButtonClicked(HomeActivityListData homeActivityListData,String groupIconSelection);
    }

    /**
     * Register the listener
     *
     * @param mItemClickListener
     */
    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
    }

}