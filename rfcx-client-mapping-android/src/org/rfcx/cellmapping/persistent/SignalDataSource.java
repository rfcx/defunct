package org.rfcx.cellmapping.persistent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

import org.rfcx.cellmapping.model.Poi;


public class SignalDataSource {

    private SQLiteDatabase database;
    private RFCXSQLiteHelper dbHelper;

    private String[] allColumns = {
            SignalSQLiteHelper.COLUMN_ID,
            SignalSQLiteHelper.COLUMN_SID,
            SignalSQLiteHelper.COLUMN_SLAT,
            SignalSQLiteHelper.COLUMN_SLNG,
            SignalSQLiteHelper.COLUMN_SNAME,
            SignalSQLiteHelper.COLUMN_SSTRENGTH,
            SignalSQLiteHelper.COLUMN_GUID
    };

    public SignalDataSource(Context context) {
        dbHelper = new RFCXSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private Poi cursorToPoi(Cursor cursor) {
        Poi poi = new Poi();

        poi.setSid(cursor.getString(cursor.getColumnIndex(SignalSQLiteHelper.COLUMN_SID)));
        poi.setGuid(cursor.getString(cursor.getColumnIndex(SignalSQLiteHelper.COLUMN_GUID)));
        poi.setName(cursor.getString(cursor.getColumnIndex(SignalSQLiteHelper.COLUMN_SNAME)));
        poi.setLat(cursor.getDouble(cursor.getColumnIndex(SignalSQLiteHelper.COLUMN_SLAT)));
        poi.setLng(cursor.getDouble(cursor.getColumnIndex(SignalSQLiteHelper.COLUMN_SLNG)));
        poi.setSignalstrenth(cursor.getInt(cursor.getColumnIndex(SignalSQLiteHelper.COLUMN_SSTRENGTH)));
        poi.setAccuracy(cursor.getDouble(cursor.getColumnIndex(SignalSQLiteHelper.COLUMN_ACCURACY)));
        return poi;
    }

    public boolean savePoi(Poi poi){

        ContentValues values = new ContentValues();
        values.put(SignalSQLiteHelper.COLUMN_SID, poi.getSid());
        values.put(SignalSQLiteHelper.COLUMN_SNAME, poi.getName());
        values.put(SignalSQLiteHelper.COLUMN_SLAT, poi.getLat());
        values.put(SignalSQLiteHelper.COLUMN_SLNG, poi.getLat());
        values.put(SignalSQLiteHelper.COLUMN_SSTRENGTH, poi.getSignalstrenth());
        values.put(SignalSQLiteHelper.COLUMN_GUID, poi.getGuid());
        values.put(SignalSQLiteHelper.COLUMN_ACCURACY, poi.getAccuracy());

        long insertId = database.insert(SignalSQLiteHelper.TABLE_NAME, null, values);
        return insertId != -1 ? true : false;
    }

    public ArrayList<Poi> getPois() {
        ArrayList<Poi> pois = new ArrayList<Poi>();

        Cursor cursor = database.query(
                SignalSQLiteHelper.TABLE_NAME,
                allColumns,
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Poi poi = this.cursorToPoi(cursor);
            pois.add(poi);
            cursor.moveToNext();
        }
        return pois;
    }
}
