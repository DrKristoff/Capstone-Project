package com.sidegigapps.chorematic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.database.ChoreDatabaseUtils;
import com.sidegigapps.chorematic.fragments.ChoreDetailFragment;

public class ChoreDetailActivity extends BaseActivity {

    ChoreDetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoreDatabaseUtils utils = new ChoreDatabaseUtils(ChoreDetailActivity.this);
                utils.markChoreAsDoneAndReschedule(fragment.choreID);
                showChoreCompleteToast();
                navigateTo(TodayActivity);
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(ChoreDetailFragment.DETAIL_URI, getIntent().getData());

            fragment = new ChoreDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chore_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            navigateUpTo(new Intent(this, ChoreListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
