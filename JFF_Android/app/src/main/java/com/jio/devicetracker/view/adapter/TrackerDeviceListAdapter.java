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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.DashboardActivity;

import java.util.List;

/**
 * Implementation of adapter for trackee list in Home Screen with grouping options.
 */
public class TrackerDeviceListAdapter extends RecyclerView.Adapter<TrackerDeviceListAdapter.ViewHolder> {

    private List mData;
    private static RecyclerViewClickListener itemListener;
    private int count;
    private int groupCount;
    private static Context mContext;

    /**
     * Constructor to add devices in home screen
     *
     * @param mData
     */
    public TrackerDeviceListAdapter(List mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    /**
     * Binds the given View to the position
     *
     * @param parent
     * @param viewType
     * @return View Holder object
     */
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_devicetracker_list, parent, false);

        return new ViewHolder(itemView);
    }

    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mData.get(position).getClass().getName().equalsIgnoreCase(Constant.GROUP_NAME_CLASS_NAME)) {
            HomeActivityListData data = (HomeActivityListData) mData.get(position);
            if (data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)) {
                holder.mIconImage.setImageResource(R.drawable.ic_user);
                holder.name.setText(data.getName());
            } else {
                holder.mIconImage.setImageResource(R.drawable.ic_group_button);
                holder.name.setText(data.getGroupName());
            }
            holder.viewOptionMenu.setOnClickListener(v -> itemListener.onPopupMenuClicked(holder.viewOptionMenu, position, data.getGroupName(), data.getPhoneNumber(), data.getGroupId(), Constant.GROUP));

            holder.mListlayout.setOnLongClickListener(v -> true);
            holder.mListlayout.setOnClickListener(v -> {
                itemListener.clickonListLayout(data.getGroupName(), data.getGroupId(), data.getProfileImage());
                return;
            });
            holder.mConsentCheckButton.setOnClickListener(v -> {
                // Check If any group Member is already checked
                for (GroupMemberDataList list : DashboardActivity.grpMemberDataList) {
                    if (list.isSelected()) {
                        Toast.makeText(mContext, Constant.SELECTION_ERROR, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                // If Already checked then uncheck it
                if (data.isSelected() == true) {
                    holder.mConsentCheckButton.setBackgroundResource(R.drawable.ic_checkboxempty);
                    data.setSelected(false);
                    DashboardActivity.grpDataList.remove(data);
                    return;
                }
                data.setSelected(!data.isSelected());
                if (data.isSelected()) {
                    for (HomeActivityListData list : DashboardActivity.grpDataList) {
                        if (list.isSelected() == true && groupCount > 0) {
                            Toast.makeText(mContext, Constant.SELECTION_ERROR, Toast.LENGTH_LONG).show();
                            data.setSelected(false);
                            DashboardActivity.grpDataList.remove(data);
                            return;
                        }
                    }
                    itemListener.checkBoxClickedForGroup(data);
                    holder.mConsentCheckButton.setBackgroundResource(R.drawable.ic_checkmark);
                    groupCount ++;
                }
            });
            holder.mConsentStatusButton.setOnClickListener(v -> itemListener.consentClick(data.getGroupId(), data.getPhoneNumber(), null)); // consentId is null for Group
        } else if (mData.get(position).getClass().getName().equalsIgnoreCase(Constant.GROUP_MEMBER_CLASS_NAME)) {
            GroupMemberDataList data = (GroupMemberDataList) mData.get(position);
            holder.mIconImage.setImageResource(R.drawable.ic_user);
            holder.name.setText(data.getName());
            holder.phone.setText(data.getNumber());
            /**
             * Disable the Consent status button and change the icon of button if consent is sent for group Member
             * Disable the Consent status button and change the icon of button if consent is approved for group Member
             */
            if(data.getConsentStatus().equalsIgnoreCase(Constant.PENDING)) {
                holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentPending));
                holder.mConsentStatusButton.setText(Constant.CONSENT_PENDING);
                holder.mConsentStatusButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pending, 0, 0, 0);
                holder.mConsentStatusButton.setEnabled(false);
            } else if(data.getConsentStatus().equalsIgnoreCase(Constant.APPROVED)) {
                holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentApproved));
                holder.mConsentStatusButton.setText(Constant.APPROVED);
                holder.mConsentStatusButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_approved, 0, 0, 0);
                holder.mConsentStatusButton.setEnabled(false);
            }
            holder.viewOptionMenu.setOnClickListener(v -> itemListener.onPopupMenuClicked(holder.viewOptionMenu, position, data.getName(), data.getNumber(), data.getGroupId(), Constant.INDIVIDUAL_MEMBER));

            holder.mListlayout.setOnLongClickListener(v -> true);
            holder.mListlayout.setOnClickListener(v -> {
                itemListener.clickonListLayout(data.getName(), data.getConsentId(), data.getProfileImage());
                return;
            });
            holder.mConsentCheckButton.setOnClickListener(v -> {
                // Check If any group is already checked
                for (HomeActivityListData list : DashboardActivity.grpDataList) {
                    if (list.isSelected()) {
                        Toast.makeText(mContext, Constant.SELECTION_ERROR, Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                // If Already checked then uncheck it
                if (data.isSelected() == true) {
                    holder.mConsentCheckButton.setBackgroundResource(R.drawable.ic_checkboxempty);
                    data.setSelected(false);
                    DashboardActivity.grpMemberDataList.remove(data);
                    return;
                }

                // marked the checked box as selected
                data.setSelected(!data.isSelected());
                if (data.isSelected()) {
                    for (GroupMemberDataList list : DashboardActivity.grpMemberDataList) {
                        if (list.isSelected() == true && count > 0) {
                            Toast.makeText(mContext, Constant.SELECTION_ERROR, Toast.LENGTH_LONG).show();
                            data.setSelected(false);
                            DashboardActivity.grpMemberDataList.remove(data);
                            return;
                        }
                    }
                    itemListener.checkBoxClickedForGroupMember(data);
                    holder.mConsentCheckButton.setBackgroundResource(R.drawable.ic_checkmark);
                    count ++;
                }
            });
            holder.mConsentStatusButton.setOnClickListener(v -> itemListener.consentClick(data.getGroupId(), data.getNumber(), data.getConsentId()));
        }

        /*holder.phone.setText(mData.get(position).getPhoneNumber());
        holder.name.setText(mData.get(position).getGroupName());
        if (mData.get(position).isGroupMember() == true) {
            holder.mIconImage.setImageResource(R.drawable.ic_group_button);
        } else if (mData.get(position).getDeviceType() != null && mData.get(position).getDeviceType().equalsIgnoreCase("People Tracker")) {
            holder.mIconImage.setImageResource(R.drawable.ic_user);
        } else if (mData.get(position).getDeviceType() != null && mData.get(position).getDeviceType().equalsIgnoreCase("Pet Tracker")) {
            holder.mIconImage.setImageResource(R.drawable.ic_pet);
            holder.mConsentStatus.setVisibility(View.INVISIBLE);
        }
        // holder.mDelete.setTransformationMethod(null);
        // holder.mEdit.setTransformationMethod(null);
        holder.mConsentStatus.setTransformationMethod(null);
        if (mData.get(position).getConsentStaus() != null && mData.get(position).getConsentStaus().trim().equalsIgnoreCase(Constant.CONSENT_APPROVED_STATUS)) {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentApproved));
            holder.mConsentStatus.setText(Constant.CONSENT_APPROVED_STATUS);
            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_approved, 0, 0, 0);
            holder.mConsentStatus.setEnabled(false);
        } else if (mData.get(position).getConsentStaus() != null && mData.get(position).getConsentStaus().trim().equals(Constant.CONSENT_PENDING)) {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentPending));
            holder.mConsentStatus.setText(Constant.CONSENT_PENDING);
            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pending, 0, 0, 0);
            holder.mConsentStatus.setEnabled(false);
        } else {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentNotSent));
            holder.mConsentStatus.setText(Constant.REQUEST_CONSENT);
            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notsent, 0, 0, 0);
        }*/

        /*if (!data.isGroupMember()) {
            holder.mConsentStatus.setOnClickListener(v -> {
                itemListener.consetClick(mData.get(position).getPhoneNumber());
            });
        } else {
            holder.mConsentStatus.setOnClickListener(v -> {
                itemListener.consentClickForGroup(mData.get(position).getName());
            });
        }*/
        /*holder.mConsent.setOnClickListener(v -> {
            data.setSelected(!data.isSelected());
            if (data.isGroupMember() && data.isSelected()) {
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkmark);
                mSelectData = new MultipleselectData();
                mSelectData.setName(mData.get(position).getName());
                itemListener.recyclerViewListClicked(v, position, mSelectData, 1);
            } else if (data.isSelected()) {
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkmark);
                mSelectData = new MultipleselectData();
                mSelectData.setPhone(mData.get(position).getPhoneNumber());
                mSelectData.setLat(mData.get(position).getLat());
                mSelectData.setLng(mData.get(position).getLng());
                mSelectData.setName(mData.get(position).getName());
                mSelectData.setImeiNumber(mData.get(position).getImeiNumber());
                itemListener.recyclerViewListClicked(v, position, mSelectData, 2);
            } else if (data.isGroupMember()) {
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkboxempty);
                itemListener.recyclerViewListClicked(v, position, mSelectData, 3);
            } else {
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkboxempty);
                itemListener.recyclerViewListClicked(v, position, mSelectData, 3);
            }
        });*/

        // holder.mEdit.setOnClickListener(v -> itemListener.recyclerviewEditList(mData.get(position).getRelation(),mData.get(position).getPhoneNumber()));
        // holder.mDelete.setOnClickListener(v -> itemListener.recyclerviewDeleteList(mData.get(position).getPhoneNumber(),position));
    }


    /**
     * return The total number of items in this adapter
     *
     * @return size
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView phone;
        public TextView name;
        public TextView status;
        public CardView mListlayout;
        public Button mConsentCheckButton;
        public Button mConsentStatusButton;
        public TextView viewOptionMenu;
        public ImageView mIconImage;

        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.phoneNumber);
            name = itemView.findViewById(R.id.nameDeviceTracker);
            mListlayout = itemView.findViewById(R.id.listLayout);
            mConsentCheckButton = itemView.findViewById(R.id.consentCheckButton);
            mConsentStatusButton = itemView.findViewById(R.id.requestConsentButton);
            viewOptionMenu = itemView.findViewById(R.id.textViewOptions);
            status = itemView.findViewById(R.id.statusView);
            mIconImage = itemView.findViewById(R.id.contactImage);
        }
    }

    /**
     * Interface to override methods in Dashboard to call those methods on particular item click
     */
    public interface RecyclerViewClickListener {
        void recyclerViewListClicked(View v, int position, MultipleselectData data, int val);

        // void recyclerviewEditList(String relation,String phoneNumber);
        // void recyclerviewDeleteList(String phoneNuber,int position);
        void clickonListLayout(String selectedGroupName, String groupId, int profileImage);

        void consentClick(String groupId, String phoneNumber, String consentId);

        void checkBoxClickedForGroupMember(GroupMemberDataList groupMemberDataList);
        void checkBoxClickedForGroup(HomeActivityListData homeActivityListData);
        void onPopupMenuClicked(View v, int position, String name, String number, String groupId, String groupMember);
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
     * Called when we remove device from home screen
     *
     * @param adapterPosition
     */
    public void removeItem(int adapterPosition) {
        mData.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }
}
