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

package com.jio.devicetracker.view.menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.FAQData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.FAQListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends Activity {

    RecyclerView faqRecyclerList;
    private String[] questionArr = {Constant.QUES_1,Constant.QUES_2,Constant.QUES_3,Constant.QUES_4};
    private String[] answerArr ={Constant.ANS_1,Constant.ANS_2,Constant.ANS_3,Constant.ANS_4};
    List<FAQData> faqDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.FAQ_TITLE);
        title.setTypeface(Util.mTypeface(this,5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        faqDataList = new ArrayList<>();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initData();
        faqRecyclerList = findViewById(R.id.faqList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        faqRecyclerList.setLayoutManager(linearLayoutManager);
        FAQListAdapter faqAdapter = new FAQListAdapter(faqDataList,this);
        faqRecyclerList.setAdapter(faqAdapter);


    }

    private void initData() {

        for(int i=0;i<questionArr.length;i++)
        {
            FAQData data = new FAQData();
            data.setQuestion(questionArr[i]);
            data.setAnswer(answerArr[i]);
            data.setExpandable(false);
            faqDataList.add(data);
        }

    }
}
