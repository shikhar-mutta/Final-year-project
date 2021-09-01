package com.cam.scan.camscannerclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_GALLERY_REQUEST_CODE = 609;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    ImageView img;

    private String currentPhotoPath = "";

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public boolean check()
    {
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA)==
            PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                    PackageManager.PERMISSION_DENIED)
            {
                String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission,PERMISSION_CODE);
            }
            else
            {
                return true ;
            }
        }
        else
        {
            return true;
        }
        return false;
    }

    public void openImagesDocument(View view) {
        if (check()) {
        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pictureIntent.setType("image/*");
        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = new String[]{"image/jpeg", "image/png"};
            pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(Intent.createChooser(pictureIntent, "Select Picture"), PICK_IMAGE_GALLERY_REQUEST_CODE);
        }
    }

    public void OpenCamera(View view) {

        //
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE,"New Image");
//        values.put(MediaStore.Images.Media.DESCRIPTION,"from the camera");
//        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        if (check()) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file;
            try {
                file = getImageFile(); // 1
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
                imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            else
                imageUri = Uri.fromFile(file); // 3

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
        }
    }
    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        System.out.println(storageDir.getAbsolutePath());
        if (storageDir.exists())
            System.out.println("File exists");
        else
            System.out.println("File not exists");
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== IMAGE_CAPTURE_CODE && resultCode == RESULT_OK){
            imageUri=Uri.parse(currentPhotoPath);
            startCrop(Uri.parse(currentPhotoPath));
//          Uri imagePath = data.getData();
            Log.d("sms","Camera result");
//          openCropActivity(imagePath, imagePath);
        }else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            try {
                Uri sourceUri = data.getData();
                File file = getImageFile();
//                Log.d("sms","Galaryffffffffffffffffffffff");
                imageUri = Uri.fromFile(file);
                openCropActivity(sourceUri, imageUri);
            } catch (Exception e) {
//                Log.d("sms","sasasadaasssssssssssss");
                Toast.makeText(this, "Please select another image", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            Log.d("<>", "onActivityResult: " + imageUri);
            Intent i = new Intent(HomeActivity.this,ScanActivity.class);
            i.setData(imageUri);
            startActivity(i);
        }
//        if(resultCode==RESULT_OK)
//        {
//            Intent i =new Intent(HomeActivity.this,ScanActivity.class);
//            i.setData(imageUri);
//            startActivity(i);
//        }
    }
    private void startCrop(@NonNull Uri uri){

        UCrop uCrop = UCrop.of(uri,uri);
        Log.d("sms","Crop sfsds");
        uCrop.start(HomeActivity.this);
    }


    private void openCropActivity(Uri sourceUri, Uri destinationUri) {

        UCrop.of(sourceUri, destinationUri).start(this);
    }

}
