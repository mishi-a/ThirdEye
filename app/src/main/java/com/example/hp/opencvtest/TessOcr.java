package com.example.hp.opencvtest;


import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
/**
 * Created by Hp on 04-03-2017.
 */
public class TessOCR extends TessBaseAPI {
    //TessBaseAPI = Java interface for the Tesseract OCR engine.
    private TessBaseAPI mTess;

    public TessOCR() {
        // TODO Auto-generated constructor stub
        mTess = new TessBaseAPI();
        //String datapath = Environment.getExternalStorageDirectory() + "/tesseract/";
        final String datapath = "/storage/emulated/0/tesseract/";
        String language = "eng";
        File dir = new File(datapath + "tessdata/");
        if (!dir.exists())
            dir.mkdirs();
        //Initializes the Tesseract engine with a specified language model. Returns true on success.
        mTess.init(datapath, language);
    }

    public String getOCRResult(Bitmap bitmap) {

        mTess.setImage(bitmap);
       // Provides an image for Tesseract to recognize. Copies the image buffer. The source image
        // may be destroyed immediately after SetImage is called. SetImage clears all recognition
        // results, and sets the rectangle to the full image, so it may be followed immediately by
        // a GetUTF8Text, and it will automatically perform recognition.
        //The recognized text is returned as a String which is coded as UTF8.
        String result = mTess.getUTF8Text();
        String[] arr = result.split(" ");
        for ( String ss : arr) {


        }
        return result;
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }

}