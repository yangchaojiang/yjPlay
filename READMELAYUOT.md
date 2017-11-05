 ## 自定义布局

### 一.布局使用说明
  1. 布局内容由使用者 任意定义布局
  2. 布局内必须有一个view的id 必须指定
  3. 布局内必须有一个view的id 必须指定
  4. 布局内必须有一个view的id 必须指定
 #### view指定id 说明
  1. 重播布局→ **<font color="red">android:id="@id/exo_player_replay_btn_id"</font>**
  2. 错误布局→ **<font color="red">android:id="@id/exo_player_error_btn_id"</font>**
  3. 非wifi播放提示布局→ **<font color="red">android:id="@id/exo_player_btn_hint_btn_id"</font>**
  4. 加载布局→ **<font color="red">android:id="@id/exo_loading_show_text"</font>**
 >>注意：
 >>
 >>**1.如果指定不指定id 系统不执行相应事件处理<br/>
          _2.注意加载布局的显示加载速度的控件”exo_loading_show_text“，控件只能是TextView<br/>
          _3.如果你需要不显示加载网络提示，不指定控件id**

 ### 三.示例如下

  1.-重播布局
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#80000000"
    android:clickable="true"
    android:gravity="center"
    android:orientation="horizontal"
    >
<!-- 示例该控件指定id>exo_player_replay_btn_id -->
    <ImageView
        android:id="@id/exo_player_replay_btn_id"
        android:layout_width="30dp"
        android:layout_height="30dp"
        app:srcCompat="@drawable/ic_replay_"
        android:textColor="@android:color/white"
        android:layout_marginRight="10dp"
          />

    <ImageView
        android:layout_marginLeft="10dp"
        android:layout_width="30dp"
        android:layout_height="30dp"
        app:srcCompat="@drawable/ic_share"
        android:id="@+id/replay_btn_imageView"
          />
</LinearLayout>
```
  3.-错误布局
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/exo_error_bg"
    android:clickable="true"
    android:gravity="center"
    android:orientation="vertical">
<!-- 示例该控件指定id>exo_player_error_btn_id -->
    <TextView
        android:id="@id/exo_player_error_btn_id"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:clickable="false"
        android:drawableBottom="@drawable/ic_sentiment_very_dissatisfied_white_48px"
        android:drawablePadding="5dp"
        android:gravity="center"
        android:shadowColor="@color/simple_exo_style_Accent"
        android:text="自定义错误页"
        android:textColor="@android:color/white" />
</LinearLayout>
```
  3.-非wifi播放提示布局
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/black"
    android:clickable="true"
    android:gravity="center"
    android:orientation="vertical">

<!-- 示例该控件指定id>exo_player_btn_hint_btn_id -->
    <TextView
        android:id="@id/exo_player_btn_hint_btn_id"
        android:layout_width="wrap_content"
        android:layout_height="40dp"
        android:background="@drawable/btn_continue_drawable"
        android:drawablePadding="5dp"
        android:gravity="center"
        android:padding="10dp"
        android:shadowColor="@color/simple_exo_style_Accent"
        android:text="自定义流量提示布局"
        android:textColor="@android:color/white" />
</LinearLayout>
```
  4.-加载布局   [动画控件→戳我](https://github.com/81813780/AVLoadingIndicatorView)
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:my="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical"
     >
    <chuangyuan.ycj.videolibrary.widget.ExoVideoAnim
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        my:circleRadius="10dp"
        my:cycle="600" />

    <!--下载速度-->
    <TextView
        android:id="@id/exo_loading_show_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:textColor="@android:color/white"
        android:textSize="16sp" />

</LinearLayout>
```
### 二.手势布局自定义布局（布局不需要指定控件id）
1.布局引用
````
 <chuangyuan.ycj.videolibrary.widget.VideoPlayerView
        android:id="@+id/exo_play_context_id"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        app:player_gesture_audio_layout_id="@layout/custom_gesture_audio"
        app:player_gesture_bright_layout_id="@layout/custom_gesture_brightness"
        app:player_gesture_progress_layout_id="@layout/custom_gesture_pro"
        ....
          />
````
2.代码使用
````
//实现你手势信息回调接口
  //exoPlayerManager.setOnGestureBrightnessListener();//亮度
  //exoPlayerManager.setOnGestureProgressListener(); //进度调节
  // exoPlayerManager.setOnGestureVolumeListener(); //音频
  //以下示例代码  
   exoPlayerManager.setOnGestureBrightnessListener(new OnGestureBrightnessListener() {
            @Override
            public void setBrightnessPosition(int mMax, int currIndex) {
                //显示你的布局
                videoPlayerView.getGestureBrightnessLayout().setVisibility(View.VISIBLE);
                //为你布局显示内容自定义内容
                videoBrightnessPro.setMax(mMax);
                videoBrightnessImg.setImageResource(chuangyuan.ycj.videolibrary.R.drawable.ic_brightness_6_white_48px);
                videoBrightnessPro.setProgress(currIndex);
            }
        });
        exoPlayerManager.setOnGestureProgressListener(new OnGestureProgressListener() {
            @Override
            public void showProgressDialog(long seekTimePosition, long duration, String seekTime, String totalTime) {
                //显示你的布局
                videoPlayerView.getGestureProgressLayout().setVisibility(View.VISIBLE);
                exo_video_dialog_pro_text.setTextColor(Color.RED);
                exo_video_dialog_pro_text.setText(seekTime + "/" + totalTime);
            }
        });
        exoPlayerManager.setOnGestureVolumeListener(new OnGestureVolumeListener() {
            @Override
            public void setVolumePosition(int mMax, int currIndex) {
                //显示你的布局
                videoPlayerView.getGestureAudioLayout().setVisibility(View.VISIBLE);
                //为你布局显示内容自定义内容
                videoAudioPro.setMax(mMax);
                videoAudioPro.setProgress(currIndex);
                videoAudioImg.setImageResource(currIndex == 0 ? R.drawable.ic_volume_off_white_48px : R.drawable.ic_volume_up_white_48px);
            }
        });
````
3.注意事项：
  * 1.你不需要关心手势操作后，布局隐藏问题。自动隐藏布局，专注你需要显示内容央视就可以了。
  * 2.手势布局可以自定义其中一个或者两个布局。有使用者自己决定需要自定义布局。