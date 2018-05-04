package chuangyuan.ycj.yjplay.defaults;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;

import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.GestureVideoPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.DataSource;

public class GuangGaoPlayerdActivity extends Activity {

    private ExoUserPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    private static final String TAG = "OfficeDetailedActivity";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coutom2);
        videoPlayerView =   findViewById(R.id.exo_play_context_id);
        exoPlayerManager = new ExoUserPlayer(this, videoPlayerView);
        videoPlayerView.setTitle("视频标题");
        videoPlayerView.setExoPlayWatermarkImg(R.mipmap.watermark_big);
        videoPlayerView.setOpenProgress2(true);
         if (Build.VERSION.SDK_INT < 21) {//低版本不支持高分辨视频
            exoPlayerManager.setPlayUri(0, getString(R.string.uri_test_10), getString(R.string.uri_test_3));
        } else {
            exoPlayerManager.setPlayUri(0,getString(R.string.uri_test_11),getString(R.string.uri_test_12));
        }
        ///默认实现  播放广告视频时手势操作禁用和开启操作
        //exoPlayerManager.setPlayerGestureOnTouch(true);
        //如果视频需要自己实现该回调 视频切换回调处理，进行布局处理，控制布局显示
        exoPlayerManager.setOnWindowListener(new VideoWindowListener() {
            @Override
            public void onCurrentIndex(int currentIndex, int windowCount) {
                if (currentIndex == 0) {
                    Log.d(TAG,"setOnWindowListener:"+currentIndex);
                    //屏蔽控制布局
                    exoPlayerManager.hideControllerView(true);
                    //true如果屏蔽控制布局 但是需要显示全屏按钮。手动显示。
                } else {
                    //恢复控制布局
                    exoPlayerManager.showControllerView(true);
                }
            }
        });
        exoPlayerManager.addVideoInfoListener(new VideoInfoListener() {

            @Override
            public void onPlayStart(long currPosition) {

            }

            @Override
            public void onLoadingChanged() {

            }

            @Override
            public void onPlayerError( ExoPlaybackException e) {

            }

            @Override
            public void onPlayEnd() {
                Toast.makeText(getApplication(),"asd",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void isPlaying(boolean playWhenReady) {

            }
        });
        Glide.with(this)
                .load(getString(R.string.uri_test_image))
                .fitCenter()
                .placeholder(R.mipmap.test)
                .into(videoPlayerView.getPreviewImage());
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayerManager.next();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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
        if (exoPlayerManager.onBackPressed()) {
            ActivityCompat.finishAfterTransition(this);

        }
    }

}
