package chuangyuan.ycj.videolibrary.listener;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AnimUtils;

import java.util.List;

/**
 * The interface Exo player view listener.
 *
 * @author yangc          date 2017/7/21         E-Mail:yangchaojiang@outlook.com         Deprecated: 控制类回调view 接口
 */
public interface ExoPlayerViewListener {

    /***
     * 显示wifi提示框
     */
    void showAlertDialog();

    /***
     * 显示隐藏记得进度条
     *
     * @param visibility 显示类型
     */
    void showHidePro(int visibility);

    /***
     * 设置水印图片
     *
     * @param res 资源id
     */
    void setWatermarkImage(int res);

    /***
     * 显示隐藏加载布局
     *
     * @param visibility 显示类型
     */
    void showLoadStateView(int visibility);

    /***
     * 显示隐藏重播布局
     *
     * @param visibility 显示类型
     */
    void showReplayView(int visibility);

    /***
     * 显示隐藏错误布局
     *
     * @param visibility 显示类型
     */
    void showErrorStateView(int visibility);

    /***
     * 显示隐藏手势布局
     *
     * @param visibility 显示类型
     */
    void showGestureView(int visibility);

    /**
     * 显示标题
     *
     * @param title 标题内容
     */
    void setTitles(@NonNull String title);

    /***
     * 显示网速
     *
     * @param netSpeed 网速的值
     */
    void showNetSpeed(@NonNull String netSpeed);

    /***
     * 手机很横竖屏切换
     *
     * @param newConfig 切换类型
     */
    void onConfigurationChanged(int newConfig);


    /***
     * 改变进度显示内容
     *
     * @param body 显示内容
     */
    void setTimePosition(@NonNull SpannableString body);

    /**
     * 改变声音
     *
     * @param mMaxVolume d最大音量
     * @param currIndex  当前音量
     */
    void setVolumePosition(int mMaxVolume, int currIndex);

    /**
     * 改变亮度
     *
     * @param mMax      最大亮度
     * @param currIndex 当前亮度
     */
    void setBrightnessPosition(int mMax, int currIndex);

    /**
     * 下一步
     */
    void next();

    /**
     * 上一部
     */
    void previous();

    /***
     * 隐藏控制布局操作不会显示
     * @param isShowFull 显示全屏按钮
     */
    void hideController(boolean isShowFull);

    /***
     * 显示控制布局操作
     * @param isShowFull 显示全屏按钮
     */
    void showController(boolean isShowFull);

    /***
     * 控制布局操作
     * @param onTouch 启用控制布局点击事件 true 启用 反则  false;
     */
    void setControllerHideOnTouch(boolean onTouch);

    /***
     * 控制布局封面图操作
     * @param visibility 类型
     * @param   isPlayer  是否时开售播放触发
     */
    void showPreview(int visibility, boolean isPlayer);

    /***
     * 设置开始播放OnTouch布局事件
     * @param isTouch 实例
     */
    void setPlayerBtnOnTouch(boolean isTouch);

    /***
     * 重置布局
     */
    void reset();

    /***
     * 获取view 高度
     * @return int height
     */
    int getHeight();

    /***
     * 手势操作OnTouch 事件
     * @param listener 实例
     */
    void setPlatViewOnTouchListener(View.OnTouchListener listener);

    /***
     * 设置显示多线路图标
     * @param showVideoSwitch true 显示 false 不现实
     */
    void setShowWitch(boolean showVideoSwitch);

    /***
     * 设置显示多线路图标
     * @param isOpenSeek true 启用 false 不启用
     */
    void setSeekBarOpenSeek(boolean isOpenSeek);

    /***
     * 是否列表
     * @return boolean boolean
     */
    boolean isList();

    /***
     * 设置播放播放控制类
     * @param player 实例
     */
    void setPlayer(@NonNull SimpleExoPlayer player);

    /***
     * 加载布局是否显示
     * @return boolean boolean
     */
    boolean isLoadingShow();

    /***
     * 获取 进度条
     * @param openSeek true开启拖拽 false 关闭
     */
    void setOpenSeek(boolean openSeek);

    /**
     * 退出全屏
     */
    void exitFull();

    /***
     * 是否在锁屏中
     * @return boolean boolean
     */
    boolean isLock();

    /**
     * 设置选择分辨率
     *
     * @param name        name
     * @param switchIndex switchIndex;
     */
    void setSwitchName(@NonNull List<String> name, int switchIndex);

    /***
     * 设置进度回调
     * @param updateProgressListener updateProgressListener
     * ***/
    void addUpdateProgressListener(@NonNull AnimUtils.UpdateProgressListener updateProgressListener);

    /***
     * 移除设置进度回调
     * @param updateProgressListener updateProgressListener
     * ***/
    void removeUpdateProgressListener(@NonNull AnimUtils.UpdateProgressListener updateProgressListener);

}