package com.sidegigapps.chorematic.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.Utils;
import com.sidegigapps.chorematic.activities.ChoreListActivity;
import com.sidegigapps.chorematic.database.ChoreDBHelper;
import com.sidegigapps.chorematic.database.ChoreDatabaseUtils;

import static android.R.style.Widget;

/**
 * Created by ryand on 11/14/2016.
 */

public class ChoreWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, ChoreListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widgetFrameLayout, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

/*    private void updateWidget(Context context){
        ChoreDatabaseUtils utils = new ChoreDatabaseUtils(context);

        int numChoresLeft = utils.getNumChoresLeftTodayForWidget();

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget);

        // Update text, images, whatever - here
        remoteViews.setTextViewText(R.id.textViewTodaysDate, Utils.getDateStringForWidget());
        remoteViews.setTextViewText(R.id.texdtViewNumChoresToday, Utils.getStringForWidget(context,numChoresLeft));

        // Trigger widget layout update
        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, ChoreWidgetProvider.class), remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("update_widget")) {
            updateWidget(context);
        }
    }*/
}
