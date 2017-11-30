package com.example.appkotlin

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bumptech.glide.Glide
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter

import java.util.ArrayList

import chuangyuan.ycj.videolibrary.video.ManualPlayer
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView


class VideoAdapter : RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private var mContext: Context? = null
    private var mVideoList: MutableList<String>? = null
    internal var onItemClickListener: RecyclerArrayAdapter.OnItemClickListener

    constructor(context: Context, videoList: MutableList<String>) {
        mContext = context
        mVideoList = videoList
    }

    constructor(context: Context) {
        mContext = context
        mVideoList = ArrayList()
    }

    override fun getItemCount(): Int {
        return mVideoList!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val itemView = LayoutInflater.from(mContext).inflate(R.layout.item_video1, parent, false)
        return VideoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = mVideoList!![position]
        holder.bindData(video)
    }

    fun addAll(list: List<String>) {
        mVideoList!!.addAll(list)
    }

    fun setOnItemClickListener(onItemClickListener: RecyclerArrayAdapter.OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var userPlayer: ManualPlayer
        internal var playerView: VideoPlayerView

        init {
            playerView = itemView.findViewById(R.id.item_exo_player_view) as VideoPlayerView
            userPlayer = ManualPlayer(mContext as Activity?, playerView)
            itemView.setOnClickListener { onItemClickListener.onItemClick(adapterPosition) }
        }

        fun bindData(videoBean: String) {
            userPlayer.setTitle("" + adapterPosition)
            userPlayer.setPlayUri(videoBean)
            Glide.with(mContext)
                    .load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                    .placeholder(R.mipmap.test)
                    .into(playerView.getPreviewImage())
        }
    }
}
