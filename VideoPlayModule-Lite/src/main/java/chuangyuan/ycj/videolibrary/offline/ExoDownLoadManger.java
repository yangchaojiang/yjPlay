package chuangyuan.ycj.videolibrary.offline;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.ProgressiveDownloadAction;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import chuangyuan.ycj.videolibrary.BuildConfig;

/**
 * author  yangc
 * date 2018/6/17
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
public class ExoDownLoadManger {
    public static final String TAG = ExoDownLoadManger.class.getName();
    protected static final String DOWNLOAD_ACTION_FILE = "actions";
    protected static final String DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions";
    protected static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
    protected static final int MAX_SIMULTANEOUS_DOWNLOADS = 2;
    private static final DownloadAction.Deserializer[] DOWNLOAD_DESERIALIZERS =
            new DownloadAction.Deserializer[]{ ProgressiveDownloadAction.DESERIALIZER
            };
    protected String userAgent;
    protected File downloadDirectory;
    protected Cache downloadCache;
    protected DownloadManager downloadManager;
    protected ExoDownloadTracker exoDownloadTracker;
    protected Context context;
    private static class Holder {
     static     ExoDownLoadManger h = new ExoDownLoadManger();
    }

    public static ExoDownLoadManger getSingle() {
        return Holder.h;
    }

    /**
     * build 数据源 工厂
     * Returns a {@link DataSource.Factory}.
     */
    public DataSource.Factory buildDataSourceFactory(Context context, TransferListener listener) {
        DefaultDataSourceFactory upstreamFactory =
                new DefaultDataSourceFactory(context, listener, buildHttpDataSourceFactory(listener));
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache());
    }

    /**
     * build 数据源 工厂
     * Returns a {@link HttpDataSource.Factory}.
     */
    protected HttpDataSource.Factory buildHttpDataSourceFactory(
            TransferListener listener) {
        return new DefaultHttpDataSourceFactory(userAgent, listener);
    }


    /**
     * 是否应该使用扩展渲染器。
     * Returns whether extension renderers should be used.
     */
    public boolean useExtensionRenderers() {
        return "withExtensions".equals(BuildConfig.FLAVOR);
    }
    /****
     * 下载管理类
     * ***/
    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    /****
     * 下载跟踪器
     * ***/
    public ExoDownloadTracker getExoDownloadTracker() {
        return exoDownloadTracker;
    }
    /****
     * 初始化加载下载管理类
     * @param  context context
     * ***/
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
                    new ExoDownloadTracker(
                            /* context= */ context,
                            buildDataSourceFactory(context,/* listener= */ null),
                            new File(getDownloadDirectory(), DOWNLOAD_TRACKER_ACTION_FILE),
                            DOWNLOAD_DESERIALIZERS,downloadServiceClass);
            downloadManager.addListener(exoDownloadTracker);
        }
    }

    /***
     * 获取下载缓存目录
     * @return    Cache
     * ***/
    protected synchronized Cache getDownloadCache() {
        if (downloadCache == null) {
            File downloadContentDirectory = new File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY);
            downloadCache = new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor());
        }
        return downloadCache;
    }

    /****
     * 获取下载文件目录
     * @return  File
     * ***/
    protected File getDownloadDirectory() {
        if (downloadDirectory == null) {
            downloadDirectory = context.getExternalFilesDir(null);
            if (downloadDirectory == null) {
                downloadDirectory = context.getFilesDir();
            }
        }
        return downloadDirectory;
    }

    protected static CacheDataSourceFactory buildReadOnlyCacheDataSource(
            DefaultDataSourceFactory upstreamFactory, Cache cache) {
        return new CacheDataSourceFactory(
                cache,
                upstreamFactory,
                new FileDataSourceFactory(),
                /* cacheWriteDataSinkFactory= */ null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                /* eventListener= */ null);
    }
}
