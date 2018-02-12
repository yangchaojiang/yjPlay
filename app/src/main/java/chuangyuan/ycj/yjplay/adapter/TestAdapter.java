package chuangyuan.ycj.yjplay.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;


/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class TestAdapter extends RecyclerArrayAdapter<String> {
    public TestAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(final ViewGroup parent, int viewType) {
        return new TestVideoHolder(parent);
    }
    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        super.setOnItemClickListener(listener);

    }
    class  TestVideoHolder extends BaseViewHolder<String>{
        ManualPlayer userPlayer;
        VideoPlayerView playerView;
        View itemView;
        public TestVideoHolder(ViewGroup parent) {
            super(parent, R.layout.item_video1);
            playerView=$(R.id.exo_play_context_id);
            itemView=$(R.id.itemView);
            userPlayer = new ManualPlayer((Activity) getContext(), playerView);
        }
        @Override
        public void setData(final String data) {
            userPlayer.setTitle(""+getAdapterPosition());
            userPlayer.setPlayUri(data);
            userPlayer.setTag(getAdapterPosition());
            Glide.with(getContext())
                    .load(playerView.getContext().getString(R.string.uri_test_image))
                    .placeholder(R.mipmap.test)
                    .into(playerView.getPreviewImage());
        }
    }

}
