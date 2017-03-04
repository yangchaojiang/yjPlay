package chuangyuan.ycj.yjplay;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;


import chuangyuan.ycj.videolibrary.video.ManualPlayer;


public class MainActivity extends Activity {

    private ManualPlayer exoPlayerManager;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        exoPlayerManager = new ManualPlayer(this,getString(R.string.url_hls));
    }

    @Override
    public void onStart() {
        super.onStart();
        exoPlayerManager.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        exoPlayerManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        exoPlayerManager.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        exoPlayerManager.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        exoPlayerManager.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        exoPlayerManager.onBackPressed();
    }
}
