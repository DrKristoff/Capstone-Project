package com.sidegigapps.chorematic.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sidegigapps.chorematic.Utils;
import com.sidegigapps.chorematic.activities.ChoreDetailActivity;
import com.sidegigapps.chorematic.activities.ChoreListActivity;
import com.sidegigapps.chorematic.R;

import com.sidegigapps.chorematic.database.ChoreContract;
import com.sidegigapps.chorematic.database.ChoreContract.ChoresEntry;

/**
 * A fragment representing a single Chore detail screen.
 * This fragment is either contained in a {@link ChoreListActivity}
 * in two-pane mode (on tablets) or a {@link ChoreDetailActivity}
 * on handsets.
 */
public class ChoreDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String ARG_ITEM_ID = "item_id";
    public static final String DETAIL_URI = "URI";
    private Uri mUri;

    private int floorID = -1;
    private int roomID = -1;
    private String floorName;
    private String roomName;

    private static final int DETAIL_LOADER = 0;
    private static final int FLOOR_LOADER = 1;
    private static final int ROOM_LOADER = 2;

    private static final String[] DETAIL_COLUMNS = {
            ChoresEntry.TABLE_NAME + "." + ChoresEntry._ID,
            ChoresEntry.COLUMN_DESCRIPTION,
            ChoresEntry.COLUMN_FREQUENCY,
            ChoresEntry.COLUMN_EFFORT,
            ChoresEntry.COLUMN_ROOM,
            ChoresEntry.COLUMN_FLOOR_ID,
            ChoresEntry.COLUMN_LAST_DONE,
            ChoresEntry.COLUMN_NEXT_DUE,
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_CHORE_ID = 0;
    public static final int COL_CHORE_DESCRIPTION = 1;
    public static final int COL_CHORE_FREQUENCY = 2;
    public static final int COL_CHORE_EFFORT = 3;
    public static final int COL_CHORE_ROOM = 4;
    public static final int COL_CHORE_FLOOR = 5;
    public static final int COL_CHORE_LAST = 6;
    public static final int COL_CHORE_NEXT = 7;

    TextView mDescription;
    TextView mFloorNameTextView;
    TextView mRoomNameTextView;
    ImageView mEffortImageView;
    TextView mFrequencyTextView;
    TextView mLastTimestampTextView;
    TextView mNextTimestampTextView;

    public ChoreDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("TITLE HERE");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chore_detail, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ChoreDetailFragment.DETAIL_URI);
        }

        mDescription = (TextView) rootView.findViewById(R.id.textViewDescription);
        mFloorNameTextView= (TextView) rootView.findViewById(R.id.floorNameTextView);
        mRoomNameTextView = (TextView) rootView.findViewById(R.id.roomNameTextView);
        mEffortImageView = (ImageView) rootView.findViewById(R.id.effortLevelImageView);
        mFrequencyTextView = (TextView) rootView.findViewById(R.id.frequencyTextView);
        mLastTimestampTextView = (TextView) rootView.findViewById(R.id.lastTimestampTextView);
        mNextTimestampTextView = (TextView) rootView.findViewById(R.id.nextTimestampTextView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(ROOM_LOADER, null, this);
        getLoaderManager().initLoader(FLOOR_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id==DETAIL_LOADER){
            if ( null != mUri ) {
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        DETAIL_COLUMNS,
                        null,
                        null,
                        null
                );
            }
        } else if(id==ROOM_LOADER){

        } else if(id==FLOOR_LOADER){

        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();

        if(id==ROOM_LOADER){

        } else if(id==DETAIL_LOADER){

        } else if(id==FLOOR_LOADER){

        }

        if (data != null && data.moveToFirst()) {

            String choreDescription = data.getString(COL_CHORE_DESCRIPTION);
            mDescription.setText(choreDescription);

            String floorName = data.getString(COL_CHORE_FLOOR);
            mFloorNameTextView.setText(floorName);

            String roomName = data.getString(COL_CHORE_ROOM);
            mRoomNameTextView.setText(roomName);

            int effortResource = Utils.getImageResourceFromText(data.getString(COL_CHORE_EFFORT));
            mEffortImageView.setImageResource(effortResource);
            mEffortImageView.setContentDescription(data.getString(COL_CHORE_EFFORT));

            String frequency = data.getString(COL_CHORE_FREQUENCY);
            mFrequencyTextView.setText(frequency);

            String lastTimestamp = data.getString(COL_CHORE_LAST);
            mLastTimestampTextView.setText(lastTimestamp);

            String nextTimestamp = data.getString(COL_CHORE_NEXT);
            mNextTimestampTextView.setText(nextTimestamp);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
