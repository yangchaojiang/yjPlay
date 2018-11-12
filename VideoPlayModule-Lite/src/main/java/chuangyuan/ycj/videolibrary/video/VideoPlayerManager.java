package chuangyuan.ycj.videolibrary.video;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.ui.PlayerControlView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import chuangyuan.ycj.videolibrary.listener.BasePlayerListener;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.listener.ExoPlayerViewListener;
import chuangyuan.ycj.videolibrary.listener.ItemVideo;
import chuangyuan.ycj.videolibrary.listener.OnCoverMapImageListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureBrightnessListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureProgressListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureVolumeListener;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.utils.AnimUtils;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * author yangc
 * date 2017/2/27
 * E-Mail:1007181167@qq.com
 * Description： video播放列表控制类
 */
public class VideoPlayerManager {
    private ExoUserPlayer mVideoPlayer;
    private boolean isClick = false;

    private VideoPlayerManager() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static VideoPlayerManager getInstance() {
        return Holder.holder;
    }

    private static class Holder {

        static VideoPlayerManager holder = new VideoPlayerManager();
    }

    /***
     * 设置当前播放 控制类
     *
     * @param videoPlayer 播放页
     */
    public void setCurrentVideoPlayer(@NonNull ExoUserPlayer videoPlayer) {
        if (mVideoPlayer == null || !videoPlayer.toString().equals(mVideoPlayer.toString())) {
            releaseVideoPlayer();
        }
        this.mVideoPlayer = videoPlayer;
    }

