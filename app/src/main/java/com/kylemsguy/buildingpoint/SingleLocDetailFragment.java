package com.kylemsguy.buildingpoint;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

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

    private static final int ZOOM_LEVEL = 18;

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
            mLatLongCenter = getArguments().getDoubleArray(ARG_LATLONG_CENTER);
            mLatLongBuilding = getArguments().getDoubleArray(ARG_LATLONG_BUILDING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theView = inflater.inflate(R.layout.fragment_single_loc_detail, container, false);
        TextView nameText = (TextView) theView.findViewById(R.id.loc_buildingname);
        nameText.setText(mName);
        ImageView mapImage = (ImageView) theView.findViewById(R.id.loc_mapview);
        double dx = (mLatLongBuilding[0] - mLatLongCenter[0]);
        double dy = (mLatLongBuilding[1] - mLatLongCenter[1]);
        double newX = mLatLongCenter[0] + dx / 2;
        double newY = mLatLongCenter[1] + dy / 2;
        //int dZoom = (int) Math.ceil(Math.sqrt(dx*dx + dy*dy));
        int dZoom = 0;
        if (Math.sqrt(dx * dx + dy * dy) * 10000 > 0.8)
            dZoom++;
        if (Math.sqrt(dx * dx + dy * dy) * 10000 > 1.2)
            dZoom++;
        System.out.println(dx + " " + dy + " " + dZoom);
        String newCenter = newX + "," + newY;
        String imageURL = "http://dev.virtualearth.net/REST/v1/Imagery/Map/AerialWithLabels/" +
                newCenter + "/" + (ZOOM_LEVEL - dZoom) + "?pushpin=" +
                mLatLongCenter[0] + "," + mLatLongCenter[1] + ";0" +
                "&pushpin=" + mLatLongBuilding[0] + "," + mLatLongBuilding[1] + ";37" +
                "&format=png&mapMetadata=0" + "&mapsize=300,300" +
                "&key=" + ProcessPointTask.API_KEY;
        Bitmap mapBitmap = null;
        try {
            mapBitmap = new LoadMapTask().execute(imageURL).get();
            System.out.println("Loaded Map Bitmap");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        mapImage.setImageBitmap(mapBitmap);
        System.out.println(imageURL);
        return theView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
