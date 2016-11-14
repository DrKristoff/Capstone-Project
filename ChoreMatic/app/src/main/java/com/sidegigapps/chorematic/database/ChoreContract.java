package com.sidegigapps.chorematic.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import com.sidegigapps.chorematic.Utils;

/**
 * Created by ryand on 11/11/2016.
 */


public class ChoreContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.sidegigapps.chorematic.database.ChoreProvider";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_FLOORS = "floors";
    public static final String PATH_ROOMS = "rooms";
    public static final String PATH_CHORES = "chores";
    public static final String PATH_EVENTS = "events";


    public static final String ROOM_TYPE_BEDROOM = "bedroom";
    public static final String ROOM_TYPE_BATHROOM = "bathroom";
    public static final String ROOM_TYPE_LIVING_ROOM= "living_room";
    public static final String ROOM_TYPE_DINING_ROOM = "dining_room";
    public static final String ROOM_TYPE_KITCHEN = "kitchen";
    public static final String ROOM_TYPE_LAUNDRY = "laundry";
    public static final String ROOM_TYPE_FAMILY_ROOM = "family_room";

    /* Inner class that defines the table contents of the chore table
    *   FLOORS
    *
    *
    * */
    public static final class FloorsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROOMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FLOORS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FLOORS;

        // Table name
        public static final String TABLE_NAME = "floors";
        public static final String DESCRIPTION = "description";
        public static final String INDEX = "floor_index";

        public static Uri buildFloorsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the chore table
    *   ROOMS
    *
    *
    * */
    public static final class RoomsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROOMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROOMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROOMS;

        // Table name
        public static final String TABLE_NAME = "rooms";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_FLOOR_INDEX = "floor_index";

        public static Uri buildRoomsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the chore table
    *   CHORE
    *
    *
    * */
    public static final class ChoresEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHORES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHORES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHORES;

        public static final String TABLE_NAME = "chores";

        // Column with the foreign key into the location table.
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_FREQUENCY = "frequency";
        public static final String COLUMN_EFFORT = "effort";
        public static final String COLUMN_ROOM = "room";  //for template rooms, room = bedroom, for example.  For user chores, room is the integer room id
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_FLOOR_ID = "floor_id";
        public static final String COLUMN_LAST_DONE = "last";
        public static final String COLUMN_NEXT_DUE = "due";


        //chore types
        public static final String TYPE_TEMPLATE = "template";
        public static final String TYPE_USER = "user";


        public static Uri buildChoresUri(long id) {

            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFloorUri(String floor) {
            return CONTENT_URI.buildUpon().appendPath(floor).build();
        }

/*        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = Utils.normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }*/

/*        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(Utils.normalizeDate(date))).build();
        }*/

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

/*        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }*/
    }

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the chore table
    *   EVENTS
    *
    *
    * */
    public static final class EventsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROOMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTS;

        // Table name
        public static final String TABLE_NAME = "events";

        // Timestamp, stored as long in milliseconds since the epoch
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String CHORE_ID = "chore";

        public static Uri buildEventsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
