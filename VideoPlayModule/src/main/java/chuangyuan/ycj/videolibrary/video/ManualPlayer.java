package chuangyuan.ycj.videolibrary.video;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.exoplayer2.util.Util;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.listener.ItemVideo;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * @author yangc
 *         date 2017/2/27
 *         E-Mail:1007181167@qq.com
 *         Description： 手动控制播放播放器
 */
public class ManualPlayer extends GestureVideoPlayer {
    /*** 已经加载 ***/
    private boolean isLoad = false;
    private View.OnTouchListener onTouchListener;
    private View.OnClickListener onClickListener;
    Lock lock = new ReentrantLock();

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

    private void intiView() {
        getPlayerViewListener().setControllerHideOnTouch(false);
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
        getPlayerViewListener().setPlayerBtnOnTouchListener(onTouchListener);
    }


    /***
     * 启动播放视频
     * */
    public void startPlayer() {
        isLoad = true;
        if (getPlayerViewListener().isList()) {
            handPause = false;
            VideoPlayerManager.getInstance().setCurrentVideoPlayer(ManualPlayer.this);
        }
        getPlayerViewListener().setPlayerBtnOnTouchListener(null);
        getPlayerViewListener().setControllerHideOnTouch(true);
        createPlayers();
        registerReceiverNet();
    }

    @Override
    public void setPlayUri(@NonNull Uri uri) {
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), uri);
        sss();
    }

    @Override
    public void setPlaySwitchUri(@NonNull List<String> videoUri, @NonNull List<String> name, int index) {
        this.videoUri = videoUri;
        this.nameUri = name;
        getPlayerViewListener().showSwitchName(name.get(index));
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), Uri.parse(videoUri.get(index)));
        sss();
    }

    @Override
    public void setPlayUri(@Size(min = 0) int indexType, @NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
        this.indexType = indexType;
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), firstVideoUri, secondVideoUri);
        sss();
    }

    @Override
    public void setPlayUri(@NonNull Uri... uris) {
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), uris);

    }

    @Override
    public <T extends ItemVideo> void setPlayUri(@NonNull List<T> uris) {
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), uris);
        sss();
    }

    private void sss() {
        getPlayerViewListener().setPlayerBtnOnTouchListener(onTouchListener);
        createPlayersNo();
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
        } else {
            createPlayersPlay();
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
            reset();
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
     **/

    public void reset() {

        if (player != null) {
            updateResumePosition();
            unNetworkBroadcastReceiver();
            player.stop();
            player.removeListener(componentListener);
            getPlayerViewListener().setPlayerBtnOnTouchListener(onTouchListener);
            getPlayerViewListener().showPreview(View.VISIBLE);
            getPlayerViewListener().reset();
            player.release();
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
