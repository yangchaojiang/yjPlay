# yjPlay
 
 
 基于exoplay 自定义播放器 支持直播
  
 1 ExoUserPlayer  基本播放器
 2 GestureVideoPlayer   增加手势  亮度，音量，快进，等手势
 3 ManualPlayer  默认手动播放，增加默认图
 4 支持自定义ui
 5 增加广广告视频预览

 ## gif 显示有点卡，帧数低，实际很流畅

 ![](sss.gif)

 ###Import

 use     import dependency in gradle
 
```
 repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
    
dependencies {

   compile 'com.ycjiang:VideoPlayModule:1.3.0'

}

 Maven

<dependency>
  <groupId>com.ycjiang</groupId>
  <artifactId>VideoPlayModule</artifactId>
  <version>1.3.0/version>
  <type>pom</type>
</dependency>

```


 ###布局引用
 ```<com.google.android.exoplayer2.ui.SimpleExoPlayerView
            android:id="@+id/player_view"
            android:layout_width="match_parent"
            android:layout_height="200dp"
            app:controller_layout_id="@layout/simple_exo_playback_control_view"
            app:default_artwork="@mipmap/video_def"
            app:player_layout_id="@layout/simple_exo_view"
            app:resize_mode="fit"
            app:surface_type="texture_view"
            app:use_artwork="true" />

```
 * 1     //   default_artwork  占位图
 * 2     //   player_layout_id  播放器布局， controller_layout_id  控制器布局`
 * 2     //   resize_mode  视频渲染模式 fit,fill,fixed_width,fixed_height
 * 3     //  surface_type 视频渲染类型 //texture_view 和surface_view
 * 4     //  show_timeout  超时时间
 * 5     //  paddingEnd，paddingStart 设置边距
 * 6     //  fastforward_increment  设置快进增量,以毫秒为单位。
 * 7     //  rewind_increment  设置快退增量,以毫秒为单位。
 * 8     //  use_controller   控制器
 * 在你app的strings.xml  可以替换对框框提示标题和内容
```
 app.strings.xml
     <string name="exo_play_reminder">您当前网络不是wifi，是否继续观看视频</string>
     <string name="exo_play_wifi_hint_no">提示</string>
 ```
 ###  JAVA
 ```
    播放代码
    ManualPlayer exoPlayerManager = new ManualPlayer(this);
      exoPlayerManager.setPlayUri("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8");
    布局引用
     <include layout="@layout/simple_exo_video_play"/>
     或者是
     播放代码
     ManualPlayer exoPlayerManager = new ManualPlayer(this,getString(R.string.url_hls));
 ```
 ```
   @Override
    public void onStart() {
        super.onStart();
        exoPlayerManager.onStart();
        Log.d(TAG, "onStart");
    }

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
    public void onStop() {
        super.onStop();
        exoPlayerManager.onStop();
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

 ## 1.3.0
 1。增加播放数据流量提醒框，增加网络变化监听
 2。toobar状态的隐藏和显示,  增加v7依赖
 3。直播隐藏进度条
 4。两个视频切换，广告视频，进度处理
 5。修复已知bug.简化处理


