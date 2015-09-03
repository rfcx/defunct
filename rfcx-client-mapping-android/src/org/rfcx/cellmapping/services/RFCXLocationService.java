package org.rfcx.cellmapping.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Urucas on 9/24/14.
 */
public class RFCXLocationService extends Service {

    private static LocationManager locationManager;
    private static RFCXLocationListener locationListener;

    public static String LOCATIONSTATECHANGE = "changeloc";
    public static String LAT = "lat";
    public static String LON = "lon";
    public static final String ACCURACY = "accuracy";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.rfcx.cellmapping";
    public static final String INTENTACTION = "intent action";
    public static final String STARTSERVICE = "start service";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new RFCXLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationListener);

        return START_STICKY;
    }

    private class RFCXLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            sendLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }

    public void sendLocation(Location location){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(LAT, location.getLatitude());
        intent.putExtra(LON, location.getLongitude());
        intent.putExtra(ACCURACY, (int) location.getAccuracy());
        intent.putExtra(RESULT, LOCATIONSTATECHANGE);
        sendBroadcast(intent);
    }

}
