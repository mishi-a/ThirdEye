package com.example.hp.opencvtest.objectdetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.hp.opencvtest.R;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

//import com.clarifai.android.starter.api.v2.App;
//import com.clarifai.android.starter.api.v2.R;

public final class RecognizeConceptsActivity extends BaseActivity {

    public static final int PICK_IMAGE = 100;
    int current_mode = 1;
    private String mCurrentPhotoPath;

    // the list of results that were returned from the API
    @BindView(R.id.resultsList) RecyclerView resultsList;

    // the view where the image the user selected is displayed
    @BindView(R.id.image) ImageView imageView;

    // switches between the text prompting the user to hit the FAB, and the loading spinner
    @BindView(R.id.pbar) ProgressBar switcher;

    // the FAB that the user clicks to select an image
    @BindView(R.id.fab) View fab;

    @NonNull private final RecognizeConceptsAdapter adapter = new RecognizeConceptsAdapter();

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switcher.setVisibility(GONE);

    }

    @Override protected void onStart() {
        super.onStart();
        resultsList.setLayoutManager(new LinearLayoutManager(this));
        resultsList.setAdapter(adapter);
    }

    @OnClick(R.id.fab)
    void pickImage() {

        Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(i, PICK_IMAGE);
        //startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMAGE);
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch(requestCode) {
            case PICK_IMAGE:
                Bundle extras = data.getExtras();
                Bitmap picture = (Bitmap) extras.get("data");
                imageView.setImageBitmap(picture);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                assert null != picture;
                picture.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                final byte[] imageBytes = stream.toByteArray();
                // imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                onImagePicked(imageBytes);
                break;
        }
    }

    private void onImagePicked(@NonNull final byte[] imageBytes) {
        // Now we will upload our image to the Clarifai API
        setBusy(true);
        // Make sure we don't show a list of old concepts while the image is being uploaded
        adapter.setData(Collections.<Concept>emptyList());

        new AsyncTask<Integer, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
            @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Integer... params) {
                // The default Clarifai model that identifies concepts in images
                if(current_mode == 0){
                    final ConceptModel generalModel = App.get().clarifaiClient().getDefaultModels().generalModel();
                     return generalModel.predict()
                          .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
                         .executeSync();
                }
                //final ConceptModel generalModel = App.get().clarifaiClient().getDefaultModels().generalModel();

                // Use this model to predict, with the image that the user just selected as the input
               // return generalModel.predict()
                 //       .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
                   //     .executeSync();
                 /*return App.get().clarifaiClient().predict("ThirdEye")
                        .withInputs(
                                ClarifaiInput.forImage(ClarifaiImage.of("imageBytes"))
                        )
                        .executeSync();*/
                else{
                    return App.get().clarifaiClient().getModelByID("ThirdEye").executeSync().get().asConceptModel().predict()
                            .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
                            .executeSync();
                }

            }

            @Override protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                setBusy(false);
                if (!response.isSuccessful()) {
                    showErrorSnackbar(R.string.error_while_contacting_api);
                    return;
                }
                final List<ClarifaiOutput<Concept>> predictions = response.get();
                if (predictions.isEmpty()) {
                    showErrorSnackbar(R.string.no_results_from_api);
                    return;
                }
                adapter.setData(predictions.get(0).data());
                //imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            }

            private void showErrorSnackbar(@StringRes int errorString) {
                Snackbar.make(
                        root,
                        errorString,
                        Snackbar.LENGTH_INDEFINITE
                ).show();
            }
        }.execute();
    }


    @Override protected int layoutRes() { return R.layout.activity_recognize; }

    private void setBusy(final boolean busy) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                //switcher.setDisplayedChild(busy ? 1 : 0);
                switcher.setVisibility(busy?VISIBLE:GONE);
                //imageView.setVisibility(busy ? GONE : VISIBLE);
                fab.setEnabled(!busy);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SharedPreferences settings = getSharedPreferences("settings", 0);
        boolean isChecked = settings.getBoolean("checkbox", false);
        MenuItem item = menu.findItem(R.id.menu_red);
        item.setChecked(true);
        MenuItem item1 = menu.findItem(R.id.menu_green);
       // item1.setChecked(isChecked);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_red:
                if (item.isChecked()) item.setChecked(false);
                else {
                    item.setChecked(true);
                    current_mode = 1;
                    SharedPreferences settings = getSharedPreferences("settings", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("checkbox", item.isChecked());
                    editor.apply();
                }
                return true;
            case R.id.menu_green:
                if (item.isChecked()) item.setChecked(false);
                else {
                    item.setChecked(true);
                    current_mode = 0;
                    SharedPreferences settings = getSharedPreferences("settings", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("checkbox2", item.isChecked());
                    editor.apply();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
