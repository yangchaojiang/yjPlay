package chuangyuan.ycj.yjplay.mp3;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.PlayerControlView;

import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.utils.AnimUtils;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.Data2Source;

public class Mp3SimpleActivity extends Activity {

    private ExoUserPlayer exoPlayerManager;
    private static final String TAG = "Mp3SimpleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3_view);
        PlayerControlView s=findViewById(R.id.mPlayerControlView);
        exoPlayerManager = new VideoPlayerManager.Builder(this,s)
                .setDataSource(new Data2Source(this))
                //设置水印图
              //  .setPlayUri("http://oph6zeldx.bkt.clouddn.com/20130104095750-MzE1ODU1.mp3")
                .setPlayUri("http://pj8st4lpc.bkt.clouddn.com/noaudio.ts")
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
                        Log.d(TAG, "currPosition:" + position + "______bufferedPosition:" + bufferedPosition + ":_______duration:" + duration);
                    }
                })
                .create()
                .startPlayer();
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
    protected void onDestroy() {
        super.onDestroy();
        exoPlayerManager.onDestroy();

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
