package com.sidegigapps.chorematic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.activities.SetupActivity;

/**
 * Created by ryand on 11/4/2016.
 */

public class MainFloorSetupFragment extends BaseSetupFragment implements View.OnClickListener {

    private int numFloors;

    public MainFloorSetupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setup_num_floors, container, false);
        rootView.findViewById(R.id.next_button).setOnClickListener(this);
        rootView.findViewById(R.id.add_button).setOnClickListener(this);
        rootView.findViewById(R.id.remove_button).setOnClickListener(this);
        rootView.findViewById(R.id.back_button).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){
            case (R.id.add_button):
                break;
            case(R.id.remove_button):
                break;
            case(R.id.next_button):
                setupActivity.nextPage();
                break;

        }

    }

}
