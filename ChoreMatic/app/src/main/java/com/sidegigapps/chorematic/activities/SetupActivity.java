package com.sidegigapps.chorematic.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.Utils;
import com.sidegigapps.chorematic.database.ChoreContract;
import com.sidegigapps.chorematic.database.ChoreDBHelper;
import com.sidegigapps.chorematic.fragments.BaseSetupFragment;
import com.sidegigapps.chorematic.fragments.FragmentHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class SetupActivity extends BaseActivity {

    private FrameLayout fragmentFrameLayout;
    FragmentController controller;

    ArrayList<HashMap<String,Integer>> roomsList = new ArrayList<>();
    ChoreDBHelper helper;

    private int numFloors, mainFloorIndex;
    private String[] floorNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_setup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        numFloors = 1;
        mainFloorIndex = 0;

        fragmentFrameLayout = (FrameLayout) findViewById(R.id.setupFrameLayout);

        controller = new FragmentController();

        setupChoreTemplateDBFromCSV();

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
        Log.d("RCD","Current Page: " + String.valueOf(controller.currentPage));
    }

    public void setMainFloorIndex(int num){
        mainFloorIndex = num;
        controller.setMainFloorIndex(num);
    }

    public void setNumFloors(int num){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt("numFloors",num).apply();

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

                boolean floorHasBedrooms = roomMap.containsKey("Bedroom");
                boolean floorHasBathrooms = roomMap.containsKey("Bathroom");

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
                    bundle.putString("description",fragmentString.split(STRING_DIVISOR)[1]);
                    bundle.putInt("index",Integer.parseInt(fragmentString.split(STRING_DIVISOR)[2]));
                    fragment.setArguments(bundle);
                } else if (fragmentType.equals(FragmentHelper.SETUP_NUM_BEDS_BATHS)){
                    Bundle bundle = new Bundle();
                    bundle.putString("description",fragmentString.split(STRING_DIVISOR)[1]);
                    bundle.putInt("index",Integer.parseInt(fragmentString.split(STRING_DIVISOR)[2]));
                    bundle.putBoolean("hasBedrooms",Boolean.valueOf(fragmentString.split(STRING_DIVISOR)[3]));
                    bundle.putBoolean("hasBathrooms",Boolean.valueOf(fragmentString.split(STRING_DIVISOR)[4]));
                    fragment.setArguments(bundle);
                }

            }
                return fragment;
        }


        public void nextPage() {
            if (currentPage<fragmentList.size()-1) {
                currentPage +=1;
                Log.d("RCD","Current Page: " + String.valueOf(currentPage));
                displayFragment();
            }
        }

        public void previousPage() {
            if (currentPage>0) {
                Log.d("RCD","Current Page: " + String.valueOf(currentPage));
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
                        Log.d("RCD",room + " on floor " + String.valueOf(i) + " added");
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
                    Log.d("RCD","setting up the " + description + " room, " + roomID);
                    addUserChoresToDatabaseFromTemplate(description, roomID);

                    cursor.moveToNext();
                }
            }
            cursor.close();

            controller.goToChoreList();
        }

        private void goToChoreList() {
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
                ContentValues cv = new ContentValues(5);
                cv.put(ChoreContract.ChoresEntry.COLUMN_DESCRIPTION, cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_DESCRIPTION)));
                cv.put(ChoreContract.ChoresEntry.COLUMN_FREQUENCY, cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_FREQUENCY)));
                cv.put(ChoreContract.ChoresEntry.COLUMN_EFFORT, cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_EFFORT)));
                cv.put(ChoreContract.ChoresEntry.COLUMN_ROOM, roomID);
                cv.put(ChoreContract.ChoresEntry.COLUMN_TYPE, ChoreContract.ChoresEntry.TYPE_USER);
                db.insert(ChoreContract.ChoresEntry.TABLE_NAME, null, cv);

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

        String floor = "bedroom";

        //Uri testUri = ChoreContract.ChoresEntry.buildFloorUri(floor);
        //Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationQuery, System.currentTimeMillis());

        //Cursor cursor = getContentResolver().query(testUri,)

        Cursor cursor = db.query(ChoreContract.ChoresEntry.TABLE_NAME, CHORE_PROJECTION,
                 null, null, null, null, null);

        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String room = cursor.getString(cursor.getColumnIndex("room"));
                String frequency = cursor.getString(cursor.getColumnIndex("frequency"));
                Log.d("RCD",description+ " in the " + room + " every " + frequency);
                cursor.moveToNext();
            }
        }
        cursor.close();

    }

    private void setupChoreTemplateDBFromCSV(){

        ChoreDBHelper helper = new ChoreDBHelper(this);

        SQLiteDatabase db = helper.getWritableDatabase();

        InputStream is;

        try {
            is = getAssets().open("chores.csv");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = "";
            db.beginTransaction();
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length != 4) {
                    Log.d("CSVParser", "Skipping Bad CSV Row");
                    continue;
                }
                ContentValues cv = new ContentValues(5);
                cv.put(ChoreContract.ChoresEntry.COLUMN_DESCRIPTION, columns[0].trim());
                cv.put(ChoreContract.ChoresEntry.COLUMN_FREQUENCY, columns[1].trim());
                cv.put(ChoreContract.ChoresEntry.COLUMN_EFFORT, columns[2].trim());
                cv.put(ChoreContract.ChoresEntry.COLUMN_ROOM, columns[3].trim());
                cv.put(ChoreContract.ChoresEntry.COLUMN_TYPE, ChoreContract.ChoresEntry.TYPE_TEMPLATE);
                db.insert(ChoreContract.ChoresEntry.TABLE_NAME, null, cv);
                Log.d("RCD","ADDED TEMPLATE: " +
                        columns[0].trim()+","+
                        columns[1].trim()+","+
                        columns[2].trim()+","+
                        columns[3].trim()+","+
                        ChoreContract.ChoresEntry.TYPE_TEMPLATE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }

}
