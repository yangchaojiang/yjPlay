package chuangyuan.ycj.yjplay;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import chuangyuan.ycj.videolibrary.factory.JDefaultDataSourceFactory;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;


/**
 * Created by yangc on 2017/8/31.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  自定义数数据源 工厂类
 */

public class DataSource implements DataSourceListener {
    public static final String TAG = "DataSource";

    private Context context;

     public   DataSource (Context context){
         this.context=context;

     }
    @Override
    public com.google.android.exoplayer2.upstream.DataSource.Factory getDataSourceFactory() {
      //  OkHttpClient  okHttpClient = new OkHttpClient();
     //   OkHttpDataSourceFactory OkHttpDataSourceFactory=    new OkHttpDataSourceFactory(okHttpClient, Util.getUserAgent(context, context.getApplicationContext().getPackageName()),new DefaultBandwidthMeter() );
         //使用OkHttpClient 数据源工厂
       //  return  OkHttpDataSourceFactory;
          //默认数据源工厂
        return new DefaultHttpDataSourceFactory(context.getPackageName(),null,100000,1000,true);
          // Rtmp数据源工厂 对 Rtmp 协议支持
          // return  new RtmpDataSourceFactory();
          //缓存使用和组合使用
        //  LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(1000000000);
         //  SimpleCache   simpleCache = new SimpleCache(new File(context.getCacheDir(), "media"), evictor);
         //缓存数据源使用，内部使用DefaultDataSourceFactory数据源工厂类
         // return  new CacheDataSinkFactory(simpleCache,10000);
         // 配合okHttp数据源工厂类
         //return  new CacheDataSourceFactory(simpleCache, OkHttpDataSourceFactory);
         //使用配合默认数据源红工厂类
         // return  new CacheDataSourceFactory(simpleCache, new JDefaultDataSourceFactory(context));
        }

}
