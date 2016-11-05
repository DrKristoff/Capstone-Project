package com.sidegigapps.chorematic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sidegigapps.chorematic.activities.BaseActivity;

/**
 * Created by ryand on 11/4/2016.
 */

public abstract class BaseFragment extends Fragment {

    protected BaseActivity baseActivity;
    protected String fragmentQuestion = "Fragment Question";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = ((BaseActivity)getActivity());
    }

    public String getFragmentQuestion(){
        return fragmentQuestion;
    }
}
