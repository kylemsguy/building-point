package com.kylemsguy.buildingpoint;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class QueryButtonFragment extends Fragment implements View.OnClickListener {

    private Button btnPoint;


    public QueryButtonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        btnPoint = (Button) inflater.inflate(R.layout.fragment_query_button, container, false);
        btnPoint.setOnClickListener(this);
        return btnPoint;
    }

    public void onClick(View v) {
        if (v == btnPoint) {
            ((TextView) getActivity().findViewById(R.id.initial_title)).setVisibility(View.GONE);
            ((ImageView) getActivity().findViewById(R.id.logo_placeholder)).setVisibility(View.GONE);
            ((MainActivity) getActivity()).doQueryPoint();
        }
    }
}
