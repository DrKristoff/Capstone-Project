package com.sidegigapps.chorematic.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.fragments.BaseFragment;
import com.sidegigapps.chorematic.fragments.FloorDetailsFragment;
import com.sidegigapps.chorematic.fragments.IdentifyMainFloorFragment;
import com.sidegigapps.chorematic.fragments.NumFloorsFragment;
import com.sidegigapps.chorematic.fragments.SetupIntroFragment;

import java.util.LinkedList;
import java.util.List;

public class SetupActivity extends BaseActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private int numFloors;
    private String[] floorNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        numFloors = 1;

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup, menu);
        return true;
    }

    public void nextPage(){

    }

    public void setNumFloors(int num){
        numFloors = num;
        floorNames = new String[num];
        mSectionsPagerAdapter.initNumFloors(num);
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        List<BaseFragment> fragments = new LinkedList<>();
        List<BaseFragment> floorDetailsFragments = new LinkedList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            init();
        }

        private void init() {
            fragments.add(new SetupIntroFragment());
            fragments.add(new NumFloorsFragment());
            fragments.add(new IdentifyMainFloorFragment());
        }

        public void initNumFloors(int numFloors){
            floorDetailsFragments.clear();
            for(int i =0;i < numFloors;i++){
                FloorDetailsFragment fragment = new FloorDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("index",i);
                //bundle.putString("name",floorNames[i]);  FOR TESTING
                bundle.putString("name","MAIN");
                fragment.setArguments(bundle);
                floorDetailsFragments.add(new FloorDetailsFragment());
            }
        }

        @Override
        public BaseFragment getItem(int position) {
            if(position<fragments.size()){
                return fragments.get(position);
            } else {
                return floorDetailsFragments.get(position-fragments.size());
            }

        }

        @Override
        public int getCount() {
            return fragments.size() + floorDetailsFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
