
package chuangyuan.ycj.videolibrary.video;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chuangyuan.ycj.videolibrary.factory.BufferingLoadControl;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.listener.ExoPlayerListener;
import chuangyuan.ycj.videolibrary.listener.ExoPlayerViewListener;
import chuangyuan.ycj.videolibrary.listener.ItemVideo;
import chuangyuan.ycj.videolibrary.listener.LoadListener;
import chuangyuan.ycj.videolibrary.listener.LoadModelType;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * @author yangc
 *         date 2017/2/28
 *         E-Mail:1007181167@qq.com
 *         Description：积累
 */
public class ExoUserPlayer {

    private static final String TAG = ExoUserPlayer.class.getName();
    /***当前活动**/
    final Activity activity;
    /*** 播放view实例***/
    private final VideoPlayerView videoPlayerView;
    /*** 获取网速大小 ***/
    private long lastTotalRxBytes = 0;
    /*** 获取最后的时间戳 ***/
    private long lastTimeStamp = 0;
    /*** 获取当前进度 ***/
    private long resumePosition;
    /*** 获取当前视频窗口位置 ***/
    private int resumeWindow;
    /*** 定时任务类 ***/
    private ScheduledExecutorService timer;
    /*** 网络状态监听***/
    private NetworkBroadcastReceiver mNetworkBroadcastReceiver;
    /*** view交互回调接口 ***/
    private PlayComponentListener playComponentListener;
    /***  视频状态回调接口 ***/
    protected ComponentListener componentListener;
    /*** 视频回调信息接口 ***/
    private VideoInfoListener videoInfoListener;
    /*** 播放view交互接口 ***/
    private ExoPlayerViewListener mPlayerViewListener;
    /*** 多个视频接口***/
    private VideoWindowListener windowListener;
    /*** 数据来源状态***/
    private boolean playerNeedsSource;
    /*** 是否循环播放  0 不开启***/
    private int loopingCount = 0;
    /*** 内核播放控制**/
    SimpleExoPlayer player;
    /***数据源管理类**/
    MediaSourceBuilder mediaSourceBuilder;
    /*** 屏蔽进度缩索引 ***/
    int indexType = -1;
    List<String> videoUri;
    List<String> nameUri;
    /*** 是否手动暂停 ***/
    Boolean handPause = false;
    /*** 是否已经在只停止恢复 ***/
    Boolean isPause = false;
    /*** 加载模式实例***/
    private LoadModelType modelType;
    /*** 设置播放参数***/
    private PlaybackParameters playbackParameters;


    /****
     * 初始化
     *
     * @param activity   活动对象
     * @param playerView 播放控件
     **/
    public ExoUserPlayer(@NonNull Activity activity, @NonNull VideoPlayerView playerView) {
        this(activity, playerView, null);
    }

    /****
     * @param activity 活动对象
     * @param reId     播放控件id
     **/
    public ExoUserPlayer(@NonNull Activity activity, @IdRes int reId) {
        this(activity, reId, null);
    }

    /****
     * 初始化
     *
     * @param activity 活动对象
     * @param reId     播放控件id
     * @param listener 自定义数据源类
     **/
    public ExoUserPlayer(@NonNull Activity activity, @IdRes int reId, @Nullable DataSourceListener listener) {
        this(activity, (VideoPlayerView) activity.findViewById(reId), listener);
    }

    /****
     * 初始化
     *
     * @param activity   活动对象
     * @param playerView 播放控件
     * @param listener   自定义数据源类
     **/
    public ExoUserPlayer(@NonNull Activity activity, @NonNull VideoPlayerView playerView, @Nullable DataSourceListener listener) {
        this.activity = activity;
        mediaSourceBuilder = new MediaSourceBuilder(listener);
        this.videoPlayerView = playerView;
        componentListener = new ComponentListener();
        playComponentListener = new PlayComponentListener();
        videoPlayerView.setExoPlayerListener(playComponentListener);
        getPlayerViewListener();
    }

    /***
     * 获取交互view接口实例
     * **/
    @NonNull
    protected ExoPlayerViewListener getPlayerViewListener() {
        if (mPlayerViewListener == null) {
            mPlayerViewListener = videoPlayerView.getComponentListener();
        }
        return mPlayerViewListener;
    }

