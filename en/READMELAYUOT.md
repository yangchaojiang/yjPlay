 ## Custom layout

### 一.instructions
  1. The content of the layout is defined by the user
  2. The layout must have a view id must be specified
  3. The layout must have a view id must be specified
  4. The layout must have a view id must be specified
 ### view指定id 说明
  1. Replay layout→ **<font color="red">android:id="@id/exo_player_replay_btn_id"</font>**
  2. The wrong layout→ **<font color="red">android:id="@id/exo_player_error_btn_id"</font>**
  3. Non-wifi play prompt layout→ **<font color="red">android:id="@id/exo_player_btn_hint_btn_id"</font>**
  4. Loading layout→ **<font color="red">android:id="@id/exo_loading_show_text"</font>**
 >>note：
 >>
 >>**1.If you specify that the id system does not specify the event handling<br/>
          _2.Notice the control that loads the layout to show the speed of loading”exo_loading_show_text“，Control can only be TextView<br/>
          _3.If you need to not display the load network traffic prompt, do not specify the control id**

 ### 三.The sample is as follows

  1.-Replay layout
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
<!-- This control specifies the id >exo_player_replay_btn_id -->
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
  3.-The wrong layout
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
        android:text="Custom error page"
        android:textColor="@android:color/white" />
</LinearLayout>
```
  3.-Non-wifi play prompt layout
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/black"
    android:clickable="true"
    android:gravity="center"
    android:orientation="vertical">

<!-- This control is specified id> exo_player_btn_hint_btn_id -->
    <TextView
        android:id="@id/exo_player_btn_hint_btn_id"
        android:layout_width="wrap_content"
        android:layout_height="40dp"
        android:background="@drawable/btn_continue_drawable"
        android:drawablePadding="5dp"
        android:gravity="center"
        android:padding="10dp"
        android:shadowColor="@color/simple_exo_style_Accent"
        android:text="Custom flow prompt layout"
        android:textColor="@android:color/white" />
</LinearLayout>
```
  4.-Loading layout   [The animation controls→Poking me](https://github.com/81813780/AVLoadingIndicatorView)
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

    <!--downloading speed-->
    <TextView
        android:id="@id/exo_loading_show_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:textColor="@android:color/white"
        android:textSize="16sp" />

</LinearLayout>
```

### 二.Gesture layout customization layout (layout does not need to specify control id)
1.Layout of the reference
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
2.Code using
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
3.matters need attention：
  * 1.You don't need to be concerned about gestures and layout hiding problems. Auto hide layout, focus on you need to display content style。
  * 2.The gesture layout can customize any arbitrary layout. The user decides that he needs to customize the layout。