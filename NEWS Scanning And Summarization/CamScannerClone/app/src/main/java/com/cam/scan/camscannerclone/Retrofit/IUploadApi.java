package com.cam.scan.camscannerclone.Retrofit;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IUploadApi {

    @Multipart
    @POST("/upload")
    Call<JsonObject> uploadFile(@Part MultipartBody.Part file);
}
