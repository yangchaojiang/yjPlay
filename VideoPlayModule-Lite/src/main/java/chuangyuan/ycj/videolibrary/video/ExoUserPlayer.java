package chuangyuan.ycj.videolibrary.video;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.util.Util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chuangyuan.ycj.videolibrary.listener.BasePlayerListener;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.listener.ExoPlayerViewListener;
import chuangyuan.ycj.videolibrary.listener.ItemVideo;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * The type Exo user player.
 * author yangc   date 2017/2/28
 * E-Mail:1007181167@qq.com
 * Description：积累
 */
public class ExoUserPlayer {
    private static final String TAG = ExoUserPlayer.class.getName();
    private Context activity;
    /*** 获取网速大小,获取最后的时间戳,获取当前进度 ***/
    private Long resumePosition = 0L, lastTotalRxBytes = 0L, lastTimeStamp = 0L;
    /*** 是否循环播放  0 不开启,获取当前视频窗口位置***/
    private int resumeWindow = 0;
    /*** 是否手动暂停*/
    boolean handPause;
    /*** 播放结束,是否选择多分辨率,是否已经在停止恢复,,已经加载,* **/
    private boolean isEnd, isSwitch, isPause, isLoad;
    /*** 定时任务类 ***/
    private ScheduledExecutorService timer;
    /*** 网络状态监听***/
    private NetworkBroadcastReceiver mNetworkBroadcastReceiver;
    /*** view交互回调接口 ***/
    private PlayComponent playComponentListener;
    /*** 视频回调信息接口 ***/
    private final CopyOnWriteArraySet<VideoInfoListener> videoInfoListeners;
    /*** 多个窗口***/
    private final CopyOnWriteArraySet<VideoWindowListener> videoWindowListeners;
    /*** base接口***/
    private final CopyOnWriteArraySet<BasePlayerListener> basePlayerListeners;
    /*** base接口***/
    private final CopyOnWriteArraySet<ExoPlayerViewListener> mPlayerViewListeners;
    /*** 内核播放控制*/
    private SimpleExoPlayer player;
    /***数据源管理类*/
    private MediaSourceBuilder mediaSourceBuilder;
    /*** 设置播放参数***/
    private PlaybackParameters playbackParameters;
    /*** 如果DRM得到保护，可能是null ***/
    private DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
    private VideoPlayerView videoPlayerView;


    /***
     * 初始化
     * @param activity 活动对象
     * @param playerView 播放控件
     * @param listener 自定义数据源类
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
     */
    public ExoUserPlayer(@NonNull Context activity, @NonNull VideoPlayerView playerView, @Nullable DataSourceListener listener) {
        this(activity, VideoPlayUtils.buildMediaSourceBuilder(activity, listener), playerView);
    }

