# yjPlay
 
 
 ###基于ExoPlayer自定义播放器 支持直播
  
 * 1 ExoUserPlayer  基本播放器
 * 2 GestureVideoPlayer   增加手势  亮度，音量，快进，等手势
 * 3 ManualPlayer  默认手动播放，增加默认图
 * 5 增加广告视频预览
 * 6 增加视频清晰度切换
 * 7 增加缓存视频功能
 * 8 支持自定义各种数据源加载 okttp,rtmp, 缓存，Cronet等协议
 * 9 支持列表播放视频

 ## gif 显示有点卡，帧数低，实际很流畅

 ![](sss.gif)

 ### 1.Import
 use   import dependency in gradle
 
```
 repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
    
dependencies {

   compile 'com.ycjiang:VideoPlayModule:1.4.8'

}

 Maven

<dependency>
  <groupId>com.ycjiang</groupId>
  <artifactId>VideoPlayModule</artifactId>
  <version>1.4.8/version>
  <type>pom</type>
</dependency>

```


 ### 2.布局属性解说
 ```
   <chuangyuan.ycj.videolibrary.widget.VideoPlayerView
        android:id="@+id/exo_play_context_id"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/transparent"
        app:resize_mode="fit"
        app:show_timeout="3000"
        app:controller_layout_id="@layout/simple_exo_playback_control_view"
        app:player_layout_id="@layout/simple_exo_view"
        app:surface_type="texture_view"
        app:use_artwork="true"
        app:paddingEnd="0dp"
        app:paddingStart="0dp"
        app:fastforward_increment="0"
        app:rewind_increment="0"
        app:user_watermark="@mipmap/watermark_big"
        app:player_list="true"
        app:use_controller="true" />

```
 * 1   player_layout_id  播放器布局， controller_layout_id  控制器布局` //必选
 * 2   use_controller   控制器   //必选
 * 3   surface_type 视频渲染类型 //texture_view 和surface_view //必选
 * 4   resize_mode  视频渲染显示模式 fit,fill,fixed_width,fixed_height //可选
 * 5   default_artwork  占位图  //可选
 * 6   show_timeout  控制类超时时间隐藏时间  默认值为3秒
 * 7   paddingEnd，paddingStart 设置边距  默认值为0
 * 8   fastforward_increment  设置快进增量,以毫秒为单位。
 * 9   rewind_increment   设置快退增量,以毫秒为单位。
 * 10  user_watermark    水印图片
 * 11  player_list      是否指定列表播放  默认false  true 列表播放
 *    在你app的strings.xml  可以替换对框框提示标题和内容

####  自定义修改提示文字
```
 app.strings.xml
     <string name="exo_play_reminder">您当前网络不是wifi，是否继续观看视频</string>
     <string name="exo_play_wifi_hint_no">提示</string>
 ```

####  在功能清单申明 AndroidManifest.xml

 在activity节点上
 加上“android:configChanges="orientation|keyboardHidden|screenSize"”
 如下实例：
 ```
 <activity   android:name="chuangyuan.ycj.yjplay.MainListActivity"
             android:configChanges="orientation|keyboardHidden|screenSize"
             android:screenOrientation="portrait">
 ```

 ### 3.JAVA代码 详情播放
 ```
    //播放代码
    //ManualPlayer  点击开始按钮播放   具有手势操作。
    //GestureVideoPlayer  具有手势操作播放（调节亮度和视频进度，和音量）
    //ExoUserPlayer 没有手势操作播放 基本播放
     ManualPlayer exoPlayerManager = new ManualPlayer(this,R.id.exo_play_context_id);
     //自定义你的数据源
     // ManualPlayer exoPlayerManager = new ManualPlayer(this,R.id.exo_play_context_id,new DataSource(this) );
    //是否开启缓存功能
     exoPlayerManager.setCache(true);//该方法是否setPlayUri 方法之前调用的setCache
     //加载m3u8
     exoPlayerManager.setPlayUri("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8");
     //架子ts.文件
     exoPlayerManager.setPlayUri("http://185.73.239.15:25461/live/1/1/924.ts");
     //播放本地视频
    // exoPlayerManager.setPlayUri("/storage/emulated/0/DCIM/Camera/VID_20170717_011150.mp4");

    //下面开启多线路播放
    //  exoPlayerManager.setShowVideoSwitch(true); //开启切换按钮，默认关闭
    //String [] test={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
    // String[] name={"超清","高清","标清"};
    //exoPlayerManager.setPlaySwitchUri(test,name);
    //添加水印
    // exoPlayerManager.setExoPlayWatermarkImg();
    //实现相应周期方法
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        exoPlayerManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        exoPlayerManager.onPause();
    }
   @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayerManager.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        exoPlayerManager.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        exoPlayerManager.onBackPressed();
    }
 ```
### 4.JAVA代码 列表播放
  * 列表播放，只能使用ManualPlayer 不允许使用 GestureVideoPlayer，ExoUserPlayer。在你的VideoHolder
  * 在列表控件使用属性 ”app:controller_layout_id="@layout/simple_exo_playback＿list_view"“ //提供默列表控制布局
  *  实例化控件如下

```
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context mContext;
    private List<String> mVideoList;
    public VideoAdapter(Context context, List<String> videoList) {
        mContext = context;
        mVideoList = videoList;
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }
    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_video1, parent, false);
        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        String video = mVideoList.get(position);
        holder.bindData(video);
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        ManualPlayer userPlayer;
        VideoPlayerView playerView;
        public VideoViewHolder(View itemView) {
            super(itemView);
            //初始化控件
            playerView = (VideoPlayerView) itemView.findViewById(R.id.item_exo_player_view);
            userPlayer = new ManualPlayer((Activity) mContext, playerView);
        }

       /**
       *绑定数据源
       ***/
        public void bindData(String videoBean) {
            userPlayer.setTitle("" + getAdapterPosition());
            userPlayer.setPlayUri(videoBean);
            Glide.with(mContext)
            .load("http://i3.letvimg.com/lc08_yunzhuanma/201707/29/20/49/3280a525bef381311b374579f360e80a_v2_MTMxODYyNjMw/thumb/2_960_540.jpg")
            .into(playerView.getPreviewImage());
        }
    }
