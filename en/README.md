# yjPlay
 
[ ![Download](https://api.bintray.com/packages/ycjiang/ycjiang/VideoPlayModule/images/download.svg) ](https://bintray.com/ycjiang/ycjiang/VideoPlayModule/_latestVersion)
 
  ### gif The display has the point card, the frame number is low, actually very fluent
  #### [Download preview apk](https://raw.githubusercontent.com/yangchaojiang/yjPlay/master/app-debug.apk)

 ![](../test.gif)

 ### Based on exo Player custom Player J Player support function：
   * 1 ExoUserPlayer  Basic play
   * 2 GestureVideoPlayer   Increase gesture brightness, volume, fast forward, etc
   * 3 ManualPlayer  By default manual playback, increase the default diagram
   * 5 Add AD video preview switch
   * 6 Add video clarity switch
   * 7 Increase the cache video function
   * 8 Support for custom data source loading Okttp, Rtmp, cache, Cronet and other protocols.
   * 9 Support list to play video
   * 10 Supports multiple file types, MP 4, M 4 A, Web M, Matroska, Ogg, WAV, MP 3, mpeg-ts, mpeg-ps, FLV, ADTS (AAC), Flac, M 3 U 8,mkv, etc
   * 11 Support network type prompts for playback
   * 12 **1.5.5**Add video loading layout, error layout, replay layout, prompt layout customization, more flexible implementation of layout style
   * 13 Support for video loading display mode (network speed mode and percentage mode)
   * 14 Supports video double speed playback
   * 15 Support for video cover image (two model covers)
   * 16 Support for custom Media Source.
   * 17 **1.7.0**Increase signal brightness adjustment, video progress, volume layout customization。
   * 18 Support the lite version and full version, choose to use more abundant。
   * 19 [Video support AES encryption, Base64 encryption (unstable), three kinds of simple encryption to poke me→戳我](../README_EN_VIDEO.md)
    * 20 Add the Default Progress Downloader for offline downloads(Support (AES/CBC) encrypted file processing),HlsDownloader,DashDownloader,SsDownloader,SegmentDownloader。
    * 21 Support to play lock screen function and control layout animation effect。
 <!--more-->

 ### [Update log→》Poking me see](../RELEASENOTES.md)
 
 ### 一.Reference library
  ````
   repositories {
          jcenter()
          mavenCentral();
      }

  dependencies {
      //full edition
      compile 'com.ycjiang:VideoPlayModule:1.9.8'
      // lite version （no smoothstreaming,dash,hls,Only regular on-demand）
      compile 'com.ycjiang:VideoPlayModule-Lite:1.9.8'


  }
  ````
  >>> Tip: you can't use a normal reference to add the code to the repositories {}
  >>> mavenCentral(url: "https://dl.bintray.com/ycjiang/ycjiang")

 ### 二.Control properties


 >>  #### 1.Control properties
  >>>>Control custom properties
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
         app:player_gesture_audio_layout_id="@layout/custom_gesture_audio"
         app:player_gesture_bright_layout_id="@layout/custom_gesture_brightness"
         app:player_gesture_progress_layout_id="@layout/custom_gesture_pro"
          />
   ````
   >>>> Basic use is as follows
   ````
         <chuangyuan.ycj.videolibrary.widget.VideoPlayerView
                 android:id="@+id/exo_play_context_id"
                 android:layout_width="match_parent"
                 android:layout_height="match_parent"
                 android:background="@android:color/transparent"
                 />
 ````
 >> #### 2.attribute specification
   >
    1.   player_layout_id  播放器布局， //必选
         player_layout_id 目前支持指定布局simple_exo_playback_control_view 后续版本，开放自定义使用

   >
    2. controller_layout_id  控制器布局`  默认有三种布局
        1.simple_exo_playback_control_view.xml  //视频封面控制布局下面，比较常规使用
        2.simple_exo_playback_list_view.xml.xml //在列表播放使用控制布局
        3.simple_exo_playback_top_view.xml.xml  //视频封面控制布局上面

   *  **Note: only texture view cannot select the surface view, and the details page plays the recommended surface view**
   >
    3.    surface_type Video render type //texture_view and surface_view //enumeration type。default surface_view


   >
    4.   use_controller   Whether the user controls the controller  Boolean type

   >
    5.   resize_mode  Video zooming display mode has 4 kinds //optional  
            1.fit          //normal mode
            2.fixed_width   
            3.fixed_height  
            4.fill          
   >
    6.   default_artwork  Placeholder figure //optional

   >
    7.   show_timeout  Control layout hides time by default of 3 seconds  //optional

   >
    8.   paddingEnd，paddingStart Set the margin default value of 0 //optional

   >
    9.   fastforward_increment  Set fast forward increments, in milliseconds。 //optional

   >
    10.  rewind_increment  Set the fast back increment, in milliseconds。  //optional

   >
    11.  user_watermark    Watermark image defaults to the upper right corner  //optional

   >
    12.  player_list      Whether to specify a list to play   // default false  true The list of play

   >
    13.  player_replay_layout_id  Customize the replay layout file

   >
    14.  player_error_layout_id   Custom error layout file

   >
    15.  player_hint_layout_id   Customize non-wifi prompt layout files

   >
    16.  player_load_layout_id   Customize video to load the layout file
   
   >
    17.  player_gesture_audio_layout_id   Custom gesture audio adjustment layout
 
   >
    18.  player_gesture_bright_layout_id   Custom gesture brightness adjustment layout
  
   >
    19.  player_gesture_progress_layout_id  Custom gesture progress adjustment layout

    20.  player_fullscreen_image_selector    Customize the full screen button selector
    
    >>注意：
         <selector xmlns:android="http://schemas.android.com/apk/res/android">
             <item android:drawable="@drawable/ic_custom_full" android:state_checked="true" />
             <item android:drawable="@drawable/ic_custom_full_in" android:state_checked="false" />
         </selector>
   >
    21.  player_back_image   Customize the return button icon

 >> #### 3.Modify the network dialog box to prompt text content
      app.strings.xml
      <string name="exo_play_reminder">Your current network is not wifi, do you continue to watch video</string>
      <string name="exo_play_wifi_hint_no">hint</string>

 >> #### 4.In the functional manifest declaration AndroidManifest.xml
     activity  tag add“android:configChanges="orientation|keyboardHidden|screenSize"”
     The following example：
            <activity android:name="chuangyuan.ycj.yjplay.MainListActivity"
             android:configChanges="orientation|keyboardHidden|screenSize"
             android:screenOrientation="portrait">


 ### 3.JAVA  

 > #### 1 Play control class
    1.ExoUserPlayer Basic play the parent, implementation basic play, setPlayUri(); 
    2.GestureVideoPlayer  The display (adjust the brightness and video progress, and volume)
    2.ManualPlayer  Click on the start button to play, with gestures and playlists

 > #### 2 Play the code
         //instantiate the play control class
          ManualPlayer exoPlayerManager = new ManualPlayer(this,R.id.exo_play_context_id);
         //Customize your data source, and then detail how to customize the data source class
          // ManualPlayer exoPlayerManager = new ManualPlayer(this,R.id.exo_play_context_id,new DataSource(this));
          //Load m 3 u 8
          exoPlayerManager.setPlayUri("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8");
          //Load ts. file
          exoPlayerManager.setPlayUri("http://185.73.239.15:25461/live/1/1/924.ts");
          //Play local video
          // exoPlayerManager.setPlayUri("/storage/emulated/0/DCIM/Camera/VID_20170717_011150.mp4");
          //Open multiple lines below
            exoPlayerManager.setShowVideoSwitch(true); //开启切换按钮，默认关闭
           String [] test={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
           String[] name={"超清","高清","标清"};
           exoPlayerManager.setPlaySwitchUri(test,name);
           //开始启动播放视频
           exoPlayerManager.startPlayer(); 

   1.Instantiate the play control class

          ManualPlayer exoPlayerManager = new ManualPlayer(this,R.id.exo_play_context_id);
          ManualPlayer exoPlayerManager = new ManualPlayer(this,videoPlayerView);

   2.Customize your data source, and then detail how to customize the data source class

         ManualPlayer exoPlayerManager = new ManualPlayer(this,R.id.exo_play_context_id,new DataSource(this));
         ManualPlayer exoPlayerManager = new ManualPlayer(this,videoPlayerView,new DataSource(this));

   3.Set the video title

          exoPlayerManager.setTitle("视频标题");

   4.Add watermark images

         exoPlayerManager.setExoPlayWatermarkImg(R.mipmap.watermark_big);

   5.Set the play progress

         exoPlayerManager.setPosition(1000)

   6.Cover drawing

           videoPlayerView.setPreviewImage(bimtap);或者 videoPlayerView.getPreviewImage())

   7.Set the video path

         exoPlayerManager.setPlayUri("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8");
         exoPlayerManager.setPlayUri(Uri.parse("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8"));
         exoPlayerManager.setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.h264"); //本地视频

   8.Set multiple lines to play

          //Open multi-line Settings, default shutdown
          exoPlayerManager.setShowVideoSwitch(true);
          //List of supported List
          String [] test={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4",
          "http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4",
           http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
           String[] name={"超清","高清","标清"};
           exoPlayerManager.setPlaySwitchUri(test,name);
    
 
   9.Set video loading prompt display mode (default Load Model Type.speed)
       
         /**Set load percentage display mode**/
         exoPlayerManager.setLoadModel(LoadModelType.PERCENR);
  
   11.Set the video multiple to play
         
        //Set to play video multiple quick and slow play less than 1 slow and more than 1
        exoPlayerManager.setPlaybackParameters(2f,2f);         
               
   11.Advertising video preview (easy to implement)
           
             /**You need to add parameters**/
             //The first parameter represents the AD video location index
              exoPlayerManager.setPlayUri(0, "http://mp4.vjshi.com/2013-07-25/2013072519392517096.mp4", "http://mp4.vjshi.com/2013-11-11/1384169050648_274.mp4");       
               //If you're playing video. Implement the interface callback
              //Video switch callback processing, layout processing, control layout display
                    exoPlayerManager.setOnWindowListener(new VideoWindowListener() {
                        @Override
                        public void onCurrentIndex(int currentIndex, int windowCount) {
                            if (currentIndex == 0) {
                                //Shielded control layout
                                exoPlayerManager.hideControllerView();
                                //If the shield controls the layout, you need to display the full screen button. Manual display, playback normally automatic restore. You don't have to deal with it yourself
                                videoPlayerView.getExoFullscreen().setVisibility(View.VISIBLE);
                            } else {
                                //Recovery control layout
                                exoPlayerManager.showControllerView();
                            }
                        }
                    });
              //skip the AD video operation
              exoPlayerManager.next();    
   12.Setting the hit play button to handle the business
      
        exoPlayerManager.setOnPlayClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(MainCustomActivity.this,"定义点击播放事件",Toast.LENGTH_LONG).show();
                                     //处理业务操作 完成后 
                                    exoPlayerManager.startPlayer();//开始播放
                      }
           }); 
   
   13.Set the Listener callback Video Info Listener

         exoPlayerManager.setVideoInfoListener(new VideoInfoListener() {
                       @Override
                       public void onPlayStart() {
                             //Start playing
                       }

                       @Override
                       public void onLoadingChanged() {
                                 //Load changes
                       }

                       @Override
                       public void onPlayerError(ExoPlaybackException e) {
                                 //Load error
                      }

                       @Override
                       public void onPlayEnd() {
                              //End of the play
                       }
                       @Override
                       public void onRepeatModeChanged(int repeatMode) {
                           //Schema changes
                       }
                   });
  
   14.Overwrite Activity and Fragment cycle methods

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
                //play back button to listen
                 if(exoPlayerManager.onBackPressed()){
                     finish();
                 }
                }


 ### 三.list
   1.The list is played, only Manual Player, in your Video Holder
   *  1.Use properties in the list control”app:controller_layout_id="@layout/simple_exo_playback＿list_view"“  //Provides a list control layout
   *  2.player_list="true" Set to true to open the list mode
   *  3.demo:
              public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
               .....           
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
                      //Initial chemical control unit
                      playerView = (VideoPlayerView) itemView.findViewById(R.id.item_exo_player_view);
                      userPlayer = new ManualPlayer((Activity) mContext, playerView);
                  }

                 /**
                 *Bind data source
                 ***/
                  public void bindData(String videoBean) {
                      userPlayer.setTitle("" + getAdapterPosition());
                      userPlayer.setPlayUri(videoBean);
                      Glide.with(mContext).load("http://..._960_540.jpg").into(playerView.getPreviewImage());
                  }
              }
  2.The list playback cycle method list implements the corresponding periodic method in the Activity or Fragment

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
                          VideoPlayerManager.getInstance().onConfigurationChanged(newConfig);//横竖屏切换
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

### 四.Data source factory class
 ####  1 Default data source
          缓存 : CacheDataSourceFactory
          http : DefaultDataSourceFactory,DefaultHttpDataSourceFactory
          Priority : PriorityDataSourceFactory
 #### 2 Customize the data source reference
      compile 'com.google.android.exoplayer:extension-okhttp:r2.5.1'
      compile 'com.google.android.exoplayer:extension-rtmp:r2.5.1'

### 五.[Custom data source usage-Poking me](RELEASESOURCE.md)
### 六.[Custom layout usage-Poking me](READMELAYUOT.md)
### 七.[Custom Media Source usage-Poking me](../RELEASEVIDEO.md) 
### 八.[Caching, encryption, video processing usage-Poking me](../README_EN_VIDEO.md) 


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
 
 

