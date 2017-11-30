package com.example.appkotlin

import android.app.Application


/**
 * Created by yangc on 2017/8/31.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        val TAG = "App"
    }
}
