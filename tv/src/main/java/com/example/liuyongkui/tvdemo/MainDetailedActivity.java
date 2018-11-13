package com.example.liuyongkui.tvdemo;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;

import chuangyuan.ycj.videolibrary.listener.OnCoverMapImageListener;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;

public class MainDetailedActivity extends AppCompatActivity {

    private ExoUserPlayer exoPlayerManager;
    private Movie mSelectedMovie;
    private static final String TAG = "MainDetailedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        mSelectedMovie = (Movie) getIntent().getSerializableExtra(DetailsActivity.MOVIE);
        //实例化
        exoPlayerManager = new VideoPlayerManager.Builder(this, VideoPlayerManager.TYPE_PLAY_GESTURE, R.id.exo_play_context_id)
                .setDataSource(new Data2Source(this))
                //设置视频标题
                .setTitle(mSelectedMovie.getTitle())
                .setPlayerGestureOnTouch(true)
                // .setPlayUri("/storage/sdcard0/DCIM/Camera/VID_20180829_100348.mp4")
                //加载rtmp 协议视频
                //加载m3u8
                 .setPlayUri( "http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8")
                .addOnWindowListener(new VideoWindowListener() {
                    @Override
                    public void onCurrentIndex(int currentIndex, int windowCount) {
                        Toast.makeText(getApplication(), currentIndex + "windowCount:" + windowCount, Toast.LENGTH_SHORT).show();
                    }
                })
                /* .addUpdateProgressListener(new AnimUtils.UpdateProgressListener() {
                     @Override
                     public void updateProgress(long position, long bufferedPosition, long duration) {
 //                     //   Log.d(TAG,"bufferedPosition:"+position);
                     //    Log.d(TAG,"duration:"+duration);
                     }
                 })*/
                .addVideoInfoListener(new VideoInfoListener() {

                    @Override
                    public void onPlayStart(long currPosition) {

                    }

                    @Override
                    public void onLoadingChanged() {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException e) {

                    }

                    @Override
                    public void onPlayEnd() {
                        // Toast.makeText(getApplication(), "asd", Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void isPlaying(boolean playWhenReady) {

                    }
                })
                .setOnCoverMapImage(new OnCoverMapImageListener() {
                    @Override
                    public void onCoverMap(ImageView v) {
                      Glide.with(v.getContext())
                                .load(mSelectedMovie.getCardImageUrl())
                                .fitCenter()
                                .into(v);
                    }
                })
                .create();
        exoPlayerManager.startPlayer();
        //播放视频
        //d隐藏控制布局
        // exoPlayerManager.hideControllerView();
        //隐藏进度条
        // exoPlayerManager.hideSeekBar();
        //显示进度条
        //exoPlayerManager.showSeekBar();
        //是否播放
        // exoPlayerManager.isPlaying();
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
    protected void onDestroy() {
        exoPlayerManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        exoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换
    }

    @Override
    public void onBackPressed() {
        if (exoPlayerManager.onBackPressed()) {
            ActivityCompat.finishAfterTransition(this);
        }
    }

    public static void sss() {

    }
}
