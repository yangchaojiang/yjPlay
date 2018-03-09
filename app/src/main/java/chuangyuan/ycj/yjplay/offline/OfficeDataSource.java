package chuangyuan.ycj.yjplay.offline;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;


import chuangyuan.ycj.videolibrary.factory.DefaultCacheDataSourceFactory;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;


/**
 * Created by yangc on 2017/8/31.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  缓存加密自定义数数据源 工厂类
 */

public class OfficeDataSource implements DataSourceListener {
    public static final String TAG = "OfficeDataSource";
    private CacheDataSource.EventListener eventListener;
    private Context context;

    public OfficeDataSource(Context context, CacheDataSource.EventListener eventListener) {
        this.context = context;
        this.eventListener = eventListener;
    }

    @Override
    public DataSource.Factory getDataSourceFactory() {
        //采用默认
        return new DefaultCacheDataSourceFactory(context,100000000,"1234567887654321".getBytes(),eventListener);
        //自定义配置
      /*  LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(100000000);
        SimpleCache simpleCache = new SimpleCache
                //设置你缓存目录
                (new File(context.getExternalCacheDir(), "media"),
                        //缓存驱逐器
                        evictor,
                        // 缓存文件加密,那么在使用AES / CBC的文件系统中缓存密钥将被加密  密钥必须是16字节长
                        "1234567887654321".getBytes());
        //使用缓存数据源工厂类
        return new DefaultCacheDataSourceFactory(simpleCache,
                //设置下载数据加载工厂类
                new JDefaultDataSourceFactory(context),
                //设置缓存标记
                0
                //最大缓存文件大小,不填写 默认2m
                4 * 1024 * 1024
                //设置缓存监听事件
        );*/
        //或者 如果需要监听事件
        //使用缓存数据源工厂类
       /* return new CacheDataSourceFactory(simpleCache,
                //设置下载数据加载工厂类
                new JDefaultDataSourceFactory(context),
                //缓存读取数据源工厂
                new FileDataSourceFactory(),
                //缓存数据接收器的工厂
                new CacheDataSinkFactory(simpleCache, CacheDataSource.DEFAULT_MAX_CACHE_FILE_SIZE),
                //设置缓存标记
                0,
                //设置缓存监听事件
                eventListener);*/
    }

}
