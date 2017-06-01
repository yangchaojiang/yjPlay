package chuangyuan.ycj.yjplay;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;



public class MainActivity extends Activity {

    private ExoUserPlayer exoPlayerManager;
    private static final String TAG = "MainActivity";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
       exoPlayerManager = new ExoUserPlayer(this, "http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4");
        //  exoPlayerManager = new ManualPlayer(this, "storage/emulated/0/demo-170322.mp4");
          exoPlayerManager = new ExoUserPlayer(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/vr_video_1.mp4");
        //exoPlayerManager = new ManualPlayer(this,"ftp://g:g@tv.dl1234.com:2121/不懂撒娇的女人粤语01.mkv");
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long position = exoPlayerManager.getPlayer().getCurrentPosition();
                Log.d(TAG, "position:" + position);
                Log.d(TAG, "position:new:" + (position + 100));
                exoPlayerManager.getPlayer().seekTo((position + 100));
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //  Log.d(TAG, UtilityAdapter.FFmpegVideoGetInfo("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"));

    }

    private  void  get(){


        DefaultHttpDataSourceFactory dataSourceFactory=      new DefaultHttpDataSourceFactory(Util.getUserAgent(this,"yjPlay"), new DefaultBandwidthMeter());
        DefaultExtractorsFactory dataSourceFactory1=new DefaultExtractorsFactory();
        final ExtractorMediaSource sampleSource =   new ExtractorMediaSource(Uri.parse( Environment.getExternalStorageDirectory().getAbsolutePath() + "/vr_video_1.mp4"),
                dataSourceFactory,dataSourceFactory1,null,null);
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        DefaultTrackSelector  selector=new DefaultTrackSelector();
        SimpleExoPlayer simpleExoPlayer= ExoPlayerFactory.newSimpleInstance(this,trackSelector);
        simpleExoPlayer.prepare(sampleSource);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        exoPlayerManager.onStart();
        Log.d(TAG, "onStart");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    protected void onNewIntent(Intent intent) {

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        exoPlayerManager.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
