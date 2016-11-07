package com.sidegigapps.chorematic.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.Utils;
import com.sidegigapps.chorematic.fragments.BaseSetupFragment;
import com.sidegigapps.chorematic.fragments.FloorDetailsSetupFragment;
import com.sidegigapps.chorematic.fragments.IdentifyMainFloorSetupFragment;
import com.sidegigapps.chorematic.fragments.NumFloorsSetupFragment;
import com.sidegigapps.chorematic.fragments.SetupIntroSetupFragment;

import java.util.LinkedList;
import java.util.List;

public class SetupActivity extends BaseActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

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

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSectionsPagerAdapter.updateFragment(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup, menu);
        return true;
    }

    public void nextPage(){
        if(mSectionsPagerAdapter.getCount()>mViewPager.getCurrentItem()){
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
        }
    }

    public void previousPage(){
        if(mViewPager.getCurrentItem()>0){
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
        }
    }

    public void setMainFloorIndex(int num){
        mainFloorIndex = num;
        mSectionsPagerAdapter.initNumFloors(numFloors);
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    public void setNumFloors(int num){
        numFloors = num;
        floorNames = new String[num];
        if(num>1){
            mSectionsPagerAdapter.addMainFloorIdentificationFragment();
            mSectionsPagerAdapter.notifyDataSetChanged();
        } else {
            setMainFloorIndex(0);
            //mSectionsPagerAdapter.setSingleFloor();
        }

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

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        List<BaseSetupFragment> fragments = new LinkedList<>();
        List<BaseSetupFragment> floorDetailsFragments = new LinkedList<>();

        boolean singleFloor = false;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            init();
        }

        private void init() {
            fragments.add(new SetupIntroSetupFragment());
            fragments.add(new NumFloorsSetupFragment());
        }

        public void initNumFloors(int numFloors){
            floorDetailsFragments.clear();
            String [] floorNames = Utils.createFloorNames(numFloors,mainFloorIndex, SetupActivity.this);
            for(int i =0;i < numFloors;i++){
                FloorDetailsSetupFragment fragment = new FloorDetailsSetupFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("index",i);
                bundle.putString("description",floorNames[i]);
                fragment.setArguments(bundle);
                floorDetailsFragments.add(fragment);
            }

            notifyDataSetChanged();
        }

        @Override
        public BaseSetupFragment getItem(int position) {
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


        public void addMainFloorIdentificationFragment() {
            fragments.add(new IdentifyMainFloorSetupFragment());
        }

        public void updateFragment(int position) {
            BaseSetupFragment fragment = getItem(position);
            fragment.update();
        }
    }
}
