package com.example.hp.opencvtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hp.opencvtest.objectdetection.RecognizeConceptsActivity;

/**
 * Created by Hp on 27-03-2017.
 */

public class imagerecognitionfragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.imagerecognitionfragment, container,
                false);
        Button button = (Button) rootView.findViewById(R.id.imgreco);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // do something
                Intent intent = new Intent(getActivity(),RecognizeConceptsActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
