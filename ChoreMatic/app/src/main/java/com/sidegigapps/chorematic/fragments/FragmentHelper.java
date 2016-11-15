package com.sidegigapps.chorematic.fragments;

import android.os.Bundle;

/**
 * Created by ryand on 11/7/2016.
 */

public class FragmentHelper {

    public static final String FLOOR_DETAILS_SETUP_FRAGMENT = "com.sidegigapps.chorematic.fragments.FloorDetailsSetupFragment";
    public static final String IDENTIFY_MAIN_FLOOR_FRAGMENT = "com.sidegigapps.chorematic.fragments.IdentifyMainFloorSetupFragment";
    public static final String NUM_FLOOR_SETUP_FRAGMENT = "com.sidegigapps.chorematic.fragments.NumFloorSetupFragment";
    public static final String SETUP_INTRO_FRAGMENT = "com.sidegigapps.chorematic.fragments.SetupIntroFragment";
    public static final String SETUP_NUM_BEDS_BATHS = "com.sidegigapps.chorematic.fragments.NumBedsAndBathsFragment";

    public static BaseSetupFragment getSetupFragmentByString(String input){
        switch(input){
            case FLOOR_DETAILS_SETUP_FRAGMENT:
                return FloorDetailsSetupFragment.newInstance();
            case IDENTIFY_MAIN_FLOOR_FRAGMENT:
                return IdentifyMainFloorSetupFragment.newInstance();
            case NUM_FLOOR_SETUP_FRAGMENT:
                return NumFloorsSetupFragment.newInstance();
            case SETUP_INTRO_FRAGMENT:
                return SetupIntroFragment.newInstance();
            case SETUP_NUM_BEDS_BATHS:
                return NumBedsAndBathsFragment.newInstance();
            default:
                return null;
        }
    }
}
