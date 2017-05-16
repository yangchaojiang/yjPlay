package chuangyuan.ycj.videolibrary.utils;

import com.google.android.exoplayer2.ExoPlaybackException;

/**
 * Created by yangc on 2017/2/25.
 * E-Mail:1007181167@qq.com
 * Description：视频视频信息回调
 */

public interface VideoInfoListener {
    /***
     * 开始播放
     * **/
    void onPlayStart();
    /***
     * 播放是否加载中
     * **/
    void onLoadingChanged();
    /***
     * 播放失败
     * @param e  异常
     * **/
    void onPlayerError(ExoPlaybackException e);

    /***
     * 播放结束
     * **/
    void onPlayEnd();

}
