package chuangyuan.ycj.videolibrary.video;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Util;
import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;

/**
 * Created by yangc on 2017/2/27.
 * E-Mail:1007181167@qq.com
 * Description： 手动播放播放器
 */
public class ManualPlayer extends GestureVideoPlayer {
    public static final String TAG = "ManualPlayer";
    private boolean isLoad = false;//已经加载

    public ManualPlayer(Activity activity, String url) {
        super(activity, url);
        setExoPlayWatermarkImg(R.mipmap.watermark_big);
    }

    public ManualPlayer(Activity activity, SimpleExoPlayerView playerView, String url) {
        super(activity, playerView, url);
    }

    public ManualPlayer(Activity activity, SimpleExoPlayerView playerView, Uri url) {
        super(activity, playerView, url);
    }

    @Override
    public void onStart() {
        if (Util.SDK_INT > 23 && isLoad) {
            createPlayers();
        } else {
            createPlayersPlay();
        }
    }

    @Override
    public void onResume() {
        if ((Util.SDK_INT <= 23 || player == null) && isLoad) {
            createPlayers();
        } else {
            createPlayersPlay();
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "onPlayerStateChanged:+playWhenReady:" + playWhenReady);
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                Log.d(TAG, "onPlayerStateChanged:加载中。。。");
                if (playWhenReady) {
                    showLoadStateView(View.VISIBLE);
                }
                break;
            case ExoPlayer.STATE_ENDED:
                Log.d(TAG, "onPlayerStateChanged:ended。。。");
                showReplayView(View.VISIBLE);
                if (videoInfoListener != null) {
                    videoInfoListener.onPlayEnd();
                }
                break;
            case ExoPlayer.STATE_IDLE://空的
                Log.d(TAG, "onPlayerStateChanged:请检查网络。。。");
                if (VideoPlayUtils.isNetworkAvailable(activity)) {
                    if (!isLoad) {
                        playVideo();
                        isLoad = true;
                    } else {
                        if (!playerNeedsSource) {
                            showErrorStateView(View.VISIBLE);
                            updateResumePosition();
                        }
                    }
                } else {
                    showErrorStateView(View.VISIBLE);
                    updateResumePosition();
                }
                break;
            case ExoPlayer.STATE_READY:
                Log.d(TAG, "onPlayerStateChanged:ready。。。");
                showLoadStateView(View.GONE);
                if (videoInfoListener != null) {
                    videoInfoListener.onPlayStart();
                }
                break;
            default:
                break;
        }
    }
}
