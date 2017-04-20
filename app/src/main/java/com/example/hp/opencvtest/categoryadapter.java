package com.example.hp.opencvtest;

/**
 * Created by Hp on 27-03-2017.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Hp on 12-03-2017.
 */

public class categoryadapter extends FragmentPagerAdapter {
    private Context mContext;
    public categoryadapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return "COLOR DETECTION";
        else if(position == 1)
            return "TEXT RECOGNITION";
        else if(position == 2)
            return  "FEATURE DETECTION";
        else if(position == 3)
            return "IMAGE MATCHING";
        else
            return "OBJECT RECOGNITION";
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return new checkcolorefragment();
        else if(position == 1)
            return new ocrfragment();
        else if(position == 2)
            return new featuredetectionfragment();
        else if(position == 3)
            return new imagematchingfragment();
        else
            return new imagerecognitionfragment();
    }

    @Override
    public int getCount() {
        return 5;
    }
}