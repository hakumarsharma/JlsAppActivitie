package com.jio.devicetracker.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Constant;

import java.util.List;

public class ProfileTrackedByListAdapter extends RecyclerView.Adapter<ProfileTrackedByListAdapter.ViewHolder> {
    private List mList;
    /**
     * Constructor to display the active session devices list
     *
     * @param mList
     */
    public ProfileTrackedByListAdapter(List mList) {
        this.mList = mList;
    }

    /**
     * Binds the given View to the position
     * @param parent
     * @param viewType
     * @return View Holder object
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_tracked_profile_list, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mList.get(position).getClass().getName().equalsIgnoreCase(Constant.GROUP_NAME_CLASS_NAME)) {
            HomeActivityListData data = (HomeActivityListData) mList.get(position);
            //holder.profile.setImageResource(R.drawable.ic_family_group);
            holder.name.setText(data.getGroupName());
            /*holder.relativeLayout.setOnClickListener(v -> {
                itemListener.clickOnListLayout(data.getProfileImage(), data.getGroupName(), data.getGroupId()
                        , data.getCreatedBy());
                return;
            });*/
        } else if (mList.get(position).getClass().getName().equalsIgnoreCase(Constant.GROUP_MEMBER_CLASS_NAME)) {
            GroupMemberDataList data = (GroupMemberDataList) mList.get(position);
            //holder.profile.setImageResource(R.drawable.ic_user);
            holder.name.setText(data.getName());
            /*holder.relativeLayout.setOnClickListener(v -> {
                itemListener.clickOnListLayout(data.getProfileImage(), data.getName(), data.getConsentId(), "");
                return;
            });*/
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
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView profile;
        public ImageView close;

        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            /*profile = itemView.findViewById(R.id.groupmemberIcon);
            groupOptLayout = itemView.findViewById(R.id.oprationLayout);
            close = itemView.findViewById(R.id.close);
            close.setOnClickListener(this);
            relativeLayout = itemView.findViewById(R.id.activeSessionLayout);
            activeSessionOptions = itemView.findViewById(R.id.operationStatus);
            activeSessionOptions.setOnClickListener(this);*/
        }

      /*  @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.operationStatus:
                    groupOptLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.close:
                    groupOptLayout.setVisibility(View.GONE);
                    break;
            }
        }*/
    }

}
