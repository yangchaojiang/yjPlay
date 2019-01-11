package chuangyuan.ycj.yjplay.media;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.util.MimeTypes;

import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.MediaSourceBuilder;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.DataSource;


public class MainCustomMediaActivity extends AppCompatActivity {

    private ExoUserPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    private static final String TAG = "MainCustomMediaActivity";
    private String url = "";
    MediaSourceBuilder mediaSourceBuilder;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coutom);
        url = getIntent().getStringExtra("uri");
        videoPlayerView = findViewById(R.id.exo_play_context_id);
        Glide.with(this)
                .load(getString(R.string.uri_test_image))
                .placeholder(R.mipmap.test)
                .fitCenter()
                .into(videoPlayerView.getPreviewImage());
        mediaSourceBuilder = new MediaSourceBuilder(this, new DataSource(getApplication()));
        videoPlayerView.setTitle("自定义视频标题");
        //设置加载显示模式
        //  mediaSourceBuilder.setClippingMediaUri(Uri.parse(getString(R.string.uri_test_6)),1000,15000);
        MediaSource source = mediaSourceBuilder.initMediaSource(Uri.parse(getString(R.string.uri_test)));
        LoopingMediaSource loopingSource = new LoopingMediaSource(source, 2);
// Build the subtitle MediaSource.
        Format subtitleFormat = Format.createTextSampleFormat( null,MimeTypes.APPLICATION_SUBRIP,C.SELECTION_FLAG_DEFAULT, null);
        MediaSource subtitleSource = new SingleSampleMediaSource.Factory(mediaSourceBuilder.getDataSource())
                        .createMediaSource(Uri.parse("http://oph6zeldx.bkt.clouddn.com/test.ass"),
                                subtitleFormat, C.TIME_UNSET);
// Plays the video with the sideloaded subtitle.
        MergingMediaSource mergedSource =
                new MergingMediaSource(source, subtitleSource);

        //初始化
        mediaSourceBuilder = new MediaSourceBuilder(this, new DataSource(getApplication()));
       //使用这个对象添加视频添加
        ConcatenatingMediaSource concatenatingMediaSource=new ConcatenatingMediaSource();
        LoopingMediaSource mediaSource = new LoopingMediaSource(concatenatingMediaSource, Integer.MAX_VALUE);
        mediaSourceBuilder.setMediaSource(mediaSource);
        //你需要动态添加方法
        concatenatingMediaSource.addMediaSource(mediaSourceBuilder.initMediaSource(Uri.parse("VideoPath")));

        exoPlayerManager = new VideoPlayerManager
                .Builder(VideoPlayerManager.TYPE_PLAY_GESTURE, videoPlayerView)
                .setDataSource(mediaSourceBuilder)
                .addOnWindowListener(new VideoWindowListener() {
                    @Override
                    public void onCurrentIndex(int currentIndex, int windowCount) {
                        Log.d(TAG, "currentIndex:" + currentIndex + "_windowCount:" + currentIndex);
                    }
                })
                .create()
                .startPlayer();


        //自定义布局使用
        videoPlayerView.getReplayLayout().findViewById(R.id.replay_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomMediaActivity.this, "自定义分享", Toast.LENGTH_SHORT).show();
            }
        });
        videoPlayerView.getErrorLayout().findViewById(R.id.exo_player_error_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomMediaActivity.this, "自定义错误", Toast.LENGTH_SHORT).show();
            }
        });
        videoPlayerView.getPlayHintLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomMediaActivity.this, "自定义提示", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MainCustomMediaActivity.this, "返回", Toast.LENGTH_LONG).show();
            finish();
        }

    }


}
