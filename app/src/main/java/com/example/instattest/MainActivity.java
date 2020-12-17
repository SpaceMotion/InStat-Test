package com.example.instattest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int LOAD_MATCH_VIDEOS_ERROR_TYPE_ALL = 0;
    private static final int LOAD_MATCH_VIDEOS_ERROR_TYPE_SOME = 1;

    private Button loadButton;
    private EditText matchIdEditText;
    private TextView teamsTextView;
    private TextView dateTextView;
    private TextView tournamentTextView;
    private TextView scoreTextView;
    private LinearLayout videosContainerLayout;
    private TextView videosLoadingTextView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadButton = findViewById(R.id.loadButton);
        matchIdEditText = findViewById(R.id.matchId);
        teamsTextView = findViewById(R.id.teams);
        dateTextView = findViewById(R.id.matchDate);
        tournamentTextView = findViewById(R.id.tournamentName);
        scoreTextView = findViewById(R.id.matchScore);
        videosContainerLayout = findViewById(R.id.videosContainer);
        videosLoadingTextView = findViewById(R.id.videosLoading);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sMatchId = matchIdEditText.getText().toString();
                if (sMatchId.length() > 0) {
                    int matchId = Integer.parseInt(sMatchId);
                    loadMatchInfo(matchId);
                    loadMatchVideos(matchId);
                } else {
                    Toast.makeText(MainActivity.this, R.string.match_info_activity_empty_match_id_error, Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void loadMatchInfo(int id) {
        setLoadingMatchInfo();
        try {
            ApiServiceImpl.getMatch(id, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject matchInfo = new JSONObject(response.body().string());
                        JSONObject tournament = matchInfo.getJSONObject(Constants.MATCH_INFO_KEY_TOURNAMENT);
                        String tournamentNameEng = tournament.getString(Constants.MATCH_INFO_KEY_TOURNAMENT_NAME_ENG);
                        String date = matchInfo.getString(Constants.MATCH_INFO_KEY_DATE);
                        JSONObject team1 = matchInfo.getJSONObject(Constants.MATCH_INFO_KEY_TEAM_1);
                        String team1NameEng = team1.getString(Constants.MATCH_INFO_KEY_TEAM_NAME_ENG);
                        int team1Score = team1.getInt(Constants.MATCH_INFO_KEY_TEAM_SCORE);
                        JSONObject team2 = matchInfo.getJSONObject(Constants.MATCH_INFO_KEY_TEAM_2);
                        String team2NameEng = team2.getString(Constants.MATCH_INFO_KEY_TEAM_NAME_ENG);
                        int team2Score = team2.getInt(Constants.MATCH_INFO_KEY_TEAM_SCORE);

                        teamsTextView.setText(team1NameEng + " vs " + team2NameEng);
                        dateTextView.setText(date);
                        tournamentTextView.setText(tournamentNameEng);
                        scoreTextView.setText(team1Score + " - " + team2Score);
                    } catch (IOException | JSONException e) {
                        clearMatchInfo();
                        showLoadMatchInfoError();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    clearMatchInfo();
                    showLoadMatchInfoError();
                }
            });
        } catch (JSONException e) {
            clearMatchInfo();
            showLoadMatchInfoError();
        }
    }

    private void loadMatchVideos(int matchId) {
        setLoadingMatchVideos();
        clearVideosContainer();
        try {
            ApiServiceImpl.getVideos(matchId, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONArray matchVideos = new JSONArray(response.body().string());
                        boolean unableToLoadSomeVideosError = false;
                        String videoButtonLabel = getResources().getText(R.string.match_info_activity_video_button).toString();
                        for (int i = 0; i < matchVideos.length(); i++) {
                            try {
                                String url = matchVideos.getJSONObject(i).getString(Constants.MATCH_VIDEOS_KEY_URL);
                                Button button = new Button(MainActivity.this);
                                button.setText(videoButtonLabel + " " + (i + 1));
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MainActivity.this, PlayVideoActivity.class);
                                        intent.putExtra(Constants.MATCH_VIDEOS_KEY_URL, url);
                                        startActivity(intent);
                                    }
                                });
                                videosContainerLayout.addView(button);
                            } catch (JSONException e) {
                                unableToLoadSomeVideosError = true;
                            }
                        }
                        clearLoadingMatchVideos();
                        if (unableToLoadSomeVideosError) {
                            showLoadMatchVideosError(LOAD_MATCH_VIDEOS_ERROR_TYPE_SOME);
                        }
                    } catch (IOException | JSONException e) {
                        clearLoadingMatchVideos();
                        showLoadMatchVideosError(LOAD_MATCH_VIDEOS_ERROR_TYPE_ALL);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    clearLoadingMatchVideos();
                    showLoadMatchVideosError(LOAD_MATCH_VIDEOS_ERROR_TYPE_ALL);
                }
            });
        } catch (JSONException e) {
            clearLoadingMatchVideos();
            showLoadMatchVideosError(LOAD_MATCH_VIDEOS_ERROR_TYPE_ALL);
        }
    }

    private void showLoadMatchVideosError(int type) {
        int messageResourceId = -1;
        if (type == LOAD_MATCH_VIDEOS_ERROR_TYPE_ALL) {
            messageResourceId = R.string.load_match_videos_load_all_error;
        } else if (type == LOAD_MATCH_VIDEOS_ERROR_TYPE_SOME) {
            messageResourceId = R.string.load_match_videos_load_specific_error;
        }
        if (messageResourceId != -1) {
            Toast.makeText(this, messageResourceId, Toast.LENGTH_SHORT);
        }
    }

    private void showLoadMatchInfoError() {
        Toast.makeText(this, R.string.load_match_info_error, Toast.LENGTH_SHORT);
    }

    private void setLoadingMatchInfo() {
        teamsTextView.setText(R.string.match_info_activity_info_loading);
        dateTextView.setText(R.string.match_info_activity_info_loading);
        tournamentTextView.setText(R.string.match_info_activity_info_loading);
        scoreTextView.setText(R.string.match_info_activity_info_loading);
    }

    private void clearMatchInfo() {
        teamsTextView.setText("");
        dateTextView.setText("");
        tournamentTextView.setText("");
        scoreTextView.setText("");
    }

    private void setLoadingMatchVideos() {
        videosLoadingTextView.setText(R.string.match_info_activity_info_loading);
    }

    private void clearLoadingMatchVideos() {
        videosLoadingTextView.setText("");
    }

    private void clearVideosContainer() {
        videosContainerLayout.removeAllViews();
    }
}