package chuangyuan.ycj.yjplay.custom;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.DataSource;


public class MainListInfoCustomActivity extends AppCompatActivity {
    private ExoUserPlayer exoPlayerManager;
    public static final String VIEW_NAME_HEADER_IMAGE = "123";
    private static final String TAG = "OfficeDetailedActivity";
    private boolean isEnd;
    private boolean isBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coutom);
        long currPosition = getIntent().getLongExtra("currPosition", 0);
        VideoPlayerView videoPlayerView = findViewById(R.id.exo_play_context_id);
        ViewCompat.setTransitionName(videoPlayerView, VIEW_NAME_HEADER_IMAGE);
        exoPlayerManager = VideoPlayerManager.getInstance().getVideoPlayer();
        //如果为空，自己new一个
        if (exoPlayerManager == null) {
            exoPlayerManager = new VideoPlayerManager.Builder(VideoPlayerManager.TYPE_PLAY_GESTURE, videoPlayerView)
                    .setDataSource(new DataSource(this))
                    .setPlayUri(getIntent().getStringExtra("uri"))
                    .setPosition(currPosition)
                    .create()
                    .startPlayer();
        } else {
            VideoPlayerManager.getInstance().switchTargetView(exoPlayerManager, videoPlayerView, true);
            exoPlayerManager.setPosition(currPosition);
        }
        videoPlayerView.setTitle("自定义视频标题");
        //自定义布局使用
        videoPlayerView.getReplayLayout().findViewById(R.id.replay_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainListInfoCustomActivity.this, "自定义分享", Toast.LENGTH_SHORT).show();
            }
        });
        videoPlayerView.getErrorLayout().findViewById(R.id.exo_player_error_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainListInfoCustomActivity.this, "自定义错误", Toast.LENGTH_SHORT).show();
            }
        });
        videoPlayerView.getPlayHintLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainListInfoCustomActivity.this, "自定义提示", Toast.LENGTH_SHORT).show();
            }
        });
        Glide.with(this)
                .load(getString(R.string.uri_test_image))
                .placeholder(R.mipmap.test)
                .fitCenter()
                .into(videoPlayerView.getPreviewImage());
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
        //是返回不是列表控制类就释放，是列表的不能释放，还要做还原处理
        if (!isBack || VideoPlayerManager.getInstance().getVideoPlayer() == null) {
            exoPlayerManager.onPause();
        }

    }


    @Override
    protected void onDestroy() {
        //是返回不是列表控制类就释放，是列表的不能释放，还要做还原处理
        if (!isBack || VideoPlayerManager.getInstance().getVideoPlayer() == null) {
            exoPlayerManager.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        exoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        //获取数据返回获取
        long currPosition = exoPlayerManager.getCurrentPosition();
        if (exoPlayerManager.onBackPressed()) {//使用播放返回键监听
            isBack = true;
            Toast.makeText(MainListInfoCustomActivity.this, "返回", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra("isEnd", isEnd);
            intent.putExtra("currPosition", currPosition);
            Log.d(TAG, "sss:" + exoPlayerManager.getCurrentPosition());
            setResult(RESULT_OK, intent);
            ActivityCompat.finishAfterTransition(this);
        }

    }


}
