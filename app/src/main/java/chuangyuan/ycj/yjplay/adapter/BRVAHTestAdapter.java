package chuangyuan.ycj.yjplay.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.exoplayer2.ExoPlaybackException;


import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;

/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */


public class BRVAHTestAdapter extends BaseQuickAdapter<String, BRVAHTestAdapter.TestVideoHolder> {
    private Context context;

    public BRVAHTestAdapter(Context context) {
        super(R.layout.item_video1);
        this.context = context;
    }

    @Override
    protected void convert(final TestVideoHolder helper, String item) {
        helper.userPlayer.setTitle("" + helper.getAdapterPosition());
        helper.userPlayer.setPlayUri(item);
        Glide.with(context).
                load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                .placeholder(R.mipmap.test)
                .into(helper.playerView.getPreviewImage());
        helper.userPlayer.setVideoInfoListener(new VideoInfoListener() {
            @Override
            public void onPlayStart() {

            }

            @Override
            public void onLoadingChanged() {

            }

            @Override
            public void onPlayerError(ExoPlaybackException e) {

            }

            @Override
            public void onPlayEnd() {
            /*    helper.userPlayer.setVideoInfoListener(null);
                if (!VideoPlayUtils.isLand(getRecyclerView().getContext())) {
                    int posss = helper.getAdapterPosition() + 1;
                    getRecyclerView().smoothScrollBy(0, helper.itemView.getBottom());
                    View view = getRecyclerView().getLayoutManager().findViewByPosition(posss);
                    TestVideoHolder testVideoHolder = (TestVideoHolder) getRecyclerView().getChildViewHolder(view);
                    testVideoHolder.userPlayer.startPlayer();
                }*/

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void isPlaying(boolean playWhenReady) {

            }
        });

    }

    @Override
    protected TestVideoHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        return super.createBaseViewHolder(parent, layoutResId);
    }

    class TestVideoHolder extends BaseViewHolder {
        ManualPlayer userPlayer;
        VideoPlayerView playerView;
        View itemView;

        public TestVideoHolder(View view) {
            super(view);
            playerView = (VideoPlayerView) view.findViewById(R.id.exo_play_context_id);
            itemView = view;
            userPlayer = new ManualPlayer((Activity) context, playerView);
        }


    }

}
