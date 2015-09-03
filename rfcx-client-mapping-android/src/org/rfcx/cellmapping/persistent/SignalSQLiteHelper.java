package org.rfcx.cellmapping.persistent;

/**
 * @copyright Urucas
 * @license   Copyright (C) 2013. All rights reserved
 * @link       http://urucas.com
 * @developers Bruno Alassia, Pamela Prosperi
 * @date {5/29/14}
**/
public class SignalSQLiteHelper {

    public static final String TABLE_NAME = "signalstrengh";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SID = "uid";
    public static final String COLUMN_GUID = "guid";
    public static final String COLUMN_SNAME = "name";
    public static final String COLUMN_SLAT = "lat";
    public static final String COLUMN_SLNG = "lng";
    public static final String COLUMN_SSTRENGTH = "signal";
    public static final String COLUMN_ACCURACY = "accuracy";

    public static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SID + " text not null, "
            + COLUMN_GUID + " text not null, "
            + COLUMN_SNAME + " text not null, "
            + COLUMN_SLAT + " double not null, "
            + COLUMN_SLNG + " double not null, "
            + COLUMN_SSTRENGTH + "int not null, "
            + COLUMN_ACCURACY + " double not null "
            + ");";
}
