package com.jio.devicetracker.view.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.adapter.AlertsFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class AlertsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);
        displayDataInList(view);
        return view;
    }

    private void displayDataInList(View view) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView mRecyclerView = view.findViewById(R.id.alertsList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        List<HomeActivityListData> groupDetailList = new DBManager(getActivity()).getAllGroupDetail();
        List<HomeActivityListData> mGroupIconList = new DBManager(getActivity()).getAllGroupIconTableData();
        List<HomeActivityListData> groupList = new ArrayList<>();
        for (HomeActivityListData data : groupDetailList) {
            if (data.getCreatedBy() != null && data.getCreatedBy().equalsIgnoreCase(new DBManager(getActivity()).getAdminLoginDetail().getUserId())
                    && !data.getStatus().equalsIgnoreCase(Constant.COMPLETED)) {
                if (!data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME) && !data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_DEVICE_GROUP_NAME)) {
                    HomeActivityListData homeActivityListData = new HomeActivityListData();
                    homeActivityListData.setGroupName(data.getGroupName());
                    homeActivityListData.setGroupId(data.getGroupId());
                    homeActivityListData.setStatus(data.getStatus());
                    homeActivityListData.setCreatedBy(data.getCreatedBy());
                    homeActivityListData.setUpdatedBy(data.getUpdatedBy());
                    homeActivityListData.setProfileImage(data.getProfileImage());
                    homeActivityListData.setFrom(data.getFrom());
                    homeActivityListData.setTo(data.getTo());
                    homeActivityListData.setConsentsCount(data.getConsentsCount());
                    for (HomeActivityListData mHomeActivityListData : mGroupIconList) {
                        if (mHomeActivityListData.getGroupId().equalsIgnoreCase(data.getGroupId())) {
                            homeActivityListData.setGroupIcon(mHomeActivityListData.getGroupIcon());
                        }
                    }
                    groupList.add(homeActivityListData);
                }
            }
        }
        AlertsFragmentAdapter alertsFragmentAdapter = new AlertsFragmentAdapter(groupList);
        mRecyclerView.setAdapter(alertsFragmentAdapter);
    }

}
