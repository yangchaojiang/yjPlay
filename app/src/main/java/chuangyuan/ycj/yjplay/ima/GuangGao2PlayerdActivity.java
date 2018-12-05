package chuangyuan.ycj.yjplay.ima;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.utils.AnimUtils;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.videolibrary.whole.WholeMediaSource;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.Data2Source;
import chuangyuan.ycj.yjplay.data.DataSource;
import chuangyuan.ycj.yjplay.data.TestDataBean;

@SuppressLint("Registered")
public class GuangGao2PlayerdActivity extends Activity {

    private ExoUserPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    private static final String TAG = "OfficeDetailedActivity";
    private WholeMediaSource mWholeMediaSource;
    private LinearLayout mLinearLayout;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coutom5);
        videoPlayerView = findViewById(R.id.exo_play_context_id);
        mLinearLayout=findViewById(R.id.mLinearLayout);
        findViewById(R.id.mButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.activityRoot),"开通会员",BaseTransientBottomBar.LENGTH_SHORT);
            }
        });
        mLinearLayout.setVisibility(View.GONE);
        String[] name = {"超清", "高清", "标清"};
        List<String> listss = new ArrayList<>();
        listss.add("https://mp4.vjshi.com/2017-06-25/989b18a610174b11dfa3edcfc0724cb6.mp4");
        listss.add("https://mp4.vjshi.com/2017-06-25/989b18a610174b11dfa3edcfc0724cb6.mp4");
        listss.add("https://mp4.vjshi.com/2017-06-25/989b18a610174b11dfa3edcfc0724cb6.mp4");
        mWholeMediaSource=new WholeMediaSource(this,new DataSource(this));
        //开启实时进度
        videoPlayerView.setOpenProgress2(true);
        exoPlayerManager = new VideoPlayerManager
                .Builder(VideoPlayerManager.TYPE_PLAY_USER, videoPlayerView)
                .setDataSource(mWholeMediaSource)
                .setShowVideoSwitch(true)
                .setPlaySwitchUri(0,0,getString(R.string.uri_test),listss, Arrays.asList(name))
                //设置视频标题
                .setTitle("视频标题")
                //设置水印图
                .setExoPlayWatermarkImg(R.mipmap.watermark_big)
                .setPlayerGestureOnTouch(false)
                .addUpdateProgressListener(new AnimUtils.UpdateProgressListener() {
                    @Override
                    public void updateProgress(long position, long bufferedPosition, long duration) {
                        Log.d(TAG,"position:"+position+":"+exoPlayerManager.getPlayer().getCurrentWindowIndex());
                     /*   if (position/1000==10L){
                            mLinearLayout.setBackgroundColor(Color.parseColor("#CC000000"));
                            mLinearLayout.setVisibility(View.VISIBLE);
                            exoPlayerManager.setStartOrPause(false);
                            exoPlayerManager.setUseController(false);
                        }*/
                    }
                })
                .create()
                .startPlayer();
        Glide.with(getBaseContext())
                .load(getString(R.string.uri_test_image))
                .fitCenter()
                .placeholder(R.mipmap.test)
                .into(videoPlayerView.getPreviewImage());
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
