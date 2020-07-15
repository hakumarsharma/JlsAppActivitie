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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jio.devicetracker.view.location.MapFragment;
import com.jio.devicetracker.view.location.MapPeopleListFragment;

public class LocationAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public LocationAdapter(FragmentManager fm, int numOfTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MapFragment();
            case 1:
                return new MapPeopleListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}

