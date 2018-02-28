# yjPlay

[![Download](https://api.bintray.com/packages/ycjiang/ycjiang/VideoPlayModule/images/download.svg?version=1.9.96) ](https://bintray.com/ycjiang/ycjiang/VideoPlayModule/1.9.96/link)
 

  ### [View English instructions→Poking me ](en/README.md)

  ### gif 显示有点卡，帧数低，实际很流畅
  #### [下载预览apk](https://raw.githubusercontent.com/yangchaojiang/yjPlay/master/app-debug.apk)

 ![](test.gif)

 ### 基于exoPlayer 自定义播放器 JPlayer支持功能：
   * 1 ExoUserPlayer  基本播放。
   * 2 GestureVideoPlayer   增加手势  亮度，音量，快进，等手势。
   * 3 ManualPlayer   可自定义触发播放。
   * 5 广告视频预览(轻松实现，完美切换)。
   * 6 视频清晰度切换。
   * 7 [缓存下载加密视频功能（边播变缓存轻松实现](README_EN_VIDEO.md)。
   * 8 支持自定义多种 kttp,Rtmp,Https,Cronet等协议。
   * 9 支持列表集合数据O播放视频（详情播放完美过度）
   * 10 支持多种文件类型，MP4，M4A，WebM，Matroska,Ogg,WAV，MP3，MPEG-TS，MPEG-PS，FLV，ADTS (AAC)，Flac，M3U8,mkv 等。
   * 11 支持网络类型 提示是否播放(可自定义冰屏蔽)。
   * 12 **1.5.5**增加,视频加载布局, 错误布局,重播布局，提示布局自定义，更灵活实现自己布局样式。
   * 13 支持视频加载中显示模式（网速模式和百分比模式）。
   * 14 支持视频加速慢速播放。
   * 15 支持视频封面图（两种模式封面图）。
   * 16 **1.7.0**支持自定义MediaSource。
   * 17 **1.7.0**增加 手势 亮度调节，视频进度，音量 布局自定义。
   * 18 支持精简版和完整版，选择使用更丰富。
   * 19 [支持自定义AES视频加密,简单加密→戳我](README_EN_VIDEO.md)
   * 20 增加自定义离线下载辅助类DefaultProgressDownloader(支持（AES/CBC）加密文件处理),HlsDownloader,DashDownloader,SsDownloader,SegmentDownloader。
   * 21 支持播放锁屏功能和控制动画效果，返回按钮和全屏按钮图标自定义。
 <!--more-->


 ### [更新日志1.9.96→》戳我查看](RELEASENOTES.md)
 
 ### 一.引用类库
  ````
   repositories {
          jcenter()
          mavenCentral();
      }

  dependencies {
     //完整版
      compile 'com.ycjiang:VideoPlayModule:1.9.96' 
     //精简版（没有smoothstreaming,dash,hls,只有常规点播功能）
     compile 'com.ycjiang:VideoPlayModule-Lite:1.9.96'

  }
  ````
  >>> 提示：无法正常引用请在 repositories{ }添加已下代码
  >>> mavenCentral(url: "https://dl.bintray.com/ycjiang/ycjiang")

 ### 二.控件属性

 >>  #### 1.控件属性
 ````
   <chuangyuan.ycj.videolibrary.widget.VideoPlayerView
         android:id="@+id/exo_play_context_id"
         android:layout_width="match_parent"
         android:layout_height="match_parent"
         android:background="@android:color/transparent"
         app:controller_layout_id="@layout/simple_exo_playback_control_view"
         app:player_layout_id="@layout/simple_exo_view"
         app:player_replay_layout_id="@layout/custom_play_replay"
         app:player_error_layout_id="@layout/custom_play_error"
         app:player_hint_layout_id="@layout/custom_play_btn_hint"
         app:player_load_layout_id="@layout/custom_exo_play_load"
         app:player_gesture_audio_layout_id="@layout/custom_gesture_audio"
         app:player_gesture_bright_layout_id="@layout/custom_gesture_brightness"
         app:player_gesture_progress_layout_id="@layout/custom_gesture_pro"
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
   >>>> 基本使用如下
   ````
         <chuangyuan.ycj.videolibrary.widget.VideoPlayerView
                 android:id="@+id/exo_play_context_id"
                 android:layout_width="match_parent"
                 android:layout_height="match_parent"
                 android:background="@android:color/transparent"
                 />
 ````
 >> #### 2.属性说明
   >
    1.   player_layout_id  播放器布局，  
         player_layout_id 目前支持指定布局simple_exo_view.xml 后续版本，开放自定义使用
   >
    2. controller_layout_id  控制器布局`  默认有四种布局
        1.simple_exo_playback_control_view.xml  //视频封面控制布局下面，比较常规使用
        2.simple_exo_playback_list_view.xml.xml //在列表播放使用控制布局
        3.simple_exo_playback_top_view.xml.xml  //视频封面控制布局上面
        4.exo_playback_control_view.xml         //exo 提供默认风格

   * **注意： 列表播放只能选择texture_view 不能选择surface_view，详情页面播放推荐surface_view**
   >
    3.    surface_type 视频渲染类型 //texture_view 和surface_view //枚举类型。默认surface_view

   >
    4.   use_controller   是否用户控制控制器  布尔类型

   >
    5.   resize_mode  视频缩放渲染显示方式一共4种  
            1.fit          //正常模式
            2.fixed_width  //保持的是视频宽度，拉伸视频高度
            3.fixed_height //保持的是视频高度，拉伸视频宽度
            4.fill          //全屏模式，拉伸视频宽高
   >
    6.   default_artwork  占位图   

   >
    7.   show_timeout  控制布局隐藏时间  默认值为3秒   

   >
    8.   fastforward_increment  设置快进增量,以毫秒为单位。  

   >
    9.  rewind_increment   设置快退增量,以毫秒为单位。  

   >
    10.  user_watermark    水印图片 默认在右上角   

   >
    11.  player_list      是否指定列表播放    默认 false  true 列表播放

   >
    12.  player_replay_layout_id  自定义重播布局文件

   >
    13.  player_error_layout_id   自定义错误布局文件

   >
    14.  player_hint_layout_id   自定义非wifi提示布局文件

   >
    15.  player_load_layout_id   自定义视频加载布局文件
    
   >
    16.  player_gesture_audio_layout_id   自定义手势音频调节布局
    
   >
    17.  player_gesture_bright_layout_id   自定义手势亮度调节布局
    
   >
    18.  player_gesture_progress_layout_id   自定义手势进度调节布局
    
   >
    19.  player_fullscreen_image_selector   自定义全屏按钮selector
    >>注意：
         <selector xmlns:android="http://schemas.android.com/apk/res/android">
             <item android:drawable="@drawable/ic_custom_full" android:state_checked="true" />
             <item android:drawable="@drawable/ic_custom_full_in" android:state_checked="false" />
         </selector>
   >
    20.  player_back_image   自定义返回按钮图标   
    
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
    1.ExoUserPlayer 基本播放父类，实现基本播放,设置setPlayUri();
    2.GestureVideoPlayer  具有手势操作播放（调节亮度和视频进度，和音量）
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
           exoPlayerManager.setPlaySwitchUri(0,test,name);
          //开始启动播放视频
           exoPlayerManager.startPlayer();

   1.实例化播放控制类

          ManualPlayer exoPlayerManager = new ManualPlayer(this,R.id.exo_play_context_id);
          ManualPlayer exoPlayerManager = new ManualPlayer(this,videoPlayerView);

   2.自定义你的数据源，后面详细介绍如何自定义数据源类

         ManualPlayer exoPlayerManager = new ManualPlayer(this,R.id.exo_play_context_id,new DataSource(this));
         ManualPlayer exoPlayerManager = new ManualPlayer(this,videoPlayerView,new DataSource(this));
         //定义多媒体
         MediaSourceBuilder   mediaSourceBuilder=new MediaSourceBuilder(this,new DataSource(getApplication()));
         //集成smoothstreaming,dash,hls
         WholeMediaSource   mediaSourceBuilder=new MediaSourceBuilder(this,new DataSource(getApplication()));
         
         ManualPlayer   exoPlayerManager = new ManualPlayer(this,mediaSourceBuilder, videoPlayerView);

   3.设置视频标题

          exoPlayerManager.setTitles("视频标题");

   4.添加水印图片

         exoPlayerManager.setExoPlayWatermarkImg(R.mipmap.watermark_big);

   5.设置开始播放进度

         exoPlayerManager.setPosition(1000);
          exoPlayerManager.setPosition(windowIndex,1000)

   6.设置封面图

           videoPlayerView.setPreviewImage(bimtap);
           videoPlayerView.getPreviewImage())
           videoPlayerView.setPreviewImage(R.res.image)

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
           exoPlayerManager.setPlaySwitchUri(0,test,name);
           //多分辨路和广告视频设置
           exoPlayerManager.setPlaySwitchUri(0, 0, getString(R.string.uri_test_11), Arrays.asList(test), Arrays.asList(name));
    
    
   9.设置视频加载提示显示模式（默认LoadModelType.SPEED (网速模式)）
     
       /**设置加载百分比显示模式**/
       exoPlayerManager.setLoadModel(LoadModelType.PERCENR);
  
   10.设置视频倍数播放）
        
       //设置播放视频倍数  快放和慢放播放 小于1 慢放 大于1 快放
       exoPlayerManager.setPlaybackParameters(2f,2f);
        
   11.广告视频预览(轻松实现)
        
          /**需要添加参数就行**/
          //第一个参数代表是广告视频位置索引
           exoPlayerManager.setPlayUri(0, "http://mp4.vjshi.com/2013-07-25/2013072519392517096.mp4", "http://mp4.vjshi.com/2013-11-11/1384169050648_274.mp4");       
            //如果自己在播放视频时特出处理。实现该接口回调
           //视频切换回调处理，进行布局处理，控制布局显示
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
   12.设置点击播放按钮需要处理业务
    
      exoPlayerManager.setOnPlayClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  Toast.makeText(MainCustomActivity.this,"定义点击播放事件",Toast.LENGTH_LONG).show();
                                   //处理业务操作 完成后 
                                  exoPlayerManager.startPlayer();//开始播放
                    }
         });
    
   13.设置监听回调VideoInfoListener
   
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
   
   14.覆写Activity和Fragment周期方法

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


 ### 三.列表
 
   1.列表播放，只能使用ManualPlayer,在你的VideoHolder
   *  1.在列表控件使用属性 ”app:controller_layout_id="@layout/simple_exo_playback＿list_view"“  //提供默列表控制布局
  
   *  2.player_list="true" 设置为true 开启列表模式
  
   *  3.设置列表item 没有播放完成当前视频播放进度,不然不会保存播放进度---> userPlayer.setTag(getAdapterPosition());
   
   *  3.设置列表item 没有播放完成当前视频播放进度,不然不会保存播放进度---> userPlayer.setTag(getAdapterPosition());
  
   *  3.设置列表item 没有播放完成当前视频播放进度,不然不会保存播放进度---> userPlayer.setTag(getAdapterPosition());
  
   *  4.demo:
       
       
              public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
              .......
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
                 /***绑定数据源***/
                  public void bindData(String videoBean) {
                      userPlayer.setTitles("" + getAdapterPosition());
                      userPlayer.setPlayUri(videoBean);
                      //设置列表item播放当前视频播放进度.不然不会保存视频播放进度
                      userPlayer.setTag(helper.getAdapterPosition());
                      Glide.with(mContext) .load("....") .into(playerView.getPreviewImage());
                  }
              }
              
              
   >>注意 更多adapter 实例请参考demo程序
              
  2.列表播放周期方法 列表在Activity或者Fragment  实现相应周期方法
  >> 在viewPager使用，不要在实现 Fragment onDestroy（）方法周期， onPause()也会释放资源。
  >> onDestroy 用户页面销毁处理,不是释放资源.
  >> onDestroy 用户页面销毁处理,不是释放资源.
  >> onDestroy 用户页面销毁处理,不是释放资源.

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
                       public void onConfigurationChanged(Configuration newConfig) {
                          //横竖屏切换
                           VideoPlayerManager.getInstance().onConfigurationChanged(newConfig);
                           super.onConfigurationChanged(newConfig);
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
          缓存 : CacheDataSourceFactory
          http : DefaultDataSourceFactory,DefaultHttpDataSourceFactory
          Priority : PriorityDataSourceFactory
 #### 2 自定义数据源引用
      compile 'com.google.android.exoplayer:extension-okhttp:r2.5.1'
      compile 'com.google.android.exoplayer:extension-rtmp:r2.5.1'

### 五.[自定义数据源用法-戳我](RELEASESOURCE.md)
### 六.[自定义布局用法-戳我](READMELAYUOT.md)
### 七.[自定义MediaSource用法-戳我](RELEASEVIDEO.md) 
### 八.[缓存,加密,视频处理用法-戳我](README_EN_VIDEO.md) 

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

 ### 混淆声明
   -dontwarn chuangyuan.ycj.**
   
   -keep class chuangyuan.ycj.** { *;}
 

