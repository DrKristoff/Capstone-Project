package com.sidegigapps.chorematic.database;

import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ryand on 11/11/2016.
 */

public class DatabaseSetupAsyncTask extends AsyncTask{

    private DatabaseReference mDatabase;
    ArrayList<HashMap<String, Integer>> roomsList;

    public DatabaseSetupAsyncTask(ArrayList<HashMap<String, Integer>> roomsList){
        super();
        this.roomsList = roomsList;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
}
