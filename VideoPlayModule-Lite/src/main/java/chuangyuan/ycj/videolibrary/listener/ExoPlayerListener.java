package chuangyuan.ycj.videolibrary.listener;


import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;

/**
 * @author yangc
 *         date 2017/7/21
 *         E-Mail:yangchaojiang@outlook.com
 *         Deprecated: view回调控制类接口
 */

public interface ExoPlayerListener {

    /***
     * 播放控制类回调
     * **/
    void onCreatePlayers();

    /***
     *清除进度
     * **/
    void onClearPosition();

    /***
     *释放控制类
     * **/
    void replayPlayers();

    /***
     *选择多线路
     * @param position 索引
     * @param   name 名称
     * **/
    void switchUri(int position, String name);

    /***
     *播放视频地址
     * **/
    void playVideoUri();

    /***
     *返回建回调
     * **/
    void onBack();

    /***
     *得到内核控制类
     * @return ExoUserPlayer
     * **/
    ExoUserPlayer getPlay();

}
