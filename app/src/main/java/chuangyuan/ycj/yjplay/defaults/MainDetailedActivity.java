package chuangyuan.ycj.yjplay.defaults;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;

import java.util.Arrays;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.GestureVideoPlayer;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.DataSource;
public class MainDetailedActivity extends Activity {

    private ManualPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    private static final String TAG = "OfficeDetailedActivity";
    String[] test;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        videoPlayerView = findViewById(R.id.exo_play_context_id);
        exoPlayerManager = new ManualPlayer(this, videoPlayerView, new DataSource(this));
        //设置视频标题
        exoPlayerManager.setTitle("视频标题");
        //设置水印图
        exoPlayerManager.setExoPlayWatermarkImg(R.mipmap.watermark_big);
        exoPlayerManager.setOnWindowListener(new VideoWindowListener() {
            @Override
            public void onCurrentIndex(int currentIndex, int windowCount) {
                Toast.makeText(getApplication(), currentIndex + "windowCount:" + windowCount, Toast.LENGTH_SHORT).show();
            }
        });
        //设置开始播放进度
        // exoPlayerManager.setPosition(1000);
        // exoPlayerManager.setPlayUri(getString(R.string.uri_test_3),getString(R.string.uri_test_h));
         //    String tes="/storage/emulated/0/DCIM/Camera/VID_20180215_131926.mp4";
        // exoPlayerManager.setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath()+"/VID_20170925_154925.mp4");
      //  test = new String[]{"/storage/emulated/0/DCIM/Camera/VID_20180215_131816.mp4","/storage/emulated/0/DCIM/Camera/VID_20180215_131816.mp4","/storage/emulated/0/DCIM/Camera/VID_20180215_131816.mp4"};
        test = new String[]{getString(R.string.uri_test_9), getString(R.string.uri_test_7), getString(R.string.uri_test_8)};
        String[] name = {"超清", "高清", "标清"};
        //开启线路设置
         exoPlayerManager.setShowVideoSwitch(true);
        //exoPlayerManager.setPlaySwitchUri(0,test,name);
       // exoPlayerManager.setPlaySwitchUri(0, 0, getString(R.string.uri_test_11), Arrays.asList(test), Arrays.asList(name));
       exoPlayerManager.setPlayUri("rtmp://live.hkstv.hk.lxdns.com/live/hks");
       // exoPlayerManager.setPlaybackParameters(0.5f,0.5f);
        exoPlayerManager.startPlayer();
      //  exoPlayerManager.startPlayer();
     //  exoPlayerManager.setPlayUri("http://live.aikan.miguvideo.com/wd_r2/cctv/cctv1hd/1200/01.m3u8");
        //exoPlayerManager.setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.mp4");
        //开始启动播放视频
        //exoPlayerManager.startPlayer();
        // TestDataBean bean = new TestDataBean();
        // TestDataBean bean1 = new TestDataBean();
        //  List<TestDataBean> listss = new ArrayList<>();
       /* if (Build.VERSION.SDK_INT < 21) {//低版本不支持高分辨视频
            bean.setUri(getString(R.string.uri_test_3));
            bean1.setUri(getString(R.string.uri_test_h));
        } else {
            //4k 视频
            //exoPlayerManager.setPlayUri("http://mp4.vjshi.com/2016-07-13/16190d61b7dbddbeb721f1b994fd7424.mp4");
            bean.setUri("http://mp4.vjshi.com/2016-07-13/16190d61b7dbddbeb721f1b994fd7424.mp4");
            bean1.setUri("http://mp4.vjshi.com/2017-10-17/b81c7a35932c5bbacdc177534398fe87.mp4");
        }*/
        // listss.add(bean);
        // listss.add(bean1);
        //exoPlayerManager.setPlayUri(listss);
        //是否屏蔽进度控件拖拽快进视频（例如广告视频，（不允许用户））
        //exoPlayerManager.setSeekBarSeek(false);
        //设置视循环播放
        //exoPlayerManager.setLooping(10);
        //d隐藏控制布局
        // exoPlayerManager.hideControllerView();
        //隐藏进度条
        // exoPlayerManager.hideSeekBar();
        //显示进度条
        //exoPlayerManager.showSeekBar();
        //是否播放
        // exoPlayerManager.isPlaying();
        //设置播放视频倍数  快进播放和慢放播放
        //exoPlayerManager.setPlaybackParameters(2f,2f);
        // videoPlayerView.getPreviewImage().setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(this)
                .load(getString(R.string.uri_test_image))
                .fitCenter()
                .placeholder(R.mipmap.test)
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
               // Toast.makeText(getApplication(), "asd", Toast.LENGTH_SHORT).show();
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
        super.onDestroy();
        exoPlayerManager.onDestroy();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      //  exoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (exoPlayerManager.onBackPressed()) {
            ActivityCompat.finishAfterTransition(this);
            exoPlayerManager.onDestroy();
        }
    }
}
