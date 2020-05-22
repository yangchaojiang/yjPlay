package chuangyuan.ycj.yjplay.custom;

import android.content.res.Configuration;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chuangyuan.ycj.videolibrary.listener.OnGestureProgressListener;
import chuangyuan.ycj.videolibrary.listener.OnLandScapeChangListener;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.videolibrary.whole.WholeMediaSource;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.Data2Source;
import chuangyuan.ycj.yjplay.data.TestDataBean;


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
    CustomPopupWindow pw;
    SpeedPopupWindow speedPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = getIntent().getIntExtra("type", 0);
        switch (type) {
            case 0:
                setContentView(R.layout.layout_coutom);
                setSupportActionBar(findViewById(R.id.mToolbar));
                getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        String[] name = {"超清", "高清", "标清"};
        TestDataBean bean = new TestDataBean();
        TestDataBean bean1 = new TestDataBean();
        TestDataBean bean2 = new TestDataBean();
        List<TestDataBean> listss = new ArrayList<>();
        bean.setUri("https://mp4.vjshi.com/2018-09-20/bc8b2ae8678e93a8b5ff87a83378b920.mp4");
        bean1.setUri("https://mp4.vjshi.com/2018-09-26/edd2743119bd799b696e503649a93c29.mp4");
        bean2.setUri("https://mp4.vjshi.com/2018-04-11/77502cefe5fe77de6f7c9e5ea7ce591b.mp4");
        listss.add(bean);
        listss.add(bean1);
        listss.add(bean2);
        videoPlayerView = findViewById(R.id.exo_play_context_id);
        exo_video_dialog_pro_text = findViewById(R.id.exo_video_dialog_pro_text);
        videoAudioImg = findViewById(R.id.exo_video_audio_img);
        videoAudioPro = findViewById(R.id.exo_video_audio_pro);
        videoBrightnessImg = findViewById(R.id.exo_video_brightness_img);
        videoBrightnessPro = findViewById(R.id.exo_video_brightness_pro);
        wholeMediaSource = new WholeMediaSource(this, new Data2Source(getApplication()));
        exoPlayerManager = new VideoPlayerManager.Builder(VideoPlayerManager.TYPE_PLAY_GESTURE, videoPlayerView)
                .setDataSource(wholeMediaSource)
                .setPosition(currPosition)
                .setShowVideoSwitch(true)
                .setPlaySwitchUri2(0, 0, getString(R.string.uri_test_1), listss, Arrays.asList(name))
                .setTitle("自定义视频标题")
                .setOnPlayClickListener(v -> {
                    Toast.makeText(MainCustomLayoutActivity.this, "定义点击播放事件", Toast.LENGTH_LONG).show();
                    //处理业务操作 完成后，
                    exoPlayerManager.startPlayer();//开始播放
                })
                //重写自定义手势监听事件，
                .setOnGestureBrightnessListener((mMax, currIndex) -> {
                    //显示你的布局
                    videoPlayerView.getGestureBrightnessLayout().setVisibility(View.VISIBLE);
                    //为你布局显示内容自定义内容
                    videoBrightnessPro.setMax(mMax);
                    videoBrightnessImg.setImageResource(chuangyuan.ycj.videolibrary.R.drawable.ic_brightness_6_white_48px);
                    videoBrightnessPro.setProgress(currIndex);
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
                .setOnGestureVolumeListener((mMax, currIndex) -> {
                    //显示你的布局
                    videoPlayerView.getGestureAudioLayout().setVisibility(View.VISIBLE);
                    //为你布局显示内容自定义内容
                    videoAudioPro.setMax(mMax);
                    videoAudioPro.setProgress(currIndex);
                    videoAudioImg.setImageResource(currIndex == 0 ? R.drawable.ic_volume_off_white_48px : R.drawable.ic_volume_up_white_48px);
                })
                .addVideoInfoListener(new VideoInfoListener() {
                    @Override
                    public void onPlayStart(long currPosition) {

                    }
                    @Override
                    public void onLoadingChanged() {

                    }
                    @Override
                    public void onPlayerError(@Nullable ExoPlaybackException e) {

                    }
                    @Override
                    public void onPlayEnd() {
                        if (pw != null) {
                            pw.dismissBelowView();
                        }
                        if (speedPopupWindow != null) {
                            speedPopupWindow.dismissBelowView();
                        }
                    }

                    @Override
                    public void isPlaying(boolean playWhenReady) {

                    }
                })
                .addUpdateProgressListener((position, bufferedPosition, duration) -> {
                   // Log.d(TAG, "bufferedPosition:" + position);
                  //  Log.d(TAG, "duration:" + duration);
                }).setOnBelowViewListener((view, name1, checkedIndex, mSwitchText, m) -> {
                    if (pw == null) {
                        pw = new CustomPopupWindow(view.getContext());
                    }
                    pw.showBelowView(view, name1, checkedIndex, mSwitchText, m);
                })
                .setOnCoverMapImage(v -> Glide.with(MainCustomLayoutActivity.this)
                        .load(getString(R.string.uri_test_image))
                        .asBitmap()
                        .fitCenter()
                        .into(v)).create();
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
        TextView exoMediaSpeed = videoPlayerView.findViewById(R.id.exoMediaSpeed);
        exoMediaSpeed.setText("1.0");
        exoPlayerManager.setPlaybackParameters(1.0f, 1);
        exoMediaSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speedPopupWindow == null) {
                    speedPopupWindow = new SpeedPopupWindow(v.getContext(), exoPlayerManager);
                }
                speedPopupWindow.showBelowView(v, true, exoMediaSpeed);
            }
        });
        /***
         * 监听横屏和竖屏事件
         * ***/
        videoPlayerView.setOnLandScapeChangListener(new OnLandScapeChangListener() {
            @Override
            public void landScapeChang(boolean isLand) {
                if (isLand) {
                    exoMediaSpeed.setVisibility(View.VISIBLE);
                } else {
                    exoMediaSpeed.setVisibility(View.GONE);
                }
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
            Toast.makeText(MainCustomLayoutActivity.this, "返回", Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
