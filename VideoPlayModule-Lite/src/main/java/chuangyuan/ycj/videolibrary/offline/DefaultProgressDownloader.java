package chuangyuan.ycj.videolibrary.offline;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.offline.Downloader;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.PriorityTaskManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chuangyuan.ycj.videolibrary.factory.JDefaultDataSourceFactory;

/**
 * author  yangc
 * date 2017/11/25
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  常规媒体流的下载器。支持断点下载
 */
public final class DefaultProgressDownloader implements Downloader {

    private static final int BUFFER_SIZE_BYTES = 128 * 1024;
    private final DataSpec dataSpec;
    private final Cache cache;
    private final CacheDataSource dataSource;
    private final PriorityTaskManager priorityTaskManager;
    private final DefaultCacheUtil.CachingCounters cachingCounters;
    private ExecutorService service;

    private DefaultProgressDownloader(
            Uri uri, String customCacheKey, DownloaderConstructorHelper constructorHelper) {
        this.dataSpec = new DataSpec(uri, 0, C.LENGTH_UNSET, customCacheKey, 0);
        this.cache = constructorHelper.getCache();
        this.dataSource = constructorHelper.buildCacheDataSource(false);
        this.priorityTaskManager = constructorHelper.getPriorityTaskManager();
        cachingCounters = new DefaultCacheUtil.CachingCounters();
    }

    @Override
    public void init() {
        DefaultCacheUtil.getCached(dataSpec, cache, cachingCounters);
    }

    @Override
    public void download(@Nullable Downloader.ProgressListener listener) {
        service = Executors.newSingleThreadExecutor();
        service.submit(new MyRunnable(listener, this));
    }

    @Override
    public void remove() {
        CacheUtil.remove(cache, CacheUtil.getKey(dataSpec));
    }

    @Override
    public long getDownloadedBytes() {
        return cachingCounters.totalCachedBytes();
    }

    @Override
    public float getDownloadPercentage() {
        long contentLength = cachingCounters.contentLength;
        return contentLength == C.LENGTH_UNSET ? Float.NaN
                : ((cachingCounters.totalCachedBytes() * 100f) / contentLength);
    }

    /***
     * 取消下载任务
     */
    public void cancel() {
        if (service != null) {
            service.shutdown();
            service = null;
        }
    }

    /***
     * 线程下载任务
     * **/
    private class MyRunnable implements java.lang.Runnable {
        private ProgressListener listener;
        private DefaultProgressDownloader defaultProgressDownloader;

        /**
         * Instantiates a new My runnable.
         *
         * @param listener the listener
         * @param th       the th
         */
        MyRunnable(@Nullable ProgressListener listener, @NonNull DefaultProgressDownloader th) {
            this.listener = listener;
            this.defaultProgressDownloader = th;

        }

        @Override
        public void run() {
            if (defaultProgressDownloader != null) {
                priorityTaskManager.add(C.PRIORITY_DOWNLOAD);
                try {
                    byte[] buffer = new byte[BUFFER_SIZE_BYTES];
                    DefaultCacheUtil.cache(dataSpec, cache, dataSource, buffer, priorityTaskManager, C.PRIORITY_DOWNLOAD,
                            cachingCounters, true, listener, defaultProgressDownloader);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onDownloadProgress(defaultProgressDownloader, -1, defaultProgressDownloader.getDownloadedBytes());
                    }
                } finally {
                    priorityTaskManager.remove(C.PRIORITY_DOWNLOAD);
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
        private String customCacheKey;
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
            this.context = context;
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
         * @param customCacheKey 唯一标识原始流的自定义键。用于缓存索引。可能是null
         * @return Builder
         */
        private Builder setCustomCacheKey(@Nullable String customCacheKey) {
            this.customCacheKey = customCacheKey;
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
            this.uri = uri;
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
                } else {
                    file = new File(cacheDir, "media");
                }
                simpleCache = new SimpleCache(file, new LeastRecentlyUsedCacheEvictor(maxCacheSize), secretKey);
            }
            if (upstreamFactory == null) {
                upstreamFactory = new JDefaultDataSourceFactory(context);
            }
            if (constructorHelper == null) {
                constructorHelper = new DownloaderConstructorHelper(simpleCache, upstreamFactory);
            }
            return new DefaultProgressDownloader(uri, uri.getPath(), constructorHelper);
        }
    }
}