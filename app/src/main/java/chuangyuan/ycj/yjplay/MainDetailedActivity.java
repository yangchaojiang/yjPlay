package chuangyuan.ycj.yjplay;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;

import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

public class MainDetailedActivity extends Activity {

    private ManualPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    private static final String TAG = "MainDetailedActivity";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        videoPlayerView = (VideoPlayerView) findViewById(R.id.exo_play_context_id);
        exoPlayerManager = new ManualPlayer(this, videoPlayerView, null);
        //设置视频标题
        exoPlayerManager.setTitle("视频标题");
        exoPlayerManager.setExoPlayWatermarkImg(R.mipmap.watermark_big);
        //设置开始播放进度
        //  exoPlayerManager.setPosition(1000);
        // exoPlayerManager.setShowVideoSwitch(true);
        // exoPlayerManager.setPlayUri("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4");
        //exoPlayerManager.setPlayUri("/storage/emulated/0/DCIM/Camera/VID_20170717_011150.mp4");
        //String [] test={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
        //  String[] name={"超清","高清","标清"};
        //exoPlayerManager.setPlaySwitchUri(test,name);
        //exoPlayerManager.setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.h264");
         exoPlayerManager.setPlayUri("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4");
        // exoPlayerManager.setPlayUri("http://192.168.0.106:8080/autograph/update/权力的游戏.Game.of.Thrones.S06E10.中英字幕.HDTVrip.mp4");
       //  exoPlayerManager.setPlayUri("http://oph6zeldx.bkt.clouddn.com/4.avi");
        //exoPlayerManager.setPlayUri("http://oph6zeldx.bkt.clouddn.com/%E6%9D%83%E5%8A%9B%E7%9A%84%E6%B8%B8%E6%88%8F.Game.of.Thrones.S06E10.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.mp4");
        //exoPlayerManager.setPlayUri("http://185.73.239.15:25461/live/1/1/924.ts");
        //  exoPlayerManager.setExoPlayWatermarkImg(R.mipmap.ic_launcher);
        // videoPlayerView.getTimeBar().setListener(null);
        // exoPlayerManager.getPlayer().getVolume();
    Glide.with(this)
                .load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                .fitCenter()
                .placeholder(R.mipmap.test)
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

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onBackPressed() {
        if (exoPlayerManager.onBackPressed()) {
            finish();

        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (isInPictureInPictureMode) {
            // Hide the controls in picture-in-picture mode.
            exoPlayerManager.getPlayerView().getPlaybackControlView().hide();
        } else {
            // Restore the playback UI based on the playback status.
            exoPlayerManager.getPlayerView().getPlaybackControlView().show();
        }
    }
}
