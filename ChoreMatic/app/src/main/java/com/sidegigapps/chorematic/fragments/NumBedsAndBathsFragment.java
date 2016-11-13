package com.sidegigapps.chorematic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    int numBaths =1;
    int numBeds = 1;

    View rootView;

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
            floorIndex = bundle.getInt("index", 99);
            description = bundle.getString("description");
            hasBedrooms = bundle.getBoolean("hasBedrooms");
            hasBathrooms = bundle.getBoolean("hasBathrooms");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setup_num_beds_baths, container, false);

        rootView.findViewById(R.id.next_button).setOnClickListener(this);
        rootView.findViewById(R.id.back_button).setOnClickListener(this);

        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        String text = String.format(getResources().getString(R.string.setup_num_bed_bath_string), description);
        textView.setText(text);



        if(hasBedrooms){
            rootView.findViewById(R.id.imageViewOneBedroom).setOnClickListener(this);
            rootView.findViewById(R.id.imageViewTwoBedrooms).setOnClickListener(this);
            rootView.findViewById(R.id.imageViewThreeBedrooms).setOnClickListener(this);
        } else {
            rootView.findViewById(R.id.numBedroomsCardView).setVisibility(View.GONE);
            numBeds =0;
        }

        if (hasBathrooms){
            rootView.findViewById(R.id.imageViewOneBathroom).setOnClickListener(this);
            rootView.findViewById(R.id.imageViewTwoBathrooms).setOnClickListener(this);
            rootView.findViewById(R.id.imageViewThreeBathrooms).setOnClickListener(this);
        } else {
            rootView.findViewById(R.id.numBathroomsCardView).setVisibility(View.GONE);
            numBaths = 0;
        }

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){
            case (R.id.imageViewOneBedroom):
                numBeds = 1;
                highlightSelectedNumbers();
                break;
            case (R.id.imageViewTwoBedrooms):
                numBeds = 2;
                highlightSelectedNumbers();
                break;
            case (R.id.imageViewThreeBedrooms):
                numBeds = 3;
                highlightSelectedNumbers();
                break;
            case (R.id.imageViewOneBathroom):
                numBaths = 1;
                highlightSelectedNumbers();
                break;
            case (R.id.imageViewTwoBathrooms):
                numBaths = 2;
                highlightSelectedNumbers();
                break;
            case (R.id.imageViewThreeBathrooms):
                numBaths = 3;
                highlightSelectedNumbers();
                break;


            case(R.id.remove_button):
                break;
            case(R.id.next_button):
                setupActivity.updateNumBedsAndBaths(floorIndex,numBaths, numBeds);
                setupActivity.nextPage();
                break;
            case(R.id.back_button):
                setupActivity.previousPage();
                break;

        }

    }

    private void highlightSelectedNumbers(){
        removeSelectedNumbers();

        switch(numBaths){
            case 1:
                ((ImageView)rootView.findViewById(R.id.imageViewOneBathroom)).setImageResource(R.drawable.one_selected);
                break;
            case 2:
                ((ImageView)rootView.findViewById(R.id.imageViewTwoBathrooms)).setImageResource(R.drawable.two_selected);
                break;

            case 3:
                ((ImageView)rootView.findViewById(R.id.imageViewThreeBathrooms)).setImageResource(R.drawable.three_selected);
                break;
        }

        switch(numBeds){
            case 1:
                ((ImageView)rootView.findViewById(R.id.imageViewOneBedroom)).setImageResource(R.drawable.one_selected);
                break;
            case 2:
                ((ImageView)rootView.findViewById(R.id.imageViewTwoBedrooms)).setImageResource(R.drawable.two_selected);
                break;

            case 3:
                ((ImageView)rootView.findViewById(R.id.imageViewThreeBedrooms)).setImageResource(R.drawable.three_selected);
                break;
        }
    }

    private void removeSelectedNumbers(){
        ((ImageView)rootView.findViewById(R.id.imageViewOneBathroom)).setImageResource(R.drawable.one);
        ((ImageView)rootView.findViewById(R.id.imageViewTwoBathrooms)).setImageResource(R.drawable.two);
        ((ImageView)rootView.findViewById(R.id.imageViewThreeBathrooms)).setImageResource(R.drawable.three);
        ((ImageView)rootView.findViewById(R.id.imageViewOneBedroom)).setImageResource(R.drawable.one);
        ((ImageView)rootView.findViewById(R.id.imageViewTwoBedrooms)).setImageResource(R.drawable.two);
        ((ImageView)rootView.findViewById(R.id.imageViewThreeBedrooms)).setImageResource(R.drawable.three);
    }

}
