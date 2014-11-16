package com.kylemsguy.buildingpoint;

import java.util.*;

import android.app.*;
import android.location.*;
import android.os.*;
import net.zhuoweizhang.bingvenueaccess.*;
import net.zhuoweizhang.bingvenueaccess.model.*;

public class ProcessPointTask extends AsyncTask<Void, Void, Void> {

    private static final String API_KEY = "AiGyFKAL6AMsIccErosDUwQAGJKEzi-_pn8TZDGuXJqYRCW4mk5GKAG3itCF9AWF";
    private static final int MAX_RANGE_IN_METERS = 500;

    private MainActivity activity;
    private float heading;
    private Location location;
    private List<Entity> entities = new ArrayList<Entity>();
    private ProgressDialog progressDialog;
    public ProcessPointTask(MainActivity activity, Location location, float heading) {
        this.activity = activity;
        this.location = location;
        this.heading = heading;
    }

    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.show();
    }

    protected Void doInBackground(Void... args) {
        try {
            BingMapsClient client = new BingMapsClient(API_KEY, "BuildingPoint");
            NearbyVenue[] venues = client.getNearbyVenues(location.getLatitude(), location.getLongitude(), MAX_RANGE_IN_METERS);
            VenueMap venue = client.getVenue(venues[0].metadata.MapId);
            entities = VenueUtils.rayCast(venue, location.getLatitude(), location.getLongitude(), (heading / Math.PI) * 180,
                MAX_RANGE_IN_METERS / 1000.0, 10 / 1000.0);
            System.out.println(entities);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(Void arg) {
        progressDialog.dismiss();
        new AlertDialog.Builder(activity).setMessage(entities.toString()).show();
    }

}