 ### Data source factory class
 ####  1.Default data source
  >>         缓存 : CacheDataSinkFactory,CacheDataSourceFactory
  >>         http : DefaultDataSourceFactory,DefaultHttpDataSourceFactory
  >>         Priority : PriorityDataSourceFactory
#### 2 Customize the data source reference

  >>     compile 'com.google.android.exoplayer:extension-okhttp:r2.5.1'
  >>      compile 'com.google.android.exoplayer:extension-rtmp:r2.5.1'

>>#### 3.Custom data source factory classes::
   * Implementing an interface DataSourceListener   getDataSourceFactory Methods customize the data source
   * Instantiate the class when you use the playback control

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
   >>                   //OkHttpClient Data source factory
   >>                 //return  OkHttpDataSourceFactory; ;
   >>                 //Default data source factory
   >>                 // return new JDefaultDataSourceFactory(context);
   >>                 //Rtmp data source factory supports Rtmp protocol
   >>                 // return  new RtmpDataSourceFactory();
   >>                //Cache usage and combination usage
   >>                 LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(1000000000);
   >>                SimpleCache   simpleCache = new SimpleCache(new File(context.getCacheDir(), "media"), evictor);
   >>                //Cache Data Source use, internal use of the Default Data Source Factory Factory class
   >>                // return  new CacheDataSinkFactory(simpleCache,10000);
   >>                // Cooperate with the okHttp data source factory class
   >>                return  new CacheDataSourceFactory(simpleCache, OkHttpDataSourceFactory);
   >>                //Use the default data source factory class
   >>                // return  new CacheDataSourceFactory(simpleCache, new JDefaultDataSourceFactory(context));
   >>                //Use the provided cache data source factory class
   >>                // return new CacheDataSourceFactory(context,1000,1000);
   >>               }
   >>    }