package chuangyuan.ycj.yjplay;

import android.app.Application;

import chuangyuan.ycj.videolibrary.office.ExoWholeDownLoadManger;
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
        ExoWholeDownLoadManger.getSingle().initDownloadManager(this,DemoDownloadService.class);
     //  ExoDownLoadManger.getSingle().initDownloadManager(this,DemoDownloadService.class);
    }

}