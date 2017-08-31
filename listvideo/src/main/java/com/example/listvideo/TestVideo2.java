package com.example.listvideo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlaybackException;
import chuangyuan.ycj.videolibrary.utils.VideoInfoListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.GestureVideoPlayer;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * Created by yangc on 2017/7/22.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class TestVideo2 extends AppCompatActivity {
    public static final String TAG = "TestVideo2";
    ManualPlayer userPlayer2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_video2);
        userPlayer2 = new ManualPlayer(this, R.id.player_view_exo);
        userPlayer2.setPlayUri("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4");
        userPlayer2.playVideo();

    }
    @Override
    protected void onResume() {
        super.onResume();
        userPlayer2.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userPlayer2.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userPlayer2.onDestroy();
    }

    @Override
    public void onBackPressed() {
        userPlayer2.onBackPressed();
    }
}
