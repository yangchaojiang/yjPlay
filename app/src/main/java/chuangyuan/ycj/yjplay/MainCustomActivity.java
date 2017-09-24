package chuangyuan.ycj.yjplay;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;


public class MainCustomActivity extends AppCompatActivity {

    private ManualPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    private static final String TAG = "MainDetailedActivity";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coutom);
        videoPlayerView = (VideoPlayerView) findViewById(R.id.exo_play_context_id);
        exoPlayerManager = new ManualPlayer(this, videoPlayerView, new DataSource(getApplication()));
        // exoPlayerManager.setShowVideoSwitch(true);
        //exoPlayerManager.setPlayUri("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4");
        //exoPlayerManager.setPlayUri("/storage/emulated/0/DCIM/Camera/VID_20170717_011150.mp4");
        //String [] test={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
        // String[] name={"超清","高清","标清"};
        //exoPlayerManager.setPlaySwitchUri(test,name);
        //exoPlayerManager.setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.h264");
        exoPlayerManager.setPlayUri("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4");
        // exoPlayerManager.setPlayUri("http://185.73.239.15:25461/live/1/1/924.ts");
        exoPlayerManager.setTitle("自定义视频标题");
        Glide.with(this)
                .load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                .placeholder(R.mipmap.test)
                .fitCenter()
                .into(videoPlayerView.getPreviewImage());
        exoPlayerManager.setVideoInfoListener(new VideoInfoListener() {
            @Override
            public void onPlayStart() {

            }

            @Override
            public void onLoadingChanged() {

            }

            @Override
            public void onPlayerError(ExoPlaybackException e) {

            }

            @Override
            public void onPlayEnd() {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }
        });
        videoPlayerView.getReplayLayout().findViewById(R.id.replay_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomActivity.this, "自定义分享", Toast.LENGTH_SHORT).show();
            }
        });
        videoPlayerView.getErrorLayout().findViewById(R.id.exo_player_error_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomActivity.this, "自定义错误", Toast.LENGTH_SHORT).show();
            }
        });
        videoPlayerView.getPlayHintLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomActivity.this, "自定义提示", Toast.LENGTH_SHORT).show();
            }
        });
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
        exoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (exoPlayerManager.onBackPressed()) {//使用播放返回键监听
            Toast.makeText(MainCustomActivity.this, "返回", Toast.LENGTH_LONG).show();
            finish();
        }

    }


}
