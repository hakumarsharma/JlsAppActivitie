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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.ExitRemovedGroupData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.network.ExitRemoveDeleteAPI;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Display the member's list available inside group
 */
public class ActiveMemberListAdapter extends RecyclerView.Adapter<ActiveMemberListAdapter.ViewHolder> {
    private List<GroupMemberDataList> mList;
    private Context mContext;

    /**
     * Constructor to add devices inside group
     *
     * @param mList
     */
    public ActiveMemberListAdapter(List<GroupMemberDataList> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }
    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage){
        CustomAlertActivity alertActivity = new CustomAlertActivity(mContext);
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_member_list, parent, false);
        return new ActiveMemberListAdapter.ViewHolder(itemView);
    }

    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ActiveMemberListAdapter.ViewHolder holder, int position) {
        holder.activeMemberName.setText(mList.get(position).getName());
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView activeMemberName;
        private Button activeMemberDeleteButton;
        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            activeMemberName = itemView.findViewById(R.id.activeMemberName);
            activeMemberDeleteButton = itemView.findViewById(R.id.activeMemberDeleteButton);
            activeMemberDeleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.activeMemberDeleteButton) {
                makeRemoveAPICall(mList.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }

    /**
     * Make a Remove API Call
     *
     * @param groupMemberDataList
     */
    private void makeRemoveAPICall(GroupMemberDataList groupMemberDataList, int position) {
        DBManager mDbManager = new DBManager(mContext);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ExitRemoveDeleteAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExitRemoveDeleteAPI api = retrofit.create(ExitRemoveDeleteAPI.class);
        ExitRemovedGroupData exitRemovedGroupData = new ExitRemovedGroupData();
        ExitRemovedGroupData.Consent consent = new ExitRemovedGroupData().new Consent();
        consent.setPhone(groupMemberDataList.getNumber());
        consent.setStatus(Constant.REMOVED);
        exitRemovedGroupData.setConsent(consent);
        RequestBody body = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), new Gson().toJson(exitRemovedGroupData));
        Call<ResponseBody> call = api.deleteGroupDetails(Constant.BEARER + mDbManager.getAdminLoginDetail().getUserToken(),
                Constant.APPLICATION_JSON, mDbManager.getAdminLoginDetail().getUserId(), Constant.SESSION_GROUPS, groupMemberDataList.getGroupId(), body);
        Util.getInstance().showProgressBarDialog(mContext);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Util.progressDialog.dismiss();
                    Toast.makeText(mContext, Constant.EXIT_FROM_GROUP_SUCCESS, Toast.LENGTH_SHORT).show();
                    mDbManager.deleteSelectedDataFromGroupMember(groupMemberDataList.getConsentId());
                    removeItem(position);
                } else {
                    Util.progressDialog.dismiss();
                    showCustomAlertWithText(Constant.REMOVE_FROM_GROUP_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.progressDialog.dismiss();
                showCustomAlertWithText(Constant.REMOVE_FROM_GROUP_FAILURE);
            }
        });
    }

    /**
     * Called when we remove device from active member screen
     * @param adapterPosition
     */
    public void removeItem(int adapterPosition) {
        mList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }
}

