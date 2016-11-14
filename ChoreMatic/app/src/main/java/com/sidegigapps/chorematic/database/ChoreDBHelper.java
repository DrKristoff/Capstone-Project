package com.sidegigapps.chorematic.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sidegigapps.chorematic.database.ChoreContract.FloorsEntry;
import com.sidegigapps.chorematic.database.ChoreContract.RoomsEntry;
import com.sidegigapps.chorematic.database.ChoreContract.ChoresEntry;
import com.sidegigapps.chorematic.database.ChoreContract.EventsEntry;

/**
 * Created by ryand on 11/11/2016.
 */

public class ChoreDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String CHORES_DATABASE_NAME = "chores.db";

    public ChoreDBHelper(Context context) {
        super(context, CHORES_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold floors data
        final String SQL_CREATE_FLOORS_TABLE = "CREATE TABLE " + FloorsEntry.TABLE_NAME + " (" +
                FloorsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FloorsEntry.INDEX + " INTEGER NOT NULL, " +
                FloorsEntry.DESCRIPTION + " TEXT NOT NULL" +
                " );";

        // Create a table to hold rooms data
        final String SQL_CREATE_ROOMS_TABLE = "CREATE TABLE " + RoomsEntry.TABLE_NAME + " (" +
                RoomsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RoomsEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                RoomsEntry.COLUMN_FLOOR_INDEX + " INTEGER NOT NULL" +
                " );";

        // Create a table to hold user chores data
        final String SQL_CREATE_CHORES_TABLE = "CREATE TABLE " + ChoresEntry.TABLE_NAME + " (" +
                ChoresEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ChoresEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                ChoresEntry.COLUMN_FREQUENCY + " TEXT NOT NULL," +
                ChoresEntry.COLUMN_EFFORT + " TEXT NOT NULL," +
                ChoresEntry.COLUMN_ROOM + " TEXT NOT NULL,"+
                ChoresEntry.COLUMN_FLOOR_ID + " INTEGER,"+
                ChoresEntry.COLUMN_LAST_DONE + " INTEGER ,"+
                ChoresEntry.COLUMN_NEXT_DUE + " INTEGER,"+
                ChoresEntry.COLUMN_TYPE + " TEXT NOT NULL"+
                " );";

        // Create a table to hold event data
        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " + EventsEntry.TABLE_NAME + " (" +
                EventsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EventsEntry.COLUMN_TIMESTAMP + " INTEGER UNIQUE NOT NULL" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FLOORS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ROOMS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CHORES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 4 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FloorsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RoomsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ChoresEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
