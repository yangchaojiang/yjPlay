package com.example.appkotlin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById(R.id.button)
                .setOnClickListener {
                    val intent = Intent(this@MainActivity, MainDetailedActivity::class.java)
                    startActivity(intent)
                }
        findViewById(R.id.button2)
                .setOnClickListener {
                    val intent = Intent(this@MainActivity, MainListActivity::class.java)
                    startActivity(intent)
                }
        findViewById(R.id.button3)
                .setOnClickListener {
                    val intent = Intent(this@MainActivity, MainCustomActivity::class.java)
                    val uri: String
                    if (Build.VERSION.SDK_INT < 21) {//低版本不支持高分辨视频
                        uri = "http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"
                    } else {
                        //1080 视频
                        uri = "http://pic.ibaotu.com/00/34/35/51S888piCamj.mp4"
                    }
                    intent.putExtra("uri", uri)
                    startActivity(intent)
                }
        findViewById(R.id.button4)
                .setOnClickListener {
                    val intent = Intent(this@MainActivity, MainCustomActivity::class.java)
                    val uri: String
                    if (Build.VERSION.SDK_INT < 21) {//低版本不支持高分辨视频
                        uri = "http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"
                    } else {
                        //1080 视频
                        uri = "http://pic.ibaotu.com/00/34/35/51S888piCamj.mp4"
                    }
                    intent.putExtra("uri", uri)
                    intent.putExtra("isOnclick", true)
                    startActivity(intent)
                }
        findViewById(R.id.button5)
                .setOnClickListener {
                    val intent = Intent(this@MainActivity, GuangGaoPlayerdActivity::class.java)
                    intent.putExtra("isOnclick", true)
                    startActivity(intent)
                }
    }
}
