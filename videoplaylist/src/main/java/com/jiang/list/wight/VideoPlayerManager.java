package com.jiang.list.wight;



import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;

/**
 * @author Jarvis
 * @version 1.0
 * @title VideoPlayer
 * @description 该类主要功能描述
 * @company 北京奔流网络技术有限公司
 * @create 2017/6/1 下午1:18
 * @changeRecord [修改记录] <br/>
 */

public class VideoPlayerManager {
    private ExoUserPlayer mVideoPlayer;
    private VideoPlayerManager() {
    }
    public static   VideoPlayerManager getInstance() {
        return Holder.holder;
    }
    private static  final  class  Holder{
     static    VideoPlayerManager holder=new VideoPlayerManager();

    }

    public void setCurrentVideoPlayer(ExoUserPlayer videoPlayer) {
    if (mVideoPlayer!=null){
        SimpleExoPlayerView.switchTargetView(mVideoPlayer.getPlayer(),mVideoPlayer.getPlayerView().getPlayerView(),videoPlayer.getPlayerView().getPlayerView());
    }
        this.mVideoPlayer = videoPlayer;
    }

    public void releaseVideoPlayer() {
        if (mVideoPlayer != null) {
           mVideoPlayer.releasePlayers();
            mVideoPlayer.onDestroy();
            mVideoPlayer = null;
        }

    }



    public boolean onBackPressed() {
        if (mVideoPlayer != null) {
            return mVideoPlayer.onBackPressed();
        }
        return false;
    }
}
