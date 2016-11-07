package com.sidegigapps.chorematic.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.Utils;
import com.sidegigapps.chorematic.activities.SetupActivity;

/**
 * Created by ryand on 11/4/2016.
 */

public class IdentifyMainFloorSetupFragment extends BaseSetupFragment implements View.OnClickListener {

    private int numFloors;
    private LinearLayout homeImageLayout;

    View.OnClickListener listener;

    public IdentifyMainFloorSetupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setup_identify_main, container, false);
        rootView.findViewById(R.id.back_button).setOnClickListener(this);
        rootView.findViewById(R.id.next_button).setOnClickListener(this);

        homeImageLayout = (LinearLayout) rootView.findViewById(R.id.homeImageLayout);


        numFloors = setupActivity.getNumFloors();

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int i = 0; i < numFloors;i++){
                    homeImageLayout.findViewWithTag(String.valueOf(i))
                            .setBackground(getResources().getDrawable(R.drawable.floor));
                }
                String viewTag = (String) view.getTag();
                int mainFloorIndex = Integer.parseInt(viewTag);
                setupActivity.setMainFloorIndex(mainFloorIndex);

                view.setBackgroundColor(Utils.fetchAccentColor(getActivity()));
            }
        };

        addFloorsToLayout();
        return rootView;
    }

    private void resetFloors(){
        while(homeImageLayout.getChildCount()>2){
            homeImageLayout.removeViewAt(2);
        }

    }

    private void addFloorsToLayout() {

        Resources resources = getResources();
        for(int i = (numFloors-1) ; i>=0;i--){
            ImageView newFloor= new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)resources.getDimension(R.dimen.floorIconWidth), (int)resources.getDimension(R.dimen.floorIconHeight));
            params.gravity = Gravity.CENTER_HORIZONTAL;
            newFloor.setBackground(resources.getDrawable(R.drawable.floor));
            newFloor.setLayoutParams(params);
            newFloor.setTag(String.valueOf(i));
            newFloor.setOnClickListener(listener);
            homeImageLayout.addView(newFloor);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){
            case(R.id.back_button):
                setupActivity.previousPage();
                break;
            case(R.id.next_button):{
                setupActivity.nextPage();
                break;
            }
        }

    }

    @Override
    public void update() {
        resetFloors();
        addFloorsToLayout();
    }
}
