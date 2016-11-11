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

public class NumBedsAndBathsFragment extends BaseSetupFragment implements View.OnClickListener {

    private int floorIndex;
    private String description ="";
    boolean hasBedrooms = false;
    boolean hasBathrooms = false;

    public NumBedsAndBathsFragment() {
    }


    public static BaseSetupFragment newInstance() {
        NumBedsAndBathsFragment fragment = new NumBedsAndBathsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            floorIndex = bundle.getInt("index", 1);
            description = bundle.getString("description");
            hasBedrooms = bundle.getBoolean("hasBedrooms");
            hasBathrooms = bundle.getBoolean("hasBathrooms");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setup_num_beds_baths, container, false);

        rootView.findViewById(R.id.imageViewOneBathroom).setOnClickListener(this);
        rootView.findViewById(R.id.imageViewTwoBathrooms).setOnClickListener(this);
        rootView.findViewById(R.id.imageViewThreeBathrooms).setOnClickListener(this);
        rootView.findViewById(R.id.imageViewOneBedroom).setOnClickListener(this);
        rootView.findViewById(R.id.imageViewTwoBedrooms).setOnClickListener(this);
        rootView.findViewById(R.id.imageViewThreeBedrooms).setOnClickListener(this);

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
