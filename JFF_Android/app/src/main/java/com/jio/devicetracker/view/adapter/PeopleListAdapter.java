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
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddMemberInGroupData;
import com.jio.devicetracker.database.pojo.ExitRemovedGroupData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.request.AddMemberInGroupRequest;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.network.ExitRemoveDeleteAPI;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.people.EditMemberDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.ViewHolder> {
    private List<GroupMemberDataList> mList;
    private Context mContext;
    private DBManager mDbManager;
    private RelativeLayout layoutOps;

    public PeopleListAdapter(List<GroupMemberDataList> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        mDbManager = new DBManager(mContext);
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage) {
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
    public PeopleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_people_list_adapter, parent, false);
        return new PeopleListAdapter.ViewHolder(itemView);
    }

    /**
     * Interface to override methods in people map list Fragment to call these methods on particular item click
     */
    public interface RecyclerViewClickListener {
        void clickonListLayout(GroupMemberDataList groupMemberList);
    }

    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(@NonNull PeopleListAdapter.ViewHolder holder, int position) {
        GroupMemberDataList data = mList.get(position);
        holder.memberName.setTypeface(Util.mTypeface(mContext, 5));
        holder.memberName.setText(data.getName());
        holder.memberAddress.setTypeface(Util.mTypeface(mContext, 3));
        if (data.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_PENDING)) {
            holder.memberStatus.setText(Constant.CONSENT_PENDING);
            holder.memberAddress.setText(Constant.CONSENT_PENDING_ADDRESS);
            holder.memberIcon.setImageResource(R.drawable.pendinginvite);
            holder.memberStatus.setTextColor(mContext.getResources().getColor(R.color.pending_color));
        } else if (data.getConsentStatus().equalsIgnoreCase(Constant.CONSET_STATUS_APPROVED)) {
            holder.memberStatus.setText(Constant.CONSENT_APPROVED_STATUS);
            holder.memberIcon.setImageResource(R.drawable.inviteaccepted);
            holder.memberStatus.setTextColor(mContext.getResources().getColor(R.color.approved_color));
            if (data.getAddress() != null && data.getAddress().length() > 0) {
                holder.memberAddress.setText(data.getAddress());
            } else {
                holder.memberAddress.setText(Constant.CONSENT_APPROVED_ADDRESS);
            }
        } else {
            holder.memberStatus.setText(Constant.CONSENT_Expired);
            holder.memberStatus.setTextColor(mContext.getResources().getColor(R.color.rejected_color));
            holder.memberAddress.setText(Constant.CONSENT_EXPIRED_ADDRESS);
            holder.memberIcon.setImageResource(R.drawable.invitetimeup);
        }
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView memberName;
        private ImageView memberIcon;
        private TextView memberStatus;
        private TextView memberAddress;
        private ImageView menuIcon;
        private ImageView closeBtn;
        private TextView editText;
        private TextView removeFromGroupText;
        private TextView shareInvite;
        public RelativeLayout layoutOps;
        public CardView peoplAddressLayout;

        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.mapMemberName);
            memberIcon = itemView.findViewById(R.id.mapMemberIcon);
            memberStatus = itemView.findViewById(R.id.mapMemberStatus);
            memberAddress = itemView.findViewById(R.id.memberAddress);
            menuIcon = itemView.findViewById(R.id.consentStatus);
            peoplAddressLayout = itemView.findViewById(R.id.peopleAddressListLayout);
            closeBtn = itemView.findViewById(R.id.close);
            editText = itemView.findViewById(R.id.edit);
            layoutOps = itemView.findViewById(R.id.oprationLayout);
            removeFromGroupText = itemView.findViewById(R.id.remove_from_group);
            shareInvite = itemView.findViewById(R.id.share_invite);
            editText.setOnClickListener(this);
            removeFromGroupText.setOnClickListener(this);
            shareInvite.setOnClickListener(this);
            menuIcon.setOnClickListener(this);
            closeBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.close:
                    layoutOps.setVisibility(View.GONE);
                    break;
                case R.id.consentStatus:
                    layoutOps.setVisibility(View.VISIBLE);
                    break;
                case R.id.edit:
                    Intent intent = new Intent(mContext, EditMemberDetailsActivity.class);
                    intent.putExtra("isFromMap",true);
                    intent.putExtra(Constant.GROUP_ID, mList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(Constant.GROUPNAME, mList.get(getAdapterPosition()).getName());
                    intent.putExtra(Constant.CONSENT_ID, mList.get(getAdapterPosition()).getConsentId());
                    mContext.startActivity(intent);
                    break;
                case R.id.remove_from_group:
                    PeopleListAdapter.this.layoutOps = layoutOps;
                    makeRemoveAPICall(mList.get(getAdapterPosition()), getAdapterPosition(), true);
                    break;
                case R.id.share_invite:
                    PeopleListAdapter.this.layoutOps = layoutOps;
                    makeRemoveAPICall(mList.get(getAdapterPosition()), getAdapterPosition(), false);
                    break;
                default:
                    // Todo
                    break;
            }

        }
    }


    /**
     * Make a Remove API Call
     *
     * @param groupMemberDataList
     */
    private void makeRemoveAPICall(GroupMemberDataList groupMemberDataList, int position, boolean isRemoveFromGroup) {
        layoutOps.setVisibility(View.GONE);
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
                    if (!isRemoveFromGroup) {
                        addMemberInGroupAPICall(groupMemberDataList);
                    }
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
     * Add Members in Group API Call, member will be part of group
     */
    private void addMemberInGroupAPICall(GroupMemberDataList groupMemberDataList) {
        AddMemberInGroupData addMemberInGroupData = new AddMemberInGroupData();
        AddMemberInGroupData.Consents consents = new AddMemberInGroupData().new Consents();
        List<AddMemberInGroupData.Consents> consentList = new ArrayList<>();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.EVENTS);
        consents.setEntities(mList);
        consents.setPhone(groupMemberDataList.getNumber());
        consents.setName(groupMemberDataList.getName());
        consentList.add(consents);
        addMemberInGroupData.setConsents(consentList);
        Util.getInstance().showProgressBarDialog(mContext);
        GroupRequestHandler.getInstance(mContext).handleRequest(new AddMemberInGroupRequest(new AddMemberInGroupRequestSuccessListener(), new AddMemberInGroupRequestErrorListener(), addMemberInGroupData, groupMemberDataList.getGroupId(), mDbManager.getAdminLoginDetail().getUserId()));
    }

    /**
     * Add Member in group Success Listener
     */
    private class AddMemberInGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GroupMemberResponse groupMemberResponse = Util.getInstance().getPojoObject(String.valueOf(response), GroupMemberResponse.class);
            Util.progressDialog.dismiss();
            if (groupMemberResponse.getCode() == Constant.SUCCESS_CODE_200) {
                showCustomAlertWithText(Constant.INVITE_SENT);
                for (GroupMemberResponse.Data data : groupMemberResponse.getData()) {
                    GroupMemberDataList groupMemberDataList = new GroupMemberDataList();
                    groupMemberDataList.setConsentId(data.getConsentId());
                    groupMemberDataList.setNumber(data.getPhone());
                    groupMemberDataList.setGroupAdmin(data.isGroupAdmin());
                    groupMemberDataList.setGroupId(data.getGroupId());
                    groupMemberDataList.setConsentStatus(data.getStatus());
                    groupMemberDataList.setName(data.getName());
                    groupMemberDataList.setUserId(data.getUserId());
                    mList.add(groupMemberDataList);
                }
                notifyDataSetChanged();
            }
        }
    }

    /**
     * Add Member in Group Error Listener
     */
    private class AddMemberInGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                Util.progressDialog.dismiss();
                showCustomAlertWithText(Constant.GROUP_MEMBER_ADDITION_FAILURE);
            } else if (error.networkResponse.statusCode == Constant.STATUS_CODE_404) {
                // Make Verify and Assign call
                Util.progressDialog.dismiss();
                showCustomAlertWithText(Constant.DEVICE_NOT_FOUND);
            } else {
                Util.progressDialog.dismiss();
                showCustomAlertWithText(Constant.GROUP_MEMBER_ADDITION_FAILURE);
            }
        }
    }


    /**
     * Called when we delete member from group
     *
     * @param adapterPosition
     */
    public void removeItem(int adapterPosition) {
        mList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }
}
