package com.sidegigapps.chorematic.fragments;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
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
    View rootView;

    LinearLayout homeImageLayout;

    public NumFloorsSetupFragment() {
    }

    public static NumFloorsSetupFragment newInstance() {
        NumFloorsSetupFragment fragment = new NumFloorsSetupFragment();
        //Bundle args = new Bundle();
        //args.putInt("index", index);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setup_num_floors, container, false);
        rootView.findViewById(R.id.next_button).setOnClickListener(this);
        rootView.findViewById(R.id.add_button).setOnClickListener(this);
        rootView.findViewById(R.id.remove_button).setOnClickListener(this);
        rootView.findViewById(R.id.back_button).setOnClickListener(this);

        rootView.findViewById(R.id.imageViewOne).setOnClickListener(this);
        rootView.findViewById(R.id.imageViewTwo).setOnClickListener(this);
        rootView.findViewById(R.id.imageViewThree).setOnClickListener(this);

        numFloors = setupActivity.getNumFloors();
        homeImageLayout = (LinearLayout) rootView.findViewById(R.id.homeImageLayout);

        floorsTextView = (TextView) rootView.findViewById(R.id.numFloorsLabel);
        updateUI();

        setNumFloors(numFloors);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){
            case (R.id.imageViewOne):
                setNumFloors(1);
                break;
            case (R.id.imageViewTwo):
                setNumFloors(2);
                break;
            case (R.id.imageViewThree):
                setNumFloors(3);
                break;
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

    private void highlightSelectedNumber(int number){
        removeSelectedNumbers();
        switch(number){
            case 1:
                ((ImageView)rootView.findViewById(R.id.imageViewOne)).setImageResource(R.drawable.one_selected);
                break;
            case 2:
                ((ImageView)rootView.findViewById(R.id.imageViewTwo)).setImageResource(R.drawable.two_selected);
                break;

            case 3:
                ((ImageView)rootView.findViewById(R.id.imageViewThree)).setImageResource(R.drawable.three_selected);
                break;
        }
    }

    private void removeSelectedNumbers(){
        ((ImageView)rootView.findViewById(R.id.imageViewOne)).setImageResource(R.drawable.one);
        ((ImageView)rootView.findViewById(R.id.imageViewTwo)).setImageResource(R.drawable.two);
        ((ImageView)rootView.findViewById(R.id.imageViewThree)).setImageResource(R.drawable.three);
    }

    private void setNumFloors(int num){
        numFloors = num;
        highlightSelectedNumber(num);
        updateUI();
    }

    private void addFloor(){
        if(numFloors ==3){
            Toast.makeText(getActivity(),"Max of 3 floors",Toast.LENGTH_SHORT).show();
            return;
        }
        numFloors +=1;
        updateUI();
    }

    private void removeFloor(){
        if(numFloors>1){
            numFloors-=1;
            updateUI();
        }
    }

    private void updateUI() {
        Resources res = getResources();
        String floorsLabel = res.getQuantityString(R.plurals.numberOfFloors, numFloors, numFloors);
        floorsTextView.setText(floorsLabel);
        resetHomeImageLayout();
    }

    private void resetHomeImageLayout(){
        homeImageLayout.removeAllViews();
        addRoofLayout();
        addFloorsToLayout();
    }

    private void addRoofLayout(){
        Resources resources = getResources();
        ImageView roofImageView= new ImageView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)resources.getDimension(R.dimen.roofIconWidth), (int)resources.getDimension(R.dimen.roofIconHeight));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        roofImageView.setBackground(resources.getDrawable(R.drawable.roof));
        roofImageView.setLayoutParams(params);
        homeImageLayout.addView(roofImageView);
    }

    private void addFloorsToLayout() {
        Resources resources = getResources();
        for(int i = 0 ; i<numFloors;i++){
            ImageView newFloor= new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)resources.getDimension(R.dimen.floorIconWidth), (int)resources.getDimension(R.dimen.floorIconHeight));
            params.gravity = Gravity.CENTER_HORIZONTAL;
            newFloor.setBackground(resources.getDrawable(R.drawable.floor));
            newFloor.setLayoutParams(params);
            newFloor.setTag(String.valueOf(i));
            homeImageLayout.addView(newFloor);
        }
    }

}
