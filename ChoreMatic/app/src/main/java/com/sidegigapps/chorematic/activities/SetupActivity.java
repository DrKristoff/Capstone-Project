package com.sidegigapps.chorematic.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.Utils;
import com.sidegigapps.chorematic.database.ChoreContract;
import com.sidegigapps.chorematic.database.ChoreDBHelper;
import com.sidegigapps.chorematic.fragments.BaseSetupFragment;
import com.sidegigapps.chorematic.fragments.FragmentHelper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import jonathanfinerty.once.Once;

public class SetupActivity extends BaseActivity {

    FrameLayout fragmentFrameLayout;
    FragmentController controller;

    ArrayList<HashMap<String,Integer>> roomsList = new ArrayList<>();
    ChoreDBHelper helper;

    private int numFloors, mainFloorIndex;
    String[] floorNames;

    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(getString(R.string.firebaseBucket));

        setContentView(R.layout.activity_setup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        numFloors = 1;
        mainFloorIndex = 0;

        fragmentFrameLayout = (FrameLayout) findViewById(R.id.setupFrameLayout);

        controller = new FragmentController();

        DownloadCSVFromFireBaseTask task = new DownloadCSVFromFireBaseTask();
        task.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup, menu);
        return true;
    }

    public void nextPage(){
        controller.nextPage();
    }

    public void previousPage(){
        controller.previousPage();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        controller.currentPage-=1;
    }

    public void setMainFloorIndex(int num){
        mainFloorIndex = num;
        controller.setMainFloorIndex(num);
    }

    public void setNumFloors(int num){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt(getString(R.string.numFloors),num).apply();

        numFloors = num;
        floorNames = new String[num];
        controller.setNumFloors(num);
    }

    public int getNumFloors(){
        return numFloors;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addRooms(int floorIndex, ArrayList<String> roomsSelected) {

        //clear rooms with greater floor index, in case back button is pressed while selecting rooms
        roomsList.subList(floorIndex, roomsList.size()).clear();
        HashMap <String,Integer> newFloorMap = new HashMap<>();

        for(String room : roomsSelected){
            newFloorMap.put(room,1);
        }

        roomsList.add(floorIndex, newFloorMap);

        if(floorIndex==numFloors-1){
            controller.storeNumBedsAndBathsFragmentsData();
        }
    }

    public void updateNumBedsAndBaths(int floorIndex, int numBaths, int numBeds){
        HashMap<String, Integer> floorMap = roomsList.get(floorIndex);
        String bathroom = getResources().getString(R.string.bathroom_string);
        String bedroom = getResources().getString(R.string.bedroom_string);
        if(numBaths==0){
            floorMap.remove(bathroom);
        } else {
            floorMap.put(bathroom,numBaths);
        }
        if(numBeds==0){
            floorMap.remove(bedroom);
        } else {
            floorMap.put(bedroom,numBeds);
        }


        if(floorIndex==numFloors-1){
            controller.setupDatabase();
        }
    }

    public class FragmentController{

        ArrayList<String> fragmentList = new ArrayList<>();
        private static final String STRING_DIVISOR = "::";

        private int currentPage = 0;

        public FragmentController(){
            init();
        }

        private void init() {

            fragmentList.add(FragmentHelper.SETUP_INTRO_FRAGMENT);
            fragmentList.add(FragmentHelper.NUM_FLOOR_SETUP_FRAGMENT);

            displayFragment();
        }

        private void displayFragment(){
            FragmentTransaction transaction =  getSupportFragmentManager().beginTransaction();
             transaction.replace(R.id.setupFrameLayout,getItem(currentPage));
            transaction.addToBackStack(null);
            transaction.commit();
        }

        public void setNumFloors(int numFloors){
            fragmentList.subList(2, fragmentList.size()).clear();
            if(numFloors==1){
                mainFloorIndex = 0;
                storeDetailFragmentsData();
            } else {
                fragmentList.add(FragmentHelper.IDENTIFY_MAIN_FLOOR_FRAGMENT);
            }
        }

        private void storeDetailFragmentsData(){
            String [] floorNames = Utils.createFloorNames(numFloors,mainFloorIndex, SetupActivity.this);
            for(int i =0;i < numFloors;i++){
                String floorDescription = floorNames[i];
                fragmentList.add(FragmentHelper.FLOOR_DETAILS_SETUP_FRAGMENT
                        + STRING_DIVISOR + floorDescription
                        + STRING_DIVISOR + String.valueOf(i));
            }
        }

        private void storeNumBedsAndBathsFragmentsData(){

            String [] floorNames = Utils.createFloorNames(numFloors,mainFloorIndex, SetupActivity.this);
            for(int i =0;i < numFloors;i++){
                String floorDescription = floorNames[i];

                HashMap<String,Integer> roomMap = roomsList.get(i);

                boolean floorHasBedrooms = roomMap.containsKey(getString(R.string.Bedroom));
                boolean floorHasBathrooms = roomMap.containsKey(getString(R.string.Bathroom));

                fragmentList.add(FragmentHelper.SETUP_NUM_BEDS_BATHS
                        + STRING_DIVISOR + floorDescription
                        + STRING_DIVISOR + String.valueOf(i)
                        + STRING_DIVISOR + String.valueOf(floorHasBedrooms)
                        + STRING_DIVISOR + String.valueOf(floorHasBathrooms));
            }

        }

        public BaseSetupFragment getItem(int position) {
            if(position>fragmentList.size()){
                return null;
            }

            String fragmentString = fragmentList.get(position);

            String fragmentType = fragmentString.split(STRING_DIVISOR)[0];
            BaseSetupFragment fragment = FragmentHelper.getSetupFragmentByString(fragmentType);

            if(fragmentString.contains(STRING_DIVISOR)){

                if(fragmentType.equals(FragmentHelper.FLOOR_DETAILS_SETUP_FRAGMENT)){
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.description),fragmentString.split(STRING_DIVISOR)[1]);
                    bundle.putInt(getString(R.string.floor_index),Integer.parseInt(fragmentString.split(STRING_DIVISOR)[2]));
                    fragment.setArguments(bundle);
                } else if (fragmentType.equals(FragmentHelper.SETUP_NUM_BEDS_BATHS)){
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.description),fragmentString.split(STRING_DIVISOR)[1]);
                    bundle.putInt(getString(R.string.index),Integer.parseInt(fragmentString.split(STRING_DIVISOR)[2]));
                    bundle.putBoolean(getString(R.string.hasBedrooms),Boolean.valueOf(fragmentString.split(STRING_DIVISOR)[3]));
                    bundle.putBoolean(getString(R.string.hasBathrooms),Boolean.valueOf(fragmentString.split(STRING_DIVISOR)[4]));
                    fragment.setArguments(bundle);
                }

            }
                return fragment;
        }


        public void nextPage() {
            if (currentPage<fragmentList.size()-1) {
                currentPage +=1;
                displayFragment();
            }
        }

        public void previousPage() {
            if (currentPage>0) {
                onBackPressed();
            }
        }

        public void setMainFloorIndex(int num) {
            fragmentList.subList(3, fragmentList.size()).clear();
            storeDetailFragmentsData();
        }

        public void setupDatabase() {

            helper = new ChoreDBHelper(getApplicationContext());
            SQLiteDatabase db = helper.getWritableDatabase();

            //Vector<ContentValues> floorsVector = new Vector<ContentValues>(numFloors);

            String [] floorNames = Utils.createFloorNames(numFloors,mainFloorIndex, SetupActivity.this);

            for(int i = 0; i < roomsList.size();i++){
                HashMap<String, Integer> floorRoomsMap = roomsList.get(i);
                //Vector<ContentValues> roomsVector = new Vector<ContentValues>(floorRoomsMap.size());
                ContentValues floorValues = new ContentValues();

                //Add floor
                floorValues.put(ChoreContract.FloorsEntry.INDEX,i);
                floorValues.put(ChoreContract.FloorsEntry.DESCRIPTION,floorNames[i]);
                db.insert(ChoreContract.FloorsEntry.TABLE_NAME, null, floorValues);
                //floorsVector.add(floorValues);

                //Add rooms
                for(String room : floorRoomsMap.keySet()){
                    int num = floorRoomsMap.get(room);
                    while(num!=0){
                        ContentValues roomValues = new ContentValues();
                        roomValues.put(ChoreContract.RoomsEntry.COLUMN_DESCRIPTION,room);
                        roomValues.put(ChoreContract.RoomsEntry.COLUMN_FLOOR_INDEX,i);
                        db.insert(ChoreContract.RoomsEntry.TABLE_NAME, null, roomValues);
                        //roomsVector.add(roomValues);
                        num-=1;
                        floorRoomsMap.put(room,num);

                    }
                }

            }

            //get Cursor of all rooms in database
            String[] ROOM_PROJECTION = new String[] {
                    "_id",
                    ChoreContract.RoomsEntry.COLUMN_DESCRIPTION
            };
            Cursor cursor = db.query(ChoreContract.RoomsEntry.TABLE_NAME, ROOM_PROJECTION,
                    null, null, null, null, null);

            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    String roomID = cursor.getString(cursor.getColumnIndex(ChoreContract.RoomsEntry._ID));
                    String description = cursor.getString(cursor.getColumnIndex(ChoreContract.RoomsEntry.COLUMN_DESCRIPTION));
                    addUserChoresToDatabaseFromTemplate(description, roomID);

                    cursor.moveToNext();
                }
            }
            cursor.close();

            controller.goToChoreList();
        }

        private void goToChoreList() {
            Once.markDone(setupCompleted);
            navigateTo(BaseActivity.TodayActivity);
            startActivity(new Intent(SetupActivity.this, ChoreListActivity.class));

        }

    }

    private void addUserChoresToDatabaseFromTemplate(String description, String roomID){
        //get Chores from template
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] CHORES_PROJECTION = new String[] {
                "_id",
                ChoreContract.ChoresEntry.COLUMN_DESCRIPTION,
                ChoreContract.ChoresEntry.COLUMN_FREQUENCY,
                ChoreContract.ChoresEntry.COLUMN_EFFORT
        };

        String selectionString = ChoreContract.ChoresEntry.TABLE_NAME +
                "." + ChoreContract.ChoresEntry.COLUMN_ROOM + " = ? AND " +
                ChoreContract.ChoresEntry.TABLE_NAME + "." + ChoreContract.ChoresEntry.COLUMN_TYPE +
                " = ? COLLATE NOCASE";
        Cursor cursor = db.query(ChoreContract.ChoresEntry.TABLE_NAME,
                CHORES_PROJECTION,
                selectionString,
                new String[]{description.toLowerCase(), ChoreContract.ChoresEntry.TYPE_TEMPLATE},
                null,
                null,
                null);

        db.beginTransaction();
        try {
            if (cursor.moveToFirst()) {
                ContentValues cv = new ContentValues(7);
                cv.put(ChoreContract.ChoresEntry.COLUMN_DESCRIPTION, cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_DESCRIPTION)));
                cv.put(ChoreContract.ChoresEntry.COLUMN_FREQUENCY, cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_FREQUENCY)));
                cv.put(ChoreContract.ChoresEntry.COLUMN_EFFORT, cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_EFFORT)));
                cv.put(ChoreContract.ChoresEntry.COLUMN_ROOM, roomID);
                cv.put(ChoreContract.ChoresEntry.COLUMN_LAST_DONE, -1);
                cv.put(ChoreContract.ChoresEntry.COLUMN_NEXT_DUE, -1);
                cv.put(ChoreContract.ChoresEntry.COLUMN_TYPE, ChoreContract.ChoresEntry.TYPE_USER);
                db.insert(ChoreContract.ChoresEntry.TABLE_NAME, null, cv);

                Log.d(getString(R.string.rcd_debug_tag),"ADDED REAL CHORE: " +
                        cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_DESCRIPTION))+","+
                        cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_FREQUENCY))+","+
                        cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_EFFORT))+","+
                        ChoreContract.ChoresEntry.TYPE_USER);


            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();



    }

    private void runTestQuery() {
        ChoreDBHelper helper = new ChoreDBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] CHORE_PROJECTION = new String[] {
                "_id",
                ChoreContract.ChoresEntry.COLUMN_DESCRIPTION,
                ChoreContract.ChoresEntry.COLUMN_FREQUENCY,
                ChoreContract.ChoresEntry.COLUMN_EFFORT,
                ChoreContract.ChoresEntry.COLUMN_ROOM,
                ChoreContract.ChoresEntry.COLUMN_LAST_DONE,
                ChoreContract.ChoresEntry.COLUMN_NEXT_DUE
        };

        Cursor cursor = db.query(ChoreContract.ChoresEntry.TABLE_NAME, CHORE_PROJECTION,
                 null, null, null, null, null);

        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String description = cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_DESCRIPTION));
                String room = cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_ROOM));
                String frequency = cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_FREQUENCY));
                cursor.moveToNext();
            }
        }
        cursor.close();

    }

    public class DownloadCSVFromFireBaseTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            StorageReference csvRef = storageRef.child("chores.csv");

            final long ONE_MEGABYTE = 1024 * 1024;
            csvRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    ChoreDBHelper helper = new ChoreDBHelper(SetupActivity.this);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    InputStream is;

                    try {

                        BufferedReader buffer = null;
                        is = new ByteArrayInputStream(bytes);
                        buffer = new BufferedReader(new InputStreamReader(is));
                        String temp = null;
                        String line = "";
                        db.beginTransaction();
                        while ((line = buffer.readLine()) != null) {
                            String[] columns = line.split(",");
                            if (columns.length != 4) {
                                continue;
                            }
                            ContentValues cv = new ContentValues(5);
                            cv.put(ChoreContract.ChoresEntry.COLUMN_DESCRIPTION, columns[0].trim());
                            cv.put(ChoreContract.ChoresEntry.COLUMN_FREQUENCY, columns[1].trim());
                            cv.put(ChoreContract.ChoresEntry.COLUMN_EFFORT, columns[2].trim());
                            cv.put(ChoreContract.ChoresEntry.COLUMN_ROOM, columns[3].trim());
                            cv.put(ChoreContract.ChoresEntry.COLUMN_TYPE, ChoreContract.ChoresEntry.TYPE_TEMPLATE);
                            db.insert(ChoreContract.ChoresEntry.TABLE_NAME, null, cv);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
            return null;
        }
    }

}
