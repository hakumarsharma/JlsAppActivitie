package com.jio.devicetracker.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.EditmemberListAdapter;

import java.util.ArrayList;
import java.util.List;

public class EditMemberActivity extends Activity implements View.OnClickListener {
    private EditmemberListAdapter adapterData;
    private List<GroupMemberDataList> dataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member);
        TextView groupName = findViewById(R.id.group_name);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.EDIT_MEMBER_PROFILE_TITLE);
        title.setTypeface(Util.mTypeface(this,5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        groupName.setTypeface(Util.mTypeface(this,3));
        RecyclerView editMemberList = findViewById(R.id.editRecyclerList);
        dataList = new ArrayList<>();
        addGroupData();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        editMemberList.setLayoutManager(mLayoutManager);
        adapterData = new EditmemberListAdapter(dataList,this);
        editMemberList.setAdapter(adapterData);

    }

    private void addGroupData() {
        for(int i = 0; i<4 ; i++){
            GroupMemberDataList data = new GroupMemberDataList();
            data.setName("Test");
            dataList.add(data);
        }
    }

    @Override
    public void onClick(View v) {

        finish();
    }
}
