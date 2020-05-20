package com.example.heyimhere;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.heyimhere.database.LocationRule;
import com.example.heyimhere.database.LocationRuleDao;

import java.util.List;

//Code based on Mahozad and alex.veprik's stack overflow post at https://stackoverflow.com/questions/8828639/get-gps-location-via-a-service-in-android

public class LocationRuleManager extends Service {
    private static final String TAG = "LOCATION";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10000; //Should be a 10 second interval.
    private static final float LOCATION_DISTANCE = 0.5f; //Not 100% sure what this does.
    private DatabaseManager mDatabaseManager;
    private LocationRuleDao mLocationRuleDao;

    private class LocationTracker implements android.location.LocationListener {
        Location mLastLocation;
        Context context;

        public LocationTracker(String provider, Context context) {
            Log.i(TAG, "LocationTracker " + provider);
            mLastLocation = new Location(provider);
            this.context = context;
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: " + location);
            mLastLocation = location;
            //If I can ever fix getRulesAroundSpot use that instead.
            List<LocationRule> rules = mLocationRuleDao.getAllList();
            if (rules != null) { //Check all potential rules for fulfilled ones.
                Log.i(TAG, "onLocationChanged: Rules found- " + rules.size());
                for(int i = 0; i < rules.size(); i++) {
                    LocationRule rule = rules.get(i);
                    float[] distance = {0f}; //getRulesAroundSpot cannot be 100% accurate, so check with this.
                    location.distanceBetween(rule.latitude, rule.longitude, location.getLatitude(), location.getLongitude(), distance);
                    if (distance[0] < rule.radius * 1609.3471) { //Convert radius from miles to meters.
                        rule.fulfilled = !rule.fulfilled;
                        mLocationRuleDao.update(rule);
                        RuleWrangler.CheckRules(context, rule.messageId, mDatabaseManager);
                    }
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationTracker[] mLocationTrackers = new LocationTracker[]{
            new LocationTracker(LocationManager.GPS_PROVIDER, this),
            new LocationTracker(LocationManager.NETWORK_PROVIDER, this)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationTrackers[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationTrackers[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "gps provider does not exist " + ex.getMessage());
        }

        mDatabaseManager = DatabaseManager.getDatabase(this);
        mLocationRuleDao = mDatabaseManager.mLocationRuleDao();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationTrackers.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationTrackers[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.i(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
