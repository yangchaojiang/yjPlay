package chuangyuan.ycj.videolibrary.listener;

import android.text.SpannableString;

/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public interface ExoPlayerViewListener {
    /***
     * 显示费wifi提示框
     ***/
    void showAlertDialog();

    /***
     * 显示隐藏记得进度条
     *
     * @param visibility 显示类型
     ***/
    void showHidePro(int visibility);

    /***
     * 设置水印图片
     *
     * @param res 资源id
     ***/
    void setWatermarkImage(int res);

    /***
     * 设置多线路显示text
     *
     * @param name 内容
     ***/
    void showSwitchName(String name);

    /***
     * 显示隐藏加载布局
     *
     * @param visibility 显示类型
     ***/
    void showLoadStateView(int visibility);

    /***
     * 显示隐藏重播布局
     *
     * @param visibility 显示类型
     ***/
    void showReplayView(int visibility);

    /***
     * 显示隐藏错误布局
     *
     * @param visibility 显示类型
     ***/
    void showErrorStateView(int visibility);

    /***
     * 显示隐藏手势布局
     *
     * @param visibility 显示类型
     ***/
    void showGestureView(int visibility);

    /**
     * 显示标题
     *
     * @param title 标题内容
     **/
    void setTitle(String title);

    /***
     * 显示网速
     *
     * @param netSpeed 网速的值
     ***/
    void showNetSpeed(String netSpeed);

    /***
     * 手机很横竖屏切换
     *
     * @param newConfig 切换类型
     ***/
    void onConfigurationChanged(int newConfig);


    /***
     * 改变进度显示内容
     *
     * @param body 显示内容
     **/
    void setTimePosition(SpannableString body);

    /**
     * 改变声音
     *
     * @param mMaxVolume d最大音量
     * @param currIndex  当前音量
     **/
    void setVolumePosition(int mMaxVolume, int currIndex);

    /**
     * 改变亮度
     *
     * @param mMaxVolume d最大音量
     * @param currIndex  当前音量
     **/
    void setBrightnessPosition(int mMaxVolume, int currIndex);
}
