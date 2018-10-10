package chuangyuan.ycj.videolibrary.office;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.ProgressiveDownloadAction;
import com.google.android.exoplayer2.source.dash.offline.DashDownloadAction;
import com.google.android.exoplayer2.source.hls.offline.HlsDownloadAction;
import com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloadAction;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import chuangyuan.ycj.videolibrary.offline.ExoDownLoadManger;
/**
 * author  yangc
 * date 2018/6/17
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
public class ExoWholeDownLoadManger extends ExoDownLoadManger {
    private static final DownloadAction.Deserializer[] DOWNLOAD_DESERIALIZERS =
            new DownloadAction.Deserializer[]{
                    DashDownloadAction.DESERIALIZER,
                    HlsDownloadAction.DESERIALIZER,
                    SsDownloadAction.DESERIALIZER,
                    ProgressiveDownloadAction.DESERIALIZER
            };
    private static class Holder {
     static ExoWholeDownLoadManger h = new ExoWholeDownLoadManger();
    }

    public static ExoWholeDownLoadManger getSingle() {
        return Holder.h;
    }
    /**
     * build 数据源 工厂
     * Returns a {@link HttpDataSource.Factory}.
     */
    public HttpDataSource.Factory buildHttpDataSourceFactory(
            TransferListener listener) {
        return new DefaultHttpDataSourceFactory(userAgent, listener);
    }

    /****
     * 下载管理类
     * ***/
    @Override
    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    /****
     * 下载跟踪器
     * ***/
    @Override
    public ExoWholeDownloadTracker getExoDownloadTracker() {
        return (ExoWholeDownloadTracker) exoDownloadTracker;
    }
    /****
     * 初始化加载下载管理类
     * @param  context context
     * ***/
    @Override
    public synchronized void initDownloadManager(@NonNull  Context context, @NonNull Class<? extends DownloadService> downloadServiceClass) {
        this.context=context.getApplicationContext();
        userAgent = Util.getUserAgent(context, context.getPackageName());
        if (downloadManager == null) {
            DownloaderConstructorHelper downloaderConstructorHelper =
                    new DownloaderConstructorHelper(
                            getDownloadCache(), buildHttpDataSourceFactory(/* listener= */ null));
            downloadManager =
                    new DownloadManager(
                            downloaderConstructorHelper,
                            MAX_SIMULTANEOUS_DOWNLOADS,
                            DownloadManager.DEFAULT_MIN_RETRY_COUNT,
                            new File(getDownloadDirectory(), DOWNLOAD_ACTION_FILE),
                            DOWNLOAD_DESERIALIZERS);
            exoDownloadTracker =
                    new ExoWholeDownloadTracker(
                            /* context= */ context,
                            buildDataSourceFactory(context,/* listener= */ null),
                            new File(getDownloadDirectory(), DOWNLOAD_TRACKER_ACTION_FILE),
                            DOWNLOAD_DESERIALIZERS,downloadServiceClass);
            downloadManager.addListener(exoDownloadTracker);
        }
    }

}
