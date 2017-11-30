package com.example.appkotlin

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast

import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlaybackException

import chuangyuan.ycj.videolibrary.listener.VideoInfoListener
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener
import chuangyuan.ycj.videolibrary.video.GestureVideoPlayer
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView

class GuangGaoPlayerdActivity : Activity() {

    private var exoPlayerManager: GestureVideoPlayer? = null
    private var videoPlayerView: VideoPlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_coutom2)
        videoPlayerView = findViewById(R.id.exo_play_context_id) as VideoPlayerView
        exoPlayerManager = GestureVideoPlayer(this, videoPlayerView, DataSource(this.application))
        exoPlayerManager!!.setTitle("视频标题")
        exoPlayerManager!!.setExoPlayWatermarkImg(R.mipmap.watermark_big)
        if (Build.VERSION.SDK_INT < 21) {//低版本不支持高分辨视频
            exoPlayerManager!!.setPlayUri(0, "http://mp4.vjshi.com/2013-07-25/2013072519392517096.mp4", "http://mp4.vjshi.com/2013-11-11/1384169050648_274.mp4")
        } else {
            exoPlayerManager!!.setPlayUri(0, "http://mp4.vjshi.com/2016-12-22/3ccab5a78036fa933c8585f4a1e57a44.mp4", "http://mp4.vjshi.com/2016-04-05/add12db77c7c5cd6dfef4c1955b36a80.mp4")
        }
        ///默认实现  播放广告视频时手势操作禁用和开启操作
        //exoPlayerManager.setPlayerGestureOnTouch(true);
        //如果视频需要自己实现该回调 视频切换回调处理，进行布局处理，控制布局显示
        exoPlayerManager!!.setOnWindowListener(object : VideoWindowListener() {
            fun onCurrentIndex(currentIndex: Int, windowCount: Int) {
                if (currentIndex == 0) {
                    //屏蔽控制布局
                    exoPlayerManager!!.hideControllerView()
                    //如果屏蔽控制布局 但是需要显示全屏按钮。手动显示，播放正常视频自动还原。
                    videoPlayerView!!.getExoFullscreen().setVisibility(View.VISIBLE)
                } else {
                    //恢复控制布局
                    exoPlayerManager!!.showControllerView()
                }
            }
        })
        exoPlayerManager!!.setVideoInfoListener(object : VideoInfoListener() {
            fun onPlayStart() {

            }

            fun onLoadingChanged() {

            }

            fun onPlayerError(e: ExoPlaybackException) {

            }

            fun onPlayEnd() {
                Toast.makeText(application, "asd", Toast.LENGTH_SHORT).show()
            }

            fun onRepeatModeChanged(repeatMode: Int) {

            }

            fun isPlaying(playWhenReady: Boolean) {

            }
        })
        Glide.with(this)
                .load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                .fitCenter()
                .placeholder(R.mipmap.test)
                .into(videoPlayerView!!.getPreviewImage())
        findViewById(R.id.button5).setOnClickListener { exoPlayerManager!!.next() }
    }

    public override fun onResume() {
        super.onResume()
        exoPlayerManager!!.onResume()
    }

    public override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        exoPlayerManager!!.onPause()
    }


    override fun onDestroy() {
        exoPlayerManager!!.onDestroy()
        super.onDestroy()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        exoPlayerManager!!.onConfigurationChanged(newConfig)//横竖屏切换
        super.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        if (exoPlayerManager!!.onBackPressed()) {
            ActivityCompat.finishAfterTransition(this)

        }
    }

    companion object {
        private val TAG = "MainDetailedActivity"
    }

}
