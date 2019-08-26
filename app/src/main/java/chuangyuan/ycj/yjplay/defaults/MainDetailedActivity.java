package chuangyuan.ycj.yjplay.defaults;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.offline.DownloadHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chuangyuan.ycj.videolibrary.listener.OnCoverMapImageListener;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.Data2Source;
import chuangyuan.ycj.yjplay.data.TestDataBean;

public class MainDetailedActivity extends Activity {

    private ExoUserPlayer exoPlayerManager;
    private static final String TAG = "MainDetailedActivity";
    String[] test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        test = new String[]{getString(R.string.uri_test_9), getString(R.string.uri_test_7), getString(R.string.uri_test_8)};
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
                //实例化
        exoPlayerManager = new VideoPlayerManager.Builder(this, VideoPlayerManager.TYPE_PLAY_GESTURE, R.id.exo_play_context_id)
                .setDataSource(new Data2Source(this))
                .setPlaySwitchUri2(0, 0, getString(R.string.uri_test_1), listss, Arrays.asList(name))
                //设置视频标题
                .setTitle("视频标题")
                //设置水印图
                .setExoPlayWatermarkImg(R.mipmap.watermark_big)
                //.setPlayUri("/storage/emulated/0/test.ts")
                //  .setPlayUri("http://oph6zeldx.bkt.clouddn.com/20130104095750-MzE1ODU1.mp3")
                // .setPlayUri(getString(R.string.uri_test_5))
                .setPlayerGestureOnTouch(true)
                // .setPlayUri("/storage/sdcard0/DCIM/Camera/VID_20180829_100348.mp4")
                //加载rtmp 协议视频
                //.setPlayUri("rtmp://live.hkstv.hk.lxdns.com/live/hks")
                //加载m3u8
                // .setPlayUri("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8")
                //加载ts.文件
                //.setPlayUri("http://185.73.239.15:25461/live/1/1/924.ts")
                //播放本地视频
                //.setPlayUri("/storage/emulated/0/DCIM/Camera/VID_20170717_011150.mp4")
                //播放列表视频
                // .setPlayUri(listss);
                //设置开始播放进度
                // .setPosition(1000)
                //开启线路设置
                 .setShowVideoSwitch(true)
                // .setPlaySwitchUri(0,test,name)
                // .setPlaySwitchUri(0, 0, getString(R.string.uri_test_11), Arrays.asList(test), Arrays.asList(name))
                //设置播放视频倍数  快进播放和慢放播放
                // .setPlaybackParameters(0.5f, 0.5f)
                //是否屏蔽进度控件拖拽快进视频（例如广告视频，（不允许用户））
                //  .setSeekBarSeek(false)
                //设置视循环播放
                // .setLooping(10)
                //视频进度回调
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
                                .load(getString(R.string.uri_test_image))
                                .fitCenter()
                                .placeholder(R.mipmap.test)
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
}
