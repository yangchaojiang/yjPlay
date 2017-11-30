package chuangyuan.ycj.videolibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Assertions;

import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.listener.ExoPlayerListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;

/**
 * author  yangc
 * date 2017/11/24
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 父类view 存放控件方法
 */

abstract class BaseView extends FrameLayout {
    public static final String TAG = VideoPlayerView.class.getName();
    /***活动窗口***/
    Activity activity;
    /***进度条控件***/
    ExoDefaultTimeBar timeBar, exoPlayerLockProgress;
    /***全屏按钮和锁屏按钮***/
    AppCompatCheckBox exoFullscreen, lockCheckBox;
    /***播放view***/
    SimpleExoPlayerView playerView;
    /***视视频标题,清晰度切换,实时视频,加载速度显示,控制进度***/
    TextView controlsTitleText, videoSwitchText, videoLoadingShowText, videoDialogProText;
    /***视频加载页,错误页,进度控件,锁屏按钮***/
    View exoLoadingLayout, exoPlayErrorLayout, exoPlayLockLayout;
    /***播放结束，提示布局,调整进度布局,控制音频和亮度布局***/
    View playReplayLayout, playBtnHintLayout, dialogProLayout, exoAudioLayout, exoBrightnessLayout;
    /***水印,封面图占位,显示音频和亮度布图***/
    ImageView exoPlayWatermark, exoPreviewImage, videoAudioImg, videoBrightnessImg;
    /***显示音频和亮度***/
    ProgressBar videoAudioPro, videoBrightnessPro;
    /***切换***/
    BelowView belowView;
    AlertDialog alertDialog;
    ExoPlayerListener mExoPlayerListener;
    /**
     * 返回按钮
     **/
    AppCompatImageView exoControlsBack;
    /***是否在上面,是否横屏,是否列表播放 默认false,是否切换按钮***/
    boolean isPreViewTop, isLand, isListPlayer, isShowVideoSwitch,isOpenLock;
    /***标题左间距***/
    int getPaddingLeft;
    @DrawableRes
    int icFullscreenSelector = R.drawable.ic_fullscreen_selector;
    @DrawableRes
    int icBackImage = R.drawable.ic_chevron_left_white_48px;

