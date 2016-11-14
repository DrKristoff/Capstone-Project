package com.sidegigapps.chorematic.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.activities.ChoreListActivity;
import com.sidegigapps.chorematic.database.ChoreContract;

/**
 * Created by ryand on 11/13/2016.
 */

public class ChoreListAdapter extends CursorAdapter {

        private static final int VIEW_TYPE_COUNT = 2;
        private static final int VIEW_TYPE_TODAY = 0;
        private static final int VIEW_TYPE_FUTURE_DAY = 1;

        public static class ViewHolder {
            public final TextView descriptionTextView;
            public int choreID;

            public ViewHolder(View view) {
                descriptionTextView = (TextView) view.findViewById(R.id.choreListItemDescriptionTextView);
            }
        }

        public ChoreListAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            int layoutId = R.layout.chore_list_item;

            View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);

            return view;
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {

            final ViewHolder viewHolder = (ViewHolder) view.getTag();

            viewHolder.choreID = cursor.getInt(cursor.getColumnIndex(ChoreContract.ChoresEntry._ID));

            // Read date from cursor
            String description = cursor.getString(cursor.getColumnIndex(ChoreContract.ChoresEntry.COLUMN_DESCRIPTION));
            // Find TextView and set formatted date on it
            viewHolder.descriptionTextView.setText(description);

            viewHolder.descriptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ChoreListActivity)context).onChoreSelected(viewHolder.choreID);
                }
            });

        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }
    }