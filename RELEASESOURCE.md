 ### 数据源工厂类
 ####  1.默认数据源
  >>         缓存 : CacheDataSinkFactory,CacheDataSourceFactory
  >>         http : DefaultDataSourceFactory,DefaultHttpDataSourceFactory
  >>         Priority : PriorityDataSourceFactory
#### 2 自定义数据源引用

  >>      compile 'com.google.android.exoplayer:extension-okhttp:2.7.1'
  >>      compile 'com.google.android.exoplayer:extension-rtmp:2.7.1'

>>#### 3.自定义数据源工厂类:
   * 实现接口 DataSourceListener  然后在getDataSourceFactory方法里 自定义 数据源
   * 在你使用播放控件时中实例化类

   >>       exoPlayerManager = new GestureVideoPlayer(this,videoPlayerView,new DataSource(this));
   >>        exoPlayerManager = new GestureVideoPlayer(this,(R.id.exo_play_context_id,new DataSource(this));

   * demo代码:

   >>     public class DataSource implements DataSourceListener {
   >>           public static final String TAG = "DataSource";
   >>           private Context context;
   >>            public   DataSource (Context context){
   >>                this.context=context;
   >>            }
   >>           @Override
   >>           public com.google.android.exoplayer2.upstream.DataSource.Factory getDataSourceFactory() {
   >>                OkHttpClient  okHttpClient = new OkHttpClient();
   >>                OkHttpDataSourceFactory OkHttpDataSourceFactory=    new OkHttpDataSourceFactory(okHttpClient, Util.getUserAgent(context, context.getApplicationContext().getPackageName()),new DefaultBandwidthMeter() );
   >>                   //使用OkHttpClient 数据源工厂
   >>                 //return  OkHttpDataSourceFactory; ;
   >>                 //默认数据源工厂
   >>                 // return new JDefaultDataSourceFactory(context);
   >>                 // Rtmp数据源工厂 对 Rtmp 协议支持
   >>                 // return  new RtmpDataSourceFactory();
   >>                //缓存使用和组合使用
   >>                 LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(1000000000);
   >>                SimpleCache   simpleCache = new SimpleCache(new File(context.getCacheDir(), "media"), evictor);
   >>                //缓存数据源使用，内部使用DefaultDataSourceFactory数据源工厂类
   >>                // return  new CacheDataSinkFactory(simpleCache,10000);
   >>                // 配合okHttp数据源工厂类
   >>                return  new CacheDataSourceFactory(simpleCache, OkHttpDataSourceFactory);
   >>                //使用配合默认数据源红工厂类
   >>                // return  new CacheDataSourceFactory(simpleCache, new JDefaultDataSourceFactory(context));
   >>                //使用提供缓存数据源工厂类
   >>                // return new CacheDataSourceFactory(context,1000,1000);
   >>               }
   >>    }
