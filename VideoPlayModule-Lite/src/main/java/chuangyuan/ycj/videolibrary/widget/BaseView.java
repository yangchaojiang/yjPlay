package chuangyuan.ycj.videolibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.AnimUtils;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    protected Activity activity;
    /***进度条控件***/
    protected ExoDefaultTimeBar exoPlayerLockProgress;
    /***全屏按钮和锁屏按钮***/
    protected AppCompatCheckBox lockCheckBox;
    /***播放view***/
    protected final SimpleExoPlayerView playerView;
    /***视视频标题,清晰度切换,实时视频,加载速度显示,控制进度***/
    protected TextView videoLoadingShowText, videoDialogProText;
    /***视频加载页,错误页,进度控件,锁屏按布局,自定义预览布局***/
    protected View exoLoadingLayout, exoPlayErrorLayout, exoPlayLockLayout, exoPlayPreviewLayout;
    /***播放结束，提示布局,调整进度布局,控制音频和亮度布局***/
    protected View playReplayLayout, playBtnHintLayout, dialogProLayout, exoAudioLayout, exoBrightnessLayout;
    /***水印,封面图占位,显示音频和亮度布图***/
    protected ImageView exoPlayWatermark, exoPreviewImage, exoPreviewBottomImage, videoAudioImg, videoBrightnessImg;
    /***显示音频和亮度***/
    protected ProgressBar videoAudioPro, videoBrightnessPro;
    /***切换***/
    protected BelowView belowView;
    protected AlertDialog alertDialog;
    protected ExoPlayerListener mExoPlayerListener;
    /***返回按钮***/
    protected AppCompatImageView exoControlsBack;
    /***是否在上面,是否横屏,是否列表播放 默认false,是否切换按钮***/
    protected boolean isLand, isListPlayer, isShowVideoSwitch, isOpenLock = true;
    /***标题左间距***/
    protected int getPaddingLeft;
    private List<String> nameSwitch;
    protected int switchIndex;
    /***多分辨路点击回调**/
    protected BelowView.OnItemClickListener onItemClickListener;
    @DrawableRes
    int icBackImage = R.drawable.ic_exo_back;

    public BaseView(@NonNull Context context) {
        this(context, null);
    }

    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity = (Activity) context;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        playerView = new SimpleExoPlayerView(getContext(), attrs);
        addView(playerView, params);
        int userWatermark = 0;
        int replayId = R.layout.simple_exo_play_replay;
        int errorId = R.layout.simple_exo_play_error;
        int playerHintId = R.layout.simple_exo_play_btn_hint;
        int defaultArtworkId = 0;
        int loadId = R.layout.simple_exo_play_load;
        int videoProgressId = R.layout.simple_exo_video_progress_dialog;
        int audioId = R.layout.simple_video_audio_brightness_dialog;
        int brightnessId = R.layout.simple_video_audio_brightness_dialog;
        int preViewLayoutId = 0;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VideoPlayerView, 0, 0);
            try {
                icBackImage = a.getResourceId(R.styleable.VideoPlayerView_player_back_image, icBackImage);
                userWatermark = a.getResourceId(R.styleable.VideoPlayerView_user_watermark, 0);
                isListPlayer = a.getBoolean(R.styleable.VideoPlayerView_player_list, false);
                replayId = a.getResourceId(R.styleable.VideoPlayerView_player_replay_layout_id, replayId);
                errorId = a.getResourceId(R.styleable.VideoPlayerView_player_error_layout_id, errorId);
                playerHintId = a.getResourceId(R.styleable.VideoPlayerView_player_hint_layout_id, playerHintId);
                defaultArtworkId = a.getResourceId(R.styleable.VideoPlayerView_default_artwork, defaultArtworkId);
                loadId = a.getResourceId(R.styleable.VideoPlayerView_player_load_layout_id, loadId);
                audioId = a.getResourceId(R.styleable.VideoPlayerView_player_gesture_audio_layout_id, audioId);
                videoProgressId = a.getResourceId(R.styleable.VideoPlayerView_player_gesture_progress_layout_id, videoProgressId);
                brightnessId = a.getResourceId(R.styleable.VideoPlayerView_player_gesture_bright_layout_id, brightnessId);
                preViewLayoutId = a.getResourceId(R.styleable.VideoPlayerView_player_preview_layout_id, preViewLayoutId);
            } finally {
                a.recycle();
            }
        }
        exoPlayErrorLayout = inflate(context, errorId, null);
        playReplayLayout = inflate(context, replayId, null);
        playBtnHintLayout = inflate(context, playerHintId, null);
        exoLoadingLayout = inflate(context, loadId, null);
        exoAudioLayout = inflate(context, audioId, null);
        exoBrightnessLayout = inflate(context, brightnessId, null);
        dialogProLayout = inflate(context, videoProgressId, null);
        exoPlayLockLayout = inflate(context, R.layout.simple_exo_play_lock, null);
        if (preViewLayoutId != 0) {
            exoPlayPreviewLayout = inflate(context, preViewLayoutId, null);
        }
        intiView();
        intiGestureView(audioId, brightnessId, videoProgressId);
        initWatermark(userWatermark, defaultArtworkId);
    }

    protected void intiView() {
        exoControlsBack = new AppCompatImageView(getContext());
        exoControlsBack.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        int ss = VideoPlayUtils.dip2px(getContext(), 7f);
        exoControlsBack.setId(R.id.exo_controls_back);
        exoControlsBack.setImageDrawable(ContextCompat.getDrawable(getContext(), icBackImage));
        exoControlsBack.setPadding(ss, ss, ss, ss);
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
        if (exoPlayPreviewLayout != null) {
            frameLayout.addView(exoPlayPreviewLayout, frameLayout.getChildCount());
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(VideoPlayUtils.dip2px(getContext(), 35f), VideoPlayUtils.dip2px(getContext(), 35f));
        frameLayout.addView(exoControlsBack, frameLayout.getChildCount(), layoutParams);
        exoPlayWatermark = (ImageView) playerView.findViewById(R.id.exo_player_watermark);
        videoLoadingShowText = (TextView) playerView.findViewById(R.id.exo_loading_show_text);
        exoPlayerLockProgress = (ExoDefaultTimeBar) exoPlayLockLayout.findViewById(R.id.exo_player_lock_progress);
        lockCheckBox = (AppCompatCheckBox) exoPlayLockLayout.findViewById(R.id.exo_player_lock_btn_id);
        exoPreviewBottomImage = (ImageView) playerView.findViewById(R.id.exo_preview_image_bottom);
        if (playerView.findViewById(R.id.exo_preview_image) != null) {
            exoPreviewImage = (ImageView) playerView.findViewById(R.id.exo_preview_image);
            exoPreviewImage.setBackgroundResource(android.R.color.black);
        } else {
            exoPreviewImage = exoPreviewBottomImage;
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

    public void onDestroy() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if (belowView != null) {
            belowView = null;
        }
        if (lockCheckBox != null) {
            lockCheckBox.setOnCheckedChangeListener(null);
        }
        if (exoControlsBack != null && exoControlsBack.animate() != null) {
            exoControlsBack.animate().cancel();
        }
        if (lockCheckBox != null && lockCheckBox.animate() != null) {
            lockCheckBox.animate().cancel();
        }
        if (activity != null && activity.isFinishing()) {
            nameSwitch = null;
            activity = null;
            onItemClickListener = null;
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
     * 设置内容横竖屏内容
     *
     * @param newConfig 旋转对象
     */

    protected void scaleLayout(int newConfig) {
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
    }

    /***
     * 显示隐藏加载页
     *
     * @param visibility 状态
     ***/
    protected void showLockState(int visibility) {
        if (exoPlayLockLayout != null) {
            if (isLand) {
                if (lockCheckBox.isChecked()) {
                    if (visibility == View.VISIBLE) {
                        playerView.getControllerView().hideNo();
                        showBackView(GONE,true);
                    }
                } else
                    exoPlayLockLayout.setVisibility(visibility);
            } else {
                exoPlayLockLayout.setVisibility(GONE);
            }
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
            showBackView(VISIBLE,true);
            showLockState(GONE);
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
            playerView.getControllerView().hideNo();
            showLoadState(GONE);
            showErrorState(GONE);
            showBtnContinueHint(GONE);
            showLockState(GONE);
            showBackView(VISIBLE,true);
        }
        if (playReplayLayout != null) {
            playReplayLayout.setVisibility(visibility);
        }
    }

    /***
     * 显示隐藏返回键
     *
     * @param visibility 状态
     * @param  is is
     ***/
    protected void showBackView(int visibility,boolean is) {
        if (exoControlsBack != null) {
            if (isListPlayer() && !isLand) {
                exoControlsBack.setVisibility(GONE);
            } else {
                if (visibility == VISIBLE&&is) {
                    exoControlsBack.setTranslationY(0);
                    exoControlsBack.setAlpha(1f);
                }
                exoControlsBack.setVisibility(visibility);
            }
        }
    }


    /***
     * 显示按钮提示页
     *
     * @param visibility 状态
     ***/
    protected void showBtnContinueHint(int visibility) {
        if (visibility == View.VISIBLE) {
            showLoadState(GONE);
            showReplay(GONE);
            showErrorState(GONE);
            showPreViewLayout(GONE);
            showBackView(VISIBLE,true);
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

    /***
     * 显示隐藏自定义预览布局
     *
     * @param visibility 状态
     ***/
    protected void showPreViewLayout(int visibility) {
        if (exoPlayPreviewLayout != null) {
            exoPlayPreviewLayout.setVisibility(visibility);
        }
    }

    /***
     * 为了播放完毕后，旋转屏幕，导致播放图像消失处理
     * @param visibility 状态
     ***/
    protected void showBottomView(int visibility, Bitmap bitmap) {
         exoPreviewBottomImage.setVisibility(visibility);
        if (bitmap != null) {
            exoPreviewBottomImage.setImageBitmap(bitmap);
        }
    }

    /**
     * 设置标题
     *
     * @param title 内容
     **/
    public void setTitle(@NonNull String title) {
        playerView.getControllerView().setTitle(title);
    }

    /**
     * 设置占位预览图
     *
     * @param previewImage 预览图
     **/
    public void setPreviewImage(Bitmap previewImage) {
        this.exoPreviewImage.setImageBitmap(previewImage);
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
     *
     * @param icFullscreenStyle 全屏按钮样式
     **/
    public void setFullscreenStyle(@DrawableRes int icFullscreenStyle) {
        playerView.getControllerView().setFullscreenStyle(icFullscreenStyle);
    }

    /**
     * 设置开启开启锁屏功能
     *
     * @param openLock 默认 true 开启   false 不开启
     **/
    public void setOpenLock(boolean openLock) {
        isOpenLock = openLock;
    }

    /**
     * 设置选择回调
     *
     * @param onItemClickListener onItemClickListener
     **/
    public void setOnSwitchItemClickListener(@Nullable BelowView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    protected List<String> getNameSwitch() {
        if (nameSwitch == null) {
            nameSwitch = new ArrayList<>();
        }
        return nameSwitch;
    }

    /**
     * 设置多分辨显示文字
     *
     * @param name        name
     * @param switchIndex switchIndex
     **/
    protected void setSwitchName(@NonNull List<String> name, @Size(min = 0) int switchIndex) {
        this.nameSwitch = name;
        this.switchIndex = switchIndex;
    }

    /****
     * 获取控制类
     *
     * @return PlaybackControlView
     ***/
    @Nullable
    public PlaybackControlView getPlaybackControlView() {
        return playerView != null ? playerView.getControllerView() : null;
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
        return playerView.getControllerView().getExoFullscreen();
    }

    @NonNull
    public TextView getSwitchText() {
        return playerView.getControllerView().getSwitchText();
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
        return (ExoDefaultTimeBar) playerView.getControllerView().getTimeBar();
    }
}