```
 * 2 在你的activity或者Fragment 实现相应周期方法
     VideoPlayerManager 类是视频播放列表，控制类。
 ```
   @Override
    protected void onPause() {
        super.onPause();
        VideoPlayerManager.getInstance().onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        VideoPlayerManager.getInstance().onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayerManager.getInstance().onDestroy();
    }
    @Override
    public void onBackPressed() {
        //返回监听类
        if (!VideoPlayerManager.getInstance().onBackPressed()){
            finish();
        }
    }
 ```
### 5.使用自定义控制布局
   * 使用自定义控制布局  custom_playback_list_view.xml 详细demo测试
```
<chuangyuan.ycj.videolibrary.widget.VideoPlayerView
        android:id="@+id/exo_play_context_id"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:background="@android:color/transparent"
        app:controller_layout_id="@layout/custom_playback_list_view"
        app:fastforward_increment="0"
        app:paddingEnd="0dp"
        app:paddingStart="0dp"
        app:player_layout_id="@layout/simple_exo_view"
        app:resize_mode="fit"
        app:rewind_increment="0"
        app:show_timeout="3000"
        app:surface_type="texture_view"
        app:use_artwork="true"
        app:auto_show="true"
        app:user_watermark="@mipmap/watermark_big"
        app:use_controller="true" />

