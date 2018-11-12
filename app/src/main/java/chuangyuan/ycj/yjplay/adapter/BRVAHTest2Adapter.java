package chuangyuan.ycj.yjplay.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.exoplayer2.ExoPlaybackException;

import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.VideoDataBean;

/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */


public class BRVAHTest2Adapter extends BaseQuickAdapter<VideoDataBean, BRVAHTest2Adapter.TestVideoHolder> {
    private Context context;

    public BRVAHTest2Adapter(Context context) {
        super(R.layout.item_video2);
        this.context = context;
    }

    @Override
    protected void convert(final TestVideoHolder helper, VideoDataBean item) {

        //使用自定义预览布局
        //如果使用自定义预览的布局，播放器标题根据业务是否设置
        Glide.with(context)
                .load(item.getImageUri())
                .placeholder(R.mipmap.test)
                .into(helper.playerView);


    }

    @Override
    protected TestVideoHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        return super.createBaseViewHolder(parent, layoutResId);
    }

    class TestVideoHolder extends BaseViewHolder {
        ImageView playerView;
        View itemView;

        public TestVideoHolder(View view) {
            super(view);
            playerView = view.findViewById(R.id.exo_play_context_ids);
            itemView = view;

        }


    }

}
