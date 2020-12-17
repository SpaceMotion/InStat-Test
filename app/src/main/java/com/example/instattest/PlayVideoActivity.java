package com.example.instattest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PlayVideoActivity extends AppCompatActivity {
    private VideoView mVideoView;
    private int mCurrentPosition = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        mVideoView = findViewById(R.id.videoView);
        initVideoView();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.PLAY_VIDEO_STATE_KEY_POSITION, mCurrentPosition);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int currentPosition = savedInstanceState.getInt(Constants.PLAY_VIDEO_STATE_KEY_POSITION);
        if (currentPosition > 0) {
            mCurrentPosition = currentPosition;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeOnPosition(mCurrentPosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurrentPosition = mVideoView.getCurrentPosition();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVideoView.suspend();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

    private void resumeOnPosition(int currentPosition) {
        mVideoView.seekTo(currentPosition != -1 ? currentPosition : 0);
        mVideoView.start();
    }

    private void initVideoView() {
        Intent intent = getIntent();
        String url = intent.getStringExtra(Constants.MATCH_VIDEOS_KEY_URL);
        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(mVideoView);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        mVideoView.setVideoPath(url);
    }
}
