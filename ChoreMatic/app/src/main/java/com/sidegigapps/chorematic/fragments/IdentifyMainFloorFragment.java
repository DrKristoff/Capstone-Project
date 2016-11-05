package com.sidegigapps.chorematic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sidegigapps.chorematic.R;

/**
 * Created by ryand on 11/4/2016.
 */

public class IdentifyMainFloorFragment extends BaseFragment implements View.OnClickListener {

    public IdentifyMainFloorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setup_identify_main, container, false);

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
            case(R.id.next_button):{
                Toast.makeText(getActivity().getApplicationContext(),"NEXT", Toast.LENGTH_SHORT).show();
                break;
            }
        }

    }
}
