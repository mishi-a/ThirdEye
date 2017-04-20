package com.example.hp.opencvtest.objectdist;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hp on 28-03-2017.
 */
public class Proc {
    private static String LOG_TAG = "RIZ: class Proc: ";
    private CameraBridgeViewBase mOpenCvCameraView;
    private static int pic_count = 0;

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

    public static  double findMarkerWidth(String imgPath){
        Mat frame = Imgcodecs.imread(imgPath);
        Mat gscale = new Mat();
        Mat blur = new Mat();
        Mat edged = new Mat();

        // convert the image to grayscale, blur it, and detect edges
        if(frame.channels()>1)
            Imgproc.cvtColor(frame, gscale, Imgproc.COLOR_BGR2GRAY);
        else
            gscale = frame;

        Imgproc.GaussianBlur(gscale, blur, new Size(5, 5), 0);
        Imgproc.Canny(blur, edged, 35, 125);

        // find the contours in the edged image and keep the largest one;
        // we'll assume that this is our piece of paper in the image
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat(edged.width(), edged.height(), CvType.CV_8UC1);
        Imgproc.findContours(edged.clone(), contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        int max_idx = 0;

        // if any contour exist...
        if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
        {
            double max_area = 0;
            double area;
            // find the contour with largest area
            for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
            {
                area = Imgproc.contourArea(contours.get(idx));
                if(area > max_area){
                    max_area = area;
                    max_idx = idx;
                }
                Imgproc.drawContours(frame, contours, idx, new Scalar(0, 0, 255));
            }

            //Riz: Save File
            //Imgproc.drawContours(frame, contours, max_idx, new Scalar(250, 0, 0));
            byte[] bytes = new byte[ frame.rows() * frame.cols() * frame.channels() ];


            File file = new File(CameraActivity.activity.getExternalFilesDir(null), "pic_contour"+ Integer.toString(pic_count) + ".jpg");
            pic_count++;

            Boolean bool = null;
            String filename = file.toString();
            bool = Imgcodecs.imwrite(filename, frame);

            if (bool == true)
                Log.d(LOG_TAG, "SUCCESS writing image to external storage");
            else
                Log.d(LOG_TAG, "Fail writing image to external storage");

            Log.i(LOG_TAG, "Max Area: " + Double.toString(max_area));
        }
        else{
            Log.e(LOG_TAG, "No Contour Found!");
        }

        MatOfPoint2f newPoint = new MatOfPoint2f(contours.get(max_idx).toArray());

        return Imgproc.arcLength(newPoint, true);
    }

    public static double distanceToImage(double focalLength, double knownWidth, double pixelsPerWidth){
        return (knownWidth * focalLength) / pixelsPerWidth;
    }
}