package chuangyuan.ycj.videolibrary.video;


import chuangyuan.ycj.videolibrary.listener.ExoPlayerListener;
import chuangyuan.ycj.videolibrary.listener.ExoPlayerViewListener;

/**
 * author yangc
 * date 2017/2/28
 * E-Mail:1007181167@qq.com
 * Description：视图操作回调控制类
 */
class PlayComponent implements ExoPlayerListener {
    private final ExoUserPlayer exoUserPlayer;


    public PlayComponent(ExoUserPlayer exoUserPlayer) {
        this.exoUserPlayer = exoUserPlayer;
    }


    @Override
    public void onCreatePlayers() {
        exoUserPlayer.startVideo();
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
        VideoPlayerManager.getInstance().serEnableHintGPRS(true);
        exoUserPlayer.playerNoAlertDialog();
    }

    @Override
    public void onDetachedFromWindow(boolean isListPlayer) {
        boolean is = isListPlayer && exoUserPlayer.getPlayer() != null;
        if (is) {
            ExoUserPlayer manualPlayer = VideoPlayerManager.getInstance().getVideoPlayer();
            if (manualPlayer != null && exoUserPlayer.toString().equals(manualPlayer.toString())) {
                manualPlayer.reset();
            }
        } else {
            for (ExoPlayerViewListener item : exoUserPlayer.getPlayerViewListeners()) {
                item.onDestroy();
            }
        }
    }


    @Override
    public void startPlayers() {
        exoUserPlayer.startPlayer();
    }

    @Override
    public void landScapeChang(boolean isLand) {
        if (isLand){
            exoUserPlayer.land();
        }
    }

}
