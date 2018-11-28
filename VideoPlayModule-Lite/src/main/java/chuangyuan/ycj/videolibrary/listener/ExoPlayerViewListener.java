package chuangyuan.ycj.videolibrary.listener;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.List;

import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;

/**
 * The interface Exo player view listener.
 *
 * @author yangc          date 2017/7/21         E-Mail:yangchaojiang@outlook.com         Deprecated: 控制类回调view 接口
 */
public interface ExoPlayerViewListener extends  BasePlayerListener{


    void startPlayer(ExoUserPlayer exoUserPlayer);

    /***
     * 显示wifi提示框
     */
    void showAlertDialog();
    /***
     * 恢复显示是否播放
     */
    void onResumeStart();
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

    /***
     * 显示网速
     *
     */
    void showNetSpeed(String netSpeed);

    /***
     * 手机很横竖屏切换
     *
     * @param isLand 是否横屏
     */
    void onConfigurationChanged(boolean isLand);


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
     * 设置是否可以显示回放控件。如果设置为{@code false }，回放控件*将永远不可见，并且与播放机断开连接。
     *
     * @param useController 是否可以显示回放控件。
     */
    void setUseController(boolean useController);
    /***
     * 显示隐藏控制布局操作
     * @param isShowFull 显示全屏按钮
     */
    void toggoleController(boolean isShowFull,boolean isShow);
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
     * 获取 进度条
     * @param openSeek true开启拖拽 false 关闭
     */
    void setOpenSeek(boolean openSeek);

    /**
     * 退出全屏
     */
    void exitFull();

    /**
     * 设置选择分辨率
     *
     * @param name        name
     * @param switchIndex switchIndex;
     */
    void setSwitchName(@NonNull List<String> name, int switchIndex);


    void setTag(Integer position);
}