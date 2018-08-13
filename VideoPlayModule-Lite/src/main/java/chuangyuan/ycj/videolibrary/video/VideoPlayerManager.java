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
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.ui.AnimUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.listener.ItemVideo;
import chuangyuan.ycj.videolibrary.listener.OnGestureBrightnessListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureProgressListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureVolumeListener;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * author yangc
 * date 2017/2/27
 * E-Mail:1007181167@qq.com
 * Description： video播放列表控制类
 */
public class VideoPlayerManager {
    private ManualPlayer mVideoPlayer;
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
    public void setCurrentVideoPlayer(@NonNull ManualPlayer videoPlayer) {
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
            mVideoPlayer.reset();
            mVideoPlayer = null;
        }
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
    public ManualPlayer getVideoPlayer() {
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
     * ****/
    public void switchTargetView(@NonNull ManualPlayer player, @Nullable VideoPlayerView newPlayerView, boolean isPlay) {
        VideoPlayerView oldPlayerView = player.getVideoPlayerView();
        if (oldPlayerView == newPlayerView) {
            return;
        }
        if (newPlayerView != null) {
            newPlayerView.getPlayerView().setPlayer(player.getPlayer());
            player.setVideoPlayerView(newPlayerView);
        }
        if (oldPlayerView != null) {
            oldPlayerView.resets();
            oldPlayerView.getPlayerView().setPlayer(null);
        }
        if (isPlay) {
            player.setStartOrPause(true);
        } else {
            if (newPlayerView != null) {
                player.reset();
                player.resetInit();
            }
        }
    }


    public static final int TYPE_PLAY_USER = 0;
    public static final int TYPE_PLAY_GESTURE = 1;
    public static final int TYPE_PLAY_MANUAL = 2;

    @IntDef({TYPE_PLAY_USER, TYPE_PLAY_GESTURE, TYPE_PLAY_MANUAL})
    @Retention(RetentionPolicy.SOURCE)
    @interface PlayerType {
    }

    /***
     * 构建内部构建者
     * **/
    public static class Builder {
        private Activity context;
        private VideoPlayerView view;
        private DataSourceListener listener;
        private MediaSourceBuilder mediaSourceBuilder;
        private int playerType = 0;
        private DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
        private OnGestureBrightnessListener onGestureBrightnessListener;
        private OnGestureVolumeListener onGestureVolumeListener;
        private OnGestureProgressListener onGestureProgressListener;
        private boolean controllerHideOnTouch;
        /*** 视频回调信息接口 ***/
        private final CopyOnWriteArraySet<VideoInfoListener> videoInfoListeners;
        /*** 多个视频接口***/
        private final CopyOnWriteArraySet<VideoWindowListener> videoWindowListeners;
        private long resumePosition;
        private int resumeWindow = -1;
        private View.OnClickListener onClickListener;
        private String customCacheKey;

        public Builder(Activity activity, @PlayerType int type, @IdRes int reId) {
            this(type, (VideoPlayerView) activity.findViewById(reId));
        }

        public Builder(@PlayerType int type, @NonNull VideoPlayerView view) {
            this.context = (Activity) view.getContext();
            this.view = view;
            this.playerType = type;
            videoInfoListeners = new CopyOnWriteArraySet<>();
            videoWindowListeners = new CopyOnWriteArraySet<>();
        }

        /*****
         * 添加多媒体加载接示例
         * @param  listener listener
         * @return  Builder
         * ****/
        public Builder setDataSource(@NonNull DataSourceListener listener) {
            this.listener = listener;
            return this;
        }
        /***
         * 添加多媒体加载接示例
         * @param  mediaSourceBuilder mediaSourceBuilder
         * @return  Builder
         * ***/
        public Builder setDataSource(@NonNull MediaSourceBuilder mediaSourceBuilder) {
            this.mediaSourceBuilder = mediaSourceBuilder;
            return this;
        }

        /***
         * 初始化多媒体
         * ***/
        private void  initMediaSourceBuilder(){
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

        /***
         * 显示水印图
         *
         * @param res 资源
         * @return   Builder
         */
        public Builder setExoPlayWatermarkImg(@DrawableRes int res) {
            view.setExoPlayWatermarkImg(res);
            return this;
        }

        /***
         * 设置标题
         *
         * @param title 名字
         * @return   Builder
         */
        public Builder setTitle(@NonNull String title) {
            view.setTitle(title);
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
            view.setSwitchName(name, switchIndex);
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
            view.setSwitchName(name, switchIndex);
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
        public Builder setLooping(@Size(min = 1) int loopingCount) {
            initMediaSourceBuilder();
            mediaSourceBuilder.setLooping(loopingCount);
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
         * @return   Builder
         */
        public Builder setOnPlayClickListener(@Nullable View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return  this;
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
         @return  Builder
         */
        public Builder setOnGestureBrightnessListener(@NonNull OnGestureBrightnessListener onGestureBrightnessListener) {
            this.onGestureBrightnessListener = onGestureBrightnessListener;
            return this;
        }

        /***
         * 实现自定义音频手势监听事件
         * @param onGestureVolumeListener 实例
         @return  Builder
         */
        public Builder setOnGestureVolumeListener(@NonNull OnGestureVolumeListener onGestureVolumeListener) {
            this.onGestureVolumeListener = onGestureVolumeListener;
            return this;
        }

        /***
         * 实现自定义进度监听事件
         * @param onGestureProgressListener 实例
         @return  Builder
         */
        public Builder setOnGestureProgressListener(@NonNull OnGestureProgressListener onGestureProgressListener) {
            this.onGestureProgressListener = onGestureProgressListener;
            return this;
        }

        /***
         * 设置手势touch 事件
         * @param controllerHideOnTouch true 启用  false 关闭
         @return  Builder
         */
        public Builder setPlayerGestureOnTouch(boolean controllerHideOnTouch) {
            this.controllerHideOnTouch = controllerHideOnTouch;
            return this;
        }

        /***
         * 增加进度监听
         * @param  updateProgressListener updateProgressListener
         @return  Builder
         * ****/
        public Builder addUpdateProgressListener(@NonNull AnimUtils.UpdateProgressListener updateProgressListener) {
            view.getPlaybackControlView().addUpdateProgressListener(updateProgressListener);
            return  this;
        }
        /***
         * 移除进度监听
         * @param  updateProgressListener updateProgressListener
         @return  Builder
         * ****/
        public Builder removeUpdateProgressListener(@NonNull AnimUtils.UpdateProgressListener updateProgressListener) {
            view.getPlaybackControlView().removeUpdateProgressListener(updateProgressListener);
            return  this;
        }
        /**
         *设置自定义键唯一标识原始流。用于缓存索引。*默认值是{ null }。 不支持流式媒体
         *
         * @param customCacheKey 唯一标识原始流的自定义密钥。用于缓存索引。
         *
         * @throws IllegalStateException If one of the {@code create} methods has already been called.
         */
        public Builder setCustomCacheKey(@NonNull String customCacheKey) {
            mediaSourceBuilder.setCustomCacheKey(customCacheKey);
            return  this;
        }
            /***
             * 创建播放器
             * **/
        public <T extends ExoUserPlayer> T create() {
            initMediaSourceBuilder();
            T exoUserPlayer;
            switch (playerType) {
                case TYPE_PLAY_GESTURE:
                    GestureVideoPlayer videoPlayer = new GestureVideoPlayer(context, mediaSourceBuilder, view);
                    videoPlayer.setOnGestureBrightnessListener(onGestureBrightnessListener);
                    videoPlayer.setOnGestureProgressListener(onGestureProgressListener);
                    videoPlayer.setOnGestureVolumeListener(onGestureVolumeListener);
                    videoPlayer.setPlayerGestureOnTouch(controllerHideOnTouch);
                    exoUserPlayer = (T) videoPlayer;
                    break;
                case TYPE_PLAY_MANUAL:
                    ManualPlayer manualPlayer = new ManualPlayer(context, mediaSourceBuilder, view);
                    manualPlayer.setOnGestureBrightnessListener(onGestureBrightnessListener);
                    manualPlayer.setOnGestureProgressListener(onGestureProgressListener);
                    manualPlayer.setOnGestureVolumeListener(onGestureVolumeListener);
                    manualPlayer.setPlayerGestureOnTouch(controllerHideOnTouch);
                    exoUserPlayer = (T) manualPlayer;
                    break;
                default:
                    exoUserPlayer = (T) new ExoUserPlayer(context, mediaSourceBuilder, view);
                    break;
            }
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
            exoUserPlayer.setOnPlayClickListener(onClickListener);
            return exoUserPlayer;
        }
    }


}
