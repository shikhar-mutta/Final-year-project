package com.cam.scan.camscannerclone;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cam.scan.camscannerclone.Retrofit.IUploadApi;
import com.cam.scan.camscannerclone.Retrofit.RetrofitClient;
import com.cam.scan.camscannerclone.Utils.Common;
import com.cam.scan.camscannerclone.Utils.IUploadCallbacks;
import com.cam.scan.camscannerclone.Utils.ProgressRequestBody;
import com.google.gson.JsonObject;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class ScanActivity extends AppCompatActivity implements IUploadCallbacks{

    ImageView imageView,btnUpload,Save,Cancel;
    IUploadApi mService;
    Uri selectedFileUri;
    ProgressDialog dialog;

    private IUploadApi getApiUpload()
    {
        return RetrofitClient.getClient().create(IUploadApi.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mService = getApiUpload();

        imageView = (ImageView)findViewById(R.id.image_view);
        btnUpload = (ImageView)findViewById(R.id.button_upload);
        Save = (ImageView)findViewById(R.id.save);
        Cancel = (ImageView)findViewById(R.id.cancel);

        Uri image_uri = getIntent().getData();
        imageView.setImageURI(image_uri);
        selectedFileUri = image_uri;

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });

    }

    private void uploadFile() {
        if (selectedFileUri != null) {
            dialog = new ProgressDialog(ScanActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Uploading..");
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setCancelable(false);
            dialog.show();


            File file = null;
            Log.d("<>", "uploadFile: " + selectedFileUri);
//                file = new File(Common.getFilePath(this, selectedFileUri));

                file = new File(selectedFileUri.getPath());


            if (file != null) {

                final ProgressRequestBody requestBody = new ProgressRequestBody(this, file);

                final MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mService.uploadFile(body)
                                .enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        Log.d("<>", "onResponse: " + response);
                                        if (response.isSuccessful()) {
                                            JsonObject responseString = response.body();
                                            Intent i = new Intent(ScanActivity.this,Summarization.class);
                                            i.putExtra("ss",responseString.get("summary").getAsString());
                                            startActivity(i);

                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                        Toast.makeText(ScanActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).start();

            }
        }
        else {
            Toast.makeText(this, "Cannot upload this file..", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onProgressUpdate(int percent) {

    }
}
