package com.kylemsguy.buildingpoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import net.zhuoweizhang.bingvenueaccess.model.*;


public class MainActivity extends Activity implements SensorEventListener, View.OnClickListener, LocationListener {

    private float[] mGravity;
    private float[] mGeomagnetic;

    private float azimuth = 0.0f;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private LocationManager mLocationManager;

    private TextView tvHeading;
    private TextView lastBamHeading;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHeading = (TextView) findViewById(R.id.tvHeading);
        lastBamHeading = (TextView) findViewById(R.id.tvLastBam);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        for (String provider : mLocationManager.getProviders(true)) {
            currentLocation = mLocationManager.getLastKnownLocation(provider);
            System.out.println("yo: " + provider + ":" + currentLocation);
            if (currentLocation != null) break;
        }

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            System.out.println("Warning: entering low-accuracy mode");
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } else {
            showLocationSettingDialog();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mLocationManager.removeUpdates(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use.
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = orientation[0];
            }
        }
        //tvHeading.setText("Heading: " + Float.toString(azimuth) + " rad");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_locsettings) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {

    }

    private void showLocationSettingDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("No location data found. Check your Location Settings.");
        alertDialog.setTitle("No Location Data");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // No code here yet
            }
        });
        alertDialog.setCancelable(true);
        alertDialog.create().show();
    }

    public void doQueryPoint() {
        if (currentLocation == null) {
            showLocationSettingDialog();
        } else {
            new ProcessPointTask(this, currentLocation, azimuth).execute();
        }
    }

    public void onLocationChanged(Location location) {
        System.out.println(location);
        this.currentLocation = location;
    }

    public void receiveEntities(List<Entity> entities, Location location) {
        if (location == null) return;
        // PASS ENTITIES TO ADAPTER IN SINGLELOCDETAILFRAGMENT
        LocDetailFragment theFragment = ((LocDetailFragment) getFragmentManager().findFragmentByTag("LocDetailFragment"));
        theFragment.getViewPager().setCurrentItem(0);
        theFragment.getAdapter().setPages(entities).setCenterLoc(new double[]{location.getLatitude(), location.getLongitude()}).
                notifyDataSetChanged();
        theFragment.getViewPager().setVisibility(entities.size() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }
}
