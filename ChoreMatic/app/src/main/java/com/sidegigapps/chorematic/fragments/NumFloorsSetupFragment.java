package com.sidegigapps.chorematic.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.activities.SetupActivity;

/**
 * Created by ryand on 11/4/2016.
 */

public class NumFloorsSetupFragment extends BaseSetupFragment implements View.OnClickListener {

    private int numFloors =1;

    TextView floorsTextView;

    LinearLayout homeImageLayout;

    public NumFloorsSetupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setup_num_floors, container, false);
        rootView.findViewById(R.id.next_button).setOnClickListener(this);
        rootView.findViewById(R.id.add_button).setOnClickListener(this);
        rootView.findViewById(R.id.remove_button).setOnClickListener(this);
        rootView.findViewById(R.id.next_button).setOnClickListener(this);

        homeImageLayout = (LinearLayout) rootView.findViewById(R.id.homeImageLayout);

        floorsTextView = (TextView) rootView.findViewById(R.id.numFloorsLabel);
        updateUI();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){
            case (R.id.add_button):
                addFloor();
                break;
            case(R.id.remove_button):
                removeFloor();
                break;
            case(R.id.next_button):
                setupActivity.setNumFloors(numFloors);
                setupActivity.nextPage();
                break;

            case(R.id.back_button):
                setupActivity.previousPage();
                break;
            }

    }

    private void addFloor(){
        if(numFloors ==3){
            Toast.makeText(getActivity(),"Max of 3 floors",Toast.LENGTH_SHORT).show();
            return;
        }
        numFloors +=1;
        updateUI();

        Context context = getActivity();
        Resources resources = getResources();
        ImageView newFloor= new ImageView(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)resources.getDimension(R.dimen.floorIconWidth), (int)resources.getDimension(R.dimen.floorIconHeight));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        newFloor.setBackground(resources.getDrawable(R.drawable.floor));
        newFloor.setLayoutParams(params);
        newFloor.setTag(String.valueOf(numFloors));
        homeImageLayout.addView(newFloor);

    }

    private void removeFloor(){
        if(numFloors>1){
            homeImageLayout.clearFocus();  //fixes bug where unfocus is called on null object
            homeImageLayout.removeView(homeImageLayout.findViewWithTag(String.valueOf(numFloors)));
            numFloors-=1;

            updateUI();
        }
    }

    private void updateUI() {
        Resources res = getResources();
        String floorsLabel = res.getQuantityString(R.plurals.numberOfFloors, numFloors, numFloors);
        floorsTextView.setText(floorsLabel);
    }

    @Override
    public void update() {

    }
}
