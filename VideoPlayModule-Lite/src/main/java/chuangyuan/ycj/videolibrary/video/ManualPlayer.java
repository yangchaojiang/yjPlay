package chuangyuan.ycj.videolibrary.video;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.exoplayer2.util.Util;

import java.util.WeakHashMap;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * author yangc
 * date 2017/2/27
 * E-Mail:1007181167@qq.com
 * Description： 手动控制播放播放器
 */
 public final class ManualPlayer extends GestureVideoPlayer {
    private  static  final String TAG=ManualPlayer.class.getName();
    /**
     * 记录视频进度缓存map
     **/
    private static WeakHashMap<Integer, Long> tags = new WeakHashMap<>();
    /**
     * 记录视频当前窗口缓存map
     **/
    private static WeakHashMap<Integer, Integer> tags2 = new WeakHashMap<>();
    private int position;

    /**
     * Instantiates a new Manual player.
     *
     * @param activity the activity
     * @param reId     the re id
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
     */
    public ManualPlayer(@NonNull Activity activity, @IdRes int reId) {
        this(activity, reId, null);
    }

    /**
     * Instantiates a new Manual player.
     *
     * @param activity   the activity
     * @param playerView the player view
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
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
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
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
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
     */
    public ManualPlayer(@NonNull Activity activity, @NonNull VideoPlayerView playerView, @Nullable DataSourceListener listener) {
        super(activity, playerView, listener);
        getPlayerViewListener().setControllerHideOnTouch(false);
        getPlayerViewListener().setPlayerBtnOnTouch(true);
    }

    /**
     * Instantiates a new Manual player.
     *
     * @param activity           the activity
     * @param mediaSourceBuilder the media source builder
     * @param playerView         the player view
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
     */
    public ManualPlayer(@NonNull Activity activity, @NonNull MediaSourceBuilder mediaSourceBuilder, @NonNull VideoPlayerView playerView) {
        super(activity, mediaSourceBuilder, playerView);
        getPlayerViewListener().setControllerHideOnTouch(false);
        getPlayerViewListener().setPlayerBtnOnTouch(true);
    }


    @Override
    public <R extends ExoUserPlayer> R startPlayer() {
        if (getPlayerViewListener().isList()) {
            handPause = false;
            VideoPlayerManager.getInstance().setCurrentVideoPlayer(ManualPlayer.this);
            if (tags.get(position) != null && tags2.get(position) != null) {
                int positions = tags.get(position).intValue();
                int index = tags2.get(position);
                setPosition(index, positions);
                tags.remove(position);
                tags2.remove(position);
            }
        }
        getPlayerViewListener().setPlayerBtnOnTouch(false);
        createPlayers();
        registerReceiverNet();
        return (R) this;
    }

    /***
     * 启动播放视频
     * */



    @Override
    public void onResume() {
        boolean is = (Util.SDK_INT <= Build.VERSION_CODES.M || player == null) && isLoad;
        if (is) {
            if (getPlayerViewListener().isList()) {
                getPlayerViewListener().setPlayerBtnOnTouch(true);
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
     *
     * @param reset 是否重置的 true  重置 false
     */
    void onListPause(boolean reset) {
        if (reset) {
            isPause = true;
            if (player != null) {
                handPause = !player.getPlayWhenReady();
                reset();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tags.clear();
        tags2.clear();
    }

    /**
     * 重置
     */
    @Override
    public void reset() {
        if (player != null) {
            unNetworkBroadcastReceiver();
            if (position == -1) {
                clearResumePosition();
            } else {
                tags.put(position, player.getCurrentPosition());
                tags2.put(position, player.getCurrentWindowIndex());
            }
            player.stop();
            player.removeListener(componentListener);
            resetInit();
            player.release();
            player = null;
        }

    }

    /***
     * 重置点击事件
     * **/
    void resetInit() {
        getPlayerViewListener().setPlayerBtnOnTouch(true);
         getPlayerViewListener().reset();
    }

    /****
     * 设置tag 标记 防止列表复用进度导致,
     * @param position  position
     * **/
    public void setTag(int position) {
        this.position = position;
    }
}
