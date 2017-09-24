package chuangyuan.ycj.yjplay;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import java.util.List;

import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context mContext;
    private List<String> mVideoList;
    public VideoAdapter(Context context, List<String> videoList) {
        mContext = context;
        mVideoList = videoList;
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }
    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_video1, parent, false);
        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        String video = mVideoList.get(position);
        holder.bindData(video);
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        ManualPlayer userPlayer;
        VideoPlayerView playerView;
        public VideoViewHolder(View itemView) {
            super(itemView);
            playerView = (VideoPlayerView) itemView.findViewById(R.id.item_exo_player_view);
            userPlayer = new ManualPlayer((Activity) mContext, playerView);
        }

        public void bindData(String videoBean) {
            userPlayer.setTitle("" + getAdapterPosition());
            userPlayer.setPlayUri(videoBean);
            Glide.with(mContext)
                    .load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                    .placeholder(R.mipmap.test)
                    .into(playerView.getPreviewImage());
        }
    }
}
