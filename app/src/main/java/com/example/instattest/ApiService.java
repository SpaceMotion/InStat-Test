package com.example.instattest;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("data")
    Call<ResponseBody> getMatch(@Body RequestBody body);

    @POST("video-urls")
    Call<ResponseBody> getVideos(@Body RequestBody body);
}
