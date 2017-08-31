package com.example.listvideo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.list.videoplayer.VideoPlayer;
import com.list.videoplayer.VideoPlayerController;

import java.util.List;

/**
 * @author Jarvis
 * @version 1.0
 * @title VideoPlayer
 * @description 该类主要功能描述
 * @company 北京奔流网络技术有限公司
 * @create 2017/6/1 下午2:32
 * @changeRecord [修改记录] <br/>
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context mContext;
    private List<VideoBean> mVideoList;

    public VideoAdapter(Context context, List<VideoBean> videoList) {
        mContext = context;
        mVideoList = videoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_video, parent, false);
        VideoViewHolder holder = new VideoViewHolder(itemView);
        VideoPlayerController controller = new VideoPlayerController(mContext);
        holder.setController(controller);
        return holder;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        VideoBean video = mVideoList.get(position);
        holder.bindData(video);
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        private VideoPlayerController mController;
        private VideoPlayer mVideoPlayer;

        public VideoViewHolder(View itemView) {
            super(itemView);
            mVideoPlayer = (VideoPlayer) itemView.findViewById(R.id.video_player);
        }

        public void setController(VideoPlayerController controller) {
            mController = controller;
        }

        public void bindData(VideoBean videoBean) {
            mController.setTitle(videoBean.getTitle());
            mVideoPlayer.setController(mController);
            mVideoPlayer.setData(videoBean.getVideoUrl(), null);
        }
    }
}
