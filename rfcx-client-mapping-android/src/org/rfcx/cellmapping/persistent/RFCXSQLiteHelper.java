package org.rfcx.cellmapping.persistent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vruno on 5/29/14.
 */
public class RFCXSQLiteHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "rfcx.mapping.db0";
    private static final int DATABASE_VERSION = 3;

    public RFCXSQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SignalSQLiteHelper.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SignalSQLiteHelper.TABLE_NAME);
        onCreate(db);
    }
}
