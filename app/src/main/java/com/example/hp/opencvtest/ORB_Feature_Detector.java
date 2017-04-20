package com.example.hp.opencvtest;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ORB_Feature_Detector extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OCVSample::Activity";
    private int w, h;
    private CameraBridgeViewBase mOpenCvCameraView;
    TextView tvName;
    Scalar RED = new Scalar(255, 0, 0);
    Scalar GREEN = new Scalar(0, 255, 0);
    //Abstract base class for 2D image feature detectors.
    /*
    A feature is a point that islikely to maintain a similar appearance when viewed from different
    distances or angles. For example, corners often have this characteristic.
     */
    FeatureDetector detector;
    /*
     A descriptor is a vector of data about a feature. Some features are not suitable for generating
     a descriptor, so an image has fewer descriptors than features.
    */
    //Abstract base class for computing descriptors for image keypoints.
    DescriptorExtractor descriptor;
    /*
    Find matches between the two sets of descriptors. If we imagine the descriptors as points in a
    multidimensional space, a match is defned in terms of some measure of distance between points.
    Descriptors that are close enough to each other are considered a match.
     */
    DescriptorMatcher matcher;
    //data type to store image pixel characteristics
    Mat descriptors2,descriptors1;
    Mat img1;
    //KeyPoint stores salient points description. It stores x, y, angle, size etc.
    //Matrix of key point
    MatOfKeyPoint keypoints1,keypoints2;

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }
    //ASync initializtion of OpenCV
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    try {
                        initializeOpenCVDependencies();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    //Initializing opencv various data type
    private void initializeOpenCVDependencies() throws IOException {
        //Enabling camera view
        mOpenCvCameraView.enableView();
        /*
         Class implementing the ORB (oriented BRIEF) keypoint detector and descriptor extractor,
         described in [RRKB11]. The algorithm uses FAST in pyramids to detect stable keypoints,
         selects the strongest features using FAST or Harris response, finds their orientation
         using first-order moments and computes the descriptors using BRIEF (where the coordinates
         of random point pairs (or k-tuples) are rotated according to the measured orientation).
        */
        //Creates a feature detector by its name.
        detector = FeatureDetector.create(FeatureDetector.ORB);
        //Creates a descriptor extractor by name.
        descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        //BruteForceHamming uses It takes the descriptor of one feature in first set and is matched
        // with all other features in second set using some distance calculation. And the closest
        // one is returned.
        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        img1 = new Mat();
        AssetManager assetManager = getAssets();
        InputStream istr = assetManager.open("a.jpeg");
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        //Converting Bitmap to Mat
        Utils.bitmapToMat(bitmap, img1);
        //Converting image to grayscale
        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGB2GRAY);
        img1.convertTo(img1, 0); //converting the image to match with the type of the cameras image
        descriptors1 = new Mat();
        keypoints1 = new MatOfKeyPoint();
        //Detects keypoints in an image (first variant) or image set (second variant).
        detector.detect(img1, keypoints1);
        //Computes the descriptors for a set of keypoints detected in an image (first variant) or
        // image set (second variant).
        descriptor.compute(img1, keypoints1, descriptors1);
    }


    public ORB_Feature_Detector() {

        Log.d(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        //Keep Screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.feature_detector_layout);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        tvName = (TextView) findViewById(R.id.text1);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        w = width;
        h = height;
    }

    public void onCameraViewStopped() {
        img1.release();
    }

    public List<DMatch> match(Mat descriptors,Mat dupdescriptors){
        Log.d(TAG, "match");
        Log.d(TAG, "match");
        MatOfDMatch matches = new MatOfDMatch();
        Log.d(TAG, "match");
        matcher.match(descriptors, dupdescriptors, matches);

        Log.d(TAG, "match");
        List<DMatch> matchesList = matches.toList();

        Double max_dist = 0.0;
        Double min_dist = 100.0;

        for (int i = 0; i < matchesList.size(); i++) {
            Double dist = (double) matchesList.get(i).distance;
            if (dist < min_dist)
                min_dist = dist;
            if (dist > max_dist)
                max_dist = dist;
        }
        Log.d(TAG, "match");
        List<DMatch> good_matches = new ArrayList<DMatch>();
        for (int i = 0; i < matchesList.size(); i++) {
            if (matchesList.get(i).distance <= (1.5 * min_dist))
                good_matches.add(matchesList.get(i));
        }
        Log.d(TAG, "match");
        Log.d(TAG,"result" + good_matches.size());
        return  good_matches;
    }
    public Mat recognize(Mat aInputFrame,int flag) {

        if(flag == 1)
            return new Mat();
        Imgproc.cvtColor(aInputFrame, aInputFrame, Imgproc.COLOR_RGB2GRAY);
        descriptors2 = new Mat();
        keypoints2 = new MatOfKeyPoint();
        detector.detect(aInputFrame, keypoints2);
        descriptor.compute(aInputFrame, keypoints2, descriptors2);

        // Matching
        MatOfDMatch matches = new MatOfDMatch();
        if (descriptors1.type() == descriptors2.type() && descriptors1.cols() == descriptors2.cols()) {
            matcher.match(descriptors1, descriptors2, matches);
        } else {
            return aInputFrame;
        }
        List<DMatch> matchesList = matches.toList();

        Double max_dist = 0.0;
        Double min_dist = 100.0;

        for (int i = 0; i < matchesList.size(); i++) {
            Double dist = (double) matchesList.get(i).distance;
            if (dist < min_dist)
                min_dist = dist;
            if (dist > max_dist)
                max_dist = dist;
        }

        LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
        for (int i = 0; i < matchesList.size(); i++) {
            if (matchesList.get(i).distance <= (1.5 * min_dist))
                good_matches.addLast(matchesList.get(i));
        }

        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(good_matches);
        Mat outputImg = new Mat();
        MatOfByte drawnMatches = new MatOfByte();
        if (aInputFrame.empty() || aInputFrame.cols() < 1 || aInputFrame.rows() < 1) {
            return aInputFrame;
        }
        Features2d.drawMatches(img1, keypoints1, aInputFrame, keypoints2, goodMatches, outputImg, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);
        Imgproc.resize(outputImg, outputImg, aInputFrame.size());

        return outputImg;
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return recognize(inputFrame.rgba(),0);

    }
}