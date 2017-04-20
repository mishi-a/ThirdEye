package com.example.hp.opencvtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    TextToSpeech t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)this.findViewById(R.id.imageView1);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    Locale locale = new Locale("en", "IN");
                    int availability = t1.isLanguageAvailable(locale);
                    switch (availability) {
                        case TextToSpeech.LANG_NOT_SUPPORTED: {
                            t1.setLanguage(Locale.UK);
                            break;
                        }
                        case TextToSpeech.LANG_MISSING_DATA: {
                            t1.setLanguage(Locale.UK);
                            break;
                        }
                        case TextToSpeech.LANG_AVAILABLE: {
                            t1.setLanguage(Locale.UK);
                            break;
                        }
                        case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                        case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE: {
                            t1.setLanguage(locale);
                            break;
                        }
                    }
                }
            }
        });
    }
    public void  search(View v){
        EditText g = (EditText) findViewById(R.id.newText);
        String x = g.getText().toString();
        if (x.matches("")) {
            Toast.makeText(this, "You did not enter anything", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = Uri.parse("http://www.google.com/#q=" + x);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public void getColor(View v){
        Intent intent = new Intent(this,color_detection.class);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }
    public void  detectfeature(View v){
        Intent intent = new Intent(this,OCRActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }
    public void  voice(View v){
        EditText g = (EditText) findViewById(R.id.newText);
        String x = g.getText().toString();
        if (x.matches("")) {
            Toast.makeText(this, "You did not enter anything", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getApplicationContext(), x,Toast.LENGTH_SHORT).show();
        t1.speak(x, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void doOCR(View v){
        Intent intent = new Intent(this,OCRActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }
    @Override
    public void onDestroy() {
// Don't forget to shutdown tts!
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onDestroy();
    }

    public void  click(View v){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }


}