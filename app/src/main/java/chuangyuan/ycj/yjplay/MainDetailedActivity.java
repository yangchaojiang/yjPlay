package chuangyuan.ycj.yjplay;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;

import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

public class MainDetailedActivity extends Activity {

    private ExoUserPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    private static final String TAG = "MainDetailedActivity";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        videoPlayerView = (VideoPlayerView) findViewById(R.id.exo_play_context_id);
        exoPlayerManager = new ExoUserPlayer(this, videoPlayerView, new DataSource(this.getApplication()));
        //设置视频标题
        exoPlayerManager.setTitle("视频标题");
        //设置水印图
        exoPlayerManager.setExoPlayWatermarkImg(R.mipmap.watermark_big);
        //设置开始播放进度
        //  exoPlayerManager.setPosition(1000);
        // exoPlayerManager.setPlayUri("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4");
        // exoPlayerManager.setPlayUri("/storage/emulated/0/DCIM/Camera/VID_20170925_154925.mp4");
        //String [] test={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
        //String[] name={"超清","高清","标清"};
        //开启线路设置
        // exoPlayerManager.setShowVideoSwitch(true);
        //exoPlayerManager.setPlaySwitchUri(test,name);
        // exoPlayerManager.setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath()+"/VID_20170925_154925.mp4");
         exoPlayerManager.setPlayUri("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4",1);
        // exoPlayerManager.setPlayUri("http://192.168.0.106:8080/autograph/update/权力的游戏.Game.of.Thrones.S06E10.中英字幕.HDTVrip.mp4");
        //是否屏蔽进度控件拖拽快进视频（例如广告视频，（不允许用户））
        //exoPlayerManager.setSeekBarSeek(false);
        //设置视循环播放
        //exoPlayerManager.setLooping(10);
        //d隐藏控制布局
        exoPlayerManager.hideControllerView();
        //隐藏进度条
       // exoPlayerManager.hideSeekBar();
        //显示进度条
      //  exoPlayerManager.showSeekBar();
        //是否播放
        exoPlayerManager.isPlaying();
      //  exoPlayerManager.showControllerView();
        Glide.with(this)
                .load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                .fitCenter()
                .placeholder(R.mipmap.test)
                .into(videoPlayerView.getPreviewImage());
       /* findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // exoPlayerManager.next();
            }
        });*/
     /*   findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  exoPlayerManager.previous();
            }
        });*/
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
      /*  if (!VideoPlayUtils.isLand(this)) { //隐藏状态栏的
            VideoPlayUtils.hideActionBar(this);
        }*/
    }

    @Override
    public void onBackPressed() {
        if (exoPlayerManager.onBackPressed()) {
            ActivityCompat.finishAfterTransition(this);

        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (isInPictureInPictureMode) {
            // Hide the controls in picture-in-picture mode.
            videoPlayerView.getPlaybackControlView().hide();
        } else {
            // Restore the playback UI based on the playback status.
            videoPlayerView.getPlaybackControlView().show();
        }
    }
}
