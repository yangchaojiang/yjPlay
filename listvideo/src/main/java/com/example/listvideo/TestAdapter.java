package com.example.listvideo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.jiang.list.wight.VideoListPlayView;
import com.jiang.list.wight.VideoPlayerManager;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;

/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class TestAdapter extends RecyclerArrayAdapter<String> {
    public static final String TAG = "TestAdapter";
    private ExoUserPlayer mExoUserPlayer;
    private ImageView mplayer_view_play;
    public TestAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(final ViewGroup parent, int viewType) {
        TestVideoHolder videoHolder= new TestVideoHolder(parent);
        videoHolder.setOnPlayClickListener(new OnPlayClickListener() {

            @Override
            public void onClick(ExoUserPlayer exoUserPlayer,ImageView player_view_play) {
                if (mExoUserPlayer!=null){
                    mExoUserPlayer.releasePlayers();
                    mplayer_view_play.setVisibility(View.VISIBLE);
                    SimpleExoPlayerView.switchTargetView(exoUserPlayer.getPlayer(),mExoUserPlayer.getPlayerView().getPlayerView(),exoUserPlayer.getPlayerView().getPlayerView());
                }
                mplayer_view_play=player_view_play;
                mExoUserPlayer=exoUserPlayer;
                exoUserPlayer.playVideo();
                player_view_play.setVisibility(View.GONE);


            }
        });
        return videoHolder;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        super.setOnItemClickListener(listener);

    }

    class  TestVideoHolder extends BaseViewHolder<String>{
        ExoUserPlayer userPlayer;
        VideoListPlayView playerView;
        private ImageView player_view_play;
        private  OnPlayClickListener OnPlayClickListener;
        View itemView;
        public TestVideoHolder(ViewGroup parent) {
            super(parent, R.layout.item_video1);
            playerView=$(R.id.item_exo_player_view);
            itemView=$(R.id.itemView);
            player_view_play=$(R.id.player_view_play);
            userPlayer = new ExoUserPlayer((Activity) getContext(), playerView);

        }

        @Override
        public void setData(final String data) {
            super.setData(data);
            playerView.setTitle(""+getAdapterPosition());
            player_view_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userPlayer.setPlayUri(data);
                    VideoPlayerManager.getInstance().setCurrentVideoPlayer(userPlayer);
                    playerView.setRecycleView(getOwnerRecyclerView(),getAdapterPosition());
                    OnPlayClickListener.onClick(userPlayer,player_view_play );

                }
            });
        }

        public void setOnPlayClickListener(TestAdapter.OnPlayClickListener onPlayClickListener) {
            OnPlayClickListener = onPlayClickListener;
        }
    }
    public  interface   OnPlayClickListener{

        void onClick(ExoUserPlayer exoUserPlayer,ImageView player_view_play);
    }
}
