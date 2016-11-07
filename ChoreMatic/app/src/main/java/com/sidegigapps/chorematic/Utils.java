package com.sidegigapps.chorematic;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

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
}
