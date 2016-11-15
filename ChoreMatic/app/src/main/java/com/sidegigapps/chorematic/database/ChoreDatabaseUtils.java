package com.sidegigapps.chorematic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.sidegigapps.chorematic.Utils;
import com.sidegigapps.chorematic.activities.SetupActivity;
import com.sidegigapps.chorematic.models.Chore;

import java.util.HashMap;

/**
 * Created by ryand on 11/14/2016.
 */

public class ChoreDatabaseUtils {

    ChoreDBHelper helper;
    Context context;
    SQLiteDatabase db;

    public ChoreDatabaseUtils(Context context){
        this.context = context;
        helper = new ChoreDBHelper(context);
        db = helper.getWritableDatabase();
    }

    //assign all chores that don't have their due date assigned

    public void scheduleChoreByDate(int choreID, long date){
        Uri uri = ChoreContract.ChoresEntry.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        String selectionClause = ChoreContract.ChoresEntry.TABLE_NAME +"." +
                ChoreContract.ChoresEntry._ID + "=?";
        String[] selectionArgs = new String[]{
                String.valueOf(choreID)
        };
        contentValues.put(ChoreContract.ChoresEntry.COLUMN_NEXT_DUE, date);

        int rowsUpdated = context.getContentResolver().update(
                uri,
                contentValues,
                selectionClause,
                selectionArgs
        );

        Log.d("RCD","rows updated: " + String.valueOf(rowsUpdated));
    }

    public int getNumChoresLeftTodayForWidget(){
        Uri uri = ChoreContract.ChoresEntry.CONTENT_URI;
        String selectionClause = ChoreContract.ChoresEntry.TABLE_NAME +"." +
                ChoreContract.ChoresEntry.COLUMN_NEXT_DUE + "=?";
        String [] selectionArgs = new String[] {
                Utils.convertTodayToStringForDB()
        };
        String[] projection = new String[]{
                ChoreContract.ChoresEntry._ID
        };

        Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                selectionClause,
                selectionArgs,
                null
        );

        return cursor.getCount();

    }

    public void scheduleAllUnscheduledChores(){
        //assign all due dates to today for testing
        Uri uri = ChoreContract.ChoresEntry.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        String selectionClause = ChoreContract.ChoresEntry.TABLE_NAME +"." +
                ChoreContract.ChoresEntry.COLUMN_NEXT_DUE + "=?";
        String[] selectionArgs = new String[]{
                "-1"
        };

        long dayNumber = Utils.convertMillisecondsToDays(System.currentTimeMillis());
        contentValues.put(ChoreContract.ChoresEntry.COLUMN_NEXT_DUE, dayNumber);

        int rowsUpdated = context.getContentResolver().update(
                uri,
                contentValues,
                selectionClause,
                selectionArgs
        );

        Log.d("RCD","rows updated: " + String.valueOf(rowsUpdated));

    }

    public Cursor getChoreByID(int choreID){
        //fetch the frequency of choreID
        Uri fetchChoreUri = ChoreContract.ChoresEntry.CONTENT_URI;
        String [] projection = new String[]{
                ChoreContract.ChoresEntry.COLUMN_FREQUENCY
        };

        String selectionClause = ChoreContract.ChoresEntry.TABLE_NAME +
                "." + ChoreContract.ChoresEntry._ID + "=?";

        String[] SelectionArgs = {String.valueOf(choreID)};

        return context.getContentResolver().query(
                fetchChoreUri,
                projection,
                selectionClause,
                SelectionArgs,
                null
        );
    }

    public void markChoreAsDoneAndReschedule(int choreID){
        Cursor cursor = getChoreByID(choreID);
        int choreFrequency;
        long today = Utils.convertMillisecondsToDays(System.currentTimeMillis());

        if (cursor.moveToFirst()) {
           choreFrequency = cursor.getInt(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_FREQUENCY));
            scheduleChoreByDate(choreID,today+choreFrequency);
            markChoreDoneToday(choreID);
        }  else {
            Log.d("RCD","problem finding Chore ID");
        }
    }

    private void markChoreDoneToday(int choreID){
        Cursor cursor = getChoreByID(choreID);

        String today = String.valueOf(Utils.convertMillisecondsToDays(System.currentTimeMillis()));

        Uri uri = ChoreContract.ChoresEntry.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        String selectionClause = ChoreContract.ChoresEntry.TABLE_NAME +"." +
                ChoreContract.ChoresEntry._ID + "=?";
        String[] selectionArgs = new String[]{
                String.valueOf(choreID)
        };
        contentValues.put(ChoreContract.ChoresEntry.COLUMN_LAST_DONE, today);

        int rowsUpdated = context.getContentResolver().update(
                uri,
                contentValues,
                selectionClause,
                selectionArgs
        );

        Log.d("RCD","rows updated: " + String.valueOf(rowsUpdated));

    }

    public void initializeNewDay() {
        Uri uri = ChoreContract.ChoresEntry.CONTENT_URI;
        String todayIndexString = Utils.convertTodayToStringForDB();
        ContentValues contentValues = new ContentValues();
        String selectionClause = ChoreContract.ChoresEntry.TABLE_NAME +"." +
                ChoreContract.ChoresEntry.COLUMN_NEXT_DUE + "<?";
        String[] selectionArgs = new String[]{
                todayIndexString
        };

        contentValues.put(ChoreContract.ChoresEntry.COLUMN_NEXT_DUE, todayIndexString);

        int rowsUpdated = context.getContentResolver().update(
                uri,
                contentValues,
                selectionClause,
                selectionArgs
        );

        Log.d("RCD","rows updated: " + String.valueOf(rowsUpdated));
    }
}
