package chuangyuan.ycj.yjplay.offline;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.offline.DownloadManager;

import chuangyuan.ycj.videolibrary.office.ExoWholeDownLoadManger;
import chuangyuan.ycj.videolibrary.office.ExoWholeDownloadTracker;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.OfficeDataSource;

public class OfficeDetailedActivity extends Activity {
    private ExoWholeDownloadTracker exoDownloadTracker;
    // private ExoDownloadTracker exoDownloadTracker;
    private ExoUserPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    private static final String TAG = "OfficeDetailedActivity";
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_office);
        exoDownloadTracker = ExoWholeDownLoadManger.getSingle().getExoDownloadTracker();
        //  exoDownloadTracker = ExoDownLoadManger.getSingle().getExoDownloadTracker();
        videoPlayerView = findViewById(R.id.exo_play_context_id);
        exoPlayerManager = new VideoPlayerManager
                .Builder(VideoPlayerManager.TYPE_PLAY_GESTURE, videoPlayerView)
                .setDataSource(new OfficeDataSource(this, null))
                .setTitle("视频标题")
                .create();

        findViewById(R.id.button10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayerManager.setCustomCacheKey("123456789012345667");
                exoPlayerManager.setPlayUri(getString(R.string.uri_test_1));
                exoPlayerManager.startPlayer();
            }
        });
        button = findViewById(R.id.button11);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exoDownloadTracker.isDownloaded(Uri.parse(getString(R.string.uri_test_3)))) {
                    exoPlayerManager.setPlayUri(getString(R.string.uri_test_8));
                    exoPlayerManager.startPlayer();
                } else {
                    customDown();
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
    private void customDown() {
        exoDownloadTracker.toggleDownload(this, "视频标题",
                Uri.parse(getString(R.string.uri_test_3)),
                null,"customCacheKey");
        exoDownloadTracker.addListener(new ExoWholeDownloadTracker.Listener() {
            @Override
            public void onDownloadsChanged(int taskState) {
                Log.d(TAG, "taskState：" + taskState);
                if (taskState == DownloadManager.TaskState.STATE_COMPLETED) {
                    exoPlayerManager.setPlayUri(getString(R.string.uri_test_8));
                    exoPlayerManager.startPlayer();
                }

            }
        });
    }
}
