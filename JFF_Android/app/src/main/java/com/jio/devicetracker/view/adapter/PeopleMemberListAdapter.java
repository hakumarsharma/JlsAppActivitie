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

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;

import com.jio.devicetracker.database.pojo.GenerateConsentTokenData;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.request.DeleteGroupRequest;
import com.jio.devicetracker.database.pojo.request.GenerateConsentTokenRequest;
import com.jio.devicetracker.database.pojo.response.CommonAPIResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.dashboard.DashboardMainActivity;
import com.jio.devicetracker.view.dashboard.PeopleFragment;
import com.jio.devicetracker.view.people.ChooseGroupFromPeopleFlow;
import com.jio.devicetracker.view.people.EditMemberDetailsActivity;
import java.util.List;

public class PeopleMemberListAdapter extends RecyclerView.Adapter<PeopleMemberListAdapter.ViewHolder> {
    private List<HomeActivityListData> mList;
    private Context mContext;
    private static RecyclerViewClickListener itemListener;
    private DBManager mDbManager;
    private String groupId;
    private int position;
    public static boolean peopleEditFlag;
    private RelativeLayout individualMemberOperationLayout;
    private HomeActivityListData homeActivityList;

    public PeopleMemberListAdapter(List<HomeActivityListData> mList, Context mContext) {
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
        if (data.getConsentStaus().equals(Constant.PENDING)) {
            holder.memberIcon.setImageResource(R.drawable.pendinginvite);
            holder.resendOption.setVisibility(View.VISIBLE);
            holder.resendOptionLine.setVisibility(View.VISIBLE);
        } else if (data.getConsentStaus().equals(Constant.APPROVED)) {
            holder.memberIcon.setImageResource(R.drawable.inviteaccepted);
        } else if (data.getConsentStaus().equals(Constant.EXPIRED)) {
            holder.memberIcon.setImageResource(R.drawable.invitetimeup);
            holder.resendOption.setVisibility(View.VISIBLE);
            holder.resendOptionLine.setVisibility(View.VISIBLE);
        }

        holder.timeLeft.setText(Util.getInstance().getTrackingExpirirationDuration(data.getFrom(), data.getTo()));
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView memberName;
        private ImageView memberIcon;
        private TextView timeLeft;
        private ImageView timer;
        private TextView resendOption;
        private CardView peopleList;
        private View resendOptionLine;
        private ImageView individualUserMenubar;
        private RelativeLayout individualMemberOperationLayout;
        private ImageView closeOperation;
        private TextView deleteIndividualUser;
        private TextView editPeopleProfile;
        private TextView addToGroup;

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
            resendOption = itemView.findViewById(R.id.resendInvite);
            resendOption.setOnClickListener(this);
            resendOptionLine = itemView.findViewById(R.id.resendInviteLine);
            timer = itemView.findViewById(R.id.timer);
            peopleList = itemView.findViewById(R.id.peopleListLayout);
            individualMemberOperationLayout = itemView.findViewById(R.id.individualMemberOperationLayout);
            individualUserMenubar = itemView.findViewById(R.id.individualUserMenubar);
            individualUserMenubar.setOnClickListener(this);
            closeOperation = itemView.findViewById(R.id.closeOperation);
            closeOperation.setOnClickListener(this);
            deleteIndividualUser = itemView.findViewById(R.id.deleteIndividualUser);
            deleteIndividualUser.setOnClickListener(this);
            editPeopleProfile = itemView.findViewById(R.id.editIndividualMember);
            editPeopleProfile.setOnClickListener(this);
            addToGroup = itemView.findViewById(R.id.addPeopleToGroup);
            addToGroup.setOnClickListener(this);
            PeopleMemberListAdapter.this.individualMemberOperationLayout = individualMemberOperationLayout;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.individualUserMenubar:
                    individualMemberOperationLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.closeOperation:
                    individualMemberOperationLayout.setVisibility(View.GONE);
                    break;
                case R.id.addPeopleToGroup:
                    DashboardMainActivity.flowFromPeople = true;
                    Intent chooseGroupIntent = new Intent(mContext, ChooseGroupFromPeopleFlow.class);
                    chooseGroupIntent.putExtra(Constant.TRACKEE_NAME, mList.get(getAdapterPosition()).getGroupName());
                    chooseGroupIntent.putExtra(Constant.TRACKEE_NUMBER, mList.get(getAdapterPosition()).getPhoneNumber());
                    chooseGroupIntent.putExtra(Constant.IS_PEOPLE_ADD_TO_GROUP,true);
                    mContext.startActivity(chooseGroupIntent);
                    break;
                case R.id.deleteIndividualUser:
                    position = getAdapterPosition();
                    deleteAlertBox(position);
                    break;
                case R.id.resendInvite:
                    individualMemberOperationLayout.setVisibility(View.GONE);
                    position = getAdapterPosition();
                    resendInviteAPICall(mList.get(position));
                    //makeRemoveAPICall(mList.get(position));
                    //makeDeleteGroupAPICall(mList.get(position).getGroupId(),true);
                    break;
                case R.id.editIndividualMember:
                    peopleEditFlag = true;
                    Intent intent = new Intent(mContext, EditMemberDetailsActivity.class);
                    intent.putExtra(Constant.GROUP_ID, mList.get(getAdapterPosition()).getGroupId());
                    intent.putExtra(Constant.GROUPNAME, mList.get(getAdapterPosition()).getGroupName());
                    intent.putExtra(Constant.CONSENT_ID, mList.get(getAdapterPosition()).getConsentId());
                    mContext.startActivity(intent);
                    break;
                default:
                    // Todo
                    break;
            }
        }

        private void deleteAlertBox(int position) {
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.number_display_dialog);
            TextView mAlertMessage = dialog.findViewById(R.id.selectNumber);
            mAlertMessage.setText(Constant.DELETE_PERSON_MESSAGE);
            dialog.setTitle(Constant.ALERT_TITLE);
            dialog.getWindow().setLayout(750, 500);
            final Button yes = dialog.findViewById(R.id.positive);
            final Button no = dialog.findViewById(R.id.negative);
            yes.setOnClickListener(v -> {
                PeopleMemberListAdapter.this.individualMemberOperationLayout = individualMemberOperationLayout;
                makeDeleteGroupAPICall(mList.get(position).getGroupId());
                dialog.dismiss();
            });

            no.setOnClickListener(v -> {
                dialog.dismiss();
                individualMemberOperationLayout.setVisibility(View.GONE);
            });
            dialog.show();
        }
    }

    private void resendInviteAPICall(HomeActivityListData homeActivityListData){
        GenerateConsentTokenData consentTokenData = new GenerateConsentTokenData();
        GenerateConsentTokenData.Consent consent = new GenerateConsentTokenData().new Consent();
        consent.setPhone(homeActivityListData.getPhoneNumber());
        consentTokenData.setConsent(consent);
        Util.getInstance().showProgressBarDialog(mContext);
        homeActivityList = homeActivityListData;
        GroupRequestHandler.getInstance(mContext).handleRequest(new GenerateConsentTokenRequest(new GenerateConsentTokenRequestSuccessListener(), new GenerateConsentTokenRequestErrorListener(), consentTokenData, homeActivityListData.getGroupId(), mDbManager.getAdminLoginDetail().getUserId()));

    }

    /**
     * Add Member in group Success Listener
     */
    private class GenerateConsentTokenRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            CommonAPIResponse apiResponse = Util.getInstance().getPojoObject(String.valueOf(response), CommonAPIResponse.class);
            if (apiResponse.getCode() == Constant.SUCCESS_CODE_200){
                removeItem(position);
                Toast.makeText(mContext, Constant.INVITE_SENT, Toast.LENGTH_SHORT).show();
                HomeActivityListData groupMemberDataList = new HomeActivityListData();
                groupMemberDataList.setConsentId(homeActivityList.getConsentId());
                groupMemberDataList.setPhoneNumber(homeActivityList.getPhoneNumber());
                groupMemberDataList.setGroupId(homeActivityList.getGroupId());
                groupMemberDataList.setConsentStaus(Constant.PENDING);
                groupMemberDataList.setGroupName(homeActivityList.getGroupName());
                groupMemberDataList.setUserId(homeActivityList.getUserId());
                groupMemberDataList.setFrom(homeActivityList.getFrom());
                groupMemberDataList.setTo(homeActivityList.getTo());
                mList.add(groupMemberDataList);
                notifyDataSetChanged();

            }
        }
    }

    /**
     * Add Member in Group Error Listener
     */
    private class GenerateConsentTokenRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_404) {
                // Make Verify and Assign call
                Util.progressDialog.dismiss();
                showCustomAlertWithText(Constant.DEVICE_NOT_FOUND);
            } else {
                Util.progressDialog.dismiss();
                showCustomAlertWithText(Constant.RESEND_INVITE_FAILED);
            }
        }
    }

    /**
     * Delete the Group and update the database
     */
    private void makeDeleteGroupAPICall(String groupId) {
        this.groupId = groupId;
        Util.getInstance().showProgressBarDialog(mContext);
        GroupRequestHandler.getInstance(mContext).handleRequest(new DeleteGroupRequest(new DeleteGroupRequestSuccessListener(), new DeleteGroupRequestErrorListener(), groupId, mDbManager.getAdminLoginDetail().getUserId()));
    }

    /**
     * Delete Group Request API Call Success Listener and create new group if Session time is completed and Request Consent button is clicked
     */
    private class DeleteGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            mDbManager.deleteSelectedDataFromGroup(groupId);
            mDbManager.deleteSelectedDataFromGroupMember(groupId);
            Util.progressDialog.dismiss();
            individualMemberOperationLayout.setVisibility(View.GONE);
            removeItem(position);
            PeopleFragment.checkMemberPresent();

        }
    }

    /**
     * Delete Group Request API Call Error Listener
     */
    private class DeleteGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            showCustomAlertWithText(Constant.GROUP_DELETION_FAILURE);
        }
    }

    /**
     * Called when we delete group
     *
     * @param adapterPosition
     */
    public void removeItem(int adapterPosition) {
        mList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }


}
