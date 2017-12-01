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
 2.使用,自动缓存你服务器视频资源。
 ````
  //实例化播放器控制类,传入您自定义数据实例
  exoPlayerManager = new GestureVideoPlayer(this, videoPlayerView,new OfficeDataSource(this,null));
 ```` 
 
 ### 二.自定义AES加密视频
  1.实例化解密数据源
```
public class EnctyptDataSource implements DataSourceListener {
    public static final String TAG = "DataSource";
    private Context context;
    private Cipher cipher;
    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;

      /***
         * @param context context
         * @param cipher cipher
         *  @param mSecretKeySpec mSecretKeySpec
         *  @param   mIvParameterSpec     mSecretKeySpec
         * **/
    public EnctyptDataSource(Context context, Cipher cipher, SecretKeySpec mSecretKeySpec, IvParameterSpec mIvParameterSpec) {
        this.context = context;
        this.cipher = cipher;
        this.mSecretKeySpec = mSecretKeySpec;
        this.mIvParameterSpec = mIvParameterSpec;
    }
    @Override
    public DataSource.Factory getDataSourceFactory() {
      //初始化解密工厂类
        return new EncryptedFileDataSourceFactory(context,cipher, mSecretKeySpec, mIvParameterSpec);
    }
}

```
2.使用
````
//实例化你加密解密类
 exoPlayerManager = new ExoUserPlayer(this, mSimpleExoPlayerView, new EnctyptDataSource(this, mCipher, mSecretKeySpec, mIvParameterSpec));
 //传入你加密视频路径
  Uri uri = Uri.fromFile(mEncryptedFile);
  exoPlayerManager.setPlayUri(uri);
  exoPlayerManager.startPlayer();
````
3.加密处理
>> 注意:原来FileOutputStream 换成CipherOutputStream 流 进行写加密文件操作,其他操作不变。
````
//写文件输出流 
 FileOutputStream fos = new FileOutputStream(mFile.getAbsolutePath(), false);
//加密输出流 
  CipherOutputStream cipherOutputStream = new CipherOutputStream(fos, mCipher);
````
 ### 三.使用自定义下载加密查看demo[OfficeDetailedActivity实例]()



 
 
 
  
 