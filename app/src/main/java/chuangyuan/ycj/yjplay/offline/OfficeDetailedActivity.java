package chuangyuan.ycj.yjplay.offline;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.offline.Downloader;

import chuangyuan.ycj.videolibrary.factory.JDefaultDataSourceFactory;
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
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_office);
        videoPlayerView = (VideoPlayerView) findViewById(R.id.exo_play_context_id);
        exoPlayerManager = new GestureVideoPlayer(this, videoPlayerView,
                new OfficeDataSource(this, null));
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        exoPlayerManager.setTitle("视频标题");
        findViewById(R.id.button10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayerManager.setPlayUri(getString(R.string.uri_test_h));
                exoPlayerManager.startPlayer();
            }
        });
        button = (Button) findViewById(R.id.button11);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayerManager.setPlayUri(getString(R.string.uri_test_1));
                if (progressBar.getProgress() == 100) {
                    exoPlayerManager.startPlayer();
                } else {
                    customDwon();
                }


            }
        });
        Glide.with(this)
                .load(getString(R.string.uri_test_image)).fitCenter()
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
                .setMaxCacheSize(100000000)
                //设置你缓存目录
                .setCacheFileDir(this.getExternalCacheDir().getAbsolutePath())
                //缓存文件加密,那么在使用AES / CBC的文件系统中缓存密钥将被加密  密钥必须是16字节长.
                .setSecretKey("1234567887654321".getBytes())
                .setUri(getString(R.string.uri_test_1))
                //设置下载数据加载工厂类
                .setHttpDataSource(new JDefaultDataSourceFactory(this))
                .build();
        downloader.init();
        if ((int) downloader.getDownloadPercentage() == 100) {
            Toast.makeText(getApplicationContext(), "下载完成" + downloader.getDownloadPercentage(), Toast.LENGTH_SHORT).show();
            progressBar.setProgress(100);
            button.setText("播放");
            exoPlayerManager.startPlayer();
        } else {
            downloader.download(new Downloader.ProgressListener() {
                @Override
                public void onDownloadProgress(Downloader downloader, float downloadPercentage, long downloadedBytes) {
                    Log.d(TAG, "downloadPercentage:" + downloadPercentage + "downloadedBytes:" + downloadedBytes);
                    progressBar.setProgress((int) downloadPercentage);
                    if ((int) downloadPercentage == 100) {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "下载完成", Toast.LENGTH_SHORT).show();
                        button.setText("播放");
                        Looper.loop();
                    }
                }
            });

        }
    }
    }
