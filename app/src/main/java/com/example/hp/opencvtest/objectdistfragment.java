package com.example.hp.opencvtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hp.opencvtest.objectdist.CameraActivity;

/**
 * Created by Hp on 28-03-2017.
 */

public class objectdistfragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.objectdistfragment, container,
                false);
        Button button = (Button) rootView.findViewById(R.id.doocr);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // do something
                Intent intent = new Intent(getActivity(),CameraActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}