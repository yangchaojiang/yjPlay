package chuangyuan.ycj.yjplay.defaults;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ui.AnimUtils;

import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.CronetDataSource2;
import chuangyuan.ycj.yjplay.data.DataSource;

public class MainDetailedActivity extends Activity {

    private ExoUserPlayer exoPlayerManager;
    private static final String TAG = "OfficeDetailedActivity";
    String[] test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        test = new String[]{getString(R.string.uri_test_9), getString(R.string.uri_test_7), getString(R.string.uri_test_8)};
        String[] name = {"超清", "高清", "标清"};
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
        //实例化
        exoPlayerManager = new VideoPlayerManager.Builder(this,VideoPlayerManager.TYPE_PLAY_MANUAL, R.id.exo_play_context_id)
                .setDataSource(new CronetDataSource2(this))
                //设置视频标题
                .setTitle("视频标题")
                //设置水印图
                .setExoPlayWatermarkImg(R.mipmap.watermark_big)
             //   .setPlayUri(getString(R.string.uri_test_5))
                .setPlayUri("/storage/sdcard0/bb.ffconcat")
                //加载rtmp 协议视频
                //.setPlayUri("rtmp://live.hkstv.hk.lxdns.com/live/hks")
                //加载m3u8
                //.setPlayUri("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8")
                //加载ts.文件
                //.setPlayUri("http://185.73.239.15:25461/live/1/1/924.ts")
                //播放本地视频
                //.setPlayUri("/storage/emulated/0/DCIM/Camera/VID_20170717_011150.mp4")
                //播放列表视频
               // .setPlayUri(listss);
                //设置开始播放进度
               // .setPosition(1000)
                //示例本地路径 或者 /storage/emulated/0/DCIM/Camera/VID_20180215_131926.mp4
               // .setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath()+"/VID_20170925_154925.mp4")
                //开启线路设置
               // .setShowVideoSwitch(true)
               // .setPlaySwitchUri(0,test,name)
               // .setPlaySwitchUri(0, 0, getString(R.string.uri_test_11), Arrays.asList(test), Arrays.asList(name))
                //设置播放视频倍数  快进播放和慢放播放
               // .setPlaybackParameters(0.5f, 0.5f)
                //是否屏蔽进度控件拖拽快进视频（例如广告视频，（不允许用户））
              //  .setSeekBarSeek(false)
                //设置视循环播放
                .setLooping(10)
                //视频进度回调
                .addOnWindowListener(new VideoWindowListener() {
                    @Override
                    public void onCurrentIndex(int currentIndex, int windowCount) {
                        Toast.makeText(getApplication(), currentIndex + "windowCount:" + windowCount, Toast.LENGTH_SHORT).show();
                    }
                })
                .addUpdateProgressListener(new AnimUtils.UpdateProgressListener() {
                    @Override
                    public void updateProgress(long position, long bufferedPosition, long duration) {
                         Log.d(TAG,"position:"+position);
                        Log.d(TAG,"bufferedPosition:"+position);
                        Log.d(TAG,"duration:"+duration);
                    }
                })
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
                .create()
                //播放视频
                .startPlayer();
        //d隐藏控制布局
        // exoPlayerManager.hideControllerView();
        //隐藏进度条
        // exoPlayerManager.hideSeekBar();
        //显示进度条
        //exoPlayerManager.showSeekBar();
        //是否播放
        // exoPlayerManager.isPlaying();
        // videoPlayerView.getPreviewImage().setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(this)
                .load(getString(R.string.uri_test_image))
                .fitCenter()
                .placeholder(R.mipmap.test)
                .into(exoPlayerManager.getPreviewImage());

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
