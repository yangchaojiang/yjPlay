package chuangyuan.ycj.yjplay.custom;

import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.source.MediaSource;

import chuangyuan.ycj.videolibrary.listener.OnCoverMapImageListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureBrightnessListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureProgressListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureVolumeListener;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.videolibrary.whole.WholeMediaSource;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.Data2Source;


public class MainCustomLayoutActivity extends AppCompatActivity {

    private ExoUserPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    public static final String VIEW_NAME_HEADER_IMAGE = "123";
    private static final String TAG = "OfficeDetailedActivity";
    private long currPosition = 0;
    TextView exo_video_dialog_pro_text;
    private ImageView videoAudioImg, videoBrightnessImg;
    /***显示音频和亮度***/
    private ProgressBar videoAudioPro, videoBrightnessPro;
    WholeMediaSource wholeMediaSource;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = getIntent().getIntExtra("type", 0);
        switch (type) {
            case 0:
                setContentView(R.layout.layout_coutom);
                 setSupportActionBar(findViewById(R.id.mToolbar));
                  getSupportActionBar().setDisplayShowHomeEnabled(true                                                                                                                                                                                       );
                break;
            case 1:
                setContentView(R.layout.layout_coutom2);
                break;
            case 2:
                setContentView(R.layout.layout_coutom3);
                break;
            case 3:
                setContentView(R.layout.layout_coutom4);
                break;
        }
        currPosition = getIntent().getLongExtra("currPosition", 0);
        videoPlayerView = findViewById(R.id.exo_play_context_id);
        exo_video_dialog_pro_text = findViewById(R.id.exo_video_dialog_pro_text);
        videoAudioImg = findViewById(R.id.exo_video_audio_img);
        videoAudioPro = findViewById(R.id.exo_video_audio_pro);
        videoBrightnessImg = findViewById(R.id.exo_video_brightness_img);
        videoBrightnessPro = findViewById(R.id.exo_video_brightness_pro);
        wholeMediaSource = new WholeMediaSource(this, new Data2Source(getApplication()));
        MediaSource videoSource = wholeMediaSource.initMediaSource(
                Uri.parse(getString(R.string.uri_test_1)));
   /*     //构建子标题媒体源
        Format subtitleFormat = Format.createTextSampleFormat(
                getPackageName(), // 跟踪的标识符。可能是null。
                MimeTypes.TEXT_SSA, // mime类型。必须正确设置
                0, // 跑道的选择标志。
                null); // 字幕语言。可能是null。
        MediaSource      subtitleSource= new SingleSampleMediaSource.Factory(wholeMediaSource.getDataSource())
                .setTreatLoadErrorsAsEndOfStream(true)
                .setMinLoadableRetryCount(5)
        .createMediaSource(Uri.parse("http://oph6zeldx.bkt.clouddn.com/test23.ass"), subtitleFormat, C.TIME_UNSET);
// Plays the video with the sideloaded subtitle.
        MergingMediaSource mergedSource =
                new MergingMediaSource(videoSource, subtitleSource);*/
        //自定义布局使用
        videoPlayerView.getReplayLayout().findViewById(R.id.replay_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomLayoutActivity.this, "自定义分享", Toast.LENGTH_SHORT).show();
            }
        });
        videoPlayerView.getErrorLayout().findViewById(R.id.exo_player_error_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomLayoutActivity.this, "自定义错误", Toast.LENGTH_SHORT).show();
            }
        });
        videoPlayerView.getPlayHintLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomLayoutActivity.this, "自定义提示", Toast.LENGTH_SHORT).show();
            }
        });
        videoPlayerView.setShowBack(false);
        wholeMediaSource.setMediaSource(videoSource);
        exoPlayerManager = new VideoPlayerManager.Builder(VideoPlayerManager.TYPE_PLAY_GESTURE, videoPlayerView)
                .setDataSource(wholeMediaSource)
                .setPosition(currPosition)
                .setTitle("自定义视频标题")
                .setVerticalFullScreen(true)
                .setOnPlayClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainCustomLayoutActivity.this, "定义点击播放事件", Toast.LENGTH_LONG).show();
                        //处理业务操作 完成后，
                        exoPlayerManager.startPlayer();//开始播放
                    }
                })
                //重写自定义手势监听事件，
                .setOnGestureBrightnessListener(new OnGestureBrightnessListener() {
                    @Override
                    public void setBrightnessPosition(int mMax, int currIndex) {
                        //显示你的布局
                        videoPlayerView.getGestureBrightnessLayout().setVisibility(View.VISIBLE);
                        //为你布局显示内容自定义内容
                        videoBrightnessPro.setMax(mMax);
                        videoBrightnessImg.setImageResource(chuangyuan.ycj.videolibrary.R.drawable.ic_brightness_6_white_48px);
                        videoBrightnessPro.setProgress(currIndex);
                    }
                })
                //重写自定义手势监听事件，
                .setOnGestureProgressListener(new OnGestureProgressListener() {
                    @Override
                    public void showProgressDialog(long seekTimePosition, long duration, String seekTime, String totalTime) {
                        //显示你的布局
                        videoPlayerView.getGestureProgressLayout().setVisibility(View.VISIBLE);
                        exo_video_dialog_pro_text.setTextColor(Color.RED);
                        exo_video_dialog_pro_text.setText(seekTime + "/" + totalTime);
                    }

                    @Override
                    public void endGestureProgress(long position) {
                        exoPlayerManager.seekTo(position);
                    }
                })
                //重写自定义手势监听事件，
                .setOnGestureVolumeListener(new OnGestureVolumeListener() {
                    @Override
                    public void setVolumePosition(int mMax, int currIndex) {
                        //显示你的布局
                        videoPlayerView.getGestureAudioLayout().setVisibility(View.VISIBLE);
                        //为你布局显示内容自定义内容
                        videoAudioPro.setMax(mMax);
                        videoAudioPro.setProgress(currIndex);
                        videoAudioImg.setImageResource(currIndex == 0 ? R.drawable.ic_volume_off_white_48px : R.drawable.ic_volume_up_white_48px);
                    }
                })
                .addVideoInfoListener(new VideoInfoListener() {
                    @Override
                    public void onPlayStart(long currPosition) {
                        // videoPlayerView.getPlayerView().setControllerHideOnTouch(false);
                    }

                    @Override
                    public void onLoadingChanged() {

                    }

                    @Override
                    public void onPlayerError(@Nullable ExoPlaybackException e) {

                    }

                    @Override
                    public void onPlayEnd() {
                        //  wholeMediaSource.release();
                        //   wholeMediaSource.setMediaUri(Uri.parse(getString(R.string.url_hls)));
                        //  exoPlayerManager.startPlayer();
                    }

                    @Override
                    public void isPlaying(boolean playWhenReady) {

                    }
                })
                .setOnCoverMapImage(new OnCoverMapImageListener() {
                    @Override
                    public void onCoverMap(ImageView v) {
                        Glide.with(MainCustomLayoutActivity.this)
                                .load(getString(R.string.uri_test_image))
                                .asBitmap()
                                .fitCenter()
                                .into(v);
                    }
                }).create();


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
        // exoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (exoPlayerManager.onBackPressed()) {//使用播放返回键监听
            Toast.makeText(MainCustomLayoutActivity.this, "返回", Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
