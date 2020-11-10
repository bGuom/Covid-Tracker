package com.mobicom.covidtracker.DB;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mobicom.covidtracker.Const.DBConstants;
import com.mobicom.covidtracker.Models.ContactData;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {


    /**
     * Constructor
     *
     * @param context
     */
    public DBHelper(Context context) {
        super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DBConstants.CREATE_ALERT_TABLE);
        db.execSQL(DBConstants.CREATE_CONTACT_TABLE);
        db.execSQL(DBConstants.CREATE_STATUS_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DBConstants.DROP_ALERT_TABLE);
        db.execSQL(DBConstants.DROP_CONTACT_TABLE);
        db.execSQL(DBConstants.DROP_STATUS_TABLE);
        // Create tables again
        onCreate(db);

    }

    public void recordContact(ContactData contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBConstants.COLUMN_CONTACT_DATE, contact.getDate());
        values.put(DBConstants.COLUMN_CONTACT_TIME, contact.getTime());
        values.put(DBConstants.COLUMN_CONTACT_LEVEL, contact.getLevel());
        values.put(DBConstants.COLUMN_CONTACT_DIAGNOSIS_KEY, contact.getKey());


        // Inserting Row
        db.insert(DBConstants.TABLE_CONTACT_HISTORY, null, values);
        db.close();
    }

    public List<ContactData> getAllContactData() {
        // array of columns to fetch
        String[] columns = {
                DBConstants.COLUMN_CONTACT_ID,
                DBConstants.COLUMN_CONTACT_DATE,
                DBConstants.COLUMN_CONTACT_TIME,
                DBConstants.COLUMN_CONTACT_LEVEL,
                DBConstants.COLUMN_CONTACT_DIAGNOSIS_KEY,
        };
        // sorting orders
        String sortOrder =
                DBConstants.COLUMN_CONTACT_DATE + " DESC";
        List<ContactData> contactList = new ArrayList<ContactData>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table

        Cursor cursor = db.query(DBConstants.TABLE_CONTACT_HISTORY, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContactData contactData = new ContactData();
                contactData.setDate(cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_CONTACT_DATE)));
                contactData.setTime(cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_CONTACT_TIME)));
                contactData.setLevel(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_CONTACT_LEVEL))));
                contactData.setKey(cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_CONTACT_DIAGNOSIS_KEY)));

                // Adding user record to list
                contactList.add(contactData);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return contactList;
    }

    //delete record from database
    public void deleteOldRecords(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + DBConstants.TABLE_CONTACT_HISTORY + " WHERE " + DBConstants.COLUMN_CONTACT_DATE + " < date('now','-21 day');");
        db.close();
    }

    public boolean checkKey(String key,String date) {

        // array of columns to fetch
        String[] columns = {
                DBConstants.COLUMN_CONTACT_DATE,
                DBConstants.COLUMN_CONTACT_DIAGNOSIS_KEY
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = DBConstants.COLUMN_CONTACT_DATE + " = ?" + " AND " + DBConstants.COLUMN_CONTACT_DIAGNOSIS_KEY + " = ?";

        // selection arguments
        String[] selectionArgs = {date, key};

        // query user table with conditions
        Cursor cursor = db.query(DBConstants.TABLE_CONTACT_HISTORY, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        return cursorCount > 0;
    }





    /**

    public User getUser(String email) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_EMAIL,
                COLUMN_USER_NAME,
                COLUMN_USER_AGE,
                COLUMN_USER_GENDER,
                COLUMN_USER_HEIGHT,
                COLUMN_USER_WEIGHT,
                COLUMN_USER_PASSWORD
        };

        User user = new User();

        SQLiteDatabase db = this.getReadableDatabase();
        String sortOrder =  COLUMN_USER_NAME + " ASC";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_EMAIL + " = '" + email + "'",null);

        if (cursor.moveToFirst()) {
            do {
                user.setUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                user.setAge(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_AGE))));
                user.setGender(cursor.getString(cursor.getColumnIndex(COLUMN_USER_GENDER)));
                user.setHeightCm(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_HEIGHT))));
                user.setWeightKg(Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_USER_WEIGHT))));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return user;
    }




    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_AGE, user.getAge());
        values.put(COLUMN_USER_GENDER, user.getGender());
        values.put(COLUMN_USER_HEIGHT, user.getHeightCm());
        values.put(COLUMN_USER_WEIGHT, user.getWeightKg());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        // updating row
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getUserId())});
        db.close();
    }


    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getUserId())});
        db.close();
    }


    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;
    }


    public boolean checkUser(String email, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        // query user table with conditions
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        return cursorCount > 0;
    }


    public void addHistory(History history) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, history.getUserID());
        values.put(COLUMN_DATE, history.getDate());
        values.put(COLUMN_PREDICTION, history.getPrediction());

        // Inserting Row
        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }



    public List<History> getAllHistory(int userId) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_HISTORY_ID,
                COLUMN_USER_ID,
                COLUMN_DATE,
                COLUMN_PREDICTION
        };
        // sorting orders
        String sortOrder =
                COLUMN_DATE + " DESC";
        List<History> historyList = new ArrayList<History>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HISTORY + " WHERE " + COLUMN_USER_ID + " = '" + userId + "' ORDER BY " + sortOrder,null);

        // query the user table

        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                History history = new History();
                history.setUserID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                history.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                history.setPrediction(cursor.getString(cursor.getColumnIndex(COLUMN_PREDICTION)));

                // Adding user record to list
                historyList.add(history);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return historyList;
    }

    public List<PredictionCount> getMostProne(int userId) {
        // sorting orders
        List<PredictionCount> historyList = new ArrayList<PredictionCount>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PREDICTION + ", " + " COUNT(" +  COLUMN_PREDICTION  +  ")  FROM " + TABLE_HISTORY + " WHERE " + COLUMN_USER_ID + " = '" + userId + "' GROUP BY " + COLUMN_PREDICTION + " ORDER BY COUNT(" + COLUMN_PREDICTION  +") DESC ",null);

        // query the user table

        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PredictionCount predictionCount = new PredictionCount();
                predictionCount.setPrediction(cursor.getString(cursor.getColumnIndex(COLUMN_PREDICTION)));
                predictionCount.setCount(cursor.getInt(1));

                // Adding user record to list
                historyList.add(predictionCount);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return historyList;
    }





    public void addReport(Report report) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, report.getUserId());
        values.put(COLUMN_FILE_NAME, report.getFileName());
        values.put(COLUMN_TYPE, report.getFileType());
        values.put(COLUMN_PATH, report.getFilePath());
        // Inserting Row
        db.insert(TABLE_REPORTS, null, values);
        db.close();
    }



    public List<Report> getAllReports(int userId) {

        List<Report> reports = new ArrayList<Report>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REPORTS + " WHERE " + COLUMN_USER_ID + " = '" + userId +"';",null);

        // query the user table

        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Report report = new Report();
                report.setReportId(Integer.parseInt (cursor.getString(cursor.getColumnIndex(COLUMN_REPOT_ID))));
                report.setUserId(Integer.parseInt (cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                report.setFileName(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_NAME)));
                report.setFileType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
                report.setFilePath(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)));
                // Adding user record to list
                reports.add(report);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return reports;
    }

    **/
}

