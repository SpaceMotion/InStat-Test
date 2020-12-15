package com.example.instattest;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ApiServiceImpl {
    public static void getMatch(int id, Callback<ResponseBody> callback) throws JSONException {
        ApiService apiService = getApiService();
        JSONObject requestBody = new JSONObject("{\"proc\": \"get_match_info\", \"params\": {\"_p_sport\":1, \"_p_match_id\": " + id + "}}");
        Call<ResponseBody> getMatchCall = apiService.getMatch(RequestBody.create(MediaType.get("application/json"), requestBody.toString()));
        getMatchCall.enqueue(callback);
    }

    public static void getVideos(int matchId, Callback<ResponseBody> callback) throws JSONException {
        ApiService apiService = getApiService();
        JSONObject requestBody = new JSONObject("{\"match_id\": " + matchId + ",\"sport_id\":1}");
        Call<ResponseBody> getVideosCall = apiService.getVideos(RequestBody.create(MediaType.get("application/json"), requestBody.toString()));
        getVideosCall.enqueue(callback);
    }

    private static ApiService getApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API_URL).build();
        ApiService apiService = retrofit.create(ApiService.class);
        return apiService;
    }
}
