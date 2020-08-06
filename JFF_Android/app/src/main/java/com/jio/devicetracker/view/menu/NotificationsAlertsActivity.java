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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.NotificationAlertsAdapter;
import com.jio.devicetracker.view.dashboard.DashboardMainActivity;
import com.jio.devicetracker.view.menu.settings.SettingsActivity;

public class NotificationsAlertsActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private TextView notifications;
    private TextView alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_alerts);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.NOTIFICATIONS_TITLE);
        title.setTypeface(Util.mTypeface(this, 5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        viewPager = findViewById(R.id.notoficationsViewPager);
        notifications = findViewById(R.id.notifications);
        notifications.setOnClickListener(this);
        alerts = findViewById(R.id.alerts);
        alerts.setOnClickListener(this);
        notifications.setText(Constant.NOTIFICATION_TAB + Html.fromHtml(getResources().getString(R.string.white_indicater)));
        NotificationAlertsAdapter notificationAlertsAdapter = new NotificationAlertsAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(notificationAlertsAdapter);
        pageChangeListener();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.notifications) {
            viewPager.setCurrentItem(0);
            notifications.setText(Constant.NOTIFICATION_TAB + Html.fromHtml(getResources().getString(R.string.white_indicater)));
            notifications.setTextColor(Color.WHITE);
            alerts.setText(Constant.ALERTS_WITHOUT_NEXT_LINE);
            alerts.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
        } else if (v.getId() == R.id.alerts) {
            viewPager.setCurrentItem(1);
            alerts.setText(Constant.ALERTS_TAB + Html.fromHtml(getResources().getString(R.string.white_indicater)));
            alerts.setTextColor(Color.WHITE);
            notifications.setText(Constant.NOTIFICATION_WITHOUT_NEXT_LINE);
            notifications.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
            RelativeLayout noticationAlertsTab = findViewById(R.id.noticationAlertsTab);
            noticationAlertsTab.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_frame_without_radious));
        } else if (v.getId() == R.id.back) {
            Intent dashboardIntent = new Intent(this, DashboardMainActivity.class);
            dashboardIntent.putExtra(Constant.START_DRAWER, Constant.YES);
            startActivity(dashboardIntent);
        }
    }

    /**
     * Called when you change the page
     */
    private void pageChangeListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Todo
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    notifications.setText(Constant.NOTIFICATION_TAB + Html.fromHtml(getResources().getString(R.string.white_indicater)));
                    notifications.setTextColor(Color.WHITE);
                    alerts.setText(Constant.ALERTS_WITHOUT_NEXT_LINE);
                    alerts.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                } else {
                    RelativeLayout noticationAlertsTab = findViewById(R.id.noticationAlertsTab);
                    noticationAlertsTab.setBackground(ContextCompat.getDrawable(NotificationsAlertsActivity.this, R.drawable.layout_frame_without_radious));
                    alerts.setText(Constant.ALERTS_TAB + Html.fromHtml(getResources().getString(R.string.white_indicater)));
                    alerts.setTextColor(Color.WHITE);
                    notifications.setText(Constant.NOTIFICATION_WITHOUT_NEXT_LINE);
                    notifications.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // To do
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent dashboardIntent = new Intent(this, DashboardMainActivity.class);
        dashboardIntent.putExtra(Constant.START_DRAWER, Constant.YES);
        startActivity(dashboardIntent);
    }

}
