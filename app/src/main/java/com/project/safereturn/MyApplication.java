package com.project.safereturn;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by thstj on 2017-09-25.
 */

public class MyApplication extends Application {

    public static Http http;

    @Override
    public void onCreate() {
        super.onCreate();

        http = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(Http.class);

    }
}
