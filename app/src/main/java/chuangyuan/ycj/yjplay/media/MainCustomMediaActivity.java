package chuangyuan.ycj.yjplay.media;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;

import chuangyuan.ycj.videolibrary.listener.LoadModelType;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.video.MediaSourceBuilder;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.data.DataSource;
import chuangyuan.ycj.yjplay.R;


public class MainCustomMediaActivity extends AppCompatActivity {

    private ManualPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    private static final String TAG = "MainCustomMediaActivity";
    private String url = "";
    MediaSourceBuilder mediaSourceBuilder;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coutom);
        url = getIntent().getStringExtra("uri");
        videoPlayerView = (VideoPlayerView) findViewById(R.id.exo_play_context_id);
        mediaSourceBuilder=new MediaSourceBuilder(this,new DataSource(getApplication()));
        exoPlayerManager = new ManualPlayer(this,mediaSourceBuilder, videoPlayerView);
        exoPlayerManager.setTitle("自定义视频标题");
        //设置加载显示模式
        exoPlayerManager.setLoadModel(LoadModelType.SPEED);
        //
        //http://demos.webmproject.org/dash/201410/vp9_glass/manifest_vp9_opus.mpd
       // MediaSource source =mediaSourceBuilder.initMediaSource(Uri.parse("rtsp://183.207.208.80:554/live_rtsp?source=025200060360101:1&user=025300000002499&session=no&device=025200060360101&timestamp=20171117110042&encrypt=5b601c621c6a9db0cb2dabf3a6ff9205"));
        //mediaSourceBuilder.setMediaSource(source);
        //LoopingMediaSource loopingSource = new LoopingMediaSource(source, 2);
         mediaSourceBuilder.setMediaUri(Uri.parse(getString(R.string.uri_test)),Uri.parse(getString(R.string.uri_test)),Uri.parse(getString(R.string.uri_test)),Uri.parse(getString(R.string.uri_test)));


        exoPlayerManager.startPlayer();
        Glide.with(this)
                .load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                .placeholder(R.mipmap.test)
                .fitCenter()
                .into(videoPlayerView.getPreviewImage());
        exoPlayerManager.setOnWindowListener(new VideoWindowListener() {
            @Override
            public void onCurrentIndex(int currentIndex, int windowCount) {
                Log.d(TAG,"currentIndex:"+currentIndex+"_windowCount:"+currentIndex);
            }
        });
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
        exoPlayerManager.setVideoInfoListener(new VideoInfoListener() {
            @Override
            public void onPlayStart() {

            }

            @Override
            public void onLoadingChanged() {

            }

            @Override
            public void onPlayerError( ExoPlaybackException e) {

            }

            @Override
            public void onPlayEnd() {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void isPlaying(boolean playWhenReady) {

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
