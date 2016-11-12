package com.sidegigapps.chorematic.activities;

import android.content.ContentValues;
import android.content.Context;
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
import java.util.Map;
import java.util.Vector;

public class SetupActivity extends BaseActivity {

    private FrameLayout fragmentFrameLayout;
    FragmentController controller;

    ArrayList<HashMap<String,Integer>> roomsList = new ArrayList<>();

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

    public void updateNumBedsAndBaths(int floorIndex, String room, int num){
        HashMap<String, Integer> floorMap = roomsList.get(floorIndex);
        floorMap.put(room,num);

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
            setupChoreTemplateDBFromCSV();

            runTestQuery();


            /*if (currentPage<fragmentList.size()-1) {
                currentPage +=1;
                Log.d("RCD","Current Page: " + String.valueOf(currentPage));
                displayFragment();
            }*/
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

            setupChoreTemplateDBFromCSV();

            ChoreDBHelper helper = new ChoreDBHelper(getApplicationContext());

            //ArrayList<HashMap<String,Integer>> roomsList

            Vector<ContentValues> roomsVector = new Vector<ContentValues>(roomsList.size());
            Vector<ContentValues> floorsVector = new Vector<ContentValues>(numFloors);

            //Vector<ContentValues> choresVector = new Vector<ContentValues>(roomsList.size());

            ContentValues roomValues = new ContentValues();
            ContentValues floorValues = new ContentValues();

            for(int i = 0; i < roomsList.size();i++){{
                HashMap<String, Integer> floorRoomsMap = roomsList.get(i);

                floorValues.put(ChoreContract.FloorsEntry.INDEX,i);
                floorValues.put(ChoreContract.FloorsEntry.DESCRIPTION,floorNames[i]);

                for(String room : floorRoomsMap.keySet()){
                    roomValues.put(ChoreContract.RoomsEntry.DESCRIPTION,room);
                    roomValues.put(ChoreContract.RoomsEntry.FLOOR_INDEX,i);
                    //roomValues.put(ChoreContract.RoomsEntry.TEMPLATE,template);

                }

                }





                floorsVector.add(floorValues);
            }


            //ContentValues roomValues = new ContentValues();

            //roomValues.put(ChoreContract.RoomsEntry.DESCRIPTION,)
        }
    }

    private void runTestQuery() {
        ChoreDBHelper helper = new ChoreDBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(ChoreContract.ChoresEntry.TABLE_NAME, new String[] {"_id", ChoreDBHelper.CHORES_TEMPLATE_COLUMN_DESCRIPTION, ChoreDBHelper.CHORES_TEMPLATE_COLUMN_FREQUENCY,
                        ChoreDBHelper.CHORES_TEMPLATE_COLUMN_EFFORT,ChoreDBHelper.CHORES_TEMPLATE_COLUMN_ROOM},
                 null, null, null, null, null);

        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String data = cursor.getString(cursor.getColumnIndex("description"));
                Log.d("RCD",data);
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
                cv.put(ChoreDBHelper.CHORES_TEMPLATE_COLUMN_DESCRIPTION, columns[0].trim());
                cv.put(ChoreDBHelper.CHORES_TEMPLATE_COLUMN_FREQUENCY, columns[1].trim());
                cv.put(ChoreDBHelper.CHORES_TEMPLATE_COLUMN_EFFORT, columns[2].trim());
                cv.put(ChoreDBHelper.CHORES_TEMPLATE_COLUMN_ROOM, columns[3].trim());
                db.insert(ChoreContract.ChoresEntry.TABLE_NAME, null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }

}
