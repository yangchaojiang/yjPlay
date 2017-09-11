package com.list.videoplayer;

/**
 * @author Jarvis
 * @version 1.0
 * @title VideoPlayer
 * @description 该类主要功能描述
 * @create 2017/6/1 上午11:32
 * @changeRecord [修改记录] <br/>
 */

public interface IVideoPlayerControl {
    void start();
    void restart();
    void pause();
    void seekTo(int pos);

    boolean isIdle();
    boolean isPreparing();
    boolean isPrepared();
    boolean isBufferingPlaying();
    boolean isBufferingPaused();
    boolean isPlaying();
    boolean isPaused();
    boolean isError();
    boolean isCompleted();

    boolean isFullScreen();
    boolean isTinyWindow();
    boolean isNormal();

    int getDuation();
    int getCurrentPosition();
    int getBufferPercentage();

    void enterFullScreen();
    boolean exitFullScreen();
    void enterTinyWindow();
    boolean exitTinyWindow();

    void release();
}
