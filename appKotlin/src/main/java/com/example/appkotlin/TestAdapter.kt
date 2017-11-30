package com.example.appkotlin

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup

import com.bumptech.glide.Glide
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter

import chuangyuan.ycj.videolibrary.video.ManualPlayer
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView


/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

class TestAdapter(context: Context) : RecyclerArrayAdapter<String>(context) {

    override fun OnCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return TestVideoHolder(parent)
    }

    override fun setOnItemClickListener(listener: RecyclerArrayAdapter.OnItemClickListener) {
        super.setOnItemClickListener(listener)

    }

    internal inner class TestVideoHolder(parent: ViewGroup) : BaseViewHolder<String>(parent, R.layout.item_video1) {
        var userPlayer: ManualPlayer
        var playerView: VideoPlayerView
        var itemView: View

        init {
            playerView = `$`<VideoPlayerView>(R.id.item_exo_player_view)
            itemView = `$`<View>(R.id.itemView)
            userPlayer = ManualPlayer(context as Activity, playerView)
        }

        override fun setData(data: String?) {
            userPlayer.setTitle("" + adapterPosition)
            userPlayer.setPlayUri(data)
            Glide.with(context).load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                    .placeholder(R.mipmap.test)
                    .into(playerView.getPreviewImage())
        }
    }

}
