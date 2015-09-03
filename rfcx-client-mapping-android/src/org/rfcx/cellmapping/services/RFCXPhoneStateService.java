package org.rfcx.cellmapping.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;


public class RFCXPhoneStateService extends Service {


    private static TelephonyManager telephonyManager;

    private static RFCXPhoneStateListener phoneStateListener;

    public static String TAG_NAME = "RFCXPhoneStateService";

    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.rfcx.cellmapping";
    public static final String INTENTACTION = "intent action";

    public static final String SERVICESTATECHANGE = "state change";

    public static final String SIGNALSTATECHANGE = "signal change";
    public static final String SIGNALSTRENGTH = "signal strength";
    public static final String SIGNALEVDODBM = "signal evdodbm";
    public static final String SIGNALCDMADBM = "signal cdmadbm";

    public static final String STARTSERVICE = "start service";
    public static final String STOPSERVICE = "stop service";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Bundle extras = intent.getExtras();
        String action = extras.getString(INTENTACTION);
        if(action.equals(STARTSERVICE)) {
            if(phoneStateListener == null) {
                phoneStateListener = new RFCXPhoneStateListener();
            }
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_SERVICE_STATE);

        }else{
            if(phoneStateListener == null) {
                phoneStateListener = new RFCXPhoneStateListener();
            }
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    private class RFCXPhoneStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            sendSignalStrengthChanged(signalStrength);
        }

        @Override
        public void onServiceStateChanged (ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            sendStateChanged(serviceState);
        }
    }


    private void sendStateChanged(ServiceState serviceState){

        Log.i("state changed", "state changed");
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(SERVICESTATECHANGE, serviceState.getState());
        intent.putExtra(RESULT, SERVICESTATECHANGE);
        sendBroadcast(intent);
    }

    private void sendSignalStrengthChanged(SignalStrength signalStrength) {

        Log.i("signal changed", "signal changed");
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(SIGNALSTRENGTH, signalStrength.getGsmSignalStrength());
        intent.putExtra(SIGNALEVDODBM, signalStrength.getEvdoDbm());
        intent.putExtra(SIGNALCDMADBM, signalStrength.getCdmaDbm());
        intent.putExtra(RESULT, SIGNALSTATECHANGE);
        sendBroadcast(intent);
    }
}
