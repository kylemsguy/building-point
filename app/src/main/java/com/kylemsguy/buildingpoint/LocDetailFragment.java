package com.kylemsguy.buildingpoint;

import android.app.Activity;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.view.ViewPager;

import net.zhuoweizhang.bingvenueaccess.model.Entity;

import java.util.Collections;
import java.util.List;

/* Bundle contents
    want name of building
    long/lat of user
    long/lat of target building
 */

public class LocDetailFragment extends Fragment {
    private DetailPagerAdapter adapter;
    private ViewPager viewPager;
    public LocDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new DetailPagerAdapter(getFragmentManager());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewPager = (ViewPager) inflater.inflate(R.layout.fragment_loc_detail, container, false);
        viewPager.setAdapter(adapter);
        return viewPager;
    }

    public DetailPagerAdapter getAdapter() {
        return adapter;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public static class DetailPagerAdapter extends FragmentStatePagerAdapter {
        private List<Entity> pages = Collections.EMPTY_LIST;
        private double[] center_loc;

        public DetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        public DetailPagerAdapter setPages(List<Entity> pages) {
            this.pages = pages;
            return this;
        }

        public DetailPagerAdapter setCenterLoc(double[] loc) {
            center_loc = loc;
            return this;
        }

        @Override
        public Fragment getItem(int position) {
            return SingleLocDetailFragment.newInstance(pages.get(position).name.get(""), center_loc, pages.get(position).location);
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}
