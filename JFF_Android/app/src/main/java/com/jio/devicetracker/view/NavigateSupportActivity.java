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

package com.jio.devicetracker.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import org.w3c.dom.Text;

public class NavigateSupportActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_support);
        initUI();

    }

    private void initUI() {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.SUPPORT_TITLE);
        title.setTypeface(Util.mTypeface(this,5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView appName = findViewById(R.id.about_app_arrow);
        ImageView termCondition = findViewById(R.id.term_policy_arrow);
        ImageView faq = findViewById(R.id.faq_arrow);
        ImageView contactUs = findViewById(R.id.contact_us_arrow);
        TextView appNameTitle = findViewById(R.id.aboutApp);
        TextView termPolicyTitle = findViewById(R.id.term_policy);
        TextView faqTitle = findViewById(R.id.faq);
        TextView contactTitle = findViewById(R.id.contact_us);
        TextView versionTitle = findViewById(R.id.version);
        appNameTitle.setTypeface(Util.mTypeface(this,5));
        termPolicyTitle.setTypeface(Util.mTypeface(this,5));
        faqTitle.setTypeface(Util.mTypeface(this,5));
        contactTitle.setTypeface(Util.mTypeface(this,5));
        versionTitle.setTypeface(Util.mTypeface(this,5));
        appName.setOnClickListener(this);
        termCondition.setOnClickListener(this);
        faq.setOnClickListener(this);
        contactUs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.about_app_arrow:
                startActivity(new Intent(this,AboutAppActivity.class));
                break;
            case R.id.term_policy_arrow:
                startActivity(new Intent(this,TermAndConditionPolicyActivity.class));
                break;
            case R.id.faq_arrow:
                startActivity(new Intent(this,FaqActivity.class));
                break;
            case R.id.contact_us_arrow:
                startActivity(new Intent(this,ContactUsActivity.class));
                break;
            default:
                break;
        }
    }

}
