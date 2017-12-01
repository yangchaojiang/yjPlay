package chuangyuan.ycj.videolibrary.video;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.exoplayer2.util.Util;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * @author yangc
 *         date 2017/2/27
 *         E-Mail:1007181167@qq.com
 *         Description： 手动控制播放播放器
 */
public final class ManualPlayer extends GestureVideoPlayer {
    private View.OnTouchListener onTouchListener;
    private View.OnClickListener onClickListener;

    public ManualPlayer(@NonNull Activity activity, @NonNull VideoPlayerView playerView) {
        this(activity, playerView, null);
    }

    public ManualPlayer(@NonNull Activity activity, @IdRes int reId) {
        this(activity, reId, null);
    }

    public ManualPlayer(@NonNull Activity activity, @IdRes int reId, @Nullable DataSourceListener listener) {
        this(activity, (VideoPlayerView) activity.findViewById(reId), listener);
    }

    public ManualPlayer(@NonNull Activity activity, @NonNull VideoPlayerView playerView, @Nullable DataSourceListener listener) {
        super(activity, playerView, listener);
        intiView();
    }

    public ManualPlayer(@NonNull Activity activity, @NonNull MediaSourceBuilder mediaSourceBuilder, @NonNull VideoPlayerView playerView) {
        super(activity, mediaSourceBuilder, playerView);
        intiView();
    }

    private void intiView() {
        onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (onClickListener != null) {
                        onClickListener.onClick(v);
                    } else {
                        startPlayer();
                    }
                }
                return false;
            }
        };
        getPlayerViewListener().setControllerHideOnTouch(false);
        getPlayerViewListener().setPlayerBtnOnTouchListener(onTouchListener);
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
        getPlayerViewListener().setPlayerBtnOnTouchListener(null);
        createPlayers();
        registerReceiverNet();
    }

    @Override
    public void onResume() {
        boolean is = (Util.SDK_INT <= Build.VERSION_CODES.M || player == null) && isLoad;
        if (is) {
            if (getPlayerViewListener().isList()) {
                getPlayerViewListener().setPlayerBtnOnTouchListener(onTouchListener);
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
     **/
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
        onClickListener = null;
    }

    /**
     * 重置
     * @param s s
     **/

    public void reset(boolean s) {
        if (player != null) {
            unNetworkBroadcastReceiver();
            if (isEnd||s) {
                setPosition(0);
            } else {
                updateResumePosition();
            }
            player.stop();
            player.removeListener(componentListener);
            getPlayerViewListener().setPlayerBtnOnTouchListener(onTouchListener);
            getPlayerViewListener().reset();
            player.release();
            if (mediaSourceBuilder != null) {
                mediaSourceBuilder.release();
            }
            player = null;
        }
    }

    /****
     * 设置点击播放按钮回调, 交给用户处理
     * @param onClickListener 回调实例
     * ***/
    public void setOnPlayClickListener(@Nullable View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