    /***
     * 页面恢复处理
     **/
    @CallSuper
    public void onResume() {
        if ((Util.SDK_INT <= Build.VERSION_CODES.M || player == null)) {
            createPlayers();
        }
    }

    /***
     * 页面暂停处理
     **/
    @CallSuper
    public void onPause() {
        isPause = true;
        if (player != null) {
            handPause = !player.getPlayWhenReady();
            releasePlayers();
        }
    }

    /**
     * 页面销毁处理
     **/
    @CallSuper
    public void onDestroy() {
        releasePlayers();
    }

    /***
     * 设置加载模式  默认 LoadModelType.SPEED
     *
     * @param loadModelType 类型
     *
     **/
    public void setLoadModel(@NonNull LoadModelType loadModelType) {
        modelType = loadModelType;
    }

    /***
     * 设置进度
     *
     * @param resumePosition 毫秒
     **/
    public void setPosition(long resumePosition) {
        this.resumePosition = resumePosition;
    }

    /***
     * 设置进度
     *@param  currWindowIndex  视频索引
     * @param currPosition 毫秒
     **/
    public void setPosition(int currWindowIndex, long currPosition) {
        this.resumeWindow = currWindowIndex;
        this.resumePosition = currPosition;

    }

    /***
     * 返回视频总数
     * @return int
     **/
    public int getWindowCount() {
        if (player == null) {
            return 0;
        }
        return player.getCurrentTimeline().isEmpty() ? 1 : player.getCurrentTimeline().getWindowCount();
    }

    /***
     *   隐藏进度条
     **/
    public void hideSeekBar() {
        getPlayerViewListener().showHidePro(View.INVISIBLE);
    }

    /***
     * 显示隐藏进度条
     **/
    public void showSeekBar() {
        getPlayerViewListener().showHidePro(View.VISIBLE);
    }

    /***
     * 下跳转下一个视频
     **/
    public void next() {
        getPlayerViewListener().next();
    }

    /***
     * 隐藏控制布局
     * ***/
    public void hideControllerView() {
        getPlayerViewListener().hideController();
    }

    /***
     * 设置循环播放视频   Integer.MAX_VALUE 无线循环
     *
     * @param loopingCount  必须大于0
     **/
    public void setLooping(@Size(min = 1) int loopingCount) {
        this.loopingCount = loopingCount;
    }

    public void showControllerView() {
        Assertions.checkArgument(getPlayerViewListener() != null);
        getPlayerViewListener().showControllerView();
    }

    /***
     * 是否播放中
     * @return boolean
     * ***/
    public boolean isPlaying() {
        Assertions.checkArgument(player != null);
        return player.getPlayWhenReady();
    }

    /**
     * 设置播放路径
     *
     * @param uri 路径
     ***/
    public void setPlayUri(@NonNull String uri) {
        setPlayUri(Uri.parse(uri));
    }

    /****
     * @param  indexType 设置当前索引视频屏蔽进度
     * @param firstVideoUri  预览的视频
     * @param secondVideoUri 第二个视频
     **/
    public void setPlayUri(@Size(min = 0) int indexType, @NonNull String firstVideoUri, @NonNull String secondVideoUri) {
        setPlayUri(indexType, Uri.parse(firstVideoUri), Uri.parse(secondVideoUri));
    }

    /****
     * @param firstVideoUri  预览的视频
     * @param secondVideoUri 第二个视频
     **/
    public void setPlayUri(@NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
        setPlayUri(0, firstVideoUri, secondVideoUri);
    }

