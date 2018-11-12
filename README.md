# yjPlay

[![Download](https://api.bintray.com/packages/ycjiang/ycjiang/VideoPlayModule/images/download.svg) ](https://bintray.com/ycjiang/ycjiang/VideoPlayModule/_latestVersion)


  ### gif 显示有点卡，帧数低，实际很流畅
  #### [下载预览apk](https://raw.githubusercontent.com/yangchaojiang/yjPlay/master/app-release.apk)

 ![](gif/tet.gif)  ![](gif/tet2.gif) 
 
 ![](gif/tet4.gif)  ![](gif/tet6.gif)

 ### 基于exoPlayer 自定义播放器 JPlayer支持功能：
   *  ExoUserPlayer 基本播放（默认关闭手势  亮度，音量，快进，等手势，支持自定义布局）
   *  自动适配适配TV。
   *  支持横屏和竖屏两种模式全屏。
   *  适配音频控制布局。
   *  支持广告视频预览(轻松实现，完美切换，<font color="red">可自定义</font>)。
   *  支持多种分辨率视频切换。
   *  [缓存下载加密视频功能（边播变缓存轻松实现](README_EN_VIDEO.md)<font color="red">不是使用AndroidVideoCache</font>。
   *  支持自定义多种 kttp,Rtmp,Https,Cronet等协议。
   *  支持列表集合 播放视频（<font color="red">列表到详情播放完美过度</font>）
   *  支持多种文件类型，MP4，M4A，WebM，Matroska,Ogg,WAV，MP3，MPEG-TS，MPEG-PS，FLV，ADTS (AAC)，Flac，M3U8,mkv 等。
   *  支持网络类型 提示是否播放(可自定义屏蔽)。
   *  支持视频加载布局, 错误布局,重播布局，提示布局自定义，更灵活实现自己布局样式。
   *  支持视频加速慢速播放。
   *  支持多种视频封面图（两种模式封面图）。
   *  支持支持自定义[MediaSource]()。
   *  支持精简版和完整版，选择使用更丰富。
   *  [增加离线下载辅助类ExoWholeDownLoadManger,ExoWholeDownloadTracker,ExoDownLoadManger,DownloadService()-->戳我](README_EN_VIDEO.md))
   *  支持播放锁屏功能和控制布局显示显示动画效果.
   *  支持返回按钮和全屏按钮图标自定义。
   *  支持自定义视频封面布局.(视频封面图布局样式完美多样化)。
   *  支持视频实时进度（头条底部进度）。
   *  支持流式API方式调用。

 <!--more-->

 ### [更新日志2.3.0→》戳我查看](RELEASENOTES.md)


 ### 一.引用类库
  ````
   repositories {
          jcenter()
          mavenCentral();
      }

  dependencies {
     //完整版
      compile 'com.ycjiang:VideoPlayModule:2.3.0'
     //精简版（没有smoothstreaming,dash,hls,只有常规点播功能）
      compile 'com.ycjiang:VideoPlayModule-Lite:2.3.0'

  }
  ````
  >>> 提示：无法正常引用请在 repositories{ }添加已下代码
  >>> mavenCentral(url: "https://dl.bintray.com/ycjiang/ycjiang")

 ### 二.控件属性
   >>>> 基本使用如下
   ````
         <chuangyuan.ycj.videolibrary.widget.VideoPlayerView
                 android:id="@+id/exo_play_context_id"
                 android:layout_width="match_parent"
                 android:layout_height="match_parent"
                 android:background="@android:color/transparent"
                 />
 ````
 >>  #### 1.控件属性
 ````
   <chuangyuan.ycj.videolibrary.widget.VideoPlayerView
         android:id="@+id/exo_play_context_id"
         android:layout_width="match_parent"
         android:layout_height="match_parent"
         app:controller_layout_id="@layout/simple_exo_playback_control_view"
         app:player_layout_id="@layout/simple_exo_view"
         app:player_replay_layout_id="@layout/custom_play_replay"
         app:player_error_layout_id="@layout/custom_play_error"
         app:player_hint_layout_id="@layout/custom_play_btn_hint"
         app:player_load_layout_id="@layout/custom_exo_play_load"
         app:player_gesture_audio_layout_id="@layout/custom_gesture_audio"
         app:player_gesture_bright_layout_id="@layout/custom_gesture_brightness"
         app:player_gesture_progress_layout_id="@layout/custom_gesture_pro"
         app:player_preview_layout_id="@layout/exo_default_preview_layout"
         app:resize_mode="fit"
         app:show_timeout="3000"
         app:surface_type="texture_view"
         app:fastforward_increment="0"
         app:rewind_increment="0"
         app:user_watermark="@mipmap/watermark_big"
         app:player_list="true"
         app:use_controller="true"
         app:player_fullscreen_image_selector="@drawable/custom_full_selector"
         app:player_back_image="@drawable/ic_back_custom"
          />
   ````
   
 >> #### 2.属性说明 自定义视图的可用属性：
  
  | name                              | type      | info                                                                        |
  |-----------------------------------|-----------|---------------------------------------------------------------------------- |
  | surface_type                      | enum      | 视频渲染类型 texture_view 和surface_view 枚举类型 默认surface_view          |  
  | resize_mode                       | enum      | 视频缩放渲染显示方式一共5种                                                 |
  |                                   | reference | 1.fit          正常模式                                                     | 
  |                                   | reference | 2.fixed_16_9  保持的是视频的比例16:9                                        | 
  |                                   | reference | 3.fixed_4_3   保持的是视频比例4:3                                           |         
  |                                   | reference | 4.fill           全屏模式，拉伸视频宽高                                     |        
  |                                   | reference | 4.match          矩阵模式                                                 |      0
  | player_layout_id                  | reference | (播放器布局):目前默认布局  simple_exo_view.xml                              |
  | controller_layout_id              | reference | 控制器布局  默认有四种布局                                                  |
  |                                   | reference | 1.simple_exo_playback_control_view.xml  视频封面控制布局下面,（默认）       | 
  |                                   | reference | 2.simple_exo_playback_list_view.xml.xml 在列表播放使用控制布局              | 
  |                                   | reference | 3.simple_exo_playback_top_view.xml.xml  视频封面控制布局上面                |
  |                                   | reference | 4.exo_playback_control_view.xml         exo 提供默认风格                    | 
  | player_replay_layout_id           | reference | 设置 自定义重播布局文件                                                     |
  | player_error_layout_id            | reference | 设置 自定义错误布局文件                                                     |
  | player_hint_layout_id             | reference | 设置 自定义非wifi提示布局文件                                               |
  | player_load_layout_id             | reference | 设置 自定义视频加载布局文件                                                 |
  | player_gesture_audio_layout_id    | reference | 设置 自定义手势音频调节布局                                                 |
  | player_gesture_bright_layout_id   | reference | 设置 自定义手势亮度调节布局                                                 |
  | player_gesture_progress_layout_id | reference | 设置 自定义手势进度调节布局                                                 |
  | player_preview_layout_id          | reference | 设置 自定义封面图布局(默认 exo_default_preview_layout.xml)                  |
  | player_list                       | boolean   | 设置 是否指定列表播放    默认 false  true 列表播放                          |
  | player_fullscreen_image_selector  | reference | 设置 自定义全屏按钮selector                                                 |
  | player_back_image                 | reference | 设置 自定义返回按钮图标                                                     |
  | default_artwork                   | reference | 设置 封面占位图                                                             |
  | show_timeout                      | integer   | 设置 控制布局隐藏时间  默认值为3秒                                          |
  | fastforward_increment             | integer   | 设置 按钮设置快进增量,以毫秒为单位（exo控制布局使用）                       |
  | rewind_increment                  | integer   | 设置 按钮设置快退增量,以毫秒为单位（exo控制布局使用）                       |
  | user_watermark                    | reference | 设置 水印图片 默认在右上角                                                  |

   * **注意：**
     >>    1.列表播放只能选择texture_view 不能选择surface_view，详情页面播放推荐surface_view
     >>
     >>    2.自定义全屏按钮selector
           
           <selector xmlns:android="http://schemas.android.com/apk/res/android">
                <item android:drawable="@drawable/ic_custom_full" android:state_checked="true" />
                <item android:drawable="@drawable/ic_custom_full_in" android:state_checked="false" />
           </selector>
    
     >>       
     >>        
     >>   3.自定义封面图布局中,也包含封
  >> #### 3.快速自定义视频进度控件颜色
   >> [如何自定义视频进度控件--->戳我](https://github.com/yangchaojiang/yjPlay/blob/dev/READMELAYUOT.md#%E4%BA%94%E8%A7%86%E9%A2%91%E6%92%AD%E6%94%BE%E8%BF%9B%E5%BA%A6%E6%8E%A7%E4%BB%B6%E8%87%AA%E5%AE%9A%E4%B9%89)
  >> 在app的module的values 文件下-> colors.xml 文件里
   ```
     <!--视频加载缓存进度的面图控件。所以自定义封面图布局后，就不要再的控制布局使用封面图控件
                   颜色-->
     <color name="timeBar_buffered_color">@color/light_green</color>
     <!--已经播放过视频的颜色-->
     <color name="timeBar_played_color">#c63020</color>
     <!--没有加载过进度的颜色-->
     <color name="timeBar_unplayed_color">@color/live_yellow</color>
     <!--视频进度圆点的颜色-->
     <color name="timeBar_scrubber_color">@color/colorAccent</color>
     
   ```
 >> #### 4.修改网络对话框提示文字内容
      app.strings.xml
      <string name="exo_play_reminder">您当前网络不是wifi，是否继续观看视频</string>
      <string name="exo_play_wifi_hint_no">提示</string>
 >> #### 5.开启竖屏全屏模式。
     ```
     VideoPlayerView.setVerticalFullScreen(true)
     ```
     或者
     ````
       exoPlayerManager = new VideoPlayerManager.Builder(VideoPlayerManager.TYPE_PLAY_GESTURE, videoPlayerView)
                     .setDataSource(wholeMediaSource)
                     .setPosition(currPosition)
                     .setTitle("自定义视频标题")
                     .setVerticalFullScreen(true)
     ````

 >> #### 6.在功能清单声明 AndroidManifest.xml
    在activity节点 加上“android:configChanges="orientation|keyboardHidden|screenSize"”
     如下实例：
            <activity android:name="chuangyuan.ycj.yjplay.MainListActivity"
             android:configChanges="orientation|keyboardHidden|screenSize"
             android:screenOrientation="portrait">


      

 ### 3.JAVA 代码

 > #### 1 播放控制类
    1.ExoUserPlayer

 > #### 2 VideoPlayerView 控件 可用方法
  | name                                           | type      | info                                                                        |
  |------------------------------------------------|-----------|---------------------------------------------------------------------------- |
  | setTitle("标题")                               | void      | 设置视频标题                                                               |  
  | setExoPlayWatermarkImg(R.mipmap.watermark_big) | void      | 设置添加水印图片                                                               |  
  | setPreviewImage(Bitmap）                       | void      | 设置封面图                                                             |  
  | setPreviewImage(R.res.image)                   | void      | 设置封面图                                                                |  
  | setPreviewImage(R.res.image)                   | void      | 设置封面图                                                                |  
  | getPreviewImage()                              | ImageView | 设置封面图控件                                                               |  
  | setPreviewImage(R.res.image)                   | void      | 设置封面图                                                                |  
  | setVerticalFullScreen(true)                    | void      | 开启竖屏全屏                                                           |

  >>更多方法参考demo用法。
  
 > #### 3 播放代码 
                  //实例化
         ExoUserPlayer exoPlayerManager = new VideoPlayerManager.Builder(VideoPlayerManager.TYPE_PLAY_MANUAL, videoPlayerView)
                          .setDataSource(new DataSource(this))
                          //加载rtmp 协议视频
                          .setPlayUri("rtmp://live.hkstv.hk.lxdns.com/live/hks")
                          //加载m3u8
                          .setPlayUri("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8")
                          //加载ts.文件
                          .setPlayUri("http://185.73.239.15:25461/live/1/1/924.ts")
                          //播放本地视频
                          .setPlayUri("/storage/emulated/0/DCIM/Camera/VID_20170717_011150.mp4")
                          //播放列表视频
                          .setPlayUri(listss);
                          //设置开始播放进度
                          .setPosition(1000)
                          //示例本地路径 或者 /storage/emulated/0/DCIM/Camera/VID_20180215_131926.mp4
                          .setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath()+"/VID_20170925_154925.mp4")
                          //开启线路设置
                          .setShowVideoSwitch(true)
                          .setPlaySwitchUri(0,test,name)
                          .setPlaySwitchUri(0, 0, getString(R.string.uri_test_11), Arrays.asList(test), Arrays.asList(name))
                          //设置播放视频倍数  快进播放和慢放播放
                          .setPlaybackParameters(0.5f, 0.5f)
                          //是否屏蔽进度控件拖拽快进视频（例如广告视频，（不允许用户））
                          .setSeekBarSeek(false)
                          //设置视循环播放
                          .setLooping(10)
                          //视频进度回调
                           .addOnWindowListener(new VideoWindowListener() {
                                             @Override
                                             public void onCurrentIndex(int currentIndex, int windowCount) {
                                                 Toast.makeText(getApplication(), currentIndex + "windowCount:" + windowCount,                                                    Toast.LENGTH_SHORT).show();
                                             }
                                         })
                          .addOnWindowListener(new VideoWindowListener() {
                              @Override
                              public void onCurrentIndex(int currentIndex, int windowCount) {
                                  Toast.makeText(getApplication(), currentIndex + "windowCount:" + windowCount, Toast.LENGTH_SHORT)                                             .show();
                              }
                          })
                          .addVideoInfoListener(new VideoInfoListener() {
                              ·····
                          })
                          //创建
                           .create()
                             //播放视频
                          .startPlayer();

   1.实例化播放控制类
          //播放控制器创建
           ExoUserPlayer exoPlayerManager = new VideoPlayerManager.Builder(VideoPlayerManager.TYPE_PLAY_USER, videoPlayerView).create();

   2.自定义你的数据源，后面详细介绍如何自定义数据源类

         ExoUserPlayer exoPlayerManager = new VideoPlayerManager.Builder(VideoPlayerManager.TYPE_PLAY_MANUAL, videoPlayerView)
                                                        .setDataSource(new DataSource(this))
                                                        .create();
         ExoUserPlayer exoPlayerManager =  new VideoPlayerManager.Builder(VideoPlayerManager.TYPE_PLAY_MANUAL, videoPlayerView)
                                                          .setDataSource(mediaSourceBuilder)
                                                          .create();
         定义多媒体
         MediaSourceBuilder   mediaSourceBuilder=new MediaSourceBuilder(this,new DataSource(getApplication()));
        集成smoothstreaming,dash,hls
         WholeMediaSource   mediaSourceBuilder=new MediaSourceBuilder(this,new DataSource(getApplication()));  
         ManualPlayer   exoPlayerManager =new VideoPlayerManager.Builder(VideoPlayerManager.TYPE_PLAY_MANUAL, videoPlayerView)
                                                            .setDataSource(mediaSourceBuilder)
                                                            .create();

 > #### 4 VideoPlayerManager.Builder 播放管理类可用方法
  | name                                 | type | info                                                                        |
  |--------------------------------------|------|---------------------------------------------------------------------------- |
  | setPosition(1000)                    | void |  设置开始播放进度                                                               |  
  | setPosition(windowIndex,1000)        | void |  设置设置当前窗口位置,开始播放进度                                                               |  
  | setPlayUri("http:...m3u8");          | void |  设置视频路径                                                             |  
  | setPlayUri(Uri.parse("uri"))         | void |  设置开始播放进度                                                               |  
  | setShowVideoSwitch(true)             | void |  设置开启多线路设置，默认关闭                                                             |  
  | setLoadModel(LoadModelType.PERCENR)  | void |  设置视频加载提示显示模式（默认LoadModelType.SPEED (网速模式)）                                                            |  
  | setPlaybackParameters(2f,2f)         | void |  设置播放视频倍数  快放和慢放播放 小于1 慢放 大于1 快放  
  | startPlayer()                        | void |  开始播放视频                                                               |  
   
   >>更多方法参考demo用法。
   
 >**注意**
  >> 1.exoPlayerManager.setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.h264"); 本地视频
  >> 2.设置多线路播放
 ````     
            开启多线路设置，默认关闭
            exoPlayerManager.setShowVideoSwitch(true);
            支持List列表
            String [] test={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4",
            "http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4",
            http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
            String[] name={"超清","高清","标清"};
            exoPlayerManager.setPlaySwitchUri(0,test,name);
            多分辨路和广告视频设置
            exoPlayerManager.setPlaySwitchUri(0, 0, getString(R.string.uri_test_11), Arrays.asList(test), Arrays.asList(name));
        
 ````
  >>  3.广告视频预览(轻松实现)
        
          /**需要添加参数就行**/
            第一个参数代表是广告视频位置索引
            exoPlayerManager.setPlayUri(0, "http://mp4.vjshi.com/2013-07-25/2013072519392517096.mp4", "http://mp4.vjshi.com/2013-11-11/1384169050648_274.mp4");       
             如果自己在播放视频时特出处理。实现该接口回调
            视频切换回调处理，进行布局处理，控制布局显示
            exoPlayerManager.setOnWindowListener(new VideoWindowListener() {
            @Override
            public void onCurrentIndex(int currentIndex, int windowCount) {
                         if (currentIndex == 0) {
                             //屏蔽控制布局
                             exoPlayerManager.hideControllerView();
                             //如果屏蔽控制布局 但是需要显示全屏按钮。手动显示，播放正常时自动还原。无需里出
                             videoPlayerView.getExoFullscreen().setVisibility(View.VISIBLE);
                         } else {
                             //恢复控制布局
                             exoPlayerManager.showControllerView();
                         }
                     }
             });
           //跳过广告视频操作
           exoPlayerManager.next();
  >> 4.设置点击播放按钮需要处理业务
    
      exoPlayerManager.setOnPlayClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  Toast.makeText(MainCustomActivity.this,"定义点击播放事件",Toast.LENGTH_LONG).show();
                                   //处理业务操作 完成后 
                                  exoPlayerManager.startPlayer();//开始播放
                    }
         });
    
  >> 5.设置监听回调VideoInfoListener
   
         exoPlayerManager.addVideoInfoListener(new VideoInfoListener() {
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
   
   >> 6.覆写Activity和Fragment周期方法

                Override
                public void onResume() {
                    super.onResume();
                    exoPlayerManager.onResume();
                }

                @Override
                public void onPause() {
                    super.onPause();
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


### 三.[列表使用说明-->戳我](RELEASEVIDEO_LIST.md)

### 四.数据源工厂类
 ####  1.默认数据源
          缓存 : CacheDataSourceFactory
          http : DefaultDataSourceFactory,DefaultHttpDataSourceFactory
          Priority : PriorityDataSourceFactory
 #### 2 ExoPlayer自定义数据源引用(根据自己需求选用)
      compile 'com.google.android.exoplayer:extension-okhttp:2.9.0'
      compile 'com.google.android.exoplayer:extension-rtmp:2.9.0'
      compile 'com.google.android.exoplayer:extension-gvr:2.9.0'
      compile 'com.google.android.exoplayer:extension-cast:2.9.0'
      compile 'com.google.android.exoplayer:extension-mediasession:2.9.0'
      compile 'com.google.android.exoplayer:extension-ima:2.9.0'
      compile 'com.google.android.exoplayer:extension-leanback:2.9.0'

### 五.[自定义数据源用法-戳我](RELEASESOURCE.md)
### 六.[自定义布局用法-戳我](READMELAYUOT.md)
### 七.[自定义MediaSource用法-戳我](RELEASEVIDEO.md) 
### 八.[缓存,加密,视频处理用法-戳我](README_EN_VIDEO.md) 

 ### 混淆声明
   -dontwarn chuangyuan.ycj.**
   
   -keep class chuangyuan.ycj.** { *;}
 
 
## [License](https://github.com/yangchaojiang/yjPlay/blob/master/LICENSE)
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


