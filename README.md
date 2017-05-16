# yjPlay
 
 
 基于exoplay 自定义播放器 支持直播
  
 1 ExoUserPlayer  基本播放器
 2 GestureVideoPlayer   增加手势  亮度，音量，快进，等手势
 3 ManualPlayer  默认手动播放，增加默认图
 4 支持自定义ui

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

   compile 'com.ycjiang:VideoPlayModule:1.1.8'

}

 Maven

<dependency>
  <groupId>com.ycjiang</groupId>
  <artifactId>VideoPlayModule</artifactId>
  <version>1.1.8/version>
  <type>pom</type>
</dependency>

```
### XML
```XML
   播放代码
   ManualPlayer exoPlayerManager = new ManualPlayer(this,getString(R.string.url_hls));
   布局引用
    <include layout="@layout/simple_exo_video_play"/>
    或者是
    播放代码
    ManualPlayer exoPlayerManager = new ManualPlayer(this,playView,getString(R.string.url_hls));  
    布局引用
      
      
   <com.google.android.exoplayer2.ui.SimpleExoPlayerView
            android:id="@+id/player_view"
            android:layout_width="match_parent"
            android:layout_height="200dp"
            app:controller_layout_id="@layout/simple_exo_playback_control_view"
            app:default_artwork="@mipmap/video_def"
            app:player_layout_id="@layout/simple_exo_player_view"
            app:resize_mode="fit"
            app:surface_type="texture_view"
            app:use_artwork="true" />
 
 
 
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
