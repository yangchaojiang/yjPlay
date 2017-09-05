# yjPlay
 
 ## gif 显示有点卡，帧数低，实际很流畅

 ![](sss.gif)

 ### 基于exoPlayer 自定义播放器 Jplayer支持功能：
   * 1 ExoUserPlayer  基本播放
   * 2 GestureVideoPlayer   增加手势  亮度，音量，快进，等手势
   * 3 ManualPlayer  默认手动播放，增加默认图
   * 5 增加广告视频预览切换
   * 6 增加视频清晰度切换
   * 7 增加缓存视频功能
   * 8 支持自定义各种数据源加载 Okttp,Rtmp, 缓存，Cronet等协议,
   * 9 支持列表播放视频
   * 10 支持多种文件类型，MP4，M4A，WebM，Matroska，Ogg，WAV，MP3，MPEG-TS，MPEG-PS，FLV，ADTS (AAC)，Flac，M3U8 等
   * 11 支持网络类型 提示是否播放
 <!--more-->
 ### 一.引用类库
  ````
   repositories {
          jcenter()
          maven { url "https://jitpack.io" }
      }

  dependencies {
     compile 'com.ycjiang:VideoPlayModule:1.4.8'

  }
  ````

 ### 二.控件属性


 > #### 1.控件属性引用
 ````
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
 ````
 > #### 2.属性说明
    1.   player_layout_id  播放器布局， //必选
        player_layout_id 目前支持指定布局simple_exo_playback_control_view 后续版本，开放自定义使用这自定义
    2.   controller_layout_id  控制器布局` //必选
        controller_layout_id  支持自定义布局
    3.   surface_type 视频渲染类型 //texture_view 和surface_view //枚举类型。默认surface_view
            列表播放只能选择texture_view 不能选择surface_view，详情页面播放推荐surface_view
    4.   use_controller   是否用户控制控制器  布尔类型
    5.   resize_mode  视频缩放渲染显示方式一共4种 //可选 美剧类型
            1.fit          //正常模式
            2.fixed_width  //保持的是视频宽度，拉伸视频高度
            3.fixed_height //保持的是视频高度，拉伸视频宽度
            4.fill          //全屏模式，拉伸视频宽高
    6.   default_artwork  占位图  //可选
         占位图 注意在控制布局后下面
    7.   show_timeout  控制布局隐藏时间  默认值为3秒  //可选
    8.   paddingEnd，paddingStart 设置边距  默认值为0  //可选
    9.   fastforward_increment  设置快进增量,以毫秒为单位。 //可选
    10.  rewind_increment   设置快退增量,以毫秒为单位。  //可选
    11.  user_watermark    水印图片 默认在右上角  //可选
    12.  player_list      是否指定列表播放   //可选 默认 false  true 列表播放

 >#### 3.修改网络对话框提示文字内容
      app.strings.xml
      <string name="exo_play_reminder">您当前网络不是wifi，是否继续观看视频</string>
      <string name="exo_play_wifi_hint_no">提示</string>

 >#### 4.在功能清单声明 AndroidManifest.xml
    在activity节点 加上“android:configChanges="orientation|keyboardHidden|screenSize"”
     如下实例：
            <activity android:name="chuangyuan.ycj.yjplay.MainListActivity"
             android:configChanges="orientation|keyboardHidden|screenSize"
             android:screenOrientation="portrait">


 ### 3.JAVA 代码

 > #### 1 播放控制类
    1.ExoUserPlayer 基本播放父类，实现基本播放,设置setPlayUri();会自动加载播放
    2.GestureVideoPlayer  具有手势操作播放（调节亮度和视频进度，和音量）会自动加载播放
    2.ManualPlayer  点击开始按钮播放,具有手势功能，和列表播放

 > #### 2 播放
         //实例化播放控制类
          ManualPlayer exoPlayerManager = new ManualPlayer(this,R.id.exo_play_context_id);
         //自定义你的数据源，后面详细介绍如何自定义数据源类
          // ManualPlayer exoPlayerManager = new ManualPlayer(this,R.id.exo_play_context_id,new DataSource(this));
          //加载m3u8
          exoPlayerManager.setPlayUri("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8");
          //加载ts.文件
          exoPlayerManager.setPlayUri("http://185.73.239.15:25461/live/1/1/924.ts");
          //播放本地视频
          // exoPlayerManager.setPlayUri("/storage/emulated/0/DCIM/Camera/VID_20170717_011150.mp4");
          //下面开启多线路播放
          //  exoPlayerManager.setShowVideoSwitch(true); //开启切换按钮，默认关闭
          //String [] test={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
          // String[] name={"超清","高清","标清"};
          //exoPlayerManager.setPlaySwitchUri(test,name);
          //添加水印图片
          // exoPlayerManager.setExoPlayWatermarkImg();

   1.实例化播放控制类

          ManualPlayer exoPlayerManager = new ManualPlayer(this,R.id.exo_play_context_id);
          ManualPlayer exoPlayerManager = new ManualPlayer(this,videoPlayerView);

   2.自定义你的数据源，后面详细介绍如何自定义数据源类

         ManualPlayer exoPlayerManager = new ManualPlayer(this,R.id.exo_play_context_id,new DataSource(this));
         ManualPlayer exoPlayerManager = new ManualPlayer(this,videoPlayerView,new DataSource(this));

   3.设置视频标题

          exoPlayerManager.setTitle("视频标题");

   4.添加水印图片

         exoPlayerManager.setExoPlayWatermarkImg(R.mipmap.watermark_big);

   5.设置开始播放进度

         exoPlayerManager.setPosition(1000)

   6.设置视频路径

         exoPlayerManager.setPlayUri("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8");
         exoPlayerManager.setPlayUri(Uri.parse("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8"));
         exoPlayerManager.setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.h264"); //本地视频

   7.设置多线路播放

          //开启多线路设置，默认关闭
          exoPlayerManager.setShowVideoSwitch(true);
          //支持List列表
          String [] test={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4",
          "http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4",
           http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
           String[] name={"超清","高清","标清"};
           exoPlayerManager.setPlaySwitchUri(test,name);
    >>

   8.设置监听回调VideoInfoListener

         exoPlayerManager.setVideoInfoListener(new VideoInfoListener() {
                       @Override
                       public void onPlayStart() {
                             //开始播放
                       }

                       @Override
                       public void onLoadingChanged() {
                                 //加载变化
                       }

                       @Override
                       public void onPlayerError(ExoPlaybackException e) {
                                 //加载错误
                      }

                       @Override
                       public void onPlayEnd() {
                              //播放结束
                       }

                       @Override
                       public void onBack() {
                           //返回回调
                           Toast.makeText(MainDetailedActivity.this,"f返回",Toast.LENGTH_LONG).show();
                           finish();

                       }
                       @Override
                       public void onRepeatModeChanged(int repeatMode) {
                           //模式变化
                       }
                   });
   9.覆写Activity和Fragment周期方法

                Override
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
                    exoPlayerManager.onDestroy();
                    super.onDestroy();

                }

                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    exoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换
                    super.onConfigurationChanged(newConfig);
                }

                @Override
                public void onBackPressed() {//使用播放返回键监听
                   exoPlayerManager.onBackPressed();
                }


 ### 三.列表
   1.列表播放，只能使用ManualPlayer,在你的VideoHolder
   *  1在列表控件使用属性 ”app:controller_layout_id="@layout/simple_exo_playback＿list_view"“  //提供默列表控制布局
   *  2.player_list="true" 设置为true 开启列表模式
   *  3.demo:
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
  2.列表播放周期方法 列表在Activity或者Fragment  实现相应周期方法

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

### 四.数据源工厂类
 ####  1.默认数据源
          缓存 : CacheDataSinkFactory,CacheDataSourceFactory
          http : DefaultDataSourceFactory,DefaultHttpDataSourceFactory
          Priority : PriorityDataSourceFactory
#### 2 自定义数据源引用

      compile 'com.google.android.exoplayer:extension-okhttp:r2.5.1'
      compile 'com.google.android.exoplayer:extension-rtmp:r2.5.1'

>>#### 3.自定义数据源工厂类:
   * 实现接口 DataSourceListener  然后在getDataSourceFactory方法里 自定义 数据源
   * 在你使用播放控件时中实例化类

### [自定义数据源和自定义控制布局用法-戳我](http://yangchaojiang.cn/2017/09/05/jPlayer-blog/#四-数据源工厂类)
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


