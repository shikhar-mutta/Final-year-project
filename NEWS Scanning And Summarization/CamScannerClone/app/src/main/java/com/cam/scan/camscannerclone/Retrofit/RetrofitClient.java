package com.cam.scan.camscannerclone.Retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofitclient = null;
    public static Retrofit getClient()
    {
        if(retrofitclient==null)
        {
            OkHttpClient httpclient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30,TimeUnit.SECONDS)
                    .writeTimeout(15,TimeUnit.SECONDS)
                    .build();

            retrofitclient = new Retrofit.Builder()
                    .baseUrl("http://192.168.0.107:5000")
                    .client(httpclient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofitclient;
    }
}
