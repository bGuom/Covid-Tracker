package com.mobicom.covidtracker.Const;

public class DBConstants {


    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "CovidTracker.db";

    // User table name
    public static final String TABLE_ALERTS = "alert";
    public static final String TABLE_CONTACT_HISTORY = "contact_history";
    public static final String TABLE_CONTACT_STATUS = "contact_status";

    // ALERT Table Columns names
    public static final String COLUMN_ALERT_ID = "alert_id";
    public static final String COLUMN_ALERT_DATE = "alert_date";


    //Contact History Table Columns names
    public static final String COLUMN_CONTACT_ID = "contact_id";
    public static final String COLUMN_CONTACT_DATE = "contact_date";
    public static final String COLUMN_CONTACT_TIME = "contact_time";
    public static final String COLUMN_CONTACT_LEVEL = "contact_level"; // Close contact 1 , range : 0
    public static final String COLUMN_CONTACT_DIAGNOSIS_KEY = "contact_key";

    //Contact Status Table Columns names
    public static final String COLUMN_STATUS_ID = "status_id";
    public static final String COLUMN_STATUS_RECEIVED_DATE = "status_date";
    public static final String COLUMN_STATUS_KEYS = "status_keys";

    // create table sql query
    public static final String CREATE_ALERT_TABLE = "CREATE TABLE " + TABLE_ALERTS + "("
            + COLUMN_ALERT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ALERT_DATE + " DATE"
            + ")";

    public static final String CREATE_CONTACT_TABLE = "CREATE TABLE " + TABLE_CONTACT_HISTORY + "("
            + COLUMN_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CONTACT_DATE + " DATE,"
            + COLUMN_CONTACT_TIME + " TEXT,"
            + COLUMN_CONTACT_LEVEL + " INTEGER,"
            + COLUMN_CONTACT_DIAGNOSIS_KEY + " TEXT"
            + ")";

    public static final String CREATE_STATUS_TABLE = "CREATE TABLE " + TABLE_CONTACT_STATUS + "("
            + COLUMN_STATUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_STATUS_RECEIVED_DATE + " DATE,"
            + COLUMN_STATUS_KEYS + " TEXT"
            + ")";

    // drop table sql query
    public static final String DROP_ALERT_TABLE = "DROP TABLE IF EXISTS " + TABLE_ALERTS;
    public static final String DROP_CONTACT_TABLE = "DROP TABLE IF EXISTS " + TABLE_CONTACT_HISTORY;
    public static final String DROP_STATUS_TABLE = "DROP TABLE IF EXISTS " + TABLE_CONTACT_STATUS;

}
