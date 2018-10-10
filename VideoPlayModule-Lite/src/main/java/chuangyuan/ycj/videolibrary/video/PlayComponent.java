package chuangyuan.ycj.videolibrary.video;

import android.support.annotation.Nullable;
import android.view.View;

import chuangyuan.ycj.videolibrary.listener.ExoPlayerListener;
/**
 * author yangc
 * date 2017/2/28
 * E-Mail:1007181167@qq.com
 * Description：视图操作回调控制类
 */
class PlayComponent implements ExoPlayerListener {
    private final ExoUserPlayer exoUserPlayer;
    private View.OnClickListener onClickListener;

    public PlayComponent(ExoUserPlayer exoUserPlayer) {
        this.exoUserPlayer = exoUserPlayer;
    }

    /****
     * 设置点击播放按钮回调, 交给用户处理
     * @param onClickListener 回调实例
     */
    public void setOnPlayClickListener(@Nullable View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onCreatePlayers() {
        exoUserPlayer.createPlayers();
    }

    @Override
    public void replayPlayers() {
        exoUserPlayer.clearResumePosition();
        exoUserPlayer.handPause = false;
        if (exoUserPlayer.getPlayer() == null) {
            exoUserPlayer.createPlayers();
        } else {
            exoUserPlayer.getPlayer().seekTo(0, 0);
            exoUserPlayer.getPlayer().setPlayWhenReady(true);
        }

    }


    @Override
    public void switchUri(int position) {
       MediaSourceBuilder mediaSourceBuilder = exoUserPlayer.getMediaSourceBuilder();
        if (mediaSourceBuilder != null && mediaSourceBuilder.getVideoUri() != null) {
            exoUserPlayer.setSwitchPlayer(mediaSourceBuilder.getVideoUri().get(position));
        }
    }

    @Override
    public void playVideoUri() {
        VideoPlayerManager.getInstance().setClick(true);
        exoUserPlayer.onPlayNoAlertVideo();
    }

    @Override
    public ExoUserPlayer getPlay() {
        return exoUserPlayer;
    }

    @Override
    public void startPlayers() {
        exoUserPlayer.startPlayer();
    }

    @Override
    public View.OnClickListener getClickListener() {
        return onClickListener;
    }

    @Override
    public void land() {
        exoUserPlayer.land();
    }
}
