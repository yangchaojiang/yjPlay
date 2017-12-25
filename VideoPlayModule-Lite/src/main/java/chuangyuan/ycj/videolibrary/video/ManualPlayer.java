package chuangyuan.ycj.videolibrary.video;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.util.Util;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * author yangc
 * date 2017/2/27
 * E-Mail:1007181167@qq.com
 * Description： 手动控制播放播放器
 */
public final class ManualPlayer extends GestureVideoPlayer {


    /**
     * Instantiates a new Manual player.
     *
     * @param activity the activity
     * @param reId     the re id
     */
    public ManualPlayer(@NonNull Activity activity, @IdRes int reId) {
        this(activity, reId, null);
    }

    /**
     * Instantiates a new Manual player.
     *
     * @param activity   the activity
     * @param playerView the player view
     */
    public ManualPlayer(@NonNull Activity activity, @NonNull VideoPlayerView playerView) {
        this(activity, playerView, null);
    }

    /**
     * Instantiates a new Manual player.
     *
     * @param activity the activity
     * @param reId     the re id
     * @param listener the listener
     */
    public ManualPlayer(@NonNull Activity activity, @IdRes int reId, @Nullable DataSourceListener listener) {
        this(activity, (VideoPlayerView) activity.findViewById(reId), listener);
    }

    /**
     * Instantiates a new Manual player.
     *
     * @param activity   the activity
     * @param playerView the player view
     * @param listener   the listener
     */
    public ManualPlayer(@NonNull Activity activity, @NonNull VideoPlayerView playerView, @Nullable DataSourceListener listener) {
        super(activity, playerView, listener);
        getPlayerViewListener().setControllerHideOnTouch(false);
        getPlayerViewListener().setPlayerBtnOnTouch(onTouchListener);
    }

    /**
     * Instantiates a new Manual player.
     *
     * @param activity           the activity
     * @param mediaSourceBuilder the media source builder
     * @param playerView         the player view
     */
    public ManualPlayer(@NonNull Activity activity, @NonNull MediaSourceBuilder mediaSourceBuilder, @NonNull VideoPlayerView playerView) {
        super(activity, mediaSourceBuilder, playerView);
        getPlayerViewListener().setControllerHideOnTouch(false);
        getPlayerViewListener().setPlayerBtnOnTouch(onTouchListener);
    }

    /***
     * 启动播放视频
     * */
    @Override
    public void startPlayer() {
        if (getPlayerViewListener().isList()) {
            handPause = false;
            VideoPlayerManager.getInstance().setCurrentVideoPlayer(ManualPlayer.this);
        }
        getPlayerViewListener().setPlayerBtnOnTouch(null);
        createPlayers();
        registerReceiverNet();
    }

    @Override
    public void onResume() {
        boolean is = (Util.SDK_INT <= Build.VERSION_CODES.M || player == null) && isLoad;
        if (is) {
            if (getPlayerViewListener().isList()) {
                getPlayerViewListener().setPlayerBtnOnTouch(onTouchListener);
            } else {
                createPlayers();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 列表暂停
     */
    void onListPause() {
        isPause = true;
        if (player != null) {
            handPause = !player.getPlayWhenReady();
            reset(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onTouchListener = null;

    }

    /**
     * 重置
     *
     * @param s s
     */
    public void reset(boolean s) {
        if (player != null) {
            unNetworkBroadcastReceiver();
            if (isEnd || s) {
                setPosition(0);
            } else {
                updateResumePosition();
            }
            player.stop();
            player.removeListener(componentListener);
            getPlayerViewListener().setPlayerBtnOnTouch(onTouchListener);
            getPlayerViewListener().reset();
            player.release();
            if (mediaSourceBuilder != null) {
                mediaSourceBuilder.release();
            }
            player = null;
        }
    }


}
