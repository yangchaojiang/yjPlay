package chuangyuan.ycj.yjplay.add;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;

import java.util.ArrayList;
import java.util.List;

import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;

public class AddVideoActivity extends Activity {

    private ExoUserPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    private static final String TAG = "AddVideoActivity";
      private  List<String> list;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coutom2);
        list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            if (Build.VERSION.SDK_INT < 21) {//低版本不支持高分辨视频
                list.add(getString(R.string.uri_test_3));
            } else {
                if (i % 7 == 0) {
                    list.add(getString(R.string.uri_test_1));
                } else if (i % 7 == 1) {
                    list.add(getString(R.string.uri_test_10));
                } else if (i % 7 == 2) {
                    list.add(getString(R.string.uri_test_5));
                } else if (i % 7 == 3) {
                    list.add(getString(R.string.uri_test_6));
                } else if (i % 7 == 4) {
                    list.add(getString(R.string.uri_test_7));
                } else if (i % 7 == 5) {
                    list.add(getString(R.string.uri_test_9));
                } else if (i % 7 == 6) {
                    list.add(getString(R.string.uri_test_8));
                }
            }
        }
        videoPlayerView = findViewById(R.id.exo_play_context_id);
        findViewById(R.id.button5).setVisibility(View.GONE);
        //开启实时进度
        videoPlayerView.setOpenProgress2(true);
        exoPlayerManager = new VideoPlayerManager
                .Builder(VideoPlayerManager.TYPE_PLAY_USER, videoPlayerView)
                //设置视频标题
                .setTitle("视频标题")
                //设置水印图
                .setExoPlayWatermarkImg(R.mipmap.watermark_big)
                .setPlayerGestureOnTouch(false)
                .addMediaUri(Uri.parse(getString(R.string.uri_test_12)))
                ///默认实现  播放广告视频时手势操作禁用和开启操作
                //.setPlayerGestureOnTouch(true);
                //如果视频需要自己实现该回调 视频切换回调处理，进行布局处理，控制布局显示
                .addOnWindowListener(new VideoWindowListener() {
                    @Override
                    public void onCurrentIndex(int currentIndex, int windowCount) {
                        Toast.makeText(getApplication(), "currentIndex::"+currentIndex, Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .startPlayer();


        Glide.with(this)
                .load(getString(R.string.uri_test_image))
                .fitCenter()
                .placeholder(R.mipmap.test)
                .into(videoPlayerView.getPreviewImage());
        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayerManager.addMediaUri(Uri.parse(list.get(exoPlayerManager.getWindowCount())));
                Toast.makeText(AddVideoActivity.this,"ok",Toast.LENGTH_SHORT).show();
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
