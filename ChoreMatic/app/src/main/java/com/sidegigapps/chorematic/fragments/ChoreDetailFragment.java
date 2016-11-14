package com.sidegigapps.chorematic.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sidegigapps.chorematic.Utils;
import com.sidegigapps.chorematic.R;

import com.sidegigapps.chorematic.database.ChoreContract;
import com.sidegigapps.chorematic.database.ChoreContract.ChoresEntry;

public class ChoreDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String ARG_ITEM_ID = "item_id";
    public static final String DETAIL_URI = "URI";
    private Uri mUri;

    private int floorIndex = -1;
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

    CollapsingToolbarLayout appBarLayout;

    public ChoreDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chore_detail, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ChoreDetailFragment.DETAIL_URI);
        }

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
            Uri roomUri = ChoreContract.RoomsEntry.CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(roomID))
                    .build();

            Log.d("RCD","ROOM QUERY: " + roomUri.toString());
            return new CursorLoader(
                    getActivity(),
                    roomUri,
                    new String[]{ChoreContract.RoomsEntry.COLUMN_DESCRIPTION, ChoreContract.RoomsEntry.COLUMN_FLOOR_INDEX},
                    null,
                    null,
                    null
            );
        } else if(id==FLOOR_LOADER){
            Uri floorUri = ChoreContract.FloorsEntry.CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(floorIndex))
                    .build();
            Log.d("RCD","FLOOR QUERY: " + floorUri.toString());
            return new CursorLoader(
                    getActivity(),
                    floorUri,
                    new String[]{ChoreContract.FloorsEntry.DESCRIPTION},
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();

        if (data != null && data.moveToFirst()) {

            if (id == DETAIL_LOADER) {
                String choreDescription = data.getString(COL_CHORE_DESCRIPTION).toUpperCase();
                //mDescription.setText(choreDescription);
                appBarLayout.setTitle(choreDescription);

                roomID = data.getInt(COL_CHORE_ROOM);

                /*String floorName = data.getString(COL_CHORE_FLOOR);
                mFloorNameTextView.setText(floorName);

                String roomName = data.getString(COL_CHORE_ROOM);
                mRoomNameTextView.setText(roomName);*/

                int effortResource = Utils.getImageResourceFromText(data.getString(COL_CHORE_EFFORT));
                mEffortImageView.setImageResource(effortResource);
                mEffortImageView.setContentDescription(data.getString(COL_CHORE_EFFORT));

                String frequency = data.getString(COL_CHORE_FREQUENCY);
                mFrequencyTextView.setText(frequency);

                String lastTimestamp = data.getString(COL_CHORE_LAST);
                mLastTimestampTextView.setText(lastTimestamp);

                String nextTimestamp = data.getString(COL_CHORE_NEXT);
                mNextTimestampTextView.setText(nextTimestamp);

                getLoaderManager().initLoader(ROOM_LOADER, null, this);

            } else if (id == ROOM_LOADER) {
                String roomDescription = data.getString(data.getColumnIndex(ChoreContract.RoomsEntry.COLUMN_DESCRIPTION));
                floorIndex = data.getInt(data.getColumnIndex(ChoreContract.RoomsEntry.COLUMN_FLOOR_INDEX));
                mRoomNameTextView.setText(roomDescription);
                getLoaderManager().initLoader(FLOOR_LOADER, null, this);
            } else if (id == FLOOR_LOADER) {
                String floorDescription = data.getString(data.getColumnIndex(ChoreContract.FloorsEntry.DESCRIPTION));
                mFloorNameTextView.setText(floorDescription);

            }
        } else {
            //display loading error
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
