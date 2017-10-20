package chuangyuan.ycj.yjplay;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

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
    protected void convert(TestVideoHolder helper, String item) {
        helper.userPlayer.setTitle("" + helper.getAdapterPosition());
        helper.userPlayer.setPlayUri(item);
        Glide.with(context).
                load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                .placeholder(R.mipmap.test)
                .into(helper.playerView.getPreviewImage());
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
            playerView = (VideoPlayerView) view.findViewById(R.id.item_exo_player_view);
            itemView = view;
            userPlayer = new ManualPlayer((Activity) context, playerView);
        }


    }

}
