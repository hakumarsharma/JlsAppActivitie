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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.List;

public class PeopleMemberListAdapter extends RecyclerView.Adapter<PeopleMemberListAdapter.ViewHolder> {
    private List<HomeActivityListData> mList;
    private Context mContext;
    private static RecyclerViewClickListener itemListener;

    public PeopleMemberListAdapter(List<HomeActivityListData> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    /**
     * Binds the given View to the position
     *
     * @param parent
     * @param viewType
     * @return View Holder object
     */
    @NonNull
    @Override
    public PeopleMemberListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_people_member_list_adapter, parent, false);
        return new PeopleMemberListAdapter.ViewHolder(itemView);
    }


    /**
     * Interface to override methods in Dashboard to call those methods on particular item click
     */
    public interface RecyclerViewClickListener {
        void clickOnListLayout(HomeActivityListData homeActivityListData);

    }

    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(@NonNull PeopleMemberListAdapter.ViewHolder holder, int position) {
        HomeActivityListData data = mList.get(position);
        holder.memberName.setTypeface(Util.mTypeface(mContext, 5));
        holder.memberName.setText(data.getGroupName());
        holder.timeLeft.setText("00h 60min");

//        if(data.getConsentStaus().equals(Constant.ACTIVE)){
//            holder.memberIcon.setImageResource(R.drawable.inviteaccepted);
//            holder.timer.setImageResource(R.drawable.ic_timetimeline_outline);
//            holder.timeLeft.setTextColor(mContext.getResources().getColor(R.color.timerColor));
//        }else if(data.getConsentStaus().equals(Constant.PENDING)){
//            holder.memberIcon.setImageResource(R.drawable.pendinginvite);
//            holder.timer.setImageResource(R.drawable.ic_timetimeline_outline);
//            holder.timeLeft.setTextColor(mContext.getResources().getColor(R.color.timerColor));
//        }else {
//            holder.memberIcon.setImageResource(R.drawable.invitetimeup);
//            holder.timer.setImageResource(R.drawable.ic_timetimeline_outline);
//            holder.timeLeft.setTextColor(mContext.getResources().getColor(R.color.timeUp));
//        }
        holder.peopleList.setOnClickListener(v -> {
            itemListener.clickOnListLayout(data);
            return;
        });
    }

    /**
     * Register the listener
     *
     * @param mItemClickListener
     */
    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
    }

    /**
     * return The total number of items in this adapter
     *
     * @return size
     */
    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView memberName;
        private ImageView memberIcon;
        private TextView timeLeft;
        private ImageView timer;
        private CardView peopleList;
        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.IndividualMemberName);
            memberIcon = itemView.findViewById(R.id.IndividualMemberIcon);
            timeLeft = itemView.findViewById(R.id.timeLeft);
            timer = itemView.findViewById(R.id.timer);
            peopleList = itemView.findViewById(R.id.peopleListLayout);
        }
    }
}