    /****
     * 初始化
     * @param activity 活动对象
     * @param mediaSourceBuilder 自定义数据源类
     * @param  playerView playerView;
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
     */
    public ExoUserPlayer(@NonNull Context activity, @NonNull MediaSourceBuilder mediaSourceBuilder, @NonNull VideoPlayerView playerView) {
        this.activity = activity.getApplicationContext();
        this.videoPlayerView = playerView;
        this.mediaSourceBuilder = mediaSourceBuilder;
        videoInfoListeners = new CopyOnWriteArraySet<>();
        videoWindowListeners = new CopyOnWriteArraySet<>();
        basePlayerListeners = new CopyOnWriteArraySet<>();
        mPlayerViewListeners = new CopyOnWriteArraySet<>();
        playComponentListener = new PlayComponent(this);
        playerView.setExoPlayerListener(playComponentListener);
        addExoPlayerViewListener(playerView.getComponentListener());
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.setPlayerBtnOnTouch(true);
        }
    }

    /***
     * 初始化
     * @param activity 活动对象
     * @param  mediaSourceBuilder 媒体加载来源实例;
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
     */
    public ExoUserPlayer(@NonNull Context activity, @NonNull MediaSourceBuilder mediaSourceBuilder) {
        this.activity = activity.getApplicationContext();
        videoInfoListeners = new CopyOnWriteArraySet<>();
        videoWindowListeners = new CopyOnWriteArraySet<>();
        basePlayerListeners = new CopyOnWriteArraySet<>();
        mPlayerViewListeners = new CopyOnWriteArraySet<>();
        playComponentListener = new PlayComponent(this);
        this.mediaSourceBuilder = mediaSourceBuilder;
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.setPlayerBtnOnTouch(true);
        }
    }

    /***
     * 网络变化任务
     **/
    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                item.showNetSpeed(getNetSpeed());
            }
        }
    };

    /***
     * 页面恢复处理
     */
    public void onResume() {
        boolean is = (Util.SDK_INT <= Build.VERSION_CODES.M || null == player) && isLoad && !isEnd;
        if (is) {
            for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                item.onResumeStart();
            }
        }
    }

    /***
     * 页面暂停处理
     */
    @CallSuper
    public void onPause() {
        isPause = true;
        if (player != null) {
            handPause = !player.getPlayWhenReady();
            releasePlayers();
        }
    }

    @CallSuper
    public void onStop() {
        onPause();
    }

    /**
     * 页面销毁处理
     */
    @CallSuper
    public void onDestroy() {
        releasePlayers();
        for (BasePlayerListener basePlayerListener : basePlayerListeners) {
            basePlayerListener.onDestroy();
        }
        if (mediaSourceBuilder != null) {
            mediaSourceBuilder.destroy();
        }
        lastTotalRxBytes=0L;
        lastTimeStamp=0L;
        resumePosition=0L;
        resumeWindow=0;
        videoInfoListeners.clear();
        videoWindowListeners.clear();
        basePlayerListeners.clear();
        mPlayerViewListeners.clear();
        isEnd = false;
        isPause = false;
        handPause = false;
        timer = null;
        mediaSourceBuilder = null;
        componentListener = null;
        playComponentListener = null;
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

    /*****
     * 设置视频控件view  主要用来列表进入详情播放使用
     * @param newVideoPlayerView videoPlayerView
     * **/
    void switchTargetView(@NonNull VideoPlayerView newVideoPlayerView) {
        if (this.videoPlayerView == newVideoPlayerView) {
            return;
        }
        if (player != null) {
            player.removeListener(componentListener);
        }
        playComponentListener = new PlayComponent(this);
        removeExoPlayerViewListener(videoPlayerView.getComponentListener());
        this.videoPlayerView = newVideoPlayerView;
        addExoPlayerViewListener(newVideoPlayerView.getComponentListener());
        newVideoPlayerView.setExoPlayerListener(playComponentListener);
        if (player == null) {
            createFullPlayer();
        } else {
            for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                item.setPlayer(player);
            }
        }
        player.addListener(componentListener);
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.setPlayerBtnOnTouch(false);
            item.toggoleController(false, false);
            item.setControllerHideOnTouch(true);
        }
        isEnd = false;
        isLoad = true;
    }

    /***
     * 释放资源
     */
    public void releasePlayers() {
        updateResumePosition();
        unNetworkBroadcastReceiver();
        if (player != null) {
            player.removeListener(componentListener);
            player.stop();
            player.release();
            player = null;
        }
        if (timer != null && !timer.isShutdown()) {
            timer.shutdown();
        }

    }

    /****
     * 初始化播放实例
     * @return ExoUserPlayer
     */
    public ExoUserPlayer startPlayer() {
        VideoPlayerManager.getInstance().setCurrentVideoPlayer(ExoUserPlayer.this);
        handPause = false;
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.startPlayer(this);
            item.setPlayerBtnOnTouch(false);
        }
        startVideo();
        registerReceiverNet();
        return this;
    }

    /***
     * 播放视频
     **/
    void startVideo() {
        boolean iss = VideoPlayUtils.isWifi(activity) || VideoPlayerManager.getInstance().isClick() || isPause || isPlaying();
        if (iss) {
            playerNoAlertDialog();
        } else {
            for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                item.showAlertDialog();
            }
        }
    }

    /***
     * 创建实例播放实例，并不开始缓冲
     **/
    void createFullPlayer() {
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        setDefaultLoadModel();
        DefaultRenderersFactory rf = new DefaultRenderersFactory(activity, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
        player = ExoPlayerFactory.newSimpleInstance(activity, rf, trackSelector, new DefaultLoadControl(), drmSessionManager);
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.setPlayer(player);
        }
        for (BasePlayerListener item : basePlayerListeners) {
            item.setPlayer(player);
        }
    }


    /***
     * 创建实例播放实例，开始缓冲
     */
    void playerNoAlertDialog() {
        if (player == null) {
            createFullPlayer();
        }
        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
        if (handPause) {
            player.setPlayWhenReady(false);
        } else {
            player.setPlayWhenReady(true);
        }
        player.setPlaybackParameters(playbackParameters);
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.showPreview(View.GONE, true);
            item.toggoleController(false, false);
            item.setControllerHideOnTouch(true);
            item.setPlayerBtnOnTouch(false);
        }
        if (haveResumePosition) {
            player.seekTo(resumeWindow, resumePosition);
        }
        player.removeListener(componentListener);
        player.addListener(componentListener);
        player.prepare(mediaSourceBuilder.getMediaSource(), !haveResumePosition, false);
        isEnd = false;
        isLoad = true;
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.onPrepared();
        }

    }

    /**
     * 兼容6.0一以下版本
     * 横屏延迟问题
     **/
    void land() {
        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
        if (handPause) {
            player.setPlayWhenReady(false);
        } else {
            player.setPlayWhenReady(true);
        }
        player.prepare(mediaSourceBuilder.getMediaSource(), !haveResumePosition, false);
    }

    /****
     * 重置进度
     */
    private void updateResumePosition() {
        if (player != null) {
            resumeWindow = player.getCurrentWindowIndex();
            resumePosition = Math.max(0, player.getContentPosition());
        }
    }

    /**
     * 清除进度
     ***/
    void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    /****
     * 获取当前网速
     *
     * @return String 返回当前网速字符
     **/
    private String getNetSpeed() {
        if (activity == null) {
            return "";
        }
        String netSpeed;
        long nowTotalRxBytes = VideoPlayUtils.getTotalRxBytes(activity);
        long nowTimeStamp = System.currentTimeMillis();
        long calculationTime = (nowTimeStamp - lastTimeStamp);
        if (calculationTime == 0) {
            netSpeed = 1 + " kb/s";
            return netSpeed;
        }
        //毫秒转换
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / calculationTime);
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        if (speed > 1024) {
            DecimalFormat df = new DecimalFormat("######0.0");
            netSpeed = df.format(VideoPlayUtils.getM(speed)) + " MB/s";
        } else {
            netSpeed = speed + " kb/s";
        }
        return netSpeed;
    }

    /****
     * 监听返回键 true 可以正常返回处理，false 切换到竖屏
     *
     * @return boolean boolean
     */
    public boolean onBackPressed() {
        if (!VideoPlayUtils.isTv(activity) && activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                item.exitFull();
            }
            return false;
        } else {
            return true;
        }
    }

    /***
     * 设置默认加载
     * **/
    private void setDefaultLoadModel() {
        if (null == timer) {
            timer = Executors.newScheduledThreadPool(2);
            /*1s后启动任务，每1s执行一次**/
            timer.scheduleWithFixedDelay(task, 400, 300, TimeUnit.MILLISECONDS);
        }
    }

    void reset() {
        if (player != null) {
            player.stop();
            player.removeListener(componentListener);
            for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                item.setPlayerBtnOnTouch(true);
                item.reset();
            }
            player.release();
            player = null;
        }
    }

    void resetList() {
        if (player != null) {
            player.stop();
            for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                item.setPlayerBtnOnTouch(true);
            }
            player.release();
            player = null;

        }
    }

    /***
     * 多种分辨率点击播放
     * @param uri uri
     * ***/
    void setSwitchPlayer(@NonNull String uri) {
        handPause = false;
        updateResumePosition();
        if (mediaSourceBuilder.getMediaSource() instanceof ConcatenatingMediaSource) {
            ConcatenatingMediaSource source = (ConcatenatingMediaSource) mediaSourceBuilder.getMediaSource();
            source.getMediaSource(source.getSize() - 1).releaseSource(null);
            source.addMediaSource(mediaSourceBuilder.initMediaSource(Uri.parse(uri)));
            isSwitch = true;
        } else {
            mediaSourceBuilder.setMediaUri(Uri.parse(uri));
            playerNoAlertDialog();
        }
    }

    /***
     * 是否播放中
     * @return boolean boolean
     */
    public boolean isPlaying() {
        if (player == null) return false;
        int playbackState = player.getPlaybackState();
        return playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED
                && player.getPlayWhenReady();
    }

    /*****************设置参数方法*************************/

    /**
     * 设置自定义键唯一标识原始流。用于缓存索引。*默认值是{ null }。 不支持流式媒体
     *
     * @param customCacheKey 唯一标识原始流的自定义密钥。用于缓存索引。
     */
    public void setCustomCacheKey(@NonNull String customCacheKey) {
        mediaSourceBuilder.setCustomCacheKey(customCacheKey);
    }

    /****
     * 设置tag 标记 防止列表复用进度导致,
     * @param position  position
     * **/
    public void setTag(int position) {
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.setTag(position);
        }
    }

    /***
     * 设置播放路径
     * @param drmSessionManager 一个可选的 {@link DrmSessionManager}. 如果DRM得到保护，可能是null
     */
    public void setDrmSessionManager(DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        this.drmSessionManager = drmSessionManager;
    }

    /****
     *  支持视频源动态添加
     *
     * @param videoUri videoUri
     */
    public void addMediaUri(@NonNull Uri videoUri) {
        mediaSourceBuilder.addMediaUri(videoUri);
    }

    /***
     * 设置播放路径
     * @param uri 路径
     */
    public void setPlayUri(@NonNull String uri) {
        setPlayUri(Uri.parse(uri));
    }

    /**
     * 设置播放路径
     *
     * @param uri 路径
     */
    public void setPlayUri(@NonNull Uri uri) {
        mediaSourceBuilder.setMediaUri(uri);
    }

    /****
     * @param indexType 设置当前索引视频屏蔽进度
     * @param firstVideoUri 预览的视频
     * @param secondVideoUri 第二个视频
     */
    public void setPlayUri(@Size(min = 0) int indexType, @NonNull String firstVideoUri, @NonNull String secondVideoUri) {
        setPlayUri(indexType, Uri.parse(firstVideoUri), Uri.parse(secondVideoUri));

    }


    /****
     * 设置视频列表播放
     * @param indexType 设置当前索引视频屏蔽进度
     * @param firstVideoUri 预览的视频
     * @param secondVideoUri 第二个视频
     */
    public void setPlayUri(@Size(min = 0) int indexType, @NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
        mediaSourceBuilder.setMediaUri(indexType, firstVideoUri, secondVideoUri);
    }

    /****
     * 设置视频列表播放
     * @param <T>     你的实体类
     * @param uris 集合
     */
    public <T extends ItemVideo> void setPlayUri(@NonNull List<T> uris) {
        mediaSourceBuilder.setMediaUri(uris);
    }

    /***
     * 设置多线路播放
     * @param index 选中播放线路
     * @param videoUri 视频地址
     * @param name 清清晰度显示名称
     * @deprecated Use {@link VideoPlayerManager.Builder#setPlaySwitchUri(int, String[], String[])} instead.
     */
    public void setPlaySwitchUri(int index, @NonNull String[] videoUri, @NonNull String[] name) {
        setPlaySwitchUri(index, Arrays.asList(videoUri), Arrays.asList(name));
    }


    /***
     * 设置多线路播放
     * @param switchIndex 选中播放线路索引
     * @param videoUri 视频地址
     * @param name 清清晰度显示名称
     * @deprecated Use {@link VideoPlayerManager.Builder#setPlaySwitchUri(int, List, List)} instead.
     */
    public void setPlaySwitchUri(int switchIndex, @NonNull List<String> videoUri, @NonNull List<String> name) {
        mediaSourceBuilder.setMediaSwitchUri(videoUri, switchIndex);
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.setSwitchName(name, switchIndex);
        }
    }


    /****
     * @param indexType 设置当前索引视频屏蔽进度
     * @param switchIndex the switch index
     * @param firstVideoUri 预览视频
     * @param secondVideoUri 内容视频多线路设置
     * @param name the name
     * @deprecated Use {@link VideoPlayerManager.Builder#setPlaySwitchUri(int, int, String, List, List)} instead.
     */
    public void setPlaySwitchUri(@Size(min = 0) int indexType, @Size(min = 0) int switchIndex, @NonNull String firstVideoUri, String[] secondVideoUri, @NonNull String[] name) {
        setPlaySwitchUri(indexType, switchIndex, firstVideoUri, Arrays.asList(secondVideoUri), Arrays.asList(name));

    }

    /****
     * @param indexType 设置当前索引视频屏蔽进度
     * @param switchIndex the switch index
     * @param firstVideoUri 预览视频
     * @param secondVideoUri 内容视频多线路设置
     * @param name the name
     * @deprecated Use {@link VideoPlayerManager.Builder#setPlaySwitchUri(int, int, String, List, List)}
     */
    public void setPlaySwitchUri(@Size(min = 0) int indexType, @Size(min = 0) int switchIndex, @NonNull String firstVideoUri, List<String> secondVideoUri, @NonNull List<String> name) {
        mediaSourceBuilder.setMediaUri(indexType, switchIndex, Uri.parse(firstVideoUri), secondVideoUri);
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.setSwitchName(name, switchIndex);
        }
    }


    /***
     * 设置进度
     * @param resumePosition 毫秒
     */
    public void setPosition(long resumePosition) {
        this.resumePosition = resumePosition;
    }

    /***
     * 设置进度
     * @param currWindowIndex 视频索引
     * @param currPosition 毫秒
     */
    public void setPosition(int currWindowIndex, long currPosition) {
        this.resumeWindow = currWindowIndex;
        this.resumePosition = currPosition;
    }

    /***
     * 设置进度
     * @param  positionMs  positionMs
     */
    public void seekTo(long positionMs) {
        if (player != null) {
            player.seekTo(positionMs);
        }
    }

    /***
     * 设置进度
     * @param  windowIndex  windowIndex
     * @param  positionMs  positionMs
     */
    public void seekTo(int windowIndex, long positionMs) {
        if (player != null) {
            player.seekTo(windowIndex, positionMs);
        }
    }


    /***
     * 设置倍数播放创建新的回放参数
     *
     * @param speed 播放速度加快   1f 是正常播放 小于1 慢放
     * @param pitch 音高被放大  1f 是正常播放 小于1 慢放
     */
    public void setPlaybackParameters(@Size(min = 0) float speed, @Size(min = 0) float pitch) {
        playbackParameters = null;
        playbackParameters = new PlaybackParameters(speed, pitch);
        if (player != null) {
            player.setPlaybackParameters(playbackParameters);
        }
    }

    /***
     * 设置播放或暂停
     * @param value true 播放  false  暂停
     */
    public void setStartOrPause(boolean value) {
        if (player != null) {
            if (value) {
                for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                    item.showPreview(View.GONE, false);
                }
            }
            player.setPlayWhenReady(value);
        }
    }

    /***
     * 设置显示多线路图标
     * @param showVideoSwitch true 显示 false 不显示
     */
    public void setShowVideoSwitch(boolean showVideoSwitch) {
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.setShowWitch(showVideoSwitch);
        }
    }

    /***
     * 设置进度进度条拖拽
     * @param isOpenSeek true 启用 false 不启用
     */
    public void setSeekBarSeek(boolean isOpenSeek) {
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.setSeekBarOpenSeek(isOpenSeek);
        }
    }

    /***
     * 设置事件分发
     * @param basePlayerListener 实例
     */
    public void addBasePlayerListener(@NonNull BasePlayerListener basePlayerListener) {
        basePlayerListeners.add(basePlayerListener);
    }

    /***
     * 设置视频信息回调
     * @param videoInfoListener 实例
     */
    public void addVideoInfoListener(@NonNull VideoInfoListener videoInfoListener) {
        videoInfoListeners.add(videoInfoListener);
    }

    /***
     *移除视频信息回调
     * @param videoInfoListener 实例
     */
    public void removeVideoInfoListener(@NonNull VideoInfoListener videoInfoListener) {
        videoInfoListeners.remove(videoInfoListener);
    }

    /****
     * 设置点击播放按钮回调, 交给用户处理
     * @param onClickListener 回调实例
     * @deprecated Use {@link VideoPlayerManager.Builder#setOnPlayClickListener(View.OnClickListener)} instead.
     */
    public void setOnPlayClickListener(@Nullable View.OnClickListener onClickListener) {
        videoPlayerView.setOnPlayClickListener(onClickListener);
    }

    /***
     * 添加多个视频窗口状态
     * @param windowListener 实例
     */
    public void addOnWindowListener(@NonNull VideoWindowListener windowListener) {
        videoWindowListeners.add(windowListener);
    }

    /***
     * 移除多个视频窗口状态
     * @param windowListener 实例
     */
    public void removeOnWindowListener(@NonNull VideoWindowListener windowListener) {
        videoWindowListeners.remove(windowListener);
    }

    /***
     *  绑定view回调
     * @param exoPlayerViewListener 实例
     */
    public void addExoPlayerViewListener(@NonNull ExoPlayerViewListener exoPlayerViewListener) {
        mPlayerViewListeners.add(exoPlayerViewListener);
    }

    /***
     *  绑定view回调
     * @param exoPlayerViewListener 实例
     */
    public void removeExoPlayerViewListener(@NonNull ExoPlayerViewListener exoPlayerViewListener) {
        mPlayerViewListeners.remove(exoPlayerViewListener);
    }

    /********************下面主要获取和操作方法*****************************************************************/

    /***
     * 返回接口实例
     * @return CopyOnWriteArraySet
     */
      CopyOnWriteArraySet<ExoPlayerViewListener> getPlayerViewListeners() {
        return mPlayerViewListeners;
    }

    /***
     * 返回视频总数
     * @return int window count
     */
    public int getWindowCount() {
        if (player == null) {
            return 0;
        }
        return player.getCurrentTimeline().isEmpty() ? 1 : player.getCurrentTimeline().getWindowCount();
    }

    /***
     * 下跳转下一个视频
     */
    public void next() {
        player.next();
    }

    /***
     * 下跳转上一个视频
     */
    public void previous() {
        player.previous();

    }

    /***
     * 隐藏控制布局
     */
    public void hideControllerView() {
        hideControllerView(false);
    }

    /***
     * 隐藏控制布局
     */
    public void showControllerView() {
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.toggoleController(false, true);
        }
    }
    /**
     * 设置是否可以显示回放控件。如果设置为{@code false }，回放控件*将永远不可见，并且与播放机断开连接。
     *
     * @param useController 是否可以显示回放控件。
     */
    public void setUseController(boolean useController) {
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.setUseController(useController);
        }
    }
    /***
     * 隐藏控制布局
     * @param isShowFull 是否显示全屏按钮
     */
    public void hideControllerView(boolean isShowFull) {
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.toggoleController(isShowFull, false);
        }
    }

    /***
     * 隐藏控制布局
     * @param isShowFull 是否显示全屏按钮
     */
    public void showControllerView(boolean isShowFull) {
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.toggoleController(isShowFull, true);
        }
    }


    /****
     * 横竖屏切换
     *
     * @param configuration 旋转
     */
    public void onConfigurationChanged(Configuration configuration) {
        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
            item.onConfigurationChanged(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE);
        }
    }

    /***
     * 获取内核播放实例
     * @return SimpleExoPlayer player
     */
    public SimpleExoPlayer getPlayer() {
        return player;
    }

    /**
     * 返回视频总进度  以毫秒为单位
     *
     * @return long duration
     */
    public long getDuration() {
        return player == null ? 0 : player.getDuration();
    }

    /**
     * 返回视频当前播放进度  以毫秒为单位
     *
     * @return long current position
     */
    public long getCurrentPosition() {
        return player == null ? 0 : player.getCurrentPosition();
    }

    /**
     * 返回视频当前播放d缓冲进度  以毫秒为单位
     *
     * @return long buffered position
     */
    public long getBufferedPosition() {
        return player == null ? 0 : player.getBufferedPosition();
    }


    public MediaSourceBuilder getMediaSourceBuilder() {
        return mediaSourceBuilder;
    }

    /***
     * 注册广播监听
     */
    private void registerReceiverNet() {
        if (mNetworkBroadcastReceiver == null) {
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mNetworkBroadcastReceiver = new NetworkBroadcastReceiver();
            activity.registerReceiver(mNetworkBroadcastReceiver, intentFilter);
        }
    }

    /***
     * 取消广播监听
     */
    private void unNetworkBroadcastReceiver() {
        if (mNetworkBroadcastReceiver != null) {
            activity.unregisterReceiver(mNetworkBroadcastReceiver);
        }
        mNetworkBroadcastReceiver = null;
    }

    public VideoPlayerView getVideoPlayerView() {
        return videoPlayerView;
    }

    /***
     * 网络监听类
     ***/
    private final class NetworkBroadcastReceiver extends BroadcastReceiver {
        long is = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (null != action && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                assert mConnectivityManager != null;
                NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {
                    if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        /////////3g网络
                        if (System.currentTimeMillis() - is > 500) {
                            is = System.currentTimeMillis();
                            if (VideoPlayerManager.getInstance().isClick()) {
                                return;
                            }
                            if (!isPause) {
                                for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                                    item.showAlertDialog();
                                }
                            }
                        }
                    } else if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        startVideo();
                    }
                }
            }

        }
    }

    /***
     * view 给控制类 回调类
     */
    private Player.EventListener componentListener = new Player.DefaultEventListener() {
        boolean isRemove;

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            if (isSwitch) {
                isSwitch = false;
                isRemove = true;
                player.seekTo(player.getNextWindowIndex(), resumePosition);

            }
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            if (getWindowCount() > 1) {
                if (isRemove) {
                    isRemove = false;
                    mediaSourceBuilder.removeMedia(resumeWindow);
                    return;
                }
                if (!videoWindowListeners.isEmpty()) {
                    for (VideoWindowListener videoWindowListener : videoWindowListeners) {
                        videoWindowListener.onCurrentIndex(player.getCurrentWindowIndex(), getWindowCount());
                    }
                }
                if (mediaSourceBuilder.getIndexType() < 0) {
                    return;
                }
                boolean setOpenSeek = !(mediaSourceBuilder.getIndexType() == player.getCurrentWindowIndex() && mediaSourceBuilder.getIndexType() > 0);
                for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                    item.setOpenSeek(setOpenSeek);
                }
            }
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

            for (VideoInfoListener videoInfoListener : videoInfoListeners) {
                videoInfoListener.isPlaying(player.getPlayWhenReady());
            }
            Log.d(TAG, "onPlayerStateChanged:" + playbackState + "+playWhenReady:" + playWhenReady);
            switch (playbackState) {
                case Player.STATE_BUFFERING:
                    if (playWhenReady) {
                        for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                            item.showLoadStateView(View.VISIBLE);
                        }
                    }
                    for (VideoInfoListener videoInfoListener : videoInfoListeners) {
                        videoInfoListener.onLoadingChanged();
                    }
                    break;
                case Player.STATE_ENDED:
                    Log.d(TAG, "onPlayerStateChanged:ended。。。");
                    isEnd = true;
                    clearResumePosition();
                    for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                        item.showReplayView(View.VISIBLE);
                    }
                    for (VideoInfoListener videoInfoListener : videoInfoListeners) {
                        videoInfoListener.onPlayEnd();
                    }
                    break;
                case Player.STATE_IDLE:
                    Log.d(TAG, "onPlayerStateChanged::网络状态差，请检查网络。。。");
                    for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                        item.showErrorStateView(View.VISIBLE);
                    }
                    break;
                case Player.STATE_READY:
                    for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                        item.showPreview(View.GONE, false);
                        item.showLoadStateView(View.GONE);
                    }
                    if (playWhenReady) {
                        Log.d(TAG, "onPlayerStateChanged:准备播放");
                        isPause = false;
                        for (VideoInfoListener videoInfoListener : videoInfoListeners) {
                            videoInfoListener.onPlayStart(getCurrentPosition());
                        }
                    }
                    break;
                default:
            }
        }


        public void onPlayerError(ExoPlaybackException e) {
            Log.e(TAG, "onPlayerError:" + e.getMessage());
            updateResumePosition();
            if (VideoPlayUtils.isBehindLiveWindow(e)) {
                clearResumePosition();
                startVideo();
            } else {
                for (ExoPlayerViewListener item : getPlayerViewListeners()) {
                    item.showErrorStateView(View.VISIBLE);
                }
                for (VideoInfoListener videoInfoListener : videoInfoListeners) {
                    videoInfoListener.onPlayerError(player.getPlaybackError());
                }
            }
        }

    };

}

