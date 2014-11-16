package com.kylemsguy.buildingpoint;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import net.zhuoweizhang.bingvenueaccess.model.Entity;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleLocDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleLocDetailFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "name";
    private static final String ARG_LATLONG_CENTER = "latlongcenter";
    private static final String ARG_LATLONG_BUILDING = "latlongbuilding";

    private String mName;
    private double[] mLatLongCenter;
    private double[] mLatLongBuilding;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name            Parameter 1.
     * @param latlongcenter   Parameter 2.
     * @param latlongbuilding Parameter 3.
     * @return A new instance of fragment SingleLocDetailFragment.
     */
    public static SingleLocDetailFragment newInstance(String name, double[] latlongcenter, double[] latlongbuilding) {
        SingleLocDetailFragment fragment = new SingleLocDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putDoubleArray(ARG_LATLONG_CENTER, latlongcenter);
        args.putDoubleArray(ARG_LATLONG_BUILDING, latlongbuilding);
        fragment.setArguments(args);
        return fragment;
    }

    public SingleLocDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(ARG_NAME);
            mLatLongCenter = getArguments().getDoubleArray(ARG_LATLONG_BUILDING);
            mLatLongBuilding = getArguments().getDoubleArray(ARG_LATLONG_CENTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_loc_detail, container, false);
    }

    private static class DetailPagerAdapter extends FragmentStatePagerAdapter {
        private List<Entity> pages;
        private double[] center_loc;

        public DetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        public void setPages(List<Entity> pages) {
            this.pages = pages;
        }

        @Override
        public Fragment getItem(int position) {
            return SingleLocDetailFragment.newInstance(pages.get(position).name.get(""), center_loc, pages.get(position).location);
        }
    }

}
