package chuangyuan.ycj.videolibrary.video;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.exoplayer2.util.Util;

import java.util.List;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.listener.ItemVideo;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * Created by yangc on 2017/2/27.
 * E-Mail:1007181167@qq.com
 * Description： 手动控制播放播放器
 */
public class ManualPlayer extends GestureVideoPlayer {
    /*** 已经加载 ***/
    private boolean isLoad = false;
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

    private void intiView() {
        mPlayerViewListener.setControllerHideOnTouch(false);
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
        mPlayerViewListener.setControllerHideOnTouch(false);
        mPlayerViewListener.setPlayerBtnOnTouchListener(onTouchListener);
    }

    /***
     * 启动播放视频
     * */
    public void startPlayer() {
        isLoad = true;
        if (getPlayerView().isListPlayer()) {
            handPause = false;
            VideoPlayerManager.getInstance().setCurrentVideoPlayer(ManualPlayer.this);
        }
        mPlayerViewListener.showPreview(View.GONE);
        mPlayerViewListener.setPlayerBtnOnTouchListener(null);
        mPlayerViewListener.setControllerHideOnTouch(true);
        createPlayers();
        registerReceiverNet();
    }

    @Override
    public void setPlayUri(@NonNull Uri uri) {
        mPlayerViewListener.setPlayerBtnOnTouchListener(onTouchListener);
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), uri);
        createPlayersNo();
    }

    @Override
    public void setPlaySwitchUri(@NonNull List<String> videoUri, @NonNull List<String> name, int index) {
        mPlayerViewListener.setPlayerBtnOnTouchListener(onTouchListener);
        this.videoUri = videoUri;
        this.nameUri = name;
        mPlayerViewListener.showSwitchName(name.get(index));
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), Uri.parse(videoUri.get(index)));
        createPlayersNo();
    }

    @Override
    public void setPlayUri(@NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri, int indexType) {
        this.indexType = indexType;
        mPlayerViewListener.setPlayerBtnOnTouchListener(onTouchListener);
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), firstVideoUri, secondVideoUri);
        createPlayersNo();
    }

    @Override
    public void setPlayUri(@NonNull Uri... uris) {
        mPlayerViewListener.setPlayerBtnOnTouchListener(onTouchListener);
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), uris);
        createPlayersNo();
    }

    @Override
    public void setPlayUri(@NonNull List<ItemVideo> uris) {
        mPlayerViewListener.setPlayerBtnOnTouchListener(onTouchListener);
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), uris);
        createPlayersNo();
    }

    @Override
    public void onResume() {
        if ((Util.SDK_INT <= 23 || player == null) && isLoad) {
            if (getPlayerView().isListPlayer()) {
                mPlayerViewListener.setPlayerBtnOnTouchListener(onTouchListener);
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
        releasePlayers();
        if (mPlayerViewListener != null) {
            mPlayerViewListener.setPlayerBtnOnTouchListener(onTouchListener);
            mPlayerViewListener.showPreview(View.VISIBLE);
            mPlayerViewListener.reset();
        }
    }

    /****
     * 设置点击播放按钮回调, 交给用户处理
     * @param onClickListener 回调实例
     * ***/
    public void setOnPlayClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
