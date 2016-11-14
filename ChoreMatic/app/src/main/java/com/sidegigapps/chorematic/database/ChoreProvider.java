package com.sidegigapps.chorematic.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ryand on 11/11/2016.
 */

public class ChoreProvider extends ContentProvider {
    ChoreDBHelper mHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int CHORES = 100;
    static final int CHORE_TEMPLATE_BY_ROOM = 101;
    static final int CHORES_BY_FREQUENCY = 102;
    static final int CHORES_BY_DUE_DATE = 103;
    static final int CHORES_BY_ID = 104;
    static final int ROOMS = 200;
    static final int FLOORS = 300;
    static final int EVENTS = 400;


    @Override
    public boolean onCreate() {
        mHelper = new ChoreDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CHORES:
            {
                retCursor = getAllUserChores(uri, projection);
                break;
            }
            case CHORE_TEMPLATE_BY_ROOM:
            {
                retCursor = getTemplateChoresByRoomType(uri, projection, selection);
                break;
            }
            case CHORES_BY_FREQUENCY:
            {
                retCursor = getUserChoresByFrequency(uri, projection, selection);
                break;
            }
            case CHORES_BY_DUE_DATE:
            {
                retCursor = getUserChoresByDueDate(uri, projection, selection);
                break;
            }
            case CHORES_BY_ID:
            {
                retCursor = getUserChoresByID(uri, projection, selection);
                break;
            }
            case ROOMS:
            {
                retCursor = getRoomByID(uri, projection, selection);
                break;
            }
            case FLOORS:
            {
                retCursor = getFloorByID(uri, projection, selection);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        //return null;
        return retCursor;
    }

    private Cursor getRoomByID(Uri uri, String[] projection, String id){
        String selectionString = ChoreContract.RoomsEntry.TABLE_NAME +
                "." + ChoreContract.RoomsEntry._ID + "= ?";

        Log.d("RCD",selectionString);

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ChoreContract.RoomsEntry.TABLE_NAME);

        return builder.query(mHelper.getReadableDatabase(),
                projection,
                selectionString,
                new String[]{id},
                null,
                null,
                null
        );
    }

    private Cursor getFloorByID(Uri uri, String[] projection, String id){
        String selectionString = ChoreContract.FloorsEntry.TABLE_NAME +
                "." + ChoreContract.FloorsEntry._ID + "= ?";

        Log.d("RCD",selectionString);

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ChoreContract.FloorsEntry.TABLE_NAME);

