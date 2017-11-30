package com.example.appkotlin

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View

import com.chad.library.adapter.base.BaseQuickAdapter
import com.jude.easyrecyclerview.decoration.DividerDecoration

import java.util.ArrayList

import chuangyuan.ycj.videolibrary.video.ManualPlayer
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager

import android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation


class MainListActivity : AppCompatActivity() {

    internal var easyRecyclerView: RecyclerView
    internal var adapter: BRVAHTestAdapter
    internal var toolbar: Toolbar
    private var linearLayoutManager: LinearLayoutManager? = null

    internal var st: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
            if (VideoPlayerManager.getInstance().onBackPressed()) {
                finish()
            }
        }

        easyRecyclerView = findViewById(R.id.list) as RecyclerView
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        easyRecyclerView.layoutManager = linearLayoutManager
        easyRecyclerView.addItemDecoration(DividerDecoration(Color.GRAY, 1))
        adapter = BRVAHTestAdapter(this)
        easyRecyclerView.adapter = adapter
        adapter.bindToRecyclerView(easyRecyclerView)
        val list = ArrayList<String>()
        for (i in 0..49) {
            if (Build.VERSION.SDK_INT < 21) {//低版本不支持高分辨视频
                list.add("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4")
            } else {
                if (i % 5 == 0) {
                    list.add("http://mp4.vjshi.com/2017-08-16/af83af63d018816474067b51a835f4a2.mp4")
                } else if (i % 5 == 1) {
                    list.add("http://mp4.vjshi.com/2017-06-17/f57dd833c4dbc3eabf17ba0f0bfaf746.mp4")
                } else if (i % 5 == 2) {
                    list.add("http://mp4.vjshi.com/2017-10-19/53bfeb9eb92c1748596eaf2a1e649020.mp4")
                } else if (i % 5 == 3) {
                    list.add("http://mp4.vjshi.com/2016-10-23/a0511ea830bb0620f94a5340a1879800.mp4")
                } else if (i % 5 == 4) {
                    list.add("http://mp4.vjshi.com/2016-10-21/84bafe60ef0af95a5292f66b9f692504.mp4")
                }
            }
        }
        adapter.addData(list)
        adapter.setOnItemClickListener { adapter, view, position ->
            val firstItemPosition = linearLayoutManager!!.findFirstVisibleItemPosition()
            if (position - firstItemPosition >= 0) {
                //得到要更新的item的view
                start(view, adapter.getItem(position)!!.toString())

            }
        }
        /* adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (position - firstItemPosition >= 0) {
                    //得到要更新的item的view
                    View view = easyRecyclerView.getChildAt(position - firstItemPosition);
                    start(view);

                }
            }
        });*/

    }

    private fun start(view: View, uri: String) {
        //进入详细暂停视频
        var currPosition: Long = 0
        val manualPlayer = VideoPlayerManager.getInstance().getVideoPlayer()
        if (manualPlayer != null) {
            manualPlayer!!.setStartOrPause(false)
            currPosition = manualPlayer!!.getCurrentPosition()
        }
        Log.d("currPosition", currPosition.toString() + "")
        val intent = Intent(this@MainListActivity, MainCustomActivity::class.java)
        val activityOptions = makeSceneTransitionAnimation(
                this, Pair(view.findViewById(R.id.item_exo_player_view),
                MainCustomActivity.VIEW_NAME_HEADER_IMAGE))
        intent.putExtra("currPosition", currPosition)
        intent.putExtra("uri", uri)
        ActivityCompat.startActivityForResult(this, intent, 10, activityOptions.toBundle())
    }

    override fun onPause() {
        super.onPause()
        VideoPlayerManager.getInstance().onPause()
    }

    override fun onResume() {
        super.onResume()
        VideoPlayerManager.getInstance().onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        VideoPlayerManager.getInstance().onDestroy()
        Log.d(MainListActivity::class.java.name, "耗时：" + (System.currentTimeMillis() - st))
    }

    override fun onBackPressed() {
        st = System.currentTimeMillis()
        if (VideoPlayerManager.getInstance().onBackPressed()) {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 10 && resultCode == Activity.RESULT_OK && data != null) {
            val isEnd = data.getBooleanExtra("isEnd", false)
            if (!isEnd) {
                val currPosition = data.getLongExtra("currPosition", 0)
                val manualPlayer = VideoPlayerManager.getInstance().getVideoPlayer()
                if (manualPlayer != null) {
                    manualPlayer!!.setPosition(currPosition)
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
