# yjPlay
 
  ### gif 显示有点卡，帧数低，实际很流畅
  #### [下载预览apk](https://raw.githubusercontent.com/yangchaojiang/yjPlay/master/app-debug.apk)

 ![](test.gif)

 ### 基于exoPlayer 自定义播放器 JPlayer支持功能：
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
   * 12 **1.5.5**增加,视频加载布局, 错误布局,重播布局，提示布局自定义，更灵活实现自己布局样式
 <!--more-->

 ### [更新日志→》戳我查看](RELEASENOTES.md)
 
   >> [查看1.5.6升级日志](RELEASENOTES.md#156)
   
 ### 一.引用类库
  ````
   repositories {
          jcenter()
          mavenCentral();
      }

  dependencies {
     compile 'com.ycjiang:VideoPlayModule:1.5.6'

  }
  ````
  >>> 提示：无法正常引用请在 repositories{ }添加已下代码
  >>> mavenCentral(url: "https://dl.bintray.com/ycjiang/ycjiang")

 ### 二.控件属性


 >>  #### 1.控件属性
  >>>>控件自定义属性
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
         app:paddingEnd="0dp"
         app:paddingStart="0dp"
         app:fastforward_increment="0"
         app:rewind_increment="0"
         app:user_watermark="@mipmap/watermark_big"
         app:player_list="true"
         app:use_controller="true"
         app:player_replay_layout_id="@layout/custom_play_replay"
         app:player_error_layout_id="@layout/custom_play_error"
         app:player_hint_layout_id="@layout/custom_play_btn_hint"
         app:player_load_layout_id="@layout/custom_exo_play_load"
          />
   ````
   >>>> 基本使用如下
   ````
         <chuangyuan.ycj.videolibrary.widget.VideoPlayerView
                 android:id="@+id/exo_play_context_id"
                 android:layout_width="match_parent"
                 android:layout_height="match_parent"
                 android:background="@android:color/transparent"
                 app:controller_layout_id="@layout/simple_exo_playback_control_view"
                 app:player_layout_id="@layout/simple_exo_view"
                 app:resize_mode="fit"
                 app:surface_type="surface_view" />
 ````
 >> #### 2.属性说明
   * 必选
   >
    1.   player_layout_id  播放器布局， //必选
         player_layout_id 目前支持指定布局simple_exo_playback_control_view 后续版本，开放自定义使用

   * 必选
   >
    2. controller_layout_id  控制器布局`  默认有三种布局
        1.simple_exo_playback_control_view.xml  //视频封面控制布局下面，比较常规使用
        2.simple_exo_playback_list_view.xml.xml //在列表播放使用控制布局
        3.simple_exo_playback_top_view.xml.xml  //视频封面控制布局上面

   * 可选 **注意： 列表播放只能选择texture_view 不能选择surface_view，详情页面播放推荐surface_view**
   >
    3.    surface_type 视频渲染类型 //texture_view 和surface_view //枚举类型。默认surface_view


   >
    4.   use_controller   是否用户控制控制器  布尔类型

   >
    5.   resize_mode  视频缩放渲染显示方式一共4种 //可选 美剧类型
            1.fit          //正常模式
            2.fixed_width  //保持的是视频宽度，拉伸视频高度
            3.fixed_height //保持的是视频高度，拉伸视频宽度
            4.fill          //全屏模式，拉伸视频宽高
   >
    6.   default_artwork  占位图  //可选

   >
    7.   show_timeout  控制布局隐藏时间  默认值为3秒  //可选

   >
    8.   paddingEnd，paddingStart 设置边距  默认值为0  //可选

   >
    9.   fastforward_increment  设置快进增量,以毫秒为单位。 //可选

   >
    10.  rewind_increment   设置快退增量,以毫秒为单位。  //可选

   >
    11.  user_watermark    水印图片 默认在右上角  //可选

   >
    12.  player_list      是否指定列表播放   //可选 默认 false  true 列表播放

   >
    13.  player_replay_layout_id  自定义重播布局文件

   >
    14.  player_error_layout_id   自定义错误布局文件

   >
    15.  player_hint_layout_id   自定义非wifi提示布局文件

   >
    16.  player_load_layout_id   自定义视频加载布局文件


 >> #### 3.修改网络对话框提示文字内容
      app.strings.xml
      <string name="exo_play_reminder">您当前网络不是wifi，是否继续观看视频</string>
      <string name="exo_play_wifi_hint_no">提示</string>

 >> #### 4.在功能清单声明 AndroidManifest.xml
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

 > #### 2 播放代码 
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
            exoPlayerManager.setShowVideoSwitch(true); //开启切换按钮，默认关闭
           String [] test={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
           String[] name={"超清","高清","标清"};
           exoPlayerManager.setPlaySwitchUri(test,name);
          //添加水印图片
          // exoPlayerManager.setExoPlayWatermarkImg();
          //是否屏蔽进度控件拖拽快进视频（例如广告视频，（不允许用户））
           exoPlayerManager.setSeekBarSeek(false);
           //设置视循环播放
           exoPlayerManager.setLooping(10);
           //d隐藏控制布局
           exoPlayerManager.hideControllerView();
            //隐藏进度条
           exoPlayerManager.hideSeekBar();
            //显示进度条
           exoPlayerManager.showSeekBar();
            //是否播放
           exoPlayerManager.isPlaying(); 
           //设置点击播放按钮需要处理业务
           exoPlayerManager.setOnPlayClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              Toast.makeText(MainCustomActivity.this,"定义点击播放事件",Toast.LENGTH_LONG).show();
                               //处理业务操作 完成后，
                               //方法实现setOnPlayClickListener（)， 需要手动调用
                               exoPlayerManager.startPlayer();//开始播放
                          }
             }); 

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

   6.设置封面图

           videoPlayerView.setPreviewImage(bimtap);或者 videoPlayerView.getPreviewImage())

   7.设置视频路径

         exoPlayerManager.setPlayUri("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8");
         exoPlayerManager.setPlayUri(Uri.parse("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8"));
         exoPlayerManager.setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.h264"); //本地视频

   8.设置多线路播放

          //开启多线路设置，默认关闭
          exoPlayerManager.setShowVideoSwitch(true);
          //支持List列表
          String [] test={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4",
          "http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4",
           http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
           String[] name={"超清","高清","标清"};
           exoPlayerManager.setPlaySwitchUri(test,name);
    >>

   9.设置监听回调VideoInfoListener

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
                       public void onRepeatModeChanged(int repeatMode) {
                           //模式变化
                       }
                   });
   10.覆写Activity和Fragment周期方法

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
                public void onBackPressed() {
                //使用播放返回键监听
                 if(exoPlayerManager.onBackPressed()){
                     finish();
                 }
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
                          if (VideoPlayerManager.getInstance().onBackPressed()){
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

### 五.[自定义数据源用法-戳我](RELEASESOURCE.md)
### 六.[自定义布局用法-戳我](READMELAYUOT.md)



## [License](https://github.com/yangchaojiang/yjPlay/blob/master/LICENSE)

 The GNU General Public License is a free, copyleft license for
software and other kinds of works.

  The licenses for most software and other practical works are designed
to take away your freedom to share and change the works.  By contrast,
the GNU General Public License is intended to guarantee your freedom to
share and change all versions of a program--to make sure it remains free
software for all its users.  We, the Free Software Foundation, use the
GNU General Public License for most of our software; it applies also to
any other work released this way by its authors.  You can apply it to
your programs, too.

<<<<<<< HEAD
=======

>>>>>>> be6eb4cae28e6c4e6034462f22c1024e8a9e2c5c
 

