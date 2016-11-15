package com.sidegigapps.chorematic.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.Utils;
import com.sidegigapps.chorematic.activities.ChoreListActivity;
import com.sidegigapps.chorematic.database.ChoreDatabaseUtils;

/**
 * Created by ryand on 11/14/2016.
 */

public class UpdateChoresReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(Intent.ACTION_DATE_CHANGED)){
            ChoreDatabaseUtils utils = new ChoreDatabaseUtils(context);
            utils.initializeNewDay();
            displayNotification(context);

        }

    }

    private void displayNotification(Context context) {
        ChoreDatabaseUtils utils = new ChoreDatabaseUtils(context);
        int numChores = utils.getNumChoresLeftTodayForWidget();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setColor(context.getResources().getColor(R.color.colorPrimary))
                        .setSmallIcon(R.drawable.hq_logo)
                        .setContentTitle(context.getString(R.string.notification_text) + Utils.getDateStringForWidget())
                        .setContentText(Utils.getStringForWidget(context,numChores));

        Intent resultIntent = new Intent(context, ChoreListActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
