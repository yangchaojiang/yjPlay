package chuangyuan.ycj.videolibrary.factory;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;


/**
 * Created by yangc on 2017/8/25.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 默认缓存缓存工厂类
 */
public class DefaultCacheDataSourceFactory implements DataSource.Factory {
    private final JDefaultDataSourceFactory defaultDatasourceFactory;
    private SimpleCache simpleCache;
    private CacheDataSource.EventListener listener;

    /***
     * @param context 上下文
     */
    public DefaultCacheDataSourceFactory(@NonNull Context context) {
        this(context, 1024 * 1024 * 1024L * 1024, null, null);
    }

    /***
     * @param context 上下文
     * @param maxCacheSize 缓存大小
     */
    public DefaultCacheDataSourceFactory(@NonNull Context context, long maxCacheSize) {
        this(context, maxCacheSize, null, null);
    }

    /***
     * @param context 上下文
     * @param maxCacheSize 缓存大小
     * @param secretKey If not null, cache keys will be stored encrypted on filesystem using AES/CBC.     The key must be 16 bytes long.
     */
    public DefaultCacheDataSourceFactory(@NonNull Context context, long maxCacheSize, byte[] secretKey) {
        this(context, maxCacheSize, secretKey, null);
    }

    /***
     * @param context 上下文
     * @param maxCacheSize 缓存大小
     * @param secretKey If not null, cache keys will be stored encrypted on filesystem using AES/CBC.     The key must be 16 bytes long.
     * @param listener the listener
     */
    public DefaultCacheDataSourceFactory(@NonNull Context context, long maxCacheSize, byte[] secretKey, @Nullable CacheDataSource.EventListener listener) {
        defaultDatasourceFactory = new JDefaultDataSourceFactory(context);
        simpleCache = new SimpleCache(new File(context.getExternalCacheDir(), "media"), new LeastRecentlyUsedCacheEvictor(maxCacheSize), secretKey);
        this.listener = listener;
    }

    @Override
    public DataSource createDataSource() {
        return new CacheDataSource(simpleCache, defaultDatasourceFactory.createDataSource(),
                new FileDataSource(), new CacheDataSink(simpleCache, CacheDataSource.DEFAULT_MAX_CACHE_FILE_SIZE),
                0, listener);
    }
}