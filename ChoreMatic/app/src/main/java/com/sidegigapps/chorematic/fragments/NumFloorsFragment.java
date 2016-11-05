package com.sidegigapps.chorematic.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sidegigapps.chorematic.R;
import com.sidegigapps.chorematic.activities.SetupActivity;

/**
 * Created by ryand on 11/4/2016.
 */

public class NumFloorsFragment extends BaseFragment implements View.OnClickListener {

    private int numFloors =1;

    TextView floorsTextView;

    public NumFloorsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setup_num_floors, container, false);
        rootView.findViewById(R.id.next_button).setOnClickListener(this);
        rootView.findViewById(R.id.add_button).setOnClickListener(this);
        rootView.findViewById(R.id.remove_button).setOnClickListener(this);
        rootView.findViewById(R.id.next_button).setOnClickListener(this);

        floorsTextView = (TextView) rootView.findViewById(R.id.numFloorsLabel);
        updateUI();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){
            case (R.id.add_button):
                numFloors +=1;
                updateUI();
                break;
            case(R.id.remove_button):
                if(numFloors>1){
                    numFloors-=1;
                    updateUI();
                }
                break;
            case(R.id.next_button):{
                ((SetupActivity)getActivity()).setNumFloors(numFloors);
                Toast.makeText(getActivity().getApplicationContext(),"NEXT", Toast.LENGTH_SHORT).show();
                break;
            }
        }

    }

    private void updateUI() {
        Resources res = getResources();
        String floorsLabel = res.getQuantityString(R.plurals.numberOfFloors, numFloors, numFloors);
        floorsTextView.setText(floorsLabel);
    }
}
