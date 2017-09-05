package chuangyuan.ycj.yjplay;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;


/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class TestAdapter extends RecyclerArrayAdapter<String> {
    public static final String TAG = "TestAdapter";
    public TestAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(final ViewGroup parent, int viewType) {
        TestVideoHolder videoHolder= new TestVideoHolder(parent);
        return videoHolder;
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
            playerView=$(R.id.item_exo_player_view);
            itemView=$(R.id.itemView);
            userPlayer = new ManualPlayer((Activity) getContext(), playerView);
        }
        @Override
        public void setData(final String data) {
            userPlayer.setTitle(""+getAdapterPosition());
            userPlayer.setPlayUri(data);
            Glide.with(getContext()).load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                    .into(playerView.getPreviewImage());
        }
    }

}
