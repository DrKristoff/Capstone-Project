package com.sidegigapps.chorematic;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.Time;
import android.util.TypedValue;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ryand on 11/6/2016.
 */
public class Utils {

    public static String[] createFloorNames(int numFloors, int mainFloorIndex, Context context){
        String[] floorNamesArray = new String[numFloors];

        if(numFloors==1){
            floorNamesArray[0] = context.getString(R.string.main_floor_name);
        } else {
            floorNamesArray[mainFloorIndex] = context.getString(R.string.main_floor_name);
        }
        if(numFloors==2){
            if(mainFloorIndex==0){
                floorNamesArray[1] = context.getString(R.string.upstairs_floor_name);
            } else {
                floorNamesArray[0] = context.getString(R.string.downstairs_floor_name);
            }
        }

        if(numFloors>2){
            for(int i =0; i < numFloors;i++) {
                floorNamesArray[i] = getOrdinalNumber(i+1);
            }

        }
        return floorNamesArray;
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

    public static long convertMillisecondsToDays(long date){
        return date/1000/60/60/24;
    }

    public static String formatDatabaseDateForUI(String longString){
        return formatDatabaseDateForUI(Long.parseLong(longString));
    }
    public static String formatDatabaseDateForUI(long databaseDate){
        long todayTimestamp = convertMillisecondsToDays(System.currentTimeMillis());
        Date date = new Date();
        SimpleDateFormat formatter;
        //if it's today, return "Today"
        if(databaseDate == todayTimestamp){
            return "Today";
        } else if(todayTimestamp-databaseDate > 7 ){
            //otherwise display day of the week
            formatter = new SimpleDateFormat("MM/DD");
            long milliseconds = databaseDate*24*60*60*1000;
            date.setTime(milliseconds);

        } else {
            //otherwise display day of the week
            formatter = new SimpleDateFormat("EEEE");
            long milliseconds = databaseDate*24*60*60*1000;
            date.setTime(milliseconds);

        }
        return formatter.format(date);
    }

    public static String getOrdinalNumber(int number){
            //0 > 1
            if(!(number >= 1 && number <= 31)) return null;

            if (number >= 11 && number <= 13) {
                return number + "th";
            }
            switch (number % 10) {
                case 1:  return number + "st";
                case 2:  return number + "nd";
                case 3:  return number + "rd";
                default: return number + "th";
            }

    }

    public static int fetchAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    public static int getImageResourceFromText(String string) {
        switch(string){
            case "low":
                return R.drawable.ic_low;
            case "medium":
                return R.drawable.ic_medium;
            case "high":
                return R.drawable.ic_high;
            default:
                return R.drawable.ic_high;
        }
    }

    public static String convertTodayToStringForDB() {
        long date = convertMillisecondsToDays(System.currentTimeMillis());
        return String.valueOf(date);
    }

    public static String getDateStringForWidget() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        Date date = new Date();
        date.setTime(System.currentTimeMillis());

        return formatter.format(date);
    }

    public static String getStringForWidget(Context context, int numChores) {
        if(numChores==0){
            return context.getString(R.string.widget_success_string);
        } else if(numChores==1){
            return context.getString(R.string.widget_one_left);
        } else {
            return String.format(context.getString(R.string.widget_num_chores_left),numChores);
        }
    }
}
