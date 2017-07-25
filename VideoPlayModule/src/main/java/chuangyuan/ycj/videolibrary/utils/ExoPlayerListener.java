package chuangyuan.ycj.videolibrary.utils;



import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;

/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public interface ExoPlayerListener {

    void onCreatePlayers();

    void onClearPosition();

    void replayPlayers();

    void switchUri(int position,String name);

    void playVideoUri();

    void   showReplayViewChange(int visibility);

    void  onBack();

    ExoUserPlayer getPlay();


}
