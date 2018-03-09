 ## 加密视频
 ### 一.使用自带缓存加密
  1. 实现DataSourceListener 接口  如下:
  
   >>默认:   new DefaultCacheDataSourceFactory(context,100000000,"1234567887654321".getBytes(),eventListener);
   
   >>自定义:  new CacheDataSourceFactory(simpleCache, new JDefaultDataSourceFactory(context),0,4 * 1024 * 1024);
 
   2.代码实例
 ````
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
        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(1024*1024*1024 * 1024);
        SimpleCache simpleCache = new SimpleCache
                //设置你缓存目录
                (new File(context.getExternalCacheDir(), "media"),
                 //缓存驱逐器
                  evictor,
                  // 缓存文件加密,那么在使用AES / CBC的文件系统中缓存密钥将被加密  密钥必须是16字节长
                  //可以为空
                  "1234567887654321".getBytes());
        //使用缓存数据源工厂类
        return new CacheDataSourceFactory(simpleCache,
                //设置下载数据加载工厂类
                new JDefaultDataSourceFactory(context),
                //设置缓存标记
                0,
                //最大缓存文件大小,不填写 默认2m 
                 4 * 1024 * 1024);
         
    }
 }
 ````

 
 ### 二.使用离线下载帮助类 AES加密视频
  1.实例化离线下载帮助类
```
  //设置下载缓存实例
        downloader = new DefaultProgressDownloader.Builder(this)
                .setMaxCacheSize(100000000)
                //设置你缓存目录
                //.setCacheFileDir(new File(getExternalCacheDir(), "media"))
                //缓存文件加密,那么在使用AES / CBC的文件系统中缓存密钥将被加密  密钥必须是16字节长.
                .setSecretKey("1234567887654321".getBytes())
                .setUri(getString(R.string.uri_test_8))
                //设置下载数据加载工厂类
                .setHttpDataSource(new JDefaultDataSourceFactory(this))
                .build();
        if ((int) downloader.getDownloadPercentage() == 100) {
            Toast.makeText(getApplicationContext(), "下载完成" + downloader.getDownloadPercentage(), Toast.LENGTH_SHORT).show();
            progressBar.setProgress(100);
            button.setText("播放");
            exoPlayerManager.startPlayer();
        } else {
            downloader.download(new Downloader.ProgressListener() {
                @Override
                public void onDownloadProgress(Downloader downloader, float downloadPercentage, long downloadedBytes) {
                    Log.d(TAG, "downloadPercentage:" + downloadPercentage + "downloadedBytes:" + downloadedBytes);
                    progressBar.setProgress((int) downloadPercentage);
                    textView.setText("sdsd:"+(int) downloadPercentage);
                    if ((int) downloadPercentage == 100) {
                        Toast.makeText(getApplicationContext(), "下载完成", Toast.LENGTH_SHORT).show();
                        button.setText("播放");
                    }
                }
            });
        }

```
 2.使用,自动缓存你服务器视频资源。视频还是原来传入网络视频链接,自动识别该视频有缓存文件。
 ````
  //实例化播放器控制类,传入您自定义数据实例
  exoPlayerManager = new GestureVideoPlayer(this, videoPlayerView,new OfficeDataSource(this,null));
 ````
 
 ### 三.使用自定义下载加密查看demo[OfficeDetailedActivity实例]()



 
 
 
  
 