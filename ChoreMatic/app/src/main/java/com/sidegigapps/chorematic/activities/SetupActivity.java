package com.sidegigapps.chorematic.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.Utils;
import com.sidegigapps.chorematic.fragments.BaseSetupFragment;
import com.sidegigapps.chorematic.fragments.FloorDetailsSetupFragment;
import com.sidegigapps.chorematic.fragments.IdentifyMainFloorSetupFragment;
import com.sidegigapps.chorematic.fragments.NumFloorsSetupFragment;
import com.sidegigapps.chorematic.fragments.SetupIntroFragment;

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
        controller.currentPage=-1;
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

        LinkedList<BaseSetupFragment> fragments = new LinkedList<>();
        LinkedList<BaseSetupFragment> floorDetailsFragments = new LinkedList<>();
        private int currentPage = 0;

        public FragmentController(){
            init();

        }

        private void init() {

            fragments.add(new SetupIntroFragment());
            fragments.add(new NumFloorsSetupFragment());

            displayFragment();
        }

        private void displayFragment(){
            FragmentTransaction transaction =  getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.setupFrameLayout,getItem(currentPage));
            transaction.addToBackStack(null);
            transaction.commit();
        }

        public void setNumFloors(int numFloors){
            floorDetailsFragments.clear();
            if(numFloors==1){
                mainFloorIndex = 0;
                createFloorDetailFragments();
            } else {
                floorDetailsFragments.add(new IdentifyMainFloorSetupFragment());
            }
        }

        private void createFloorDetailFragments(){
            String [] floorNames = Utils.createFloorNames(numFloors,mainFloorIndex, SetupActivity.this);
            for(int i =0;i < numFloors;i++){
                FloorDetailsSetupFragment fragment = new FloorDetailsSetupFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("index",i);
                bundle.putString("description",floorNames[i]);
                fragment.setArguments(bundle);
                floorDetailsFragments.add(fragment);
            }
        }

        public BaseSetupFragment getItem(int position) {
            if(position<fragments.size()){
                return fragments.get(position);
            } else {
                return floorDetailsFragments.get(position-fragments.size());
            }

        }

        public int getCount() {
            return fragments.size() + floorDetailsFragments.size();
        }


        public void nextPage() {
            if (currentPage<getCount()-1) {
                currentPage +=1;
                displayFragment();
            }
        }

        public void previousPage() {
            if (currentPage>0) {
                currentPage -=1;
                onBackPressed();
            }
        }

        public void setMainFloorIndex(int num) {
            floorDetailsFragments.clear();
            createFloorDetailFragments();
            if(numFloors>1) floorDetailsFragments.addFirst(new IdentifyMainFloorSetupFragment());
        }
    }

}
