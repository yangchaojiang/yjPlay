package chuangyuan.ycj.yjplay.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context mContext;
    private List<String> mVideoList;
    RecyclerArrayAdapter.OnItemClickListener onItemClickListener;

    public VideoAdapter(Context context, List<String> videoList) {
        mContext = context;
        mVideoList = videoList;
    }

    public VideoAdapter(Context context) {
        mContext = context;
        mVideoList = new ArrayList<>();
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

    public void addAll(List<String> list) {
        mVideoList.addAll(list);
    }

    public void setOnItemClickListener(RecyclerArrayAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        ManualPlayer userPlayer;
        VideoPlayerView playerView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            playerView = (VideoPlayerView) itemView.findViewById(R.id.exo_play_context_id);
            userPlayer = new ManualPlayer((Activity) mContext, playerView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }

        public void bindData(String videoBean) {
            playerView.setTitle("" + getAdapterPosition());
            userPlayer.setTag(getAdapterPosition());
            userPlayer.setPlayUri(videoBean);
            Glide.with(mContext)
                    .load(mContext.getString(R.string.uri_test_image))
                    .placeholder(R.mipmap.test)
                    .into(playerView.getPreviewImage());
        }
    }
}
