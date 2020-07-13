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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.DeviceTableData;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.request.DeleteDeviceRequest;
import com.jio.devicetracker.database.pojo.request.DeleteGroupRequest;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.dashboard.DeviceFragment;
import com.jio.devicetracker.view.people.EditMemberDetailsActivity;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private Context mContext;
    private List<HomeActivityListData> mList;
    private String groupId;
    private DBManager mDbManager;
    private int position;
    private RelativeLayout devicesOperationLayout;
    private static RecyclerViewClickListener itemListener;
    private String phoneNumber;
    private String deviceId;
    public static boolean deviceEditFlag;

    public DeviceListAdapter(List<HomeActivityListData> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        this.mDbManager = new DBManager(mContext);
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
    public DeviceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_layout_adapter, parent, false);
        return new DeviceListAdapter.ViewHolder(itemView);
    }


    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(@NonNull DeviceListAdapter.ViewHolder holder, int position) {
        HomeActivityListData data = mList.get(position);
        holder.trackingDeviceName.setTypeface(Util.mTypeface(mContext, 5));
        holder.trackingDeviceName.setText(data.getGroupName());
        if (data.getGroupIcon() != null && data.getGroupIcon().equalsIgnoreCase("Girl")) {
            holder.deviceMemberIcon.setBackgroundResource(R.drawable.wife);
        } else if (data.getGroupIcon() != null && data.getGroupIcon().equalsIgnoreCase("Woman")) {
            holder.deviceMemberIcon.setBackgroundResource(R.drawable.mother);
        } else if (data.getGroupIcon() != null && data.getGroupIcon().equalsIgnoreCase("Man")) {
            holder.deviceMemberIcon.setBackgroundResource(R.drawable.father);
        } else if (data.getGroupIcon() != null && data.getGroupIcon().equalsIgnoreCase("Boy")) {
            holder.deviceMemberIcon.setBackgroundResource(R.drawable.husband);
        } else if (data.getGroupIcon() != null && data.getGroupIcon().equalsIgnoreCase("Kid")) {
            holder.deviceMemberIcon.setBackgroundResource(R.drawable.kid);
        } else if (data.getGroupIcon() != null && data.getGroupIcon().equalsIgnoreCase("Other")) {
            holder.deviceMemberIcon.setBackgroundResource(R.drawable.other);
        } else if (data.getGroupIcon() != null && data.getGroupIcon().equalsIgnoreCase("Dog")) {
            holder.deviceMemberIcon.setBackgroundResource(R.drawable.dog);
        } else if (data.getGroupIcon() != null && data.getGroupIcon().equalsIgnoreCase("Cat")) {
            holder.deviceMemberIcon.setBackgroundResource(R.drawable.cat);
        } else if (data.getGroupIcon() != null && data.getGroupIcon().equalsIgnoreCase("OtherPet")) {
            holder.deviceMemberIcon.setBackgroundResource(R.drawable.other_pet);
        } else {
            holder.deviceMemberIcon.setBackgroundResource(R.drawable.ic_family_group);
        }
        holder.deviceListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.clickonListLayout(data);
            }
        });
    }

    /**
     * Interface to override methods in Groups Fragment to call these methods on particular item click
     */
    public interface RecyclerViewClickListener {
        void clickonListLayout(HomeActivityListData homeActivityListData);
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

        private RelativeLayout devicesOperationLayout;
        private ImageView devicesCloseOperation;
        private TextView editDevices;
        private TextView devicesDelete;
        private TextView trackingDeviceName;
        private ImageView deviceMenubar;
        private ImageView deviceMemberIcon;
        private CardView deviceListLayout;

        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackingDeviceName = itemView.findViewById(R.id.trackingDeviceName);
            devicesCloseOperation = itemView.findViewById(R.id.devicesCloseOperation);
            editDevices = itemView.findViewById(R.id.editDevices);
            devicesDelete = itemView.findViewById(R.id.devicesDelete);
            deviceMenubar = itemView.findViewById(R.id.deviceMenubar);
            devicesOperationLayout = itemView.findViewById(R.id.devicesOperationLayout);
            deviceMemberIcon = itemView.findViewById(R.id.deviceMemberIcon);
            deviceListLayout = itemView.findViewById(R.id.deviceListLayout);
            deviceMenubar.setOnClickListener(this);
            editDevices.setOnClickListener(this);
            devicesDelete.setOnClickListener(this);
            devicesCloseOperation.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.deviceMenubar:
                    devicesOperationLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.devicesCloseOperation:
                    devicesOperationLayout.setVisibility(View.GONE);
                    break;
                case R.id.devicesDelete:
                    //DeviceListAdapter.this.position = getAdapterPosition();
                    position = getAdapterPosition();
                    deleteAlertBox();
                    break;
                case R.id.editDevices:
                    deviceEditFlag = true;
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

        private void deleteAlertBox() {
            AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
            adb.setTitle(Constant.ALERT_TITLE);
            adb.setMessage(Constant.DELETE_CONFIRMATION_MESSAGE);
            adb.setIcon(android.R.drawable.ic_dialog_alert);
            adb.setPositiveButton("OK", (dialog, which) -> {
                DeviceListAdapter.this.devicesOperationLayout = devicesOperationLayout;
                phoneNumber = mList.get(getAdapterPosition()).getPhoneNumber();
                deviceId = mDbManager.getGroupMemberDetailByConsentId(mList.get(getAdapterPosition()).getConsentId()).getDeviceId();
                makeDeleteGroupAPICall(mList.get(getAdapterPosition()).getGroupId());
            });
            adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            adb.show();
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
            removeItem(position);
            devicesOperationLayout.setVisibility(View.GONE);
            DeviceFragment.checkMemberPresent();
            DeviceTableData mDeviceTableData = mDbManager.getDeviceTableData(phoneNumber);
            if (mDeviceTableData != null) {
                int count = mDeviceTableData.getAdditionCount();
                DeviceTableData deviceTableData = new DeviceTableData();
                deviceTableData.setAdditionCount(--count);
                deviceTableData.setPhoneNumber(phoneNumber);
                mDbManager.updateIntoDeviceTable(deviceTableData);
            }
            checkToDeleteDeviceAPICall();
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

    // Make a delete device API call if it is the last delete from app
    private void checkToDeleteDeviceAPICall() {
        if (mDbManager.getDeviceTableData(phoneNumber) != null
                && mDbManager.getDeviceTableData(phoneNumber).getAdditionCount() == 0) {
            GroupRequestHandler.getInstance(mContext).handleRequest(new DeleteDeviceRequest(new DeleteDeviceRequestSuccessListener(), new DeleteDeviceRequestErrorListener(), deviceId));
        }
    }

    /**
     * Delete device for user Success Listener
     */
    private class DeleteDeviceRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            System.out.println("Device deleted from user account");
        }
    }

    /**
     * Delete device for user error listener
     */
    private class DeleteDeviceRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println("Error in deleting device from user account");
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