```
  * 注意自定义控制布局事项 不能改变控件类型，
     可以改变控件的样式，位置，属性等

### 6.数据源工厂类
#### 1 默认数据源
  * 缓存 : CacheDataSinkFactory,CacheDataSourceFactory
  * http : DefaultDataSourceFactory,DefaultHttpDataSourceFactory
  * file : FileDataSourceFactory
  * Priority : PriorityDataSourceFactory

#### 2 自定义数据源引用
 ````
     compile 'com.google.android.exoplayer:extension-okhttp:r2.5.1'
     compile 'com.google.android.exoplayer:extension-rtmp:r2.5.1'

 ````
#### 3.自定义数据源工厂类
 * 1.实现接口 DataSourceListener  然后在getDataSourceFactory方法里 自定义 数据源
 * 2.在你使用播放控件时中实例化类
```
    exoPlayerManager = new GestureVideoPlayer(this,videoPlayerView,new DataSource(this));
    exoPlayerManager = new GestureVideoPlayer(this,(R.id.exo_play_context_id,new DataSource(this));

```
 * 3.自定义类DataSource
```
public class DataSource implements DataSourceListener {
    public static final String TAG = "DataSource";
    private Context context;
     public   DataSource (Context context){
         this.context=context;
     }
    @Override
    public com.google.android.exoplayer2.upstream.DataSource.Factory getDataSourceFactory() {
        OkHttpClient  okHttpClient = new OkHttpClient();
        OkHttpDataSourceFactory OkHttpDataSourceFactory=    new OkHttpDataSourceFactory(okHttpClient, Util.getUserAgent(context, context.getApplicationContext().getPackageName()),new DefaultBandwidthMeter() );
         //使用OkHttpClient 数据源工厂
          //return  OkHttpDataSourceFactory; ;
          //默认数据源工厂
          // return new JDefaultDataSourceFactory(context);
          // Rtmp数据源工厂 对 Rtmp 协议支持
          // return  new RtmpDataSourceFactory();
         //缓存使用和组合使用
          LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(1000000000);
         SimpleCache   simpleCache = new SimpleCache(new File(context.getCacheDir(), "media"), evictor);
         //缓存数据源使用，内部使用DefaultDataSourceFactory数据源工厂类
         // return  new CacheDataSinkFactory(simpleCache,10000);
         // 配合okHttp数据源工厂类
         return  new CacheDataSourceFactory(simpleCache, OkHttpDataSourceFactory);
         //使用配合默认数据源红工厂类
         // return  new CacheDataSourceFactory(simpleCache, new JDefaultDataSourceFactory(context));
        }
}
```

## 升级日志
 ### 1.4.8
   * 1 修改自定义加载源类，不再用单利模式,采用控件实使用进行调用
 ### 1.4.7
   * 1 修复1.4.6切换bug
 ### 1.4.6
   * 1 增加视频列表播放支持
   * 2 增加VideoPlayerManager 列表播放管理类
   * 3 增加自定义进度条控件
   * 4 修复bug
 ### 1.4.5
   * 1 增加视频缓存功能
   * 2 去掉ExoUserPlayer 构造方法设置uri
   * 3 修复已知问题
   * 4 增加自定义数据源工厂类，实现自己文件数据源类型
   * 5 升级内核版本
   ### 1.4.4
   * 1  修复线路切换文字不改变稳定
   * 2  增加线路提供方法。集合和数组
   * 3  提供布局设置水印,修复水印方法，去掉默认水印
  ### 1.4.3
  * 1  修改重新播放页面
  * 2  增加进度默认设置
  * 3  增加占位图设置
  * 4  修复全屏事问题
 ###  1.4.1
 * 1 修复其他问题,
 * 2 还原 自定义属性
 ### 1.4.0
 * 1 增加视频清晰度切换，在横屏
 * 2 修改手势类，之间业务剥离出来
 * 3 修复其他问题,
 * 4 升级内核版本，布局和业务分离处理
 * 5 重新整理项目结构，不兼容1.4.0 版本之前

 ### 1.3.0
 * 1.增加播放数据流量提醒框，增加网络变化监听
 * 2.toobar状态的隐藏和显示,  增加v7依赖
 * 3.直播隐藏进度条
 * 4.两个视频切换，广告视频，进度处理
 * 5.修复已知bug.简化处理


