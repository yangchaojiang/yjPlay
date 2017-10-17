package chuangyuan.ycj.videolibrary.listener;

import com.google.android.exoplayer2.ExoPlaybackException;

/**
 *
 * @author yangc
 * date 2017/2/25
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

    /***
     *模式发生改变
     * @param  repeatMode  { int REPEAT_MODE_OFF = 0; int REPEAT_MODE_ONE = 1,REPEAT_MODE_ALL = 2}
     *  Normal playback without repetition REPEAT_MODE_OFF
     * "Repeat One" mode to repeat the currently playing window infinitely. REPEAT_MODE_ONE
     * "Repeat All" mode to repeat the entire timeline infinitely.REPEAT_MODE_ALL
     * ***/
     void onRepeatModeChanged(int repeatMode);

}
