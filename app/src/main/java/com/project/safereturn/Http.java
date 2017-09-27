package com.project.safereturn;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by thstj on 2017-09-25.
 */

public interface Http {

    @GET("json")
    Call<JsonObject> getAddress(@Query("latlng") String latlng, @Query("api") String api, @Query("language") String language);

}
