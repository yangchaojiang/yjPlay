 ## 加密视频
 ### 一.使用自带缓存加密
  1. 实现DataSourceListener 接口  如下:
  
   >>默认:   new DefaultCacheDataSourceFactory(context,100000000,"1234567887654321".getBytes(),eventListener);
   
   >>自定义:  new CacheDataSourceFactory(simpleCache, new JDefaultDataSourceFactory(context),0,4 * 1024 * 1024);
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
  1.实例化离线下载帮助类 有关类:如下
 >>    1.全文件下载使用ExoWholeDownLoadManger,ExoWholeDownloadTracker
 >>
 >>    2.常规视频质直链的文件使用,ExoDownLoadManger,DownloadTracker
 >>
 >>    ExoWholeDownLoadManger和ExoDownLoadManger使用方法一样。 ExoWholeDownLoadManger支持流媒体下载
 >>    ExoWholeDownLoadManger,内部已经集成(HlsDownloadHelper,DashDownloadHelper,SsDownloadHelper,ProgressiveDownloadHelper)。    
 >>    ExoDownLoadManger只有（ProgressiveDownloadHelper）
 
  示例代码如下:
```
   //实例化下载管理类
   ExoDownLoadManger.getSingle().initDownloadManager(this,DemoDownloadService.class);

```
 2.实现下载乃文件Service服务
```
/** 下载媒体的服务. */
public class DemoDownloadService extends DownloadService {
  //前台通知的通知ID。, 一定不
  private static final int FOREGROUND_NOTIFICATION_ID = 1;//

 //用于创建低优先级通知通道的ID，或者如果应用将根据需要创建通知通道，则为{null}。, 如果指定，则每个包必须是*唯一的，如果该值太长，则该值可能会被截断
  private static final String CHANNEL_ID = "download_channel";//通知栏
  //前台通知更新之间的最大间隔，以毫秒为单位。
  public static final long DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL = 1000;
  private static final int JOB_ID = 1;
  //则该通道的用户可见名称的字符串资源标识符。, 建议的最大长度是40个字符;, 值*可能会被截断，如果它太长
 private  static  final  int hannel_name  =R.string.exo_download_notification_channel_name;
  public DemoDownloadService() {
    super(
        FOREGROUND_NOTIFICATION_ID,
        DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
        CHANNEL_ID,
            hannel_name);
  }

  @Override
  protected DownloadManager getDownloadManager() {
   // return ((App) getApplication()).getDownloadManager();
    return ExoDownLoadManger.getSingle().getDownloadManager();
  }

  //得到调度器
  @Override
  protected PlatformScheduler getScheduler() {
    return Util.SDK_INT >= 21 ? new PlatformScheduler(this, JOB_ID) : null;
  }

  //获得前台通知
  @Override
  protected Notification getForegroundNotification(TaskState[] taskStates) {
    return DownloadNotificationUtil.buildProgressNotification(
        /* context= */ this,
        R.drawable.exo_controls_play,
        CHANNEL_ID,
        /* contentIntent= */ null,
        /* message= */ null,
        taskStates);
  }

  /***
   * 更新进度
   * ***/
  @Override
  protected void onTaskStateChanged(TaskState taskState) {
    if (taskState.action.isRemoveAction) {
      return;
    }
    Notification notification = null;
    if (taskState.state == TaskState.STATE_COMPLETED) {
      notification =
          DownloadNotificationUtil.buildDownloadCompletedNotification(
              /* context= */ this,
              R.drawable.exo_controls_play,
              CHANNEL_ID,
              /* contentIntent= */ null,
              Util.fromUtf8Bytes(taskState.action.data));
    } else if (taskState.state == TaskState.STATE_FAILED) {
      notification =
          DownloadNotificationUtil.buildDownloadFailedNotification(
              /* context= */ this,
              R.drawable.exo_controls_play,
              CHANNEL_ID,
              /* contentIntent= */ null,
              Util.fromUtf8Bytes(taskState.action.data));
    }
    int notificationId = FOREGROUND_NOTIFICATION_ID + 1 + taskState.taskId;
    NotificationUtil.setNotification(this, notificationId, notification);
  }
}

```
  4.开始下载文件
  
````
 //得到下载跟踪器
  DownloadTracker  downloadTracker = ExoDownLoadManger.getSingle().getDownloadTracker();
 //下载文件
 downloadTracker.toggleDownload(this, getPackageName(), Uri.parse(getString(R.string.uri_test_8)), "mp4");
 //是否已经下载
  downloadTracker.isDownloaded(Uri.parse(getString(R.string.uri_test_8))
````  
 2.使用,自动缓存你服务器视频资源。视频还是原来传入网络视频链接,自动识别该视频有缓存文件。
 ````
  //实例化播放器控制类,传入您自定义数据实例
  exoPlayerManager = new GestureVideoPlayer(this, videoPlayerView,new OfficeDataSource(this,null));
 ````
 
 ### 三.使用自定义下载加密查看demo[OfficeDetailedActivity实例]()



 
 
 
  
 