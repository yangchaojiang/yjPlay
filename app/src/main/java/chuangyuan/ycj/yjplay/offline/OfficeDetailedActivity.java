package chuangyuan.ycj.yjplay.offline;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.offline.Downloader;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;
import java.io.IOException;

import chuangyuan.ycj.videolibrary.factory.JDefaultDataSourceFactory;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.offline.DefaultProgressDownloader;
import chuangyuan.ycj.videolibrary.video.GestureVideoPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;

public class OfficeDetailedActivity extends Activity {

    private GestureVideoPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    DefaultProgressDownloader downloader;
    private static final String TAG = "OfficeDetailedActivity";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_office);
        videoPlayerView = (VideoPlayerView) findViewById(R.id.exo_play_context_id);
        exoPlayerManager = new GestureVideoPlayer(this, videoPlayerView,
                new OfficeDataSource(this,null));
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        exoPlayerManager.setTitle("视频标题");
        exoPlayerManager.setExoPlayWatermarkImg(R.mipmap.watermark_big);
        exoPlayerManager.setPlayUri(getString(R.string.uri_test_1));
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
        super.onDestroy();
        if (downloader != null) {
            downloader.cancel();
        }

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
            exoPlayerManager.onDestroy();
        }
    }

    /***
     * 自定义下载
     * ***/
    private void customDwon() {
        //设置下载缓存实例
        downloader = new DefaultProgressDownloader.Builder(this)
                //设置你缓存目录
                .setCacheFileDir(this.getExternalCacheDir().getAbsolutePath())
                //缓存文件加密,那么在使用AES / CBC的文件系统中缓存密钥将被加密  密钥必须是16字节长.
                .setCache("1234567887654321".getBytes())
                .setUri(getString(R.string.uri_test_1))
                //设置自定义 你缓存文件key 可以为空，推荐视频该链接作为key密钥
                .setCustomCacheKey(getString(R.string.uri_test_1))
                //设置下载数据加载工厂类
                .setHttpDataSource(new JDefaultDataSourceFactory(this))
                .build();
        downloader.download(new Downloader.ProgressListener() {
            @Override
            public void onDownloadProgress(Downloader downloader, float downloadPercentage, long downloadedBytes) {
                Log.d(TAG, "downloadPercentage:" + downloadPercentage + "downloadedBytes:" + downloadedBytes);
                progressBar.setProgress((int) downloadPercentage);
            }
        });
        downloader.init();
    }
}
