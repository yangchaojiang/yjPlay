package chuangyuan.ycj.videolibrary.factory;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;


/**
 * Created by yangc on 2017/8/25.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 数据缓存工厂类
 */

public  class CacheDataSourceFactory implements DataSource.Factory {
    private final Context context;
    private final DefaultDataSourceFactory defaultDatasourceFactory;
    private final long maxFileSize, maxCacheSize;
     private String cachePath;//缓存路径
    /***
     * @param  context 上下问
     * @param maxCacheSize 缓存大小
     * @param maxFileSize  最大文件大小
     ***/
    public CacheDataSourceFactory(Context context, long maxCacheSize, long maxFileSize) {
        super();
        this.context = context;
        this.maxCacheSize = maxCacheSize;
        this.maxFileSize = maxFileSize;
        String userAgent = Util.getUserAgent(context, context.getPackageName());
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        defaultDatasourceFactory = new DefaultDataSourceFactory(this.context, bandwidthMeter,
                new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter));
    }
    /***
     * @param  context 上下问
     * @param maxCacheSize 缓存大小
     * @param maxFileSize  最大文件大小
     * @param    cachePath  注意sd设置权限  设置视频缓存路径 {  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />}
     ***/
    public CacheDataSourceFactory(Context context, long maxCacheSize, long maxFileSize,String cachePath) {
        super();
        this.context = context;
        this.cachePath=cachePath;
        this.maxCacheSize = maxCacheSize;
        this.maxFileSize = maxFileSize;
        String userAgent = Util.getUserAgent(context, context.getPackageName());
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        defaultDatasourceFactory = new DefaultDataSourceFactory(this.context, bandwidthMeter,
                new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter));
    }

    @Override
    public DataSource createDataSource() {
        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(maxCacheSize);
        SimpleCache simpleCache;
        if (cachePath==null){
              simpleCache = new SimpleCache(new File(context.getCacheDir(), "media"), evictor);
        }else {
              simpleCache = new SimpleCache(new File(cachePath), evictor);
        }
        return new CacheDataSource(simpleCache, defaultDatasourceFactory.createDataSource(),
                new FileDataSource(), new CacheDataSink(simpleCache, maxFileSize),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
    }
}