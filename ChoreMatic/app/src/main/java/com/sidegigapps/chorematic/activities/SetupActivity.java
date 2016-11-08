package com.sidegigapps.chorematic.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.Utils;
import com.sidegigapps.chorematic.fragments.BaseSetupFragment;
import com.sidegigapps.chorematic.fragments.FloorDetailsSetupFragment;
import com.sidegigapps.chorematic.fragments.FragmentHelper;
import com.sidegigapps.chorematic.fragments.IdentifyMainFloorSetupFragment;
import com.sidegigapps.chorematic.fragments.NumFloorsSetupFragment;
import com.sidegigapps.chorematic.fragments.SetupIntroFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SetupActivity extends BaseActivity {

    private FrameLayout fragmentFrameLayout;
    FragmentController controller;

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

    public class FragmentController{

        ArrayList<String> fragmentList = new ArrayList<>();
        private static final String STRING_DIVISOR = "::";

        private int currentPage = 0;

        public FragmentController(){
            init();
        }

        private void init() {

            //fragments.add(new SetupIntroFragment());
            //fragments.add(new NumFloorsSetupFragment());

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
                createFloorDetailFragments();
            } else {
                fragmentList.add(FragmentHelper.IDENTIFY_MAIN_FLOOR_FRAGMENT);
            }
        }

        private void createFloorDetailFragments(){
            String [] floorNames = Utils.createFloorNames(numFloors,mainFloorIndex, SetupActivity.this);
            for(int i =0;i < numFloors;i++){
                String floorDescription = floorNames[i];
                fragmentList.add(FragmentHelper.FLOOR_DETAILS_SETUP_FRAGMENT
                        + STRING_DIVISOR + floorDescription);
            }
        }

        public BaseSetupFragment getItem(int position) {
            if(position>fragmentList.size()){
                return null;
            }

            String fragmentString = fragmentList.get(position);

            if(fragmentString.contains(STRING_DIVISOR)){
                BaseSetupFragment fragment = FragmentHelper.getSetupFragmentByString(FragmentHelper.FLOOR_DETAILS_SETUP_FRAGMENT);
                Bundle bundle = new Bundle();
                bundle.putString("description",fragmentString.split(STRING_DIVISOR)[1]);
                fragment.setArguments(bundle);
                return fragment;
            } else {
                return FragmentHelper.getSetupFragmentByString(fragmentString);
            }
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
            createFloorDetailFragments();
        }
    }

}
