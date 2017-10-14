package chuangyuan.ycj.videolibrary.listener;


import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;

/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: view回调控制类接口
 */

public interface ExoPlayerListener {

    void onCreatePlayers();

    void onClearPosition();

    void replayPlayers();

    void switchUri(int position, String name);

    void playVideoUri();

    void onBack();

    ExoUserPlayer getPlay();

    void onDetachedFromWindow();

}
