package com.example.hp.opencvtest;

/**
 * Created by Hp on 08-03-2017.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Locale;

public class color_detection extends AppCompatActivity implements OnTouchListener, CvCameraViewListener2 {
    //CameraBridgeViewBase basic class, implementing the interaction with Camera and OpenCV library
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mRgba;
    //Scalar like array for multi channel image
    private Scalar mBlobColorHsv;
    private Scalar mBlobColorRgba;
    private Color_util detectcolor ;
    TextView touch_coordinates;
    TextView touch_color;
    TextToSpeech t1;
    double x = -1;
    double y = -1;

    //Loading opencv library
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    /*
                     enable the camera connection.
                     onCameraViewStarted callback will be delivered only after both this method is
                     called and surface is available
                     */
                    mOpenCvCameraView.enableView();
                    //setOnTouchListener = to get co-ordinates of screen where you touch the screen
                    mOpenCvCameraView.setOnTouchListener(color_detection.this);
                }
                break;
                default: {
                    //Caliing parent method
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting the layout for this activity
        setContentView(R.layout.activity_color_detection);
        //To prevent screen from getting off
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //show the X,Y co-ordinate of touch
        touch_coordinates = (TextView) findViewById(R.id.touch_coordinates);
        //Color of the location where you touch
        touch_color = (TextView) findViewById(R.id.touch_color);
        //Finding opencvcamera view
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_tutorial_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        //setting listener for opencvcamera view
        mOpenCvCameraView.setCvCameraViewListener(this);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {

                    t1.setLanguage(Locale.UK);

                }
            }
        });
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
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    /*
    This method is invoked when camera preview has started. After this method is invoked the frame
    will start to be delivered to client via the onCameraFrame() callback.
    width - - the width of the frames that will be delivered
    height - - the height of the frames that will be delivered
     */
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        //8 bits per pixel and so range of [0:255].
        //Scalar color = new Scalar( 255 ) CV_8UC1- grayscale image(single channel)
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
    }
    /*
    This method is invoked when camera preview has been stopped for some reason. No frames will be
    delivered via onCameraFrame() callback after this method is called.
     */
    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }
    /*
    This method is invoked when delivery of the frame needs to be done. The returned values - is a
     modified frame which needs to be displayed on the screen.
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //RGBA color value is specified with: rgba(red, green, blue, alpha). The alpha parameter is
        // a number between 0.0 (fully transparent) and 1.0 (fully opaque).
        //if we change rgba to gray camera shows gray image i.e oncamera frame returned image displayed
        //on camera.
        mRgba = inputFrame.rgba();
        return mRgba;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /*
        Getting the the number of rows and columns or (-1, -1) when the matrix has more than 2 dimensions
         */
        int cols = mRgba.cols();
        int rows = mRgba.rows();
        /*
        getHeight() = The height of your view, in pixels.
        getwidth()  = The Width of your view, in pixels.
         */
        double yLow = (double)mOpenCvCameraView.getHeight() * 0.2401961;
        double yHigh = (double)mOpenCvCameraView.getHeight() * 0.7696078;
        //Finding scale in X and Y direction
        double xScale = (double)cols / (double)mOpenCvCameraView.getWidth();
        double yScale = (double)rows / (yHigh - yLow);
       // double yScale = (double)rows / (double)mOpenCvCameraView.getHeight();
        /*
        getX() and getY() are used to get the X/Y coordinates of the touch interaction.
         */
        x = event.getX();
        y = event.getY();
        y = y - yLow;
        //Finding Exact coordinate
        x = x * xScale;
        y = y * yScale;

        if((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        touch_coordinates.setText("X: " + Double.valueOf(x) + ", Y: " + Double.valueOf(y));

        Rect touchedRect = new Rect();

        touchedRect.x = (int)x;
        touchedRect.y = (int)y;
        //returns the rectangle's width. This does not check for a valid rectangle
        // (i.e. left <= right) so the result may be negative.
        touchedRect.width = 8;

        //returns the rectangle's height. This does not check for a valid rectangle
        // (i.e. top <= bottom so the result may be negative.
         touchedRect.height = 8;
        /*
         IMPORTANT =  can change 176 to 185 to = Rect touchedRect = new Rect((int)x,(int)y,8,8);
         */
        //Extracts a rectangular submatrix from mrgba Mat to touchedRegionRgba
        Mat touchedRegionRgba = mRgba.submat(touchedRect);
        Mat touchedRegionHsv = new Mat();
        //Converting image from RGBA to HSV
        /*
          HSV is so named for three values—Hue, Saturation and Value. This color space describes
          colors (hue or tint) in terms of their shade (saturation or amount of gray) and their
          brightness value.
          HSV is defined in a way that is similar to how humans perceive color.
         */
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);
        // Calculate average color of touched region
        /*
        core.SumElems = Calculates the sum of array elements.The functions sum calculate and return
         the sum of array elements, independently for each channel.
         */
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width * touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = convertScalarHsv2Rgba(mBlobColorHsv);
        //Set text by converting array of byte to hexadecimal
        /*touch_color.setText("Color: #" + String.format("%02X", (int)mBlobColorRgba.val[0])
                + String.format("%02X", (int)mBlobColorRgba.val[1])
                + String.format("%02X", (int)mBlobColorRgba.val[2]));
        */
        touch_color.setTextColor(Color.rgb((int) mBlobColorRgba.val[0],
                (int) mBlobColorRgba.val[1],
                (int) mBlobColorRgba.val[2]));
        touch_coordinates.setTextColor(Color.rgb((int)mBlobColorRgba.val[0],
                (int)mBlobColorRgba.val[1],
                (int)mBlobColorRgba.val[2]));
        detectcolor = new Color_util();
        String color = detectcolor.getColorNameFromRgb((int)mBlobColorRgba.val[0],(int)mBlobColorRgba.val[1],(int)mBlobColorRgba.val[2]);
        touch_color.setText(color);
        voice(color);
        return false;
    }
    public void  voice(String x){
        if (x.matches("")) {
                 return;
        }
        t1.speak(x, TextToSpeech.QUEUE_FLUSH, null);
    }

    private Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        /*
        This is an overloaded member function, provided for convenienc.
        rows =	Number of rows in a 2D array.
        cols =	Number of columns in a 2D array.
        type =	Array type. Use CV_8UC1, ..., CV_64FC4 to create 1-4 channel matrices, or CV_8UC(n),
         ..., CV_64FC(n) to create multi-channel (up to CV_CN_MAX channels) matrices.
         scalar = 	An optional value to initialize each matrix element with.
         */
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        //converting HSV to RGB
        /*
          The function converts an input image from one color space to another. In case of a
          transformation to-from RGB color space, the order of the channels should be specified
          explicitly (RGB or BGR). Note that the default color format in OpenCV is often referred to
          as RGB but it is actually BGR (the bytes are reversed). So the first byte in a standard
         (24-bit) color image will be an 8-bit Blue component, the second byte will be Green, and
          the third byte will be Red. The fourth, fifth, and sixth bytes would then be the second
          pixel (Blue, then Green, then Red), and so on.
          ---here integer 4 = dstCn - Number of channels in the destination image. If the parameter is
          0, the number of the channels is derived automatically from src and code.
         */
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
}