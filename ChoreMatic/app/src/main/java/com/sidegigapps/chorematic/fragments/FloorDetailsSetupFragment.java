package com.sidegigapps.chorematic.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.activities.SetupActivity;

/**
 * Created by ryand on 11/4/2016.
 */

public class FloorDetailsSetupFragment extends BaseSetupFragment implements View.OnClickListener {

    private int floorIndex;
    private String description ="";

    public FloorDetailsSetupFragment() {
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
    public void update() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setup_floor_details, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);

        Resources res = getResources();
        String text = String.format(res.getString(R.string.floor_details_instructions), description);
        textView.setText(text);

        rootView.findViewById(R.id.back_button).setOnClickListener(this);
        rootView.findViewById(R.id.next_button).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){
            case (R.id.back_button):
                setupActivity.previousPage();
                break;
            case (R.id.next_button):
                setupActivity.nextPage();
                break;
        }

    }
}
