package com.jio.devicetracker.view.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.AlertsFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class AlertsFragment extends Fragment implements View.OnClickListener {

    private List<GroupMemberDataList> groupMemberList;
    private TextView alertsMemberName;
    private TextView alertMemberAddress;
    private ImageView alertBackButton;
    private ImageView alertNextButton;
    private int position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);
        alertsMemberName = view.findViewById(R.id.alertsMemberName);
        alertMemberAddress = view.findViewById(R.id.alertMemberAddress);
        alertBackButton = view.findViewById(R.id.alertBackButton);
        alertNextButton = view.findViewById(R.id.alertNextButton);
        alertBackButton.setOnClickListener(this);
        alertNextButton.setOnClickListener(this);
        displayDataInList(view);
        displayIndividualUserDetail(position);
        return view;
    }

    private void displayDataInList(View view) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView mRecyclerView = view.findViewById(R.id.alertsList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DBManager mDbManager = new DBManager(getActivity());
        List<GroupMemberDataList> groupMemberDataLists = mDbManager.getAllGroupMemberData();
        groupMemberList = new ArrayList<>();
        for (GroupMemberDataList groupMemberDataList : groupMemberDataLists) {
            if (mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName() != null
                && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)
                    && mDbManager.getGroupDetail(groupMemberDataList.getGroupId()).getStatus().equalsIgnoreCase(Constant.ACTIVE)
                    && groupMemberDataList.getConsentStatus().equalsIgnoreCase(Constant.APPROVED)) {
                groupMemberList.add(groupMemberDataList);
            }
        }
        AlertsFragmentAdapter alertsFragmentAdapter = new AlertsFragmentAdapter(groupMemberList);
        mRecyclerView.setAdapter(alertsFragmentAdapter);
    }

    private void displayIndividualUserDetail(int position) {
        if (!groupMemberList.isEmpty()) {
            alertsMemberName.setText(groupMemberList.get(position).getName());
            alertMemberAddress.setText("Nagina Singh Colony, Chapra, Bihar");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.alertBackButton) {
            if (position > 0) {
                displayIndividualUserDetail(--position);
                Util.getInstance().createNotificationChannel(getActivity());
            }
        } else if (v.getId() == R.id.alertNextButton) {
            if (position < groupMemberList.size() - 1) {
                displayIndividualUserDetail(++position);
                Util.getInstance().createNotificationChannel(getActivity());
            }
        }
    }
}
