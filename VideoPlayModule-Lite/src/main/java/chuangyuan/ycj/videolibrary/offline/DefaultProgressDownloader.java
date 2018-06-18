package chuangyuan.ycj.videolibrary.offline;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.ProgressiveDownloader;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chuangyuan.ycj.videolibrary.factory.JDefaultDataSourceFactory;
import chuangyuan.ycj.videolibrary.listener.ExoProgressListener;

/**
 * author  yangc
 * date 2017/11/25
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  常规媒体流的下载器。支持断点下载
 */
/** @deprecated Use {@link ExoDownloadTracker}. */
@Deprecated
public   class DefaultProgressDownloader {

    private final ProgressiveDownloader downloader;
    @Nullable
    private ExoProgressListener listener;
    @Nullable
    private ScheduledExecutorService service;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int ss = (int) getDownloadPercentage();
            if (listener != null) {
                listener.onDownloadProgress(downloader, getDownloadPercentage(), getDownloadedBytes());
            }
            if (ss == 100) {
                cancel();
            }
        }
    };

    protected DefaultProgressDownloader(Uri uri, DownloaderConstructorHelper constructorHelper) {
        downloader = new ProgressiveDownloader(uri, uri.toString(), constructorHelper);

    }


    /****
     * @param  listener  下载回调
     * 为-1 就是下载错误，进度
     * ***/
    public void download(@Nullable ExoProgressListener listener) {
        service = Executors.newScheduledThreadPool(2);
        this.listener = listener;
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
        service.execute(new MyRunnable());

    }

    public void remove() {
        downloader.remove();
    }

    public long getDownloadedBytes() {
        return downloader.getDownloadedBytes();
    }

    public float getDownloadPercentage() {
        return downloader.getDownloadPercentage();
    }

    /***
     * 取消下载任务
     */
    public void cancel() {
        if (service != null) {
            service.shutdown();
            service = null;
        }
        if (handler != null) {
            handler.removeMessages(1);
        }
    }

    /***
     * 线程下载任务
     * **/
    private final class MyRunnable implements java.lang.Runnable {
        @Override
        public void run() {
            try {
                downloader.download();
            } catch (Exception e) {
                e.printStackTrace();
                cancel();
                if (listener != null) {
                    listener.onDownloadProgress(downloader, -1, -1);
                }
            }
        }
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private Context context;
        private Uri uri;
        private Cache simpleCache;
        private DataSource.Factory upstreamFactory;
        private DownloaderConstructorHelper constructorHelper;
        private byte[] secretKey;
        private String cacheDir;
        private long maxCacheSize;

        /**
         * Instantiates a new Builder.
         *
         * @param context the context
         */
        public Builder(@NonNull Context context) {
            this.context = context.getApplicationContext();
        }

        /**
         * Sets cache.
         *
         * @param cache 缓存文件实例
         * @return Builder cache
         */
        public Builder setCache(@NonNull Cache cache) {
            this.simpleCache = cache;
            return this;
        }

        /**
         * Sets cache file dir.
         *
         * @param cacheDir 缓存文件所在目录
         * @return Builder cache file dir
         */
        public Builder setCacheFileDir(@NonNull String cacheDir) {
            this.cacheDir = cacheDir;
            return this;
        }

        /**
         * Sets secret key.
         *
         * @param secretKey 如果不是null，那么在使用AES / CBC的文件系统中缓存密钥将被加密                  密钥必须是16字节长.
         * @return Builder secret key
         */
        public Builder setSecretKey(@Nullable byte[] secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        /**
         * Sets uri.
         *
         * @param uri 需要下载uri
         * @return Builder uri
         */
        public Builder setUri(@NonNull String uri) {
            this.uri = Uri.parse(uri);
            return this;
        }

        /**
         * Sets uri.
         *
         * @param uri 需要下载uri
         * @return Builder uri
         */
        public Builder setUri(@NonNull Uri uri) {
            this.uri = uri;
            return this;
        }

        /**
         * Sets max cache size.
         *
         * @param maxCacheSize 缓存大小
         * @return Builder max cache size
         */
        public Builder setMaxCacheSize(long maxCacheSize) {
            this.maxCacheSize = maxCacheSize;
            return this;
        }

        /**
         * Sets http data source.
         *
         * @param upstreamFactory 下载时需要数据数据源工厂类
         * @return Builder http data source
         */
        public Builder setHttpDataSource(@NonNull DataSource.Factory upstreamFactory) {
            this.upstreamFactory = upstreamFactory;
            return this;
        }

        /**
         * Sets downloader helper.
         *
         * @param constructorHelper constructorHelper
         * @return Builder downloader helper
         */
        public Builder setDownloaderHelper(@NonNull DownloaderConstructorHelper constructorHelper) {
            this.constructorHelper = constructorHelper;
            return this;
        }

        /**
         * Build default progress downloader.
         *
         * @return DefaultProgressDownloader default progress downloader
         */
        public DefaultProgressDownloader build() {
            if (simpleCache == null) {
                if (maxCacheSize == 0) {
                    maxCacheSize = 1024 * 1024 * 1024L * 1024;
                }
                File file;
                if (cacheDir == null) {
                    file = new File(context.getExternalCacheDir(), "media");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else {
                    file = new File(cacheDir);
                }
                simpleCache = new SimpleCache(file, new LeastRecentlyUsedCacheEvictor(maxCacheSize), secretKey);
            }
            if (upstreamFactory == null) {
                upstreamFactory = new JDefaultDataSourceFactory(context);
            }
            if (constructorHelper == null) {
                constructorHelper = new DownloaderConstructorHelper(simpleCache, upstreamFactory);
            }
            return new DefaultProgressDownloader(uri, constructorHelper);
        }
    }
}