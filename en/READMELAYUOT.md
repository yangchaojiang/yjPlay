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