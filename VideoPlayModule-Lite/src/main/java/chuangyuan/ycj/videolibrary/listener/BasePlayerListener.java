package chuangyuan.ycj.videolibrary.listener;

import com.google.android.exoplayer2.SimpleExoPlayer;

public interface BasePlayerListener {

    void onDestroy();
    /***
     *绑定播放播放控制类
     * @param player 实例
     */
    void setPlayer(SimpleExoPlayer player);

}
