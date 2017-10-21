package chuangyuan.ycj.yjplay;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;

import chuangyuan.ycj.videolibrary.listener.LoadModelType;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;


public class MainCustomActivity extends AppCompatActivity {

    private ManualPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    public static final String VIEW_NAME_HEADER_IMAGE = "123";
    private static final String TAG = "MainDetailedActivity";
    private boolean isOnclick = false;
    private long currPosition = 0;
    private boolean isEnd;
    private String url = "";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coutom);
        isOnclick = getIntent().getBooleanExtra("isOnclick", false);
        currPosition = getIntent().getLongExtra("currPosition", 0);
        url = getIntent().getStringExtra("uri");
        videoPlayerView = (VideoPlayerView) findViewById(R.id.exo_play_context_id);
        ViewCompat.setTransitionName(videoPlayerView, VIEW_NAME_HEADER_IMAGE);
        exoPlayerManager = new ManualPlayer(this, videoPlayerView, new DataSource(getApplication()));
        exoPlayerManager.setPosition(currPosition);
        exoPlayerManager.setTitle("自定义视频标题");
        //设置加载显示模式
        exoPlayerManager.setLoadModel(LoadModelType.SPEED);

        exoPlayerManager.setPlayUri(url);
        if (currPosition > 0) {
            exoPlayerManager.startPlayer();
        }
        Glide.with(this)
                .load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                .placeholder(R.mipmap.test)
                .fitCenter()
                .into(videoPlayerView.getPreviewImage());

        //自定义布局使用
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
        //
        if (isOnclick) {
            exoPlayerManager.setOnPlayClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainCustomActivity.this, "定义点击播放事件", Toast.LENGTH_LONG).show();
                    //处理业务操作 完成后，
                    exoPlayerManager.startPlayer();//开始播放
                }
            });
        }
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
                isEnd = true;
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void isPlaying(boolean playWhenReady) {
                //  Toast.makeText(getApplication(),"playWhenReady"+playWhenReady,Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent();
            intent.putExtra("isEnd", isEnd);
            intent.putExtra("currPosition", exoPlayerManager.getCurrentPosition());
            setResult(RESULT_OK, intent);
            ActivityCompat.finishAfterTransition(this);
        }

    }


}
