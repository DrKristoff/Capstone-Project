package com.sidegigapps.chorematic.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.Utils;
import com.sidegigapps.chorematic.adapters.ChoreListAdapter;
import com.sidegigapps.chorematic.database.ChoreContract;
import com.sidegigapps.chorematic.database.ChoreContract.ChoresEntry;
import com.sidegigapps.chorematic.database.ChoreDatabaseUtils;
import com.sidegigapps.chorematic.fragments.ChoreDetailFragment;

public class ChoreListActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        NavigationView.OnNavigationItemSelectedListener  {

    private boolean mTwoPane;
    ListView mListView;
    private ChoreListAdapter mChoreListAdapter;

    ChoreDatabaseUtils dbUtils;

    private static final String CHOREDETAILFRAGMENT_TAG = "CDFTAG";

    private static final String[] Chores_Projection = {
            ChoresEntry._ID,
            ChoresEntry.COLUMN_DESCRIPTION,
            ChoresEntry.COLUMN_FREQUENCY,
            ChoresEntry.COLUMN_EFFORT,
            ChoresEntry.COLUMN_ROOM,
            ChoresEntry.COLUMN_FLOOR_ID,
            ChoresEntry.COLUMN_LAST_DONE,
            ChoresEntry.COLUMN_NEXT_DUE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_list);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.today_activity_title);

        dbUtils = new ChoreDatabaseUtils(this);
        dbUtils.scheduleAllUnscheduledChores();


        mListView = (ListView) findViewById(R.id.chore_list);

        mChoreListAdapter = new ChoreListAdapter(this,null,0);
        mListView.setAdapter(mChoreListAdapter);

        if (findViewById(R.id.chore_detail_container) != null) {
            mTwoPane = true;
        }

        getSupportLoaderManager().initLoader(0,null,this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setupNavigationDrawer();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri mUri = ChoreContract.ChoresEntry.CONTENT_URI
                .buildUpon().appendPath("date").appendPath(Utils.convertTodayToStringForDB())
                .build();
        if ( null != mUri ) {
            return new CursorLoader(
                    this,
                    mUri,
                    Chores_Projection,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mChoreListAdapter.swapCursor(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public void onChoreSelected(int choreID){
        onChoreSelected(ChoreContract.ChoresEntry.CONTENT_URI.buildUpon()
                .appendPath("id")
                .appendPath(String.valueOf(choreID)).build()
        );
    }

    public void markChoreAsDone(int choreID){
        dbUtils.markChoreAsDoneAndReschedule(choreID);
    }

    public void markChoreAsToDo(int choreID){
        ///TO DO
        // if you have checked it as done and want to un do
        //
    }

    public void onChoreSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(ChoreDetailFragment.DETAIL_URI, contentUri);

            ChoreDetailFragment fragment = new ChoreDetailFragment();
            fragment.setArguments(args);


            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                    R.anim.fade_out);

            fragmentTransaction
                    .replace(R.id.chore_detail_container, fragment, CHOREDETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, ChoreDetailActivity.class)
                    .setData(contentUri);
            Animation a = AnimationUtils.loadAnimation(this, R.anim.slide);
            a.reset();
            FrameLayout fl = (FrameLayout) findViewById(R.id.frameLayout);
            fl.clearAnimation();
            fl.startAnimation(a);
            startActivity(intent);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_today) {
            navigateTo(BaseActivity.TodayActivity);
        } else if (id == R.id.nav_calendar) {
            navigateTo(BaseActivity.CalendarActivity);
        } else if (id == R.id.nav_sign_out) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
