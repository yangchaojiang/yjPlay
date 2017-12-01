package chuangyuan.ycj.videolibrary.video;


import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author yangc
 *         date 2017/2/27
 *         E-Mail:1007181167@qq.com
 *         Description： video播放列表控制类
 */
public class VideoPlayerManager {
    private ManualPlayer mVideoPlayer;
    private boolean isClick = false;

    private VideoPlayerManager() {
    }

    public static VideoPlayerManager getInstance() {
        return Holder.holder;
    }

    private static final class Holder {
        static VideoPlayerManager holder = new VideoPlayerManager();
    }

    /***
     * 设置当前播放 控制类
     *
     * @param videoPlayer 播放页
     **/
    public void setCurrentVideoPlayer(@NonNull ManualPlayer videoPlayer) {
        releaseVideoPlayer();
        this.mVideoPlayer = videoPlayer;
    }

    /***
     * 释放当前播放
     **/
    public void releaseVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.reset(false);
        }
    }

    /***
     * d手机屏幕旋转配置
     * @param newConfig newConfig
     **/
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoPlayer != null) {
            mVideoPlayer.onConfigurationChanged(newConfig);
        }
    }

    /***
     * 设置返回建监听
     *
     * @return boolean
     **/
    public boolean onBackPressed() {
        return mVideoPlayer == null || mVideoPlayer.onBackPressed();
    }

    /**
     * 页面暂停播放暂停
     **/
    public void onPause() {
        if (mVideoPlayer != null) {
            mVideoPlayer.onListPause();
        }
    }

    /**
     * 页面恢复
     **/
    public void onResume() {
        if (mVideoPlayer != null) {
            mVideoPlayer.onResume();
        }
    }

    /**
     * 页面销毁
     **/
    public void onDestroy() {
        if (mVideoPlayer != null) {
            mVideoPlayer.onDestroy();
            mVideoPlayer = null;
        }
    }

    /**
     * 获取当前播放类
     *
     * @return ManualPlayer
     **/
    @Nullable
    public ManualPlayer getVideoPlayer() {
        return mVideoPlayer;
    }

    /**
     * 获取当前状态
     *
     * @return ManualPlayer
     **/
    boolean isClick() {
        return isClick;
    }

    /**
     * 获取当前播放类
     *
     * @param click 实例
     **/
    public void setClick(boolean click) {
        isClick = click;
    }
}
