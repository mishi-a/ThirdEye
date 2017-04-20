package com.example.hp.opencvtest;

/**
 * Created by Hp on 28-03-2017.
 */

import org.opencv.core.Mat;

public interface Feature {
    public double[] extract (String path);
    public double[] extract (Mat image);
}