        return builder.query(mHelper.getReadableDatabase(),
                projection,
                selectionString,
                new String[]{id},
                null,
                null,
                null
        );
    }

    private Cursor getTemplateChoresByRoomType(Uri uri, String[] projection, String room){
        String selectionString = ChoreContract.ChoresEntry.TABLE_NAME +
                "." + ChoreContract.ChoresEntry.COLUMN_DESCRIPTION + "= ? AND " +
                ChoreContract.ChoresEntry.TABLE_NAME +
                "." + ChoreContract.ChoresEntry.COLUMN_TYPE + "= ? ";

        Log.d("RCD",selectionString);

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ChoreContract.ChoresEntry.TABLE_NAME);

        return builder.query(mHelper.getReadableDatabase(),
                projection,
                selectionString,
                new String[]{room,ChoreContract.ChoresEntry.TYPE_TEMPLATE},
                null,
                null,
                null
        );
    }

    private Cursor getUserChoresByFrequency(Uri uri, String[] projection, String frequency){
        String selectionString = ChoreContract.ChoresEntry.TABLE_NAME +
                "." + ChoreContract.ChoresEntry.COLUMN_FREQUENCY + "=" + frequency;
        Log.d("RCD",selectionString);

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ChoreContract.ChoresEntry.TABLE_NAME);

        return builder.query(mHelper.getReadableDatabase(),
                projection,
                selectionString,
                null,
                null,
                null,
                null
        );
    }

    private Cursor getAllUserChores(Uri uri, String[] projection){
        String selectionString = ChoreContract.ChoresEntry.TABLE_NAME +
                "." + ChoreContract.ChoresEntry.COLUMN_TYPE + "= ? ";

        Log.d("RCD",selectionString);

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ChoreContract.ChoresEntry.TABLE_NAME);

        return builder.query(mHelper.getReadableDatabase(),
                projection,
                selectionString,
                new String[]{ChoreContract.ChoresEntry.TYPE_USER},
                null,
                null,
                null
        );
    }

    private Cursor getUserChoresByDueDate(Uri uri, String[] projection, String frequency){
        String selectionString = ChoreContract.ChoresEntry.TABLE_NAME +
                "." + ChoreContract.ChoresEntry.COLUMN_NEXT_DUE + "=?";
        Log.d("RCD",selectionString);

        long date = ChoreContract.ChoresEntry.getDateFromUri(uri);

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ChoreContract.ChoresEntry.TABLE_NAME);

        return builder.query(mHelper.getReadableDatabase(),
                projection,
                selectionString,
                new String[]{Long.toString(date)},
                null,
                null,
                null
        );
    }

    private Cursor getUserChoresByID(Uri uri, String[] projection, String frequency){
        String _id = uri.getPathSegments().get(2);
        String selectionString = ChoreContract.ChoresEntry.TABLE_NAME +
                "." + ChoreContract.ChoresEntry._ID + "=?";
        Log.d("RCD",selectionString);

        long date = ChoreContract.ChoresEntry.getDateFromUri(uri);

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ChoreContract.ChoresEntry.TABLE_NAME);

        return builder.query(mHelper.getReadableDatabase(),
                projection,
                selectionString,
                new String[]{_id},
                null,
                null,
                null
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ChoreContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ChoreContract.PATH_CHORES, CHORES);
        matcher.addURI(authority, ChoreContract.PATH_FLOORS + "/#", FLOORS);
        matcher.addURI(authority, ChoreContract.PATH_ROOMS + "/#", ROOMS);
        matcher.addURI(authority, ChoreContract.PATH_CHORES + "/rooms/*", CHORE_TEMPLATE_BY_ROOM);
        matcher.addURI(authority, ChoreContract.PATH_CHORES + "/frequency/*", CHORES_BY_FREQUENCY);
        matcher.addURI(authority, ChoreContract.PATH_CHORES + "/id/*", CHORES_BY_ID);
        matcher.addURI(authority, ChoreContract.PATH_CHORES + "/date/*", CHORES_BY_DUE_DATE);

        return matcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case CHORES:
                rowsDeleted = db.delete(
                        ChoreContract.ChoresEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FLOORS:
                rowsDeleted = db.delete(
                        ChoreContract.FloorsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ROOMS:
                rowsDeleted = db.delete(
                        ChoreContract.RoomsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EVENTS:
                rowsDeleted = db.delete(
                        ChoreContract.EventsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CHORES:
                rowsUpdated = db.update(ChoreContract.ChoresEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FLOORS:
                rowsUpdated = db.update(ChoreContract.FloorsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case ROOMS:
                rowsUpdated = db.update(ChoreContract.RoomsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case EVENTS:
                normalizeDate(values);
                rowsUpdated = db.update(ChoreContract.EventsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(ChoreContract.EventsEntry.COLUMN_TIMESTAMP)) {
            long dateValue = values.getAsLong(ChoreContract.EventsEntry.COLUMN_TIMESTAMP);
            values.put(ChoreContract.EventsEntry.COLUMN_TIMESTAMP, ChoreContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;
        switch (match) {
            case CHORES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(ChoreContract.ChoresEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case FLOORS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(ChoreContract.FloorsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case ROOMS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(ChoreContract.RoomsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case EVENTS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(ChoreContract.ChoresEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CHORES:
            case CHORE_TEMPLATE_BY_ROOM:
            case CHORES_BY_FREQUENCY:
            case CHORES_BY_DUE_DATE:
                return ChoreContract.ChoresEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case CHORES: {
                normalizeDate(contentValues);
                long _id = db.insert(ChoreContract.ChoresEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = ChoreContract.ChoresEntry.buildChoresUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FLOORS: {
                long _id = db.insert(ChoreContract.FloorsEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = ChoreContract.FloorsEntry.buildFloorsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ROOMS: {
                normalizeDate(contentValues);
                long _id = db.insert(ChoreContract.RoomsEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = ChoreContract.RoomsEntry.buildRoomsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case EVENTS: {
                normalizeDate(contentValues);
                long _id = db.insert(ChoreContract.EventsEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = ChoreContract.EventsEntry.buildEventsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

/*    private Cursor getChoresByFrequency(){

    }*/
}
