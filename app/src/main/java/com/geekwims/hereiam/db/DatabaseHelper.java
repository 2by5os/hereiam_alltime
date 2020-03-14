package com.geekwims.hereiam.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

import static java.sql.DriverManager.println;

public class DatabaseHelper {
    private static final String TAG = DatabaseHelper.class.getName();

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "hereiam_db";
    private static final String BATTERY_LOG_TABLE_NAME = "battery_log";
    private static final String BATTERY_LOG_TABLE_CREATE =
            "CREATE TABLE " + BATTERY_LOG_TABLE_NAME + " (_id integer, begin_time integer, end_time integer, begin_level integer, end_level integer);";

    private SQLiteDatabase db;

    private DatabaseHelper() {
    }

    public static DatabaseHelper getInstance(Context context) {
        DatabaseHelper instance = new DatabaseHelper();
        instance.createDatabase(context);

        if (!instance.isExistDatabase()) {
            Log.d(TAG, "DB is not exist");
            
            return null;
        }
        
        instance.createTable();

        return instance;
    }

    public boolean isExistDatabase() {
        return db != null;
    }

    private void createDatabase(Context context) {
        Log.d(TAG, "creating database [ " + context.getFilesDir().getPath() + "/" + DATABASE_NAME + "]");

        try {
            db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().getPath() + "/" + DATABASE_NAME, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        if (!isExistTable(BATTERY_LOG_TABLE_NAME)) {
            db.execSQL(BATTERY_LOG_TABLE_CREATE);
        }
    }

    public void insert(long begin_time, long end_time, int begin_level, int end_level) {
        Log.d(TAG, "inserting records into table " + BATTERY_LOG_TABLE_NAME);

        db.execSQL("insert into " + BATTERY_LOG_TABLE_NAME + " (begin_time, end_time, begin_level, end_level) values" + " ( " + begin_time + ", " + end_time + ", " + begin_level + " , " + end_level + ");");
    }

    public Cursor getBatteryLogCursor() {
        Log.d(TAG, "getBatteryLogCursor...");

        return db.rawQuery("select begin_time, end_time, begin_level, end_level from " + BATTERY_LOG_TABLE_NAME, null);
    }

    public boolean isExistTable(String name) {
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + name + "';";

        Cursor c = db.rawQuery(query, null);

        boolean result = (c != null) && (c.getCount() > 0);

        if (c != null) c.close();

        return result;
    }
}