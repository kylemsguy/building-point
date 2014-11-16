package com.kylemsguy.buildingpoint;

import android.app.Activity;
<<<<<<< HEAD
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
=======
import android.app.AlertDialog;
import android.content.DialogInterface;
>>>>>>> master
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
<<<<<<< HEAD
import android.os.IBinder;
=======
import android.provider.Settings;
>>>>>>> master
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
<<<<<<< HEAD
import android.widget.Toast;
=======
import android.content.Intent;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
>>>>>>> master

import java.util.List;

import net.zhuoweizhang.bingvenueaccess.model.*;

import net.openspatial.*;


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

	/* Nod */
	public static final String TAG = "NodCompass";

	private boolean nodEnabled = false;
	private EulerAngle nodEulerAngle;
	private double nodCalibrateSubtractAngle;
	private double nodCalibrateAddAngle;
	private long nodLastDownButton0;
	private long nodLastDownButton1;

	OpenSpatialService mOpenSpatialService;
    private ServiceConnection mOpenSpatialServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mOpenSpatialService = ((OpenSpatialService.OpenSpatialServiceBinder)service).getService();

            mOpenSpatialService.initialize(TAG, new OpenSpatialService.OpenSpatialServiceCallback() {
                @Override
                public void deviceConnected(BluetoothDevice device) {
                    try {
                        mOpenSpatialService.registerForPose6DEvents(device, new OpenSpatialEvent.EventListener() {
                            @Override
                            public void onEventReceived(OpenSpatialEvent event) {
								nodEulerAngle = ((Pose6DEvent) event).getEulerAngle();
                            }
                        });

                        mOpenSpatialService.registerForButtonEvents(device, new OpenSpatialEvent.EventListener() {
                            @Override
                            public void onEventReceived(OpenSpatialEvent event) {
                                ButtonEvent bEvent = (ButtonEvent)event;
								System.out.println("Button event: " + bEvent.buttonEventType);

                                if (bEvent.buttonEventType == ButtonEvent.ButtonEventType.TOUCH0_DOWN &&
									System.currentTimeMillis() - nodLastDownButton0 > 1000) {
                                    doQueryPoint();
									nodLastDownButton0 = System.currentTimeMillis();
                                } else if (bEvent.buttonEventType == ButtonEvent.ButtonEventType.TOUCH1_DOWN &&
									System.currentTimeMillis() - nodLastDownButton1 > 1000) {
                                    System.out.println("Recenter!");
									Toast.makeText(MainActivity.this, "Recentered!", Toast.LENGTH_SHORT
).show();
									if (nodEulerAngle != null) {
										nodCalibrateSubtractAngle = nodEulerAngle.yaw;
										nodCalibrateAddAngle = azimuth;
									}
									nodLastDownButton1 = System.currentTimeMillis();
                                }
                            }
                        });
						mOpenSpatialService.registerForGestureEvents(device, new OpenSpatialEvent.EventListener() {
							@Override
							public void onEventReceived(OpenSpatialEvent revent) {
								GestureEvent event = (GestureEvent) revent;
								System.out.println("Gesture! " + event.gestureEventType);
								int delta = 0;
								if (event.gestureEventType == GestureEvent.GestureEventType.SCROLL_DOWN) {
									delta = 1;
								} else if (event.gestureEventType == GestureEvent.GestureEventType.SCROLL_UP) {
									delta = -1;
								}
								if (delta != 0) {
									LocDetailFragment theFragment = ((LocDetailFragment) getFragmentManager().findFragmentByTag("LocDetailFragment"));
									int newIndex = theFragment.getViewPager().getCurrentItem() + delta;
									if (newIndex >= 0 && newIndex < theFragment.getViewPager().getAdapter().getCount()) {
										theFragment.getViewPager().setCurrentItem(newIndex, true);
									}
								}
							}
						});

                    } catch (OpenSpatialException e) {
                        System.err.println("Error registering for Pose6DEvent " + e);
						e.printStackTrace();
                    }
                }

                @Override
                public void buttonEventRegistrationResult(BluetoothDevice device, int i) {
                }

                @Override
                public void pointerEventRegistrationResult(BluetoothDevice device, int i) {
                }

                @Override
                public void pose6DEventRegistrationResult(BluetoothDevice device, int i) {

                }

                @Override
                public void gestureEventRegistrationResult(BluetoothDevice device, int i) {
                }
                @Override
                public void motion6DEventRegistrationResult(BluetoothDevice device, int i) {
                }
				@Override
				public void deviceDisconnected(BluetoothDevice device) {
				}

            });

            mOpenSpatialService.getConnectedDevices();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mOpenSpatialService = null;
        }
    };

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

		bindService(new Intent(this, OpenSpatialService.class), mOpenSpatialServiceConnection, BIND_AUTO_CREATE);

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
        } else if (id == R.id.action_toggle_nod) {
			nodEnabled = !nodEnabled;
			setTitle(nodEnabled? "BuildingPoint: Nod Enabled!": "BuildingPoint");
		}
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
		float yaw = nodEnabled && nodEulerAngle != null? (float) ((Math.PI * 2) - (nodEulerAngle.yaw - nodCalibrateSubtractAngle) + nodCalibrateAddAngle):
			azimuth;
		if (nodEnabled) {
			setTitle("nod: " + (((nodEulerAngle.yaw - nodCalibrateSubtractAngle) + nodCalibrateAddAngle) % Math.PI )+ " reg: " + azimuth);
		}
        new ProcessPointTask(this, currentLocation, yaw).execute();

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
