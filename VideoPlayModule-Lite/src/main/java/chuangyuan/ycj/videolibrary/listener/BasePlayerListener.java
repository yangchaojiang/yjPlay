package chuangyuan.ycj.videolibrary.listener;

public interface BasePlayerListener {
    void onPlayNoAlertVideo();

    void onDestroy();

    void onResume();

    void onPause();

     void setPlayerGestureOnTouch(boolean setOpenSeek);

}
