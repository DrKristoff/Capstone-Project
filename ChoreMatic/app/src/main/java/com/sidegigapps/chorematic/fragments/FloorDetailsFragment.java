package com.sidegigapps.chorematic.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sidegigapps.chorematic.R;

/**
 * Created by ryand on 11/4/2016.
 */

public class FloorDetailsFragment extends BaseFragment implements View.OnClickListener {

    private int floorIndex;
    private String description ="";

    public FloorDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            floorIndex = bundle.getInt("index", 1);
            description = bundle.getString("description");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setup_floor_details, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);

        Resources res = getResources();
        String text = String.format(res.getString(R.string.floor_details_instructions), description);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){
            case (R.id.add_button):
                break;
        }

    }
}
