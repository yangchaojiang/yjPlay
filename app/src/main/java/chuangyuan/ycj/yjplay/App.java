package chuangyuan.ycj.yjplay;

import android.app.Application;

import com.google.android.exoplayer2.util.Util;

import chuangyuan.ycj.videolibrary.offline.ExoDownLoadManger;
import chuangyuan.ycj.yjplay.offline.DemoDownloadService;


/**
 * Created by yangc on 2017/8/31.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
       String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        ExoDownLoadManger.getSingle().initDownloadManager(this,DemoDownloadService.class,userAgent);
    }

}