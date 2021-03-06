package com.sidegigapps.chorematic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.activities.BaseActivity;
import com.sidegigapps.chorematic.activities.SetupActivity;

/**
 * Created by ryand on 11/4/2016.
 */

public abstract class BaseSetupFragment extends Fragment {

    protected SetupActivity setupActivity;
    protected String fragmentQuestion = getString(R.string.fragment_question);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivity = ((SetupActivity)getActivity());
    }

    public String getFragmentQuestion(){
        return fragmentQuestion;
    }


}
