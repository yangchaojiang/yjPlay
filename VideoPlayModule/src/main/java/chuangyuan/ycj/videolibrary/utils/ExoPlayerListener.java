package chuangyuan.ycj.videolibrary.utils;


import com.google.android.exoplayer2.SimpleExoPlayer;

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

    SimpleExoPlayer getPlay();

    void   showReplayViewChange(int visibility);

    void  onBack();

}
