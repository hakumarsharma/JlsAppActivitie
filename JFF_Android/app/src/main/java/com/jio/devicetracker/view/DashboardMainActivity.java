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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.DashboardAdapter;

import java.util.Locale;

public class DashboardMainActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DashboardAdapter dashboardAdapter;
    private TabItem tabPeople;
    private TabItem tabGroups;
    private TabItem tabDevices;
    private ImageView addGroupInDashboard;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_main);
        Toolbar toolbar = findViewById(R.id.dashboardToolbar);
        TextView title = findViewById(R.id.toolbar_title);
        drawerLayout = findViewById(R.id.drawerLayout);
        title.setText(Constant.DASHBOARD_TITLE);
        title.setTypeface(Util.mTypeface(this,5));
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        toggle.setDrawerIndicatorEnabled(false);

        toggle.setHomeAsUpIndicator(R.drawable.ic_more);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setNavigationData();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        initUI();
    }

    private void initUI() {
        tabLayout = findViewById(R.id.tablayout);
        tabPeople = findViewById(R.id.tabPeople);
        tabGroups = findViewById(R.id.tabGroups);
        tabDevices = findViewById(R.id.tabDevices);
        viewPager = findViewById(R.id.viewPager);
        addGroupInDashboard = findViewById(R.id.createGroup);
        addGroupInDashboard.setVisibility(View.VISIBLE);
        addGroupInDashboard.setOnClickListener(this);
        dashboardAdapter = new DashboardAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(dashboardAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        onTabClicked();
    }

    private void onTabClicked() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    tabLayout.getTabAt(0).setText(Constant.GROUP_WITH_DOT);
                } else if (tab.getPosition() == 1) {
                    tabLayout.getTabAt(1).setText(Constant.PEOPLE_WITH_DOT);
                } else {
                    tabLayout.getTabAt(2).setText(Constant.DEVICES_WITH_DOT);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    tabLayout.getTabAt(0).setText(Constant.GROUP_WITHOUT_DOT);
                } else if (tab.getPosition() == 1) {
                    tabLayout.getTabAt(1).setText(Constant.PEOPLE_WITHOUT_DOT);
                } else {
                    tabLayout.getTabAt(2).setText(Constant.DEVICES_WITHOUT_DOT);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.createGroup) {
        switch (tabLayout.getSelectedTabPosition()){
            case 0 :
                startActivity(new Intent(this, CreateGroupActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, AddPeopleActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, QRReaderInstruction.class));
                break;
        }
        }
    }

    private void setNavigationData() {
        NavigationView navigationView = findViewById(R.id.nv);
        View header = navigationView.getHeaderView(0);
        ImageView profileIcn = header.findViewById(R.id.profileIcon);
        TextView userAccountName = header.findViewById(R.id.user_account_name);
        TextView userPhoneNumber = header.findViewById(R.id.user_number);
        if(!Util.userName.isEmpty() && !Util.userNumber.isEmpty()) {
            userAccountName.setText(Util.userName.substring(0, 1).toUpperCase(Locale.ROOT) + Util.userName.substring(1));
            userPhoneNumber.setText(Util.userNumber);
        }
        ImageView backDrawer = header.findViewById(R.id.back);
        backDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });
        profileIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              gotoNavigateUserProfileActivity();
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.notification:
                        //gotoNotificationActivity();
                        break;
                    case R.id.settings:
                        //gotoSettingsActivity();
                        break;
                    case R.id.howtoadd:
                        //goToHowtoAddActivity();
                        break;
                    case R.id.support:
                        startActivity(new Intent(DashboardMainActivity.this,NavigateSupportActivity.class));
                        break;
                    case R.id.logout:
                        //updateLogoutData();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void gotoNavigateUserProfileActivity() {
        Intent intent = new Intent(this,NavigateUserProfileActivity.class);
        startActivity(intent);
    }
}
