package com.example.appkotlin

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast

import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlaybackException

import chuangyuan.ycj.videolibrary.listener.LoadModelType
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener
import chuangyuan.ycj.videolibrary.video.ManualPlayer
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView


class MainCustomActivity : AppCompatActivity() {

    private var exoPlayerManager: ManualPlayer? = null
    private var videoPlayerView: VideoPlayerView? = null
    private var isOnclick = false
    private var currPosition: Long = 0
    private var isEnd: Boolean = false
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_coutom)
        isOnclick = intent.getBooleanExtra("isOnclick", false)
        currPosition = intent.getLongExtra("currPosition", 0)
        url = intent.getStringExtra("uri")
        videoPlayerView = findViewById(R.id.exo_play_context_id) as VideoPlayerView
        ViewCompat.setTransitionName(videoPlayerView, VIEW_NAME_HEADER_IMAGE)
        exoPlayerManager = ManualPlayer(this, videoPlayerView, DataSource(application))
        exoPlayerManager!!.setPosition(currPosition)
        exoPlayerManager!!.setTitle("自定义视频标题")
        //设置加载显示模式
        exoPlayerManager!!.setLoadModel(LoadModelType.SPEED)

        exoPlayerManager!!.setPlayUri(url)
        if (currPosition > 0) {
            exoPlayerManager!!.startPlayer()
        }
        Glide.with(this)
                .load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
                .placeholder(R.mipmap.test)
                .fitCenter()
                .into(videoPlayerView!!.getPreviewImage())

        //自定义布局使用
        videoPlayerView!!.getReplayLayout().findViewById(R.id.replay_btn_imageView).setOnClickListener(View.OnClickListener { Toast.makeText(this@MainCustomActivity, "自定义分享", Toast.LENGTH_SHORT).show() })
        videoPlayerView!!.getErrorLayout().findViewById(R.id.exo_player_error_text).setOnClickListener(View.OnClickListener { Toast.makeText(this@MainCustomActivity, "自定义错误", Toast.LENGTH_SHORT).show() })
        videoPlayerView!!.getPlayHintLayout().setOnClickListener(View.OnClickListener { Toast.makeText(this@MainCustomActivity, "自定义提示", Toast.LENGTH_SHORT).show() })
        //
        if (isOnclick) {
            exoPlayerManager!!.setOnPlayClickListener(View.OnClickListener {
                Toast.makeText(this@MainCustomActivity, "定义点击播放事件", Toast.LENGTH_LONG).show()
                //处理业务操作 完成后，
                exoPlayerManager!!.startPlayer()//开始播放
            })
        }
        exoPlayerManager!!.setVideoInfoListener(object : VideoInfoListener() {
            fun onPlayStart() {

            }

            fun onLoadingChanged() {

            }

            fun onPlayerError(e: ExoPlaybackException) {

            }

            fun onPlayEnd() {
                isEnd = true
            }

            fun onRepeatModeChanged(repeatMode: Int) {

            }

            fun isPlaying(playWhenReady: Boolean) {
                //  Toast.makeText(getApplication(),"playWhenReady"+playWhenReady,Toast.LENGTH_SHORT).show();
            }
        })
    }

    public override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
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
        if (exoPlayerManager!!.onBackPressed()) {//使用播放返回键监听
            Toast.makeText(this@MainCustomActivity, "返回", Toast.LENGTH_LONG).show()
            val intent = Intent()
            intent.putExtra("isEnd", isEnd)
            intent.putExtra("currPosition", exoPlayerManager!!.getCurrentPosition())
            setResult(Activity.RESULT_OK, intent)
            ActivityCompat.finishAfterTransition(this)
        }

    }

    companion object {
        val VIEW_NAME_HEADER_IMAGE = "123"
        private val TAG = "MainDetailedActivity"
    }


}
