package com.jio.devicetracker.view.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jio.devicetracker.view.ManualAddFragment;
import com.jio.devicetracker.view.QRCodeFragment;


public class HowToUseAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public HowToUseAdapter(FragmentManager fm, int numOfTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new QRCodeFragment();
            case 1:
                return new ManualAddFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}