    public BaseView(@NonNull Context context) {
        this(context, null);
    }

    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VideoPlayerView, 0, 0);
            try {
                icFullscreenSelector = a.getResourceId(R.styleable.VideoPlayerView_player_fullscreen_image_selector, icFullscreenSelector);
                icBackImage = a.getResourceId(R.styleable.VideoPlayerView_player_back_image, icBackImage);
            } finally {
                a.recycle();
            }
        }
    }

    protected void intiView() {
        exoControlsBack = new AppCompatImageView(getContext());
        exoControlsBack.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        int ss=VideoPlayUtils.dip2px(getContext(), 5f);
        exoControlsBack.setId(R.id.exo_controls_back);
        exoControlsBack.setImageDrawable(ContextCompat.getDrawable(getContext(),icBackImage));
        exoControlsBack.setPadding(ss,ss,ss,ss);
        FrameLayout frameLayout = playerView.getContentFrameLayout();
        frameLayout.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.black));
        exoPlayErrorLayout.setVisibility(GONE);
        playReplayLayout.setVisibility(GONE);
        playBtnHintLayout.setVisibility(GONE);
        exoLoadingLayout.setVisibility(GONE);
        dialogProLayout.setVisibility(GONE);
        exoAudioLayout.setVisibility(GONE);
        exoBrightnessLayout.setVisibility(GONE);
        exoPlayLockLayout.setVisibility(GONE);
        exoPlayLockLayout.setBackgroundColor(Color.TRANSPARENT);
        exoLoadingLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.simple_exo_color_33));
        exoLoadingLayout.setClickable(true);
        frameLayout.addView(exoBrightnessLayout, frameLayout.getChildCount());
        frameLayout.addView(exoAudioLayout, frameLayout.getChildCount());
        frameLayout.addView(dialogProLayout, frameLayout.getChildCount());
        frameLayout.addView(exoPlayErrorLayout, frameLayout.getChildCount());
        frameLayout.addView(playReplayLayout, frameLayout.getChildCount());
        frameLayout.addView(playBtnHintLayout, frameLayout.getChildCount());
        frameLayout.addView(exoLoadingLayout, frameLayout.getChildCount());
        frameLayout.addView(exoPlayLockLayout, frameLayout.getChildCount());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(VideoPlayUtils.dip2px(getContext(), 36f), VideoPlayUtils.dip2px(getContext(), 36f));
        frameLayout.addView(exoControlsBack, frameLayout.getChildCount(), layoutParams);
        exoPlayWatermark = (ImageView) playerView.findViewById(R.id.exo_player_watermark);
        controlsTitleText = playerView.getUserControllerView().getControlsTitleText();
        videoLoadingShowText = (TextView) playerView.findViewById(R.id.exo_loading_show_text);
        videoSwitchText = (TextView) playerView.findViewById(R.id.exo_video_switch);
        timeBar = (ExoDefaultTimeBar) playerView.findViewById(R.id.exo_progress);
        exoPlayerLockProgress = (ExoDefaultTimeBar) exoPlayLockLayout.findViewById(R.id.exo_player_lock_progress);
        lockCheckBox = (AppCompatCheckBox) exoPlayLockLayout.findViewById(R.id.exo_player_lock_btn_id);
        exoFullscreen = (AppCompatCheckBox) findViewById(R.id.exo_video_fullscreen);
        exoFullscreen.setButtonDrawable(icFullscreenSelector);
        if (playerView.findViewById(R.id.exo_preview_image) != null) {
            isPreViewTop = true;
            exoPreviewImage = (ImageView) playerView.findViewById(R.id.exo_preview_image);
        } else {
            isPreViewTop = false;
            exoPreviewImage = (ImageView) playerView.findViewById(R.id.exo_preview_image_bottom);
        }
    }

    /***
     * 初始化手势布局view
     * @param audioId 音频布局id
     * @param brightnessId 亮度布局id
     * @param videoProgressId 进度布局id
     * ***/
    protected void intiGestureView(int audioId, int brightnessId, int videoProgressId) {
        if (audioId == R.layout.simple_video_audio_brightness_dialog) {
            videoAudioImg = (ImageView) exoAudioLayout.findViewById(R.id.exo_video_audio_brightness_img);
            videoAudioPro = (ProgressBar) exoAudioLayout.findViewById(R.id.exo_video_audio_brightness_pro);
        }
        if (brightnessId == R.layout.simple_video_audio_brightness_dialog) {
            videoBrightnessImg = (ImageView) exoBrightnessLayout.findViewById(R.id.exo_video_audio_brightness_img);
            videoBrightnessPro = (ProgressBar) exoBrightnessLayout.findViewById(R.id.exo_video_audio_brightness_pro);
        }
        if (videoProgressId == R.layout.simple_exo_video_progress_dialog) {
            videoDialogProText = (TextView) dialogProLayout.findViewById(R.id.exo_video_dialog_pro_text);
        }
    }

    /***
     * 设置水印图和封面图
     * @param userWatermark  userWatermark  水印图
     *@param  defaultArtworkId  defaultArtworkId   封面图
     * **/
    protected void initWatermark(int userWatermark, int defaultArtworkId) {
        if (userWatermark != 0) {
            exoPlayWatermark.setImageResource(userWatermark);
        }
        if (defaultArtworkId != 0) {
            setPreviewImage(BitmapFactory.decodeResource(getResources(), defaultArtworkId));
        } else {
            if (!isPreViewTop) {
                setPreviewImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_black));
            }
        }
    }

    /***
     * 显示网络提示框
     ***/
    protected void showDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
        alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(activity.getString(R.string.exo_play_reminder));
        alertDialog.setMessage(activity.getString(R.string.exo_play_wifi_hint_no));
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showBtnContinueHint(View.VISIBLE);

            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showBtnContinueHint(View.GONE);
                mExoPlayerListener.playVideoUri();
            }
        });
        alertDialog.show();
    }

    /***
     * 设置是横屏,竖屏
     *
     * @param newConfig 旋转对象
     */
    void doOnConfigurationChanged(int newConfig) {
        Log.d(TAG, "doOnConfigurationChanged:" + newConfig);
        //横屏
        if (newConfig == Configuration.ORIENTATION_LANDSCAPE) {
            if (isLand) {
                return;
            }
            isLand = true;
            VideoPlayUtils.hideActionBar(activity);
            this.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            //判断是否开启多线路支持
            if (isShowVideoSwitch) {
                videoSwitchText.setVisibility(VISIBLE);
            }
            //是否列表
            if (isListPlayer()) {
                exoControlsBack.setVisibility(VISIBLE);
                getPaddingLeft = controlsTitleText.getPaddingLeft();
                controlsTitleText.setPadding(VideoPlayUtils.dip2px(getContext(), 40), 0, 0, 0);
            }
            lockCheckBox.setChecked(false);
            //显示锁屏按钮
            showLockState(VISIBLE);
            //显更改全屏按钮选中，自动旋转屏幕
            exoFullscreen.setChecked(true);
        } else {//竖屏
            if (!isLand) {
                return;
            }
            isLand = false;
            this.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE);
            VideoPlayUtils.showActionBar(activity);
            //多线路支持隐藏
            if (videoSwitchText != null) {
                videoSwitchText.setVisibility(GONE);
            }
            //列表播放
            if (isListPlayer()) {
                showBackView(GONE);
                controlsTitleText.setPadding(getPaddingLeft, 0, 0, 0);
            }
            //隐藏锁屏按钮移除
            showLockState(GONE);
            //更改全屏按钮选中，自动旋转屏幕
            exoFullscreen.setChecked(false);
        }
        scaleLayout(newConfig);

    }

    /***
     * 设置内容横竖屏内容
     *
     * @param newConfig 旋转对象
     */

    private void scaleLayout(int newConfig) {
        if (playerView.getPlayer() != null) {
            playerView.getPlayer().setPlayWhenReady(false);
        }
        if (newConfig == Configuration.ORIENTATION_PORTRAIT) {
            ViewGroup parent = (ViewGroup) playerView.getParent();
            if (parent != null) {
                parent.removeView(playerView);
            }
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(playerView, params);
        } else {
            ViewGroup parent = (ViewGroup) playerView.getParent();
            if (parent != null) {
                parent.removeView(playerView);
            }
            ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            contentView.addView(playerView, params);
        }
        if (playerView.getPlayer() != null) {
            playerView.getPlayer().setPlayWhenReady(true);
        }
    }

    /***
     * 显示隐藏加载页
     *
     * @param visibility 状态
     ***/
    protected void showLockState(int visibility) {
        Assertions.checkState(exoPlayLockLayout != null);
        if (isLand) {
            if (lockCheckBox.isChecked()) {
                if (visibility == View.VISIBLE) {
                    playerView.getUserControllerView().hideNo();
                    showBackView(GONE);
                    showFullscreenView(GONE);
                }
            } else
                exoPlayLockLayout.setVisibility(visibility);
        } else {
            exoPlayLockLayout.setVisibility(GONE);
        }

    }


    /***
     * 显示隐藏加载页
     *
     * @param visibility 状态
     ***/
    protected void showLoadState(int visibility) {
        if (visibility == View.VISIBLE) {
            showErrorState(GONE);
            showReplay(GONE);
            showLockState(GONE);
        }
        if (exoLoadingLayout != null) {
            exoLoadingLayout.setVisibility(visibility);
        }
    }

    /***
     * 显示隐藏错误页
     *
     * @param visibility 状态
     ***/
    protected void showErrorState(int visibility) {
        if (visibility == View.VISIBLE) {
            playerView.hideController();
            showLoadState(GONE);
            showReplay(GONE);
            showBackView(VISIBLE);
            showLockState(GONE);
            showFullscreenView(GONE);
        }
        if (exoPlayErrorLayout != null) {
            exoPlayErrorLayout.setVisibility(visibility);
        }
    }


    /***
     * 显示隐藏重播页
     *
     * @param visibility 状态
     ***/
    protected void showReplay(int visibility) {
        if (visibility == View.VISIBLE) {
            playerView.hideController();
            showLoadState(GONE);
            showErrorState(GONE);
            showBtnContinueHint(GONE);
            showBackView(VISIBLE);
            showLockState(GONE);
            showFullscreenView(GONE);
        }
        if (playReplayLayout != null) {
            playReplayLayout.setVisibility(visibility);
        }
    }

    /***
     * 显示隐藏返回键
     *
     * @param visibility 状态
     ***/
    protected void showBackView(int visibility) {
        if (exoControlsBack != null) {
            if (isListPlayer() && !isLand) {
                exoControlsBack.setVisibility(GONE);
            } else {
                exoControlsBack.setVisibility(visibility);
            }
        }
    }

    /***
     * 显示隐藏全屏按钮
     *
     * @param visibility 状态
     ***/
    public void showFullscreenView(int visibility) {
        if (exoFullscreen == null) {
            return;
        }
        if (isPreViewTop) {
            exoFullscreen.setVisibility(getPreviewImage().getVisibility() == VISIBLE ? GONE : visibility);
        } else {
            exoFullscreen.setVisibility(visibility);
        }
    }

    /***
     * 显示按钮提示页
     *
     * @param visibility 状态
     ***/
    protected void showBtnContinueHint(int visibility) {
        if (visibility == View.VISIBLE) {
            showBackView(VISIBLE);
            showLoadState(GONE);
            showReplay(GONE);
            showErrorState(GONE);
        }
        if (playBtnHintLayout != null) {
            playBtnHintLayout.setVisibility(visibility);
        }
    }

    /***
     * 显示隐藏手势布局
     *
     * @param visibility 状态
     ***/
    protected void showGesture(int visibility) {
        if (exoAudioLayout != null) {
            exoAudioLayout.setVisibility(visibility);
        }
        if (exoBrightnessLayout != null) {
            exoBrightnessLayout.setVisibility(visibility);
        }
        if (dialogProLayout != null) {
            dialogProLayout.setVisibility(visibility);
        }
    }

    /**
     * 设置标题
     *
     * @param title 内容
     **/
    public void setTitle(@NonNull String title) {
        controlsTitleText.setText(title);
    }

    /**
     * 设置占位预览图
     *
     * @param previewImage 预览图
     **/
    public void setPreviewImage(Bitmap previewImage) {
        this.exoPreviewImage.setImageBitmap(previewImage);
    }

    /**
     * 设置占位预览图
     *
     * @param previewImage 预览图
     * @deprecated {@link #setPreviewImage(Bitmap) }
     **/
    @Deprecated
    public void setArtwork(@NonNull Bitmap previewImage) {
        setPreviewImage(previewImage);
    }

    /**
     * 设置是是否占位预览图
     *
     * @param useArtwork true 显示  false 隐藏
     **/
    private void setUseArtwork(boolean useArtwork) {
        exoPreviewImage.setVisibility(useArtwork ? VISIBLE : GONE);
    }

    /***
     * 设置播放的状态回调
     *
     * @param mExoPlayerListener 回调
     ***/
    public void setExoPlayerListener(ExoPlayerListener mExoPlayerListener) {
        this.mExoPlayerListener = mExoPlayerListener;
    }

    /***
     * 设置开启线路切换按钮
     *
     * @param showVideoSwitch true 显示  false 不现实
     **/
    public void setShowVideoSwitch(boolean showVideoSwitch) {
        isShowVideoSwitch = showVideoSwitch;
    }

    /**
     * 设置全屏按钮样式
     *@param  icFullscreenStyle  全屏按钮样式
     **/
    public void setFullscreenStyle(@DrawableRes int icFullscreenStyle) {
        this.icFullscreenSelector = icFullscreenStyle;
        if (exoFullscreen != null) {
            exoFullscreen.setButtonDrawable(icFullscreenStyle);
        }
    }
    /**
     * 设置开启开启锁屏功能
     *@param  openLock  默认 true 开启   false 不开启
     **/
    public void setOpenLock(boolean openLock) {
        isOpenLock = openLock;
    }
    /****
     * 获取控制类
     *
     * @return PlaybackControlView
     ***/
    @Nullable
    public PlaybackControlView getPlaybackControlView() {
        return playerView != null ? playerView.getUserControllerView() : null;
    }

    /***
     * 获取当前加载布局
     *
     * @return View
     */
    public boolean isLoadingLayoutShow() {
        return exoLoadingLayout.getVisibility() == VISIBLE;
    }

    /***
     * 获取视频加载view
     *
     * @return View
     **/
    @Nullable
    public View getLoadLayout() {
        return exoLoadingLayout;
    }

    /***
     * 流量播放提示view
     *
     * @return View
     **/
    @Nullable
    public View getPlayHintLayout() {
        return playBtnHintLayout;
    }

    /***
     * 重播展示view
     *
     * @return View
     **/
    @Nullable
    public View getReplayLayout() {
        return playReplayLayout;
    }

    /***
     * 错误展示view
     *
     * @return View
     **/
    @Nullable
    public View getErrorLayout() {
        return exoPlayErrorLayout;
    }

    /***
     * 获取手势音频view
     *
     * @return View 手势
     **/
    @NonNull
    public View getGestureAudioLayout() {
        return exoAudioLayout;
    }

    /***
     * 获取手势亮度view
     *
     * @return View
     **/
    @NonNull
    public View getGestureBrightnessLayout() {
        return exoBrightnessLayout;
    }

    /***
     * 获取手势视频进度调节view
     *
     * @return View
     **/
    @NonNull
    public View getGestureProgressLayout() {
        return dialogProLayout;
    }

    /***
     * 是否属于列表播放
     *
     * @return boolean
     ***/
    public boolean isListPlayer() {
        return isListPlayer;
    }

    /***
     * 获取全屏按钮
     * @return boolean
     ***/
    public AppCompatCheckBox getExoFullscreen() {
        return exoFullscreen;
    }

    /**
     * 获取g播放控制类
     *
     * @return ExoUserPlayer
     **/
    @Nullable
    public ExoUserPlayer getPlay() {
        return mExoPlayerListener != null ? mExoPlayerListener.getPlay() : null;
    }

    /***
     * 获取预览图
     *
     * @return ImageView
     ***/
    @NonNull
    public ImageView getPreviewImage() {
        return exoPreviewImage;
    }

    /***
     * 获取内核播放view
     *
     * @return SimpleExoPlayerView
     **/
    @NonNull
    public SimpleExoPlayerView getPlayerView() {
        return playerView;
    }

    /**
     * 获取进度条
     *
     * @return ExoDefaultTimeBar
     **/
    @NonNull
    public ExoDefaultTimeBar getTimeBar() {
        return timeBar;
    }

}