    /****
     * @param  indexType  设置当前索引视频屏蔽进度
     * @param firstVideoUri  预览的视频
     * @param secondVideoUri 第二个视频

     **/
    public void setPlayUri(@Size(min = 0) int indexType, @NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
        if (mediaSourceBuilder != null) {
            mediaSourceBuilder.release();
        }
        this.indexType = indexType;
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), firstVideoUri, secondVideoUri);
        createPlayers();
        registerReceiverNet();
    }

    /****
     * 设置视频列表播放
     * @param uris  视频列表集合
     **/
    public void setPlayUri(@NonNull Uri... uris) {
        if (mediaSourceBuilder != null) {
            mediaSourceBuilder.release();
        }
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), uris);
        createPlayers();
        registerReceiverNet();
    }
     public <T extends ItemVideo> void setPlayUri(@NonNull List<T> uris) {
        if (mediaSourceBuilder != null) {
            mediaSourceBuilder.release();
        }
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), uris);
        createPlayers();
        registerReceiverNet();
    }

    /**
     * 设置多线路播放
     *
     * @param videoUri 视频地址
     * @param name     清清晰度显示名称
     **/
    public void setPlaySwitchUri(@NonNull String[] videoUri, @NonNull String[] name) {
        setPlaySwitchUri(Arrays.asList(videoUri), Arrays.asList(name));
    }

    /**
     * 设置多线路播放
     *
     * @param videoUri 视频地址
     * @param name     清清晰度显示名称
     * @param index    选中播放线路
     **/
    public void setPlaySwitchUri(@NonNull String[] videoUri, @NonNull String[] name, int index) {
        setPlaySwitchUri(Arrays.asList(videoUri), Arrays.asList(name), index);
    }

    /**
     * 设置多线路播放
     *
     * @param videoUri 视频地址
     * @param name     清清晰度显示名称
     **/
    public void setPlaySwitchUri(@NonNull List<String> videoUri, @NonNull List<String> name) {
        setPlaySwitchUri(videoUri, name, 0);
    }

    /**
     * 设置多线路播放
     *
     * @param videoUri 视频地址
     * @param name     清清晰度显示名称
     * @param index    选中播放线路
     **/
    public void setPlaySwitchUri(@NonNull List<String> videoUri, @NonNull List<String> name, int index) {
        this.videoUri = videoUri;
        this.nameUri = name;
        getPlayerViewListener().showSwitchName(nameUri.get(index));
        if (mediaSourceBuilder != null) {
            mediaSourceBuilder.release();
        }
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), Uri.parse(videoUri.get(index)));
        createPlayers();
        registerReceiverNet();
    }


    /**
     * 设置播放路径
     *
     * @param uri 路径
     ***/
    public void setPlayUri(@NonNull Uri uri) {
        if (mediaSourceBuilder != null) {
            mediaSourceBuilder.release();
        }
        mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), uri);
        createPlayers();
        registerReceiverNet();
    }

    /***
     * 设置倍数播放创建新的回放参数
     *
     * @param speed 播放速度加快   1f 是正常播放 小于1 慢放
     * @param pitch  音高被放大  1f 是正常播放 小于1 慢放
     */
    public void setPlaybackParameters(@Size(min = 0) float speed, @Size(min = 0) float pitch) {
        playbackParameters = new PlaybackParameters(speed, pitch);
    }

    /****
     * 横竖屏切换
     *
     * @param configuration 旋转
     ***/
    public void onConfigurationChanged(Configuration configuration) {
        getPlayerViewListener().onConfigurationChanged(configuration.orientation);
    }

    /***
     * 显示水印图
     *
     * @param res 资源
     ***/
    public void setExoPlayWatermarkImg(int res) {
        getPlayerViewListener().setWatermarkImage(res);
    }

    /***
     * 设置标题
     *
     * @param title 名字
     ***/
    public void setTitle(@NonNull String title) {
        getPlayerViewListener().setTitle(title);
    }


    /***
     * 设置播放或暂停
     * @param value  true 播放  false  暂停
     * **/
    public void setStartOrPause(boolean value) {
        if (player != null) {
            player.setPlayWhenReady(value);
        }
    }

    /***
     * 设置显示多线路图标
     * @param showVideoSwitch true 显示 false 不显示
     * **/
    public void setShowVideoSwitch(boolean showVideoSwitch) {
        getPlayerViewListener().setShowWitch(showVideoSwitch);
    }

    /***
     * 设置显示多线路图标
     * @param isOpenSeek true 启用 false 不启用
     * **/
    public void setSeekBarSeek(boolean isOpenSeek) {
        getPlayerViewListener().setSeekBarOpenSeek(isOpenSeek);
    }

    /***
     * 获取内核播放实例
     * @return SimpleExoPlayer
     * ****/
    public SimpleExoPlayer getPlayer() {
        return player;
    }

    /**
     * 返回视频总进度  以毫秒为单位
     *
     * @return long
     **/
    public long getDuration() {
        return player == null ? 0 : player.getDuration();
    }

    /**
     * 返回视频当前播放进度  以毫秒为单位
     *
     * @return long
     **/
    public long getCurrentPosition() {
        return player == null ? 0 : player.getCurrentPosition();
    }

    /**
     * 返回视频当前播放d缓冲进度  以毫秒为单位
     *
     * @return long
     **/
    public long getBufferedPosition() {
        return player == null ? 0 : player.getBufferedPosition();
    }


    /***
     * 设置视频信息回调
     * @param videoInfoListener 实例
     * ***/
    public void setVideoInfoListener(VideoInfoListener videoInfoListener) {
        this.videoInfoListener = videoInfoListener;
    }

    /***
     * 设置多个视频状态回调
     * @param windowListener 实例
     * ***/
    public void setOnWindowListener(VideoWindowListener windowListener) {
        this.windowListener = windowListener;
    }

    /***
     * 释放资源
     **/
    public void releasePlayers() {
        if (player != null) {
            updateResumePosition();
            unNetworkBroadcastReceiver();
            player.stop();
            player.release();
            player.removeListener(componentListener);
            player = null;
        }
        if (mediaSourceBuilder != null) {
            mediaSourceBuilder.release();
        }
        if (activity.isDestroyed() || activity.isFinishing()) {
            isPause = null;
            handPause = null;
            indexType = -1;
            nameUri = null;
            videoUri = null;
            if (task != null) {
                task.cancel();
            }
            if (timer != null && !timer.isShutdown()) {
                timer.shutdown();
            }
            timer = null;
            task = null;
            mPlayerViewListener = null;
            mediaSourceBuilder = null;
            componentListener = null;
            videoInfoListener = null;
            playComponentListener = null;
        }
    }


    /****
     * 创建
     **/
    void createPlayers() {
        if (player == null) {
            player = createFullPlayer();
            playerNeedsSource = true;
        }
        playVideo();

    }

    /****
     * 创建
     **/
    void createPlayersNo() {
        if (player == null) {
            player = createFullPlayer();
            playerNeedsSource = true;
        }
    }

    /***
     * 设置默认加载
     * **/
    private void setDefaultLoadModel() {
        if (null == timer) {
            timer = Executors.newScheduledThreadPool(2);
            /*1s后启动任务，每1s执行一次**/
            timer.scheduleWithFixedDelay(task, 400, 800, TimeUnit.MILLISECONDS);
        }
    }

    /****
     * 创建
     **/
    void createPlayersPlay() {
        player = createFullPlayer();
    }

    private SimpleExoPlayer createFullPlayer() {
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        BufferingLoadControl loadControl = new BufferingLoadControl();
        if (null == modelType) {
            setDefaultLoadModel();
        } else {
            loadControl.setListener(componentListener);
        }
        RenderersFactory renderersFactory = new DefaultRenderersFactory(activity);
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        if (playbackParameters != null) {
            player.setPlaybackParameters(playbackParameters);

        }
        getPlayerViewListener().setPlayer(player);
        return player;
    }

    /***
     * 播放视频
     **/
    private void playVideo() {
        if (VideoPlayUtils.isWifi(activity)) {
            onPlayNoAlertVideo();
        } else {
            if (isPause) {
                onPlayNoAlertVideo();
            } else {
                getPlayerViewListener().showAlertDialog();
            }
        }
    }

    /***
     * 播放视频
     **/

    protected void onPlayNoAlertVideo() {

        if (player == null) {
            createPlayersPlay();
        }
        if (mPlayerViewListener != null) {
            mPlayerViewListener.showPreview(View.GONE);
        }
        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
        if (haveResumePosition) {
            player.seekTo(resumeWindow, resumePosition);
        }
        if (handPause) {
            player.setPlayWhenReady(false);
        } else {
            player.setPlayWhenReady(true);
        }
        if (loopingCount == 0) {
            player.prepare(mediaSourceBuilder.getMediaSource(), !haveResumePosition, false);
        } else {
            player.prepare(mediaSourceBuilder.setLooping(loopingCount), !haveResumePosition, false);
        }
        player.addListener(componentListener);
        playerNeedsSource = false;
    }

    /****
     * 重置进度
     **/
    void updateResumePosition() {
        if (player != null) {
            resumeWindow = player.getCurrentWindowIndex();
            resumePosition = player.isCurrentWindowSeekable() ? Math.max(0, player.getCurrentPosition())
                    : C.TIME_UNSET;
        }
    }

    /**
     * 清除进度
     ***/
    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    /***
     * 网络变化任务
     **/
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (getPlayerViewListener().isLoadingShow()) {
                getPlayerViewListener().showNetSpeed(getNetSpeed());
            }
        }
    };

    /****
     * 获取当前网速
     *
     * @return String 二返回当前网速字符
     **/
    private String getNetSpeed() {
        String netSpeed;
        long nowTotalRxBytes = VideoPlayUtils.getTotalRxBytes(activity);
        long nowTimeStamp = System.currentTimeMillis();
        long calculationTime = (nowTimeStamp - lastTimeStamp);
        if (calculationTime == 0) {
            netSpeed = String.valueOf(1) + " kb/s";
            return netSpeed;
        }
        //毫秒转换
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / calculationTime);
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        if (speed > 1024) {
            DecimalFormat df = new DecimalFormat("######0.0");
            netSpeed = String.valueOf(df.format(VideoPlayUtils.getM(speed))) + " MB/s";
        } else {
            netSpeed = String.valueOf(speed) + " kb/s";
        }
        return netSpeed;
    }

    /****
     * 监听返回键 true 可以正常返回处理，false 切换到竖屏
     *
     * @return boolean
     ***/
    public boolean onBackPressed() {
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getPlayerViewListener().exitFull();
            return false;
        } else {
            return true;
        }
    }


    /***
     * 注册广播监听
     **/
    void registerReceiverNet() {
        if (mNetworkBroadcastReceiver == null) {
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mNetworkBroadcastReceiver = new NetworkBroadcastReceiver();
            activity.registerReceiver(mNetworkBroadcastReceiver, intentFilter);
        }
    }

    /***
     * 取消广播监听
     **/
    void unNetworkBroadcastReceiver() {
        if (mNetworkBroadcastReceiver != null) {
            activity.unregisterReceiver(mNetworkBroadcastReceiver);
        }
        mNetworkBroadcastReceiver = null;
    }

    /***
     * 网络监听类
     ***/
    private class NetworkBroadcastReceiver extends BroadcastReceiver {
        long is = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {
                    /////////////网络连接
                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        /////WiFi网络
                    } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        /////////3g网络
                        if (System.currentTimeMillis() - is > 500) {
                            is = System.currentTimeMillis();
                            if (!isPause) {
                                getPlayerViewListener().showAlertDialog();
                            }
                        }
                    }
                    Log.d(TAG, "onReceive:" + netInfo.getType() + "__:");
                }
            }

        }
    }

    /****
     * 播放回调view事件处理
     * ***/
    private class PlayComponentListener implements ExoPlayerListener {
        @Override
        public void onCreatePlayers() {
            createPlayers();
        }

        @Override
        public void onClearPosition() {
            clearResumePosition();

        }

        @Override
        public void replayPlayers() {
            clearResumePosition();
            onPlayNoAlertVideo();
        }

        @Override
        public void switchUri(int position, String name) {
            if (mediaSourceBuilder != null) {
                mediaSourceBuilder.release();
            }
            mediaSourceBuilder.setMediaSourceUri(activity.getApplicationContext(), Uri.parse(videoUri.get(position)));
            updateResumePosition();
            onPlayNoAlertVideo();
        }

        @Override
        public void playVideoUri() {
            onPlayNoAlertVideo();

        }

        @Override
        public ExoUserPlayer getPlay() {
            return ExoUserPlayer.this;
        }


        @Override
        public void onBack() {
            activity.onBackPressed();
        }
    }

    /***
     * view 给控制类 回调类
     * ***/
    private class ComponentListener implements Player.EventListener, LoadListener {
        /***
         * 视频播放播放
         **/
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.d(TAG, "onTracksChanged:" + player.getCurrentWindowIndex() + "_:" + player.getCurrentTimeline().getWindowCount());

            if (getWindowCount() > 1) {
                if (windowListener != null && newIndex == 0) {
                    windowListener.onCurrentIndex(newIndex, getWindowCount());
                }
                if (indexType < 0) {
                    return;
                }
                GestureVideoPlayer gestureVideoPlayer = null;
                if (ExoUserPlayer.this instanceof GestureVideoPlayer) {
                    gestureVideoPlayer = (GestureVideoPlayer) ExoUserPlayer.this;
                }
                boolean setOpenSeek = 0 != indexType;
                if (gestureVideoPlayer != null) {
                    gestureVideoPlayer.setPlayerGestureOnTouch(setOpenSeek);
                }
                getPlayerViewListener().getTimeBarView().setOpenSeek(setOpenSeek);
            }
        }

        private int newIndex;

        @Override
        public void onPositionDiscontinuity() {
            Log.d(TAG, "onPositionDiscontinuity:" + player.getCurrentWindowIndex() + "_:" + player.getCurrentTimeline().getWindowCount());
            if (getWindowCount() > 1) {
                boolean is = windowListener != null && newIndex != player.getCurrentWindowIndex();
                if (is) {
                    newIndex = player.getCurrentWindowIndex();
                    windowListener.onCurrentIndex(player.getCurrentWindowIndex(), getWindowCount());
                }
                if (indexType < 0) {
                    return;
                }
                GestureVideoPlayer gestureVideoPlayer = null;
                if (ExoUserPlayer.this instanceof GestureVideoPlayer) {
                    gestureVideoPlayer = (GestureVideoPlayer) ExoUserPlayer.this;
                }
                boolean setOpenSeek = !(indexType == newIndex & indexType > 0);
                if (gestureVideoPlayer != null) {
                    gestureVideoPlayer.setPlayerGestureOnTouch(setOpenSeek);
                }
                getPlayerViewListener().getTimeBarView().setOpenSeek(setOpenSeek);
            }

        }

        /*****
         * 进度条控制 加载页
         *********/
        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        /**
         * 视频的播放状态
         * STATE_IDLE 播放器空闲，既不在准备也不在播放
         * STATE_PREPARING 播放器正在准备
         * STATE_BUFFERING 播放器已经准备完毕，但无法立即播放。此状态的原因有很多，但常见的是播放器需要缓冲更多数据才能开始播放
         * STATE_ENDED 播放已完毕
         */
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playWhenReady) {
                //防锁屏
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                //解锁屏
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            if (videoInfoListener != null) {
                videoInfoListener.isPlaying(player.getPlayWhenReady());
            }
            Log.d(TAG, "onPlayerStateChanged:" + playbackState + "+playWhenReady:" + playWhenReady);
            switch (playbackState) {
                case Player.STATE_BUFFERING:
                    Log.d(TAG, "onPlayerStateChanged:加载中。。。");
                    if (playWhenReady) {
                        getPlayerViewListener().showLoadStateView(View.VISIBLE);
                    }
                    if (videoInfoListener != null) {
                        videoInfoListener.onLoadingChanged();
                    }
                    break;
                case Player.STATE_ENDED:
                    Log.d(TAG, "onPlayerStateChanged:ended。。。");
                    newIndex = 0;
                    getPlayerViewListener().showReplayView(View.VISIBLE);
                    if (videoInfoListener != null) {
                        videoInfoListener.onPlayEnd();
                    }
                    break;
                case Player.STATE_IDLE:
                    Log.d(TAG, "onPlayerStateChanged::网络状态差，请检查网络。。。");
                    updateResumePosition();
                    if (!VideoPlayUtils.isNetworkAvailable(activity)) {
                        if (playerNeedsSource) {
                            getPlayerViewListener().showErrorStateView(View.VISIBLE);
                        }
                    } else {
                        getPlayerViewListener().showErrorStateView(View.VISIBLE);
                    }
                    break;
                case Player.STATE_READY:
                    Log.d(TAG, "onPlayerStateChanged:ready。。。");
                    getPlayerViewListener().showLoadStateView(View.GONE);
                    if (videoInfoListener != null) {
                        isPause = false;
                        videoInfoListener.onPlayStart();
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            if (videoInfoListener != null) {
                videoInfoListener.onRepeatModeChanged(repeatMode);
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            Log.e(TAG, "onPlayerError:" + e.getMessage());
            updateResumePosition();
            playerNeedsSource = true;
            if (VideoPlayUtils.isBehindLiveWindow(e)) {
                clearResumePosition();
                playVideo();
            } else {
                getPlayerViewListener().showErrorStateView(View.VISIBLE);
                if (videoInfoListener != null) {
                    videoInfoListener.onPlayerError(e);
                }
            }
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }

        @Override
        public void onProgress(long pro) {
            getPlayerViewListener().showNetSpeed(String.valueOf(pro) + "%");
        }
    }
}

