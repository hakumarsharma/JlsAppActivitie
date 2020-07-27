package com.jio.devicetracker.view.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jio.devicetracker.view.geofence.GeofenceListViewFragment;
import com.jio.devicetracker.view.geofence.GeofenceMapViewFragment;
import com.jio.devicetracker.view.location.MapFragment;
import com.jio.devicetracker.view.location.MapPeopleListFragment;

public class GeofenceViewAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public GeofenceViewAdapter(FragmentManager fm, int numOfTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new GeofenceMapViewFragment();
            case 1:
                return new GeofenceListViewFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
