package org.rfcx.cellmapping.controller;

import android.content.Context;

import java.sql.SQLException;
import java.util.ArrayList;

import org.rfcx.cellmapping.model.Poi;
import org.rfcx.cellmapping.persistent.SignalDataSource;


/**
 * Created by Urucas on 9/23/14.
 */
public class PersistentController {

    private SignalDataSource signalDataSource;

    public boolean savePoi(Context context, Poi poi) throws SQLException {

        if (signalDataSource == null) {
            signalDataSource = new SignalDataSource(context);
            signalDataSource.open();
        }
        return signalDataSource.savePoi(poi);
    }

    public ArrayList<Poi> getPois(Context context) throws SQLException {
        if (signalDataSource == null) {
            signalDataSource = new SignalDataSource(context);
            signalDataSource.open();
        }
        return signalDataSource.getPois();
    }
}
