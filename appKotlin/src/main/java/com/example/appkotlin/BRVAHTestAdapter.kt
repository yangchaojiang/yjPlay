package com.example.appkotlin

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.exoplayer2.ExoPlaybackException

import chuangyuan.ycj.videolibrary.listener.VideoInfoListener
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils
import chuangyuan.ycj.videolibrary.video.ManualPlayer
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView

/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

class BRVAHTestAdapter(private val context: Context) : BaseQuickAdapter<String, BRVAHTestAdapter.TestVideoHolder>(R.layout.item_video1) {

    override fun convert(helper: TestVideoHolder, item: String) {
        helper.userPlayer.setTitle("" + helper.adapterPosition)
        helper.userPlayer.setPlayUri(item)
        Glide.with(context).load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                .placeholder(R.mipmap.test)
                .into(helper.playerView.getPreviewImage())
        helper.userPlayer.setVideoInfoListener(object : VideoInfoListener() {
            fun onPlayStart() {

            }

            fun onLoadingChanged() {

            }

            fun onPlayerError(e: ExoPlaybackException) {

            }

            fun onPlayEnd() {
                helper.userPlayer.setVideoInfoListener(null)
                if (!VideoPlayUtils.isLand(recyclerView.context)) {
                    val posss = helper.adapterPosition + 1
                    recyclerView.smoothScrollBy(0, helper.itemView.getBottom())
                    val view = recyclerView.layoutManager.findViewByPosition(posss)
                    val testVideoHolder = recyclerView.getChildViewHolder(view) as TestVideoHolder
                    testVideoHolder.userPlayer.startPlayer()
                }

            }

            fun onRepeatModeChanged(repeatMode: Int) {

            }

            fun isPlaying(playWhenReady: Boolean) {

            }
        })

    }

    override fun createBaseViewHolder(parent: ViewGroup, layoutResId: Int): TestVideoHolder {
        return super.createBaseViewHolder(parent, layoutResId)
    }

    internal inner class TestVideoHolder(var itemView: View) : BaseViewHolder(itemView) {
        var userPlayer: ManualPlayer
        var playerView: VideoPlayerView

        init {
            playerView = itemView.findViewById(R.id.item_exo_player_view) as VideoPlayerView
            userPlayer = ManualPlayer(context as Activity, playerView)
        }


    }

}
