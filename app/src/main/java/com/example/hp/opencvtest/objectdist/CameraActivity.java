package com.example.hp.opencvtest.objectdist;

/**
 * Created by Hp on 28-03-2017.
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.hp.opencvtest.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;


public class CameraActivity extends Activity {
    private final String RIZ_TAG = "Riz: CamActivity: ";

    public static Activity activity;

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(RIZ_TAG, "OpenCV loaded successfully");
                    // Create and set View
                    setContentView(R.layout.objectdist_activity_camera);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, Camera2BasicFragment.newInstance())
                            .commit();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        /*RIZ: To check if opencv is loaded. If not exit app*/
        Log.i(RIZ_TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mOpenCVCallBack))
        {
            Log.e(RIZ_TAG, "Cannot connect to OpenCV Manager");
        }

        setContentView(R.layout.objectdist_activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }

}
