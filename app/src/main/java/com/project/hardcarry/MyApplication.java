package com.project.hardcarry;

import android.app.Application;
import android.content.SharedPreferences;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application {

    public static Http http;

    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();

        http = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(Http.class);

        preferences = getSharedPreferences("setting", MODE_PRIVATE);
        editor = preferences.edit();

    }
}
