package org.rfcx.cellmapping.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



import java.sql.SQLException;
import java.util.ArrayList;

import org.rfcx.cellmapping.CellMappingApp;
import org.rfcx.cellmapping.R;
import org.rfcx.cellmapping.exceptions.UnauthorizeException;
import org.rfcx.cellmapping.interfaces.CheckinsCallback;
import org.rfcx.cellmapping.model.Poi;
import org.rfcx.cellmapping.model.User;
import org.rfcx.cellmapping.services.RFCXLocationService;
import org.rfcx.cellmapping.services.RFCXPhoneStateService;
import org.rfcx.cellmapping.utils.Utils;

public class MainActivity extends ActionBarActivity{

    private TelephonyManager telephonyManager;
    private LocationListener locationListener;
    private LocationManager locationManager;

    private Button startBtt;
    private Button stopBtt;
    private Button syncBtt;

    private static Location _location;

    private int _serviceState, _signalStrength, _signalCDMADbm, _signalEVODbm;
    private Location myLocation;

    private boolean isSynchronizing = false;
    private TextView signalStregthTextView;
    private TextView locationTextView;
    private TextView serviceStateTextView;

    private User user;
    private TextView checkinsFound;

    private ArrayList<Poi> pois = new ArrayList<Poi>();
    private ProgressDialog dialog;
    private TextView accuracyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            user = CellMappingApp.getUser();
            getSupportActionBar().setTitle(
                    String.format(getResources().getString(R.string.welcome), user.getName())
            );

        }catch(UnauthorizeException e) {
            CellMappingApp.logout();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        syncBtt = (Button) findViewById(R.id.syncBtt);
        syncBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncData();
            }
        });

        Intent stateIntent = new Intent(MainActivity.this, RFCXPhoneStateService.class);
        stateIntent.putExtra(RFCXPhoneStateService.INTENTACTION, RFCXPhoneStateService.STARTSERVICE);
        startService(stateIntent);

        Intent locationIntent = new Intent(MainActivity.this, RFCXLocationService.class);
        locationIntent.putExtra(RFCXLocationService.INTENTACTION, RFCXLocationService.STARTSERVICE);
        startService(locationIntent);

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Get Current Location
            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lat = myLocation.getLatitude();
            lon = myLocation.getLongitude();
            refreshUI();

        } catch (Exception e) {}

        signalStregthTextView = (TextView) findViewById(R.id.signalStrength);
        locationTextView = (TextView) findViewById(R.id.latLngTextView);
        serviceStateTextView = (TextView) findViewById(R.id.serviceStateTextView);
        checkinsFound = (TextView) findViewById(R.id.checkinsFound);
        accuracyTextView = (TextView) findViewById(R.id.accuracyTextView);

        checkinsFound.setText(String.format(
                getResources().getString(R.string.checkinsfound),
                pois.size()
        ));

        // before start listening signal strength and service states
        // we validate the SIM is available
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telephonyManager.getSimState();
        if(simState != TelephonyManager.SIM_STATE_READY) {
            Utils.Toast(MainActivity.this, R.string.simnull);
        }

        // we validate the SIM carrier is available
        String carrier = telephonyManager.getSimOperatorName();
        if(carrier == null) {
            Utils.Toast(MainActivity.this, R.string.carriernull);
        }
    }

    private void syncData() {

        dialog = ProgressDialog.show(MainActivity.this, null, null, true);
        dialog.setCancelable(false);

        /*
        CellMappingApp.getApiController().sync(pois, new CheckinsCallback() {
            @Override
            public void onSucess() {

            }

            @Override
            public void onError() {

            }
        });
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(phoneStateReceiver);
        unregisterReceiver(phoneLocationReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(phoneStateReceiver, new IntentFilter(RFCXPhoneStateService.NOTIFICATION));
        registerReceiver(phoneLocationReceiver, new IntentFilter(RFCXLocationService.NOTIFICATION));
    }

    private void saveSignal() {

        if(isSynchronizing) return;

        if(_serviceState != ServiceState.STATE_IN_SERVICE) {
            Utils.Toast(MainActivity.this, R.string.outofservice);
            return;
        }

        if(lat == 0 || lon == 0) {
            Utils.Toast(MainActivity.this, R.string.latlngnotfound);
            return;
        }
        // Just a bit of theory
            /*
             * Arbitrary Strength Unit (ASU) is an integer value proportional to the received signal
             * strength measured by the mobile phone.
             *
             * In GSM networks, ASU maps to RSSI (received signal strength indicator, see TS 27.007[1] sub clause 8.5).
             * dBm = 2 ?????? ASU - 113, ASU in the range of 0..31 and 99 (for not known or not detectable).
             *
             * In UMTS networks, ASU maps to RSCP level (received signal code power, see TS 27.007[1] sub clause 8.69 and TS 27.133 sub clause 9.1.1.3).
             * dBm = ASU - 116, ASU in the range of -5..91 and 255 (for not known or not detectable).
             *
             * In LTE networks, ASU maps to RSRP (reference signal received power, see TS 36.133, sub-clause 9.1.4).
             * The valid range of ASU is from 0 to 97. For the range 1 to 96, ASU maps to (ASU - 141) ????????? dBm < (ASU - 140).
             * The value of 0 maps to RSRP below -140 dBm and the value of 97 maps to RSRP above -44 dBm.
             *
             */

        // We will use GSM Networks
        /*
         *  0        -113 dBm or less
         *  1        -111 dBm
         *  2...30   -109... -53 dBm
         *  31        -51 dBm or greater
         *  99 not known or not detectable
         */

        Poi poi = new Poi();
        poi.setLat(lat);
        poi.setLng(lon);
        poi.setCdmaDbm(_signalCDMADbm);
        poi.setEvoDbm(_signalEVODbm);
        poi.setSignalstrenth(_signalStrength);
        poi.setAccuracy(accuracy);
        poi.setSid(String.valueOf(user.getSid()));
        poi.setName(user.getName());
        poi.setGuid(user.getGUID());

        try {
            CellMappingApp.getController().savePoi(MainActivity.this, poi);
            pois.add(poi);
            refreshUI();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private BroadcastReceiver phoneStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras = intent.getExtras();
            String result = extras.getString(RFCXPhoneStateService.RESULT);

            if(result.equals(RFCXPhoneStateService.SIGNALSTATECHANGE)) {
                _signalStrength = extras.getInt(RFCXPhoneStateService.SIGNALSTRENGTH);
                _signalCDMADbm  = extras.getInt(RFCXPhoneStateService.SIGNALCDMADBM);
                _signalEVODbm   = extras.getInt(RFCXPhoneStateService.SIGNALEVDODBM);

            }else if(result.equals(RFCXPhoneStateService.SERVICESTATECHANGE)) {
                _serviceState =  extras.getInt(RFCXPhoneStateService.SIGNALSTATECHANGE);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    saveSignal();
                }
            });
        }
    };

    private double lat;
    private double lon;
    private double accuracy;

    private BroadcastReceiver phoneLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String result = extras.getString(RFCXLocationService.RESULT);

            if(result.equals(RFCXLocationService.LOCATIONSTATECHANGE)) {
                lat = extras.getDouble(RFCXLocationService.LAT);
                lon = extras.getDouble(RFCXLocationService.LON);
                accuracy = extras.getInt(RFCXLocationService.ACCURACY);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveSignal();
                    }
                });
            }
        }
    };

    private void refreshUI() {

        String state;
        switch (_serviceState){
            case ServiceState.STATE_IN_SERVICE:
                state = "State in service"; break;
            case ServiceState.STATE_EMERGENCY_ONLY:
                state = "State Emergency Only"; break;
            case ServiceState.STATE_POWER_OFF:
                state = "State State power off"; break;
            default:
                state = "State out of service"; break;
        }
        serviceStateTextView.setText(String.format(
                getResources().getString(R.string.servicestate),
                state
        ));
        signalStregthTextView.setText(String.format(
                getResources().getString(R.string.signalstrength),
                String.valueOf(_signalStrength)
        ));
        locationTextView.setText(String.format(
                getResources().getString(R.string.location),
                String.valueOf(lat),
                String.valueOf(lon)
        ));
        checkinsFound.setText(String.format(
                getResources().getString(R.string.checkinsfound),
                pois.size()
        ));
        accuracyTextView.setText(String.format(
                getResources().getString(R.string.accuracy),
                accuracy
        ));
    }
}