    /***
     * 释放当前播放
     */
    public void releaseVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.reset(true);
        }
        mVideoPlayer = null;
    }

    /***
     * d手机屏幕旋转配置
     * @param newConfig newConfig
     */
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoPlayer != null) {
            mVideoPlayer.onConfigurationChanged(newConfig);
        }
    }

    /***
     * 设置返回建监听
     *
     * @return boolean boolean
     */
    public boolean onBackPressed() {
        return mVideoPlayer == null || mVideoPlayer.onBackPressed();
    }

    /**
     * 页面暂停播放暂停
     *
     * @param isReset isReset  没有特殊情况 默认 true 释放
     */
    public void onPause(boolean isReset) {
        if (mVideoPlayer != null) {
            mVideoPlayer.onListPause(isReset);
        }
    }

    /**
     * 页面恢复
     */
    public void onResume() {
        if (mVideoPlayer != null) {
            mVideoPlayer.onResume();
        }
    }

    /**
     * 页面销毁
     */
    public void onStop() {
        if (mVideoPlayer != null) {
            mVideoPlayer.onStop();
        }
    }

    /**
     * 页面销毁
     */
    public void onDestroy() {
        if (mVideoPlayer != null) {
            mVideoPlayer.onDestroy();
            mVideoPlayer = null;
        }
    }

    /**
     * 获取当前播放类
     *
     * @return ManualPlayer video player
     */
    @Nullable
    public ExoUserPlayer getVideoPlayer() {
        if (mVideoPlayer != null && mVideoPlayer.getPlayer() != null) {
            return mVideoPlayer;
        }
        return null;
    }

    /**
     * 获取当前状态
     *
     * @return ManualPlayer boolean
     */
    boolean isClick() {
        return isClick;
    }

    /**
     * 获取当前播放类
     *
     * @param click 实例
     */
    public void setClick(boolean click) {
        isClick = click;
    }

    /*****
     * @param player 播放控制器
     *@param  newPlayerView 新的view
     *@param    isPlay  isPlay 是否播放
     * @deprecated Use {@link #switchTargetViewNew(VideoPlayerView)}.
     * ****/
    public void switchTargetView(@NonNull ExoUserPlayer player, @Nullable VideoPlayerView newPlayerView, boolean isPlay) {
        VideoPlayerView oldPlayerView = player.getVideoPlayerView();
        if (oldPlayerView == newPlayerView) {
            return;
        }
        if (oldPlayerView != null) {
            oldPlayerView.resets();
        }
        if (newPlayerView != null) {
            player.switchTargetView(newPlayerView);
        }
        if (isPlay) {
            player.setStartOrPause(true);
        } else {
            if (newPlayerView != null) {
                player.reset(true);
                for (ExoPlayerViewListener item : player.getPlayerViewListeners()) {
                    item.setPlayerBtnOnTouch(true);
                    item.reset();
                }
            }
        }
    }

    /*****
     *@param  newPlayerView 新的view
     * ****/
    public void switchTargetViewNew(@NonNull VideoPlayerView newPlayerView) {
        if (getVideoPlayer() != null) {
            getVideoPlayer().switchTargetView(newPlayerView);
        }
    }

    /*****
     *@param  oldPlayerView 旧的view
     ****/
    public void switchTargetViewResult(@NonNull VideoPlayerView oldPlayerView, long currPosition, boolean isEnd) {
        ExoUserPlayer manualPlayer = getVideoPlayer();
        if (manualPlayer != null) {
            manualPlayer.setPosition(currPosition);
            manualPlayer.switchTargetView(oldPlayerView);
            if (isEnd) {
                manualPlayer.reset(true);
                oldPlayerView.resets();
            } else {
                manualPlayer.startVideo();
            }
        }
    }


    public static final int TYPE_PLAY_USER = 0;
    public static final int TYPE_PLAY_GESTURE = 1;

    @IntDef({TYPE_PLAY_USER, TYPE_PLAY_GESTURE})
    @Retention(RetentionPolicy.SOURCE)
    @interface PlayerType {
    }

    /***
     * 构建内部构建者
     * **/
    public static class Builder {
        private Context context;
        private VideoPlayerView mVideoPlayerView;
        private PlayerControlView mExoPlayerControlView;
        private DataSourceListener listener;
        private MediaSourceBuilder mediaSourceBuilder;
        private int playerType = TYPE_PLAY_GESTURE;
        private DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
        private OnGestureBrightnessListener onGestureBrightnessListener;
        private OnGestureVolumeListener onGestureVolumeListener;
        private OnGestureProgressListener onGestureProgressListener;
        private boolean controllerHideOnTouch = true;
        /*** 视频回调信息接口 ***/
        private final CopyOnWriteArraySet<VideoInfoListener> videoInfoListeners;
        /*** 多个视频接口***/
        private final CopyOnWriteArraySet<VideoWindowListener> videoWindowListeners;
        /*** 视频加载准备接口***/
        private long resumePosition;
        private int resumeWindow = -1;
        private View.OnClickListener onClickListener;
        private OnCoverMapImageListener mapImage;

        public Builder(Activity activity, @PlayerType int type, @IdRes int reId) {
            this(type, (VideoPlayerView) activity.findViewById(reId));
        }

        public Builder(@PlayerType int type, @NonNull VideoPlayerView view) {
            this.context = VideoPlayUtils.scanForActivity(view.getContext());
            this.mVideoPlayerView = view;
            this.playerType = type;
            videoInfoListeners = new CopyOnWriteArraySet<>();
            videoWindowListeners = new CopyOnWriteArraySet<>();
        }

        public Builder(@NonNull Context mContext, @NonNull PlayerControlView mExoPlayerControlView) {
            this.context = mContext;
            videoInfoListeners = new CopyOnWriteArraySet<>();
            videoWindowListeners = new CopyOnWriteArraySet<>();
            this.mExoPlayerControlView = mExoPlayerControlView;
        }

        /***
         * 初始化多媒体
         * ***/
        private void initMediaSourceBuilder() {
            if (mediaSourceBuilder == null) {
                try {
                    Class<?> clazz = Class.forName("chuangyuan.ycj.videolibrary.whole.WholeMediaSource");
                    Constructor<?> constructor = clazz.getConstructor(Context.class, DataSourceListener.class);
                    this.mediaSourceBuilder = (MediaSourceBuilder) constructor.newInstance(context, listener);
                } catch (Exception e) {
                    this.mediaSourceBuilder = new MediaSourceBuilder(context, listener);
                }
            }
        }

        /*****
         * 添加多媒体加载接示例
         * @param  listener listener
         * @return Builder
         * ****/
        public Builder setDataSource(@NonNull DataSourceListener listener) {
            this.listener = listener;
            return this;
        }

        /***
         * 添加多媒体加载接示例
         * @param  mediaSourceBuilder mediaSourceBuilder
         * @return Builder
         * ***/
        public Builder setDataSource(@NonNull MediaSourceBuilder mediaSourceBuilder) {
            this.mediaSourceBuilder = mediaSourceBuilder;
            return this;
        }


        /**
         * 是否开启竖屏全屏
         *
         * @param verticalFullScreen isWGh  默认 false  true 开启
         * @return Builder
         */
        public Builder setVerticalFullScreen(boolean verticalFullScreen) {
            mVideoPlayerView.setVerticalFullScreen(verticalFullScreen);
            return this;
        }

        /***
         * 显示水印图
         *
         * @param res 资源
         * @return Builder
         */
        public Builder setExoPlayWatermarkImg(@DrawableRes int res) {
            mVideoPlayerView.setExoPlayWatermarkImg(res);
            return this;
        }

        /***
         * 设置标题
         *
         * @param title 名字
         * @return Builder
         */
        public Builder setTitle(@NonNull String title) {
            mVideoPlayerView.setTitle(title);
            return this;
        }

        /***
         * 设置进度
         * @param resumePosition 毫秒
         *@return Builder
         */
        public Builder setPosition(long resumePosition) {
            this.resumePosition = resumePosition;
            return this;
        }

        /***
         * 设置进度
         * @param currWindowIndex 视频索引
         * @param currPosition 毫秒
         *@return Builder
         */
        public Builder setPosition(int currWindowIndex, long currPosition) {
            this.resumeWindow = currWindowIndex;
            this.resumePosition = currPosition;
            return this;
        }

        /***
         * 设置播放路径
         * @param drmSessionManager 一个可选的 {@link DrmSessionManager}. 如果DRM得到保护，可能是null
         *@return Builder
         */
        public Builder setDrmSessionManager(DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
            this.drmSessionManager = drmSessionManager;
            return this;
        }

        /****
         *  支持视频源动态添加
         *
         * @param videoUri videoUri
         */
        public Builder addMediaUri(@NonNull Uri videoUri) {
            initMediaSourceBuilder();
            mediaSourceBuilder.addMediaUri(videoUri);
            return this;
        }

        /***
         * 设置播放路径
         * @param uri 路径
         *@return Builder
         */
        public Builder setPlayUri(@NonNull String uri) {
            return setPlayUri(Uri.parse(uri));
        }

        /****
         * @param indexType 设置当前索引视频屏蔽进度
         * @param firstVideoUri 预览的视频
         * @param secondVideoUri 第二个视频
         *@return Builder
         */
        public Builder setPlayUri(@Size(min = 0) int indexType, @NonNull String firstVideoUri, @NonNull String secondVideoUri) {
            return setPlayUri(indexType, Uri.parse(firstVideoUri), Uri.parse(secondVideoUri));

        }

        /***
         * 设置多线路播放
         * @param index 选中播放线路
         * @param videoUri 视频地址
         * @param name 清清晰度显示名称
         *@return Builder
         */
        public Builder setPlaySwitchUri(int index, @NonNull String[] videoUri, @NonNull String[] name) {
            return setPlaySwitchUri(index, Arrays.asList(videoUri), Arrays.asList(name));
        }


        /***
         * 设置多线路播放
         * @param switchIndex 选中播放线路索引
         * @param videoUri 视频地址
         * @param name 清清晰度显示名称
         *@return Builder
         */
        public Builder setPlaySwitchUri(int switchIndex, @NonNull List<String> videoUri, @NonNull List<String> name) {
            initMediaSourceBuilder();
            mediaSourceBuilder.setMediaSwitchUri(videoUri, switchIndex);
            mVideoPlayerView.setSwitchName(name, switchIndex);
            return this;
        }

        /****
         * @param indexType 设置当前索引视频屏蔽进度
         * @param switchIndex the switch index
         * @param firstVideoUri 预览视频
         * @param secondVideoUri 内容视频多线路设置
         * @param name the name
         *@return Builder
         */
        public Builder setPlaySwitchUri(@Size(min = 0) int indexType, @Size(min = 0) int switchIndex, @NonNull String firstVideoUri, String[] secondVideoUri, @NonNull String[] name) {
            return setPlaySwitchUri(indexType, switchIndex, firstVideoUri, Arrays.asList(secondVideoUri), Arrays.asList(name));

        }

        /****
         * @param indexType 设置当前索引视频屏蔽进度
         * @param switchIndex the switch index
         * @param firstVideoUri 预览视频
         * @param secondVideoUri 内容视频多线路设置
         * @param name the name
         *@return Builder
         */
        public Builder setPlaySwitchUri(@Size(min = 0) int indexType, @Size(min = 0) int switchIndex, @NonNull String firstVideoUri, List<String> secondVideoUri, @NonNull List<String> name) {
            initMediaSourceBuilder();
            mediaSourceBuilder.setMediaUri(indexType, switchIndex, Uri.parse(firstVideoUri), secondVideoUri);
            if (mVideoPlayerView != null) {
                mVideoPlayerView.setSwitchName(name, switchIndex);
            }
            return this;
        }

        /**
         * 设置播放路径
         *
         * @param uri 路径
         * @return Builder
         */
        public Builder setPlayUri(@NonNull Uri uri) {
            initMediaSourceBuilder();
            mediaSourceBuilder.setMediaUri(uri);
            return this;
        }

        /****
         * 设置视频列表播放
         * @param indexType 设置当前索引视频屏蔽进度
         * @param firstVideoUri 预览的视频
         * @param secondVideoUri 第二个视频
         *@return Builder
         */
        public Builder setPlayUri(@Size(min = 0) int indexType, @NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
            initMediaSourceBuilder();
            mediaSourceBuilder.setMediaUri(indexType, firstVideoUri, secondVideoUri);
            return this;
        }

        /***
         * 设置循环播放视频   Integer.MAX_VALUE 无线循环
         *
         * @param loopingCount 必须大于0
         *@return Builder
         */
        public Builder setLoopingMediaSource(@Size(min = 1) int loopingCount, Uri videoUri) {
            initMediaSourceBuilder();

            mediaSourceBuilder.setLoopingMediaSource(loopingCount, videoUri);
            return this;
        }

        /****
         * 设置视频列表播放
         * @param <T>     你的实体类
         * @param uris 集合
         *@return Builder
         */
        public <T extends ItemVideo> Builder setPlayUri(@NonNull List<T> uris) {
            initMediaSourceBuilder();
            mediaSourceBuilder.setMediaUri(uris);
            return this;
        }

        /****
         * 设置点击播放按钮回调, 交给用户处理
         * @param onClickListener 回调实例
         * @return Builder
         */
        public Builder setOnPlayClickListener(@Nullable View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        /***
         * 设置视频信息回调
         * @param videoInfoListener 实例
         * @return Builder
         */
        public Builder addVideoInfoListener(@NonNull VideoInfoListener videoInfoListener) {
            videoInfoListeners.add(videoInfoListener);
            return this;
        }

        /***
         * 设置多个视频状态回调
         * @param windowListener 实例
         * @return Builder
         */
        public Builder addOnWindowListener(@NonNull VideoWindowListener windowListener) {
            videoWindowListeners.add(windowListener);
            return this;
        }

        /***
         * 实现自定义亮度手势监听事件
         * @param onGestureBrightnessListener 实例
         @return Builder
         */
        public Builder setOnGestureBrightnessListener(@NonNull OnGestureBrightnessListener onGestureBrightnessListener) {
            this.onGestureBrightnessListener = onGestureBrightnessListener;
            return this;
        }

        /***
         * 实现自定义音频手势监听事件
         * @param onGestureVolumeListener 实例
         @return Builder
         */
        public Builder setOnGestureVolumeListener(@NonNull OnGestureVolumeListener onGestureVolumeListener) {
            this.onGestureVolumeListener = onGestureVolumeListener;
            return this;
        }

        /***
         * 实现自定义进度监听事件
         * @param onGestureProgressListener 实例
         @return Builder
         */
        public Builder setOnGestureProgressListener(@NonNull OnGestureProgressListener onGestureProgressListener) {
            this.onGestureProgressListener = onGestureProgressListener;
            return this;
        }

        /***
         * 设置手势touch动作
         * @param controllerHideOnTouch true 启用  false 关闭
         @return Builder
         */
        public Builder setPlayerGestureOnTouch(boolean controllerHideOnTouch) {
            this.controllerHideOnTouch = controllerHideOnTouch;
            return this;
        }

        /***
         * 增加进度监听
         * @param  updateProgressListener updateProgressListener
         @return Builder
          * ****/
        public Builder addUpdateProgressListener(@NonNull AnimUtils.UpdateProgressListener updateProgressListener) {
            if (mVideoPlayerView != null) {
                mVideoPlayerView.getPlaybackControlView().addUpdateProgressListener(updateProgressListener);
            } else {
                mExoPlayerControlView.addUpdateProgressListener(updateProgressListener);
            }
            return this;
        }

        /**
         * 设置自定义键唯一标识原始流。用于缓存索引。*默认值是{ null }。 不支持流式媒体
         *
         * @param customCacheKey 唯一标识原始流的自定义密钥。用于缓存索引。
         * @throws IllegalStateException If one of the {@code create} methods has already been called.
         */
        public Builder setCustomCacheKey(@NonNull String customCacheKey) {
            mediaSourceBuilder.setCustomCacheKey(customCacheKey);
            return this;
        }

        /**
         * 加载封面图回调
         *
         * @param mapImage 加载封面图回调
         */
        public Builder setOnCoverMapImage(@NonNull OnCoverMapImageListener mapImage) {
            this.mapImage = mapImage;
            return this;
        }

        /***
         * 创建播放器
         *
         * **/
        public ExoUserPlayer create() {
            initMediaSourceBuilder();
            ExoUserPlayer exoUserPlayer;
            if (mVideoPlayerView != null) {
                exoUserPlayer = new ExoUserPlayer(context, mediaSourceBuilder, mVideoPlayerView);
                GestureModule gestureModule = new GestureModule((Activity) mVideoPlayerView.getContext(), exoUserPlayer);
                if (playerType == TYPE_PLAY_GESTURE) {
                    gestureModule.setOnGestureBrightnessListener(onGestureBrightnessListener);
                    gestureModule.setOnGestureProgressListener(onGestureProgressListener);
                    gestureModule.setOnGestureVolumeListener(onGestureVolumeListener);
                    exoUserPlayer.addBasePlayerListener(gestureModule);
                }
                if (mapImage != null) {
                    mapImage.onCoverMap(mVideoPlayerView.getPreviewImage());
                }
                mVideoPlayerView.setOnEndGestureListener(gestureModule);
                mVideoPlayerView.setPlayerGestureOnTouch(controllerHideOnTouch);
                mVideoPlayerView.setOnPlayClickListener(onClickListener);
            } else {
                exoUserPlayer = new ExoUserPlayer(context, mediaSourceBuilder);
                exoUserPlayer.addBasePlayerListener(new BasePlayerListener() {

                    @Override
                    public void onDestroy() {
                    }

                    @Override
                    public void setPlayer(SimpleExoPlayer simpleExoPlayer) {
                        mExoPlayerControlView.setPlayer(simpleExoPlayer);
                    }
                });
            }

            exoUserPlayer.createFullPlayer();
            exoUserPlayer.setDrmSessionManager(drmSessionManager);
            for (VideoInfoListener videoInfoListener : videoInfoListeners) {
                exoUserPlayer.addVideoInfoListener(videoInfoListener);
            }
            for (VideoWindowListener videoWindowListener : videoWindowListeners) {
                exoUserPlayer.addOnWindowListener(videoWindowListener);
            }
            if (resumeWindow != -1) {
                exoUserPlayer.setPosition(resumeWindow, resumePosition);
            } else {
                exoUserPlayer.setPosition(resumePosition);
            }
            return exoUserPlayer;
        }
    }


}
