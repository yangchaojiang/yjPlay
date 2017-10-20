package chuangyuan.ycj.videolibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.listener.ExoPlayerListener;
import chuangyuan.ycj.videolibrary.listener.ExoPlayerViewListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;

/**
 * @author yangc
 *         date 2017/7/21
 *         E-Mail:yangchaojiang@outlook.com
 *         Deprecated: 视频播放video
 */

public class VideoPlayerView extends FrameLayout implements PlaybackControlView.VisibilityListener {
    public static final String TAG = VideoPlayerView.class.getName();
    /***活动窗口***/
    protected Activity activity;
    /***进度条控件***/
    private ExoDefaultTimeBar timeBar;
    /***全屏按钮***/
    private ImageButton exoFullscreen;
    /***播放view***/
    protected SimpleExoPlayerView playerView;
    /***视视频标题,清晰度切换,实时视频加载速度显示,控制进度布局***/
    protected TextView controlsTitleText, videoSwitchText, videoLoadingShowText, videoDialogProText;
    /***视频加载页,错误页,进度控件返回按钮***/
    private View exoLoadingLayout, exoPlayErrorLayout, exoControlsBack;
    /***播放结束，提示布局,调整进度布局,控制音频和亮度布局***/
    private View playReplayLayout, playBtnHintLayout, dialogProLayout, exoAudioBrightnessLayout;
    /***水印,封面图占位,显示音频和亮度布图***/
    private ImageView exoPlayWatermark, exoPreviewImage, videoAudioBrightnessImg;
    /***切换***/
    private BelowView belowView;
    /***显示音频和亮度***/
    private ProgressBar videoAudioBrightnessPro;
    private AlertDialog alertDialog;
    /***是否切换按钮***/
    private boolean isShowVideoSwitch;
    protected ExoPlayerListener mExoPlayerListener;
    /***是否列表播放 默认false***/
    private boolean isListPlayer;
    /***谁赢图是否在上面***/
    private boolean isPreViewTop;
    /***标题做表间距***/
    private int getPaddingLeft;
    /***回调接口实现类***/
    private ComponentListener componentListener = new ComponentListener();

    public VideoPlayerView(Context context) {
        super(context, null);
        activity = (Activity) context;
        intiView();
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity = (Activity) context;
        int userWatermark = 0;
        int replayId = 0;
        int errorId = 0;
        int playerHintId = 0;
        int defaultArtworkId = 0;
        int loadId = 0;
        int back = R.layout.simple_exo_back_view;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        playerView = new SimpleExoPlayerView(getContext(), attrs);
        addView(playerView, params);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VideoPlayerView, 0, 0);
            try {
                userWatermark = a.getResourceId(R.styleable.VideoPlayerView_user_watermark, 0);
                isListPlayer = a.getBoolean(R.styleable.VideoPlayerView_player_list, false);
                replayId = a.getResourceId(R.styleable.VideoPlayerView_player_replay_layout_id, replayId);
                errorId = a.getResourceId(R.styleable.VideoPlayerView_player_error_layout_id, errorId);
                playerHintId = a.getResourceId(R.styleable.VideoPlayerView_player_hint_layout_id, playerHintId);
                defaultArtworkId = a.getResourceId(R.styleable.VideoPlayerView_default_artwork, defaultArtworkId);
                loadId = a.getResourceId(R.styleable.VideoPlayerView_player_load_layout_id, loadId);
                if (replayId == 0) {
                    replayId = R.layout.simple_exo_play_replay;
                }
                if (errorId == 0) {
                    errorId = R.layout.simple_exo_play_error;
                }
                if (playerHintId == 0) {
                    playerHintId = R.layout.simple_exo_play_btn_hint;
                }
                if (loadId == 0) {
                    loadId = R.layout.simple_exo_play_load;
                }
            } finally {
                a.recycle();
            }
        }
        exoPlayErrorLayout = LayoutInflater.from(context).inflate(errorId, null);
        playReplayLayout = LayoutInflater.from(context).inflate(replayId, null);
        playBtnHintLayout = LayoutInflater.from(context).inflate(playerHintId, null);
        exoControlsBack = LayoutInflater.from(context).inflate(back, null);
        exoLoadingLayout = LayoutInflater.from(context).inflate(loadId, null);
        exoAudioBrightnessLayout = LayoutInflater.from(context).inflate(R.layout.simple_video_audio_brightness_dialog, null);
        dialogProLayout = LayoutInflater.from(context).inflate(R.layout.simple_exo_video_progress_dialog, null);
        intiView();
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

    private void intiView() {
        FrameLayout frameLayout = playerView.getContentFrameLayout();
        frameLayout.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.black));
        exoPlayErrorLayout.setVisibility(GONE);
        playReplayLayout.setVisibility(GONE);
        playBtnHintLayout.setVisibility(GONE);
        exoLoadingLayout.setVisibility(GONE);
        dialogProLayout.setVisibility(GONE);
        exoAudioBrightnessLayout.setVisibility(GONE);
        exoLoadingLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.simple_exo_color_33));
        frameLayout.addView(exoAudioBrightnessLayout, frameLayout.getChildCount());
        frameLayout.addView(dialogProLayout, frameLayout.getChildCount());
        frameLayout.addView(exoPlayErrorLayout, frameLayout.getChildCount());
        frameLayout.addView(playReplayLayout, frameLayout.getChildCount());
        frameLayout.addView(playBtnHintLayout, frameLayout.getChildCount());
        frameLayout.addView(exoLoadingLayout, frameLayout.getChildCount());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(VideoPlayUtils.dip2px(getContext(), 36f), VideoPlayUtils.dip2px(getContext(), 36f));
        frameLayout.addView(exoControlsBack, frameLayout.getChildCount(), layoutParams);
        exoPlayWatermark = (ImageView) playerView.findViewById(R.id.exo_player_watermark);
        controlsTitleText = (TextView) playerView.findViewById(R.id.exo_controls_title);
        videoLoadingShowText = (TextView) playerView.findViewById(R.id.exo_loading_show_text);
        videoSwitchText = (TextView) playerView.findViewById(R.id.exo_video_switch);
        timeBar = (ExoDefaultTimeBar) playerView.findViewById(R.id.exo_progress);
        videoAudioBrightnessImg = (ImageView) playerView.findViewById(R.id.exo_video_audio_brightness_img);
        videoAudioBrightnessPro = (ProgressBar) playerView.findViewById(R.id.exo_video_audio_brightness_pro);
        videoDialogProText = (TextView) playerView.findViewById(R.id.exo_video_dialog_pro_text);
        exoFullscreen = (ImageButton) findViewById(R.id.exo_video_fullscreen);
        if (playerView.findViewById(R.id.exo_player_replay_btn_id) != null) {
            playerView.findViewById(R.id.exo_player_replay_btn_id).setOnClickListener(componentListener);
        }
        if (playerView.findViewById(R.id.exo_player_error_btn_id) != null) {
            playerView.findViewById(R.id.exo_player_error_btn_id).setOnClickListener(componentListener);
        }
        if (playerView.findViewById(R.id.exo_player_btn_hint_btn_id) != null) {
            playerView.findViewById(R.id.exo_player_btn_hint_btn_id).setOnClickListener(componentListener);
        }
        if (playerView.findViewById(R.id.exo_preview_image) != null) {
            isPreViewTop = true;
            exoPreviewImage = (ImageView) playerView.findViewById(R.id.exo_preview_image);
        } else {
            isPreViewTop = false;
            exoPreviewImage = (ImageView) playerView.findViewById(R.id.exo_preview_image_bottom);
        }
        exoControlsBack.setOnClickListener(componentListener);
        playerView.findViewById(R.id.exo_video_fullscreen).setOnClickListener(componentListener);
        playerView.setControllerVisibilityListener(this);
        if (isListPlayer && !VideoPlayUtils.isLand(activity)) {
            exoControlsBack.setVisibility(GONE);
        }
    }

    /****
     * 获取控制类
     *
     * @return PlaybackControlView
     ***/
    @Nullable
    public PlaybackControlView getPlaybackControlView() {
        if (playerView != null) {
            return playerView.getUseControllerView();
        }
        return null;
    }

    /***
     * 销毁处理
     * **/
    public void onDestroy() {
        if (activity.isFinishing()) {
            activity = null;
            componentListener = null;
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if (belowView != null) {
            belowView = null;
        }
    }

    /***
     * 控制类显示隐藏
     ***/
    @Override
    public void onVisibilityChange(int visibility) {
        if (activity == null) {
            return;
        }
        showBackView(visibility);
        if (belowView != null && visibility == View.GONE) {
            belowView.dismissBelowView();
            if (exoPreviewImage != null) {
                exoPreviewImage.setVisibility(GONE);
            }
        }
    }

    /***
     * 设置是横屏,竖屏
     *
     * @param newConfig 旋转对象
     */
    private void doOnConfigurationChanged(int newConfig) {
        //横屏
        if (newConfig == Configuration.ORIENTATION_LANDSCAPE) {
            VideoPlayUtils.hideActionBar(activity);
            exoControlsBack.setVisibility(VISIBLE);
            this.setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            //判断是否开启多线路支持
            if (isShowVideoSwitch) {
                videoSwitchText.setVisibility(View.VISIBLE);
                videoSwitchText.setOnClickListener(componentListener);
            }
            if (isListPlayer()) {
                getPaddingLeft = controlsTitleText.getPaddingLeft();
                controlsTitleText.setPadding(VideoPlayUtils.dip2px(getContext(), 40), 0, 0, 0);
            }
        } else {//竖屏
            this.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE);
            VideoPlayUtils.showActionBar(activity);
            //多线路支持隐藏
            if (videoSwitchText != null) {
                videoSwitchText.setVisibility(View.GONE);
            }
            //列表播放
            if (isListPlayer()) {
                showBackView(GONE);
                controlsTitleText.setPadding(getPaddingLeft, 0, 0, 0);
            }
        }
        scaleLayout(newConfig);
    }

    @Override
    protected void onDetachedFromWindow() {
        boolean is = getPlay() != null && VideoPlayerManager.getInstance().getVideoPlayer() != null;
        if (is) {
            if (getPlay().toString().equals(VideoPlayerManager.getInstance().getVideoPlayer().toString())) {
                Log.d(TAG, "onDetachedFromWindow");
                VideoPlayerManager.getInstance().getVideoPlayer().reset();
            }
        }
        super.onDetachedFromWindow();
    }

    /***
     * 设置内容横竖屏内容
     *
     * @param newConfig 旋转对象
     */

    protected void scaleLayout(int newConfig) {
        //shiping
        if (newConfig == Configuration.ORIENTATION_PORTRAIT) {
            ViewGroup parent = (ViewGroup) playerView.getParent();
            if (parent != null) {
                parent.removeView(playerView);
            }
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
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
    private void showLoadState(int visibility) {
        if (visibility == View.VISIBLE) {
            playerView.hideController();
            showErrorState(View.GONE);
            showReplay(View.GONE);
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
    private void showErrorState(int visibility) {
        if (visibility == View.VISIBLE) {
            playerView.hideController();
            showLoadState(View.GONE);
            showReplay(View.GONE);
            showBackView(VISIBLE);
            playerView.setOnTouchListener(null);
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
    private void showReplay(int visibility) {
        if (visibility == View.VISIBLE) {
            playerView.hideController();
            showLoadState(View.GONE);
            showErrorState(View.GONE);
            showBtnContinueHint(GONE);
            showBackView(VISIBLE);
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
    private void showBackView(int visibility) {
        if (exoControlsBack != null) {
            if (isListPlayer() && !VideoPlayUtils.isLand(activity)) {
                exoControlsBack.setVisibility(GONE);
            } else {
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
            showBackView(VISIBLE);
            showLoadState(View.GONE);
            showReplay(View.GONE);
            showErrorState(View.GONE);
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
    private void showGesture(int visibility) {
        if (exoAudioBrightnessLayout != null) {
            exoAudioBrightnessLayout.setVisibility(visibility);
        }
        if (exoAudioBrightnessLayout != null) {
            dialogProLayout.setVisibility(visibility);
        }
    }

    /****
     * 监听返回键
     ***/
    private void exitFullView() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        exoFullscreen.setImageResource(R.drawable.ic_fullscreen_white);
        doOnConfigurationChanged(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /***
     * 显示网络提示框
     ***/
    private void showDialog() {
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
     * 是否属于列表播放
     *
     * @return boolean
     ***/
    public boolean isListPlayer() {
        return isListPlayer;
    }

    /**
     * 获取g播放控制类
     *
     * @return ExoUserPlayer
     **/
    @Nullable
    public ExoUserPlayer getPlay() {
        if (mExoPlayerListener != null) {
            return mExoPlayerListener.getPlay();
        }
        return null;
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

    /***
     * 获取监听事件
     *
     * @return ComponentListener
     ***/
    public ComponentListener getComponentListener() {
        return componentListener;
    }

    /**
     * 监听类
     **/
    private class ComponentListener implements ExoPlayerViewListener, View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.exo_video_fullscreen) {
                //横屏
                if (VideoPlayUtils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    exoFullscreen.setImageResource(R.drawable.ic_fullscreen_white);
                    doOnConfigurationChanged(Configuration.ORIENTATION_PORTRAIT);
                    //竖屏
                } else if (VideoPlayUtils.getOrientation(activity) == Configuration.ORIENTATION_PORTRAIT) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    exoFullscreen.setImageResource(R.drawable.ic_fullscreen_exit_white);
                    doOnConfigurationChanged(Configuration.ORIENTATION_LANDSCAPE);
                }
            } else if (v.getId() == R.id.exo_controls_back) {
                mExoPlayerListener.onBack();
            } else if (v.getId() == R.id.exo_player_error_btn_id) {
                if (VideoPlayUtils.isNetworkAvailable(activity)) {
                    showErrorStateView(View.GONE);
                    mExoPlayerListener.onCreatePlayers();
                } else {
                    Toast.makeText(activity, R.string.net_network_no_hint, Toast.LENGTH_SHORT).show();
                }
            } else if (v.getId() == R.id.exo_player_replay_btn_id) {

                if (VideoPlayUtils.isNetworkAvailable(activity)) {
                    showReplay(View.GONE);
                    mExoPlayerListener.replayPlayers();
                } else {
                    Toast.makeText(activity, R.string.net_network_no_hint, Toast.LENGTH_SHORT).show();
                }
                //切换
            } else if (v.getId() == R.id.exo_video_switch) {
                if (belowView == null) {
                    belowView = new BelowView(activity);
                    belowView.setOnItemClickListener(new BelowView.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, String name) {
                            belowView.dismissBelowView();
                            videoSwitchText.setText(name);
                            mExoPlayerListener.switchUri(position, name);
                        }
                    });
                }
                belowView.showBelowView(v, true);
                //提示播放
            } else if (v.getId() == R.id.exo_player_btn_hint_btn_id) {
                showBtnContinueHint(View.GONE);
                mExoPlayerListener.playVideoUri();
            }
        }

        @Override
        public void showAlertDialog() {
            showDialog();

        }

        @Override
        public void showHidePro(int visibility) {
            if (timeBar != null) {
                timeBar.setVisibility(visibility);
            }

        }

        @Override
        public void setWatermarkImage(int res) {
            if (exoPlayWatermark != null) {
                exoPlayWatermark.setImageResource(res);
            }
        }

        @Override
        public void showSwitchName(@NonNull String name) {
            videoSwitchText.setText(name);
        }

        @Override
        public void showLoadStateView(int visibility) {
            showLoadState(visibility);
        }

        @Override
        public void showReplayView(int visibility) {
            showReplay(visibility);
        }

        @Override
        public void showErrorStateView(int visibility) {
            showErrorState(visibility);
        }

        @Override
        public void setTitle(@NonNull String title) {
            controlsTitleText.setText(title);
        }

        @Override
        public void showNetSpeed(@NonNull final String netSpeed) {
            playerView.post(new Runnable() {
                @Override
                public void run() {
                    if (videoLoadingShowText != null) {
                        videoLoadingShowText.setText(netSpeed);
                    }
                }
            });
        }

        @Override
        public void onConfigurationChanged(int newConfig) {
            doOnConfigurationChanged(newConfig);
        }

        @Override
        public void showGestureView(int visibility) {
            showGesture(visibility);
        }

        @Override
        public void setTimePosition(@NonNull SpannableString seekTime) {
            if (dialogProLayout != null) {
                dialogProLayout.setVisibility(View.VISIBLE);
                videoDialogProText.setText(seekTime);
            }
        }

        @Override
        public void setVolumePosition(int mMaxVolume, int currIndex) {

            if (exoAudioBrightnessLayout != null) {
                exoAudioBrightnessLayout.setVisibility(View.VISIBLE);
                videoAudioBrightnessPro.setMax(mMaxVolume);
                videoAudioBrightnessPro.setProgress(currIndex);
                videoAudioBrightnessImg.setImageResource(currIndex == 0 ? R.drawable.ic_volume_off_white_48px : R.drawable.ic_volume_up_white_48px);
            }
        }

        @Override
        public void setBrightnessPosition(int mMaxVolume, int currIndex) {
            if (exoAudioBrightnessLayout != null) {
                if (!exoAudioBrightnessLayout.isShown()) {
                    exoAudioBrightnessLayout.setVisibility(View.VISIBLE);
                    videoAudioBrightnessPro.setMax(mMaxVolume);
                    videoAudioBrightnessImg.setImageResource(R.drawable.ic_brightness_6_white_48px);
                }
                videoAudioBrightnessPro.setProgress(currIndex);
            }
        }

        @Override
        public void next() {
            if (playerView.getUseControllerView() != null) {
                playerView.getUseControllerView().next();
            }
        }

        @Override
        public void previous() {
            if (playerView.getUseControllerView() != null) {
                playerView.getUseControllerView().previous();
            }
        }

        @Override
        public void hideController() {
            if (playerView != null) {
                playerView.hideController();
                playerView.setControllerHideOnTouch(false);
            }
        }

        @Override
        public void showControllerView() {
            if (playerView != null) {
                playerView.showController();
            }
        }

        @Override
        public void setControllerHideOnTouch(boolean onTouch) {
            if (playerView != null) {
                playerView.setControllerHideOnTouch(onTouch);
            }
        }

        @Override
        public void showPreview(int visibility) {
            getPreviewImage().setVisibility(visibility);
        }

        @Override
        public void setPlayerBtnOnTouchListener(OnTouchListener listener) {
            if (playerView != null) {
                playerView.getUseControllerView().getPlayButton().setOnTouchListener(listener);
            }
        }

        @Override
        public void reset() {
            if (playReplayLayout != null) {
                playReplayLayout.setVisibility(GONE);
            }
            if (exoLoadingLayout != null) {
                exoLoadingLayout.setVisibility(GONE);
            }
            if (exoPlayErrorLayout != null) {
                exoPlayErrorLayout.setVisibility(GONE);
            }
            if (playBtnHintLayout != null) {
                playBtnHintLayout.setVisibility(GONE);
            }
            onDestroy();
            if (getPlaybackControlView() != null) {
                getPlaybackControlView().showNo();

            }
            playerView.setOnTouchListener(null);

        }

        @Override
        public int getHeight() {
            return playerView.getHeight();
        }

        @Override
        public void setPlatViewOnTouchListener(OnTouchListener listener) {
            playerView.setOnTouchListener(listener);
        }

        @Override
        public void setShowWitch(boolean showVideoSwitch) {
            setShowVideoSwitch(showVideoSwitch);
        }

        @Override
        public void setSeekBarOpenSeek(boolean isOpenSeek) {
            getTimeBar().setOpenSeek(isOpenSeek);
        }

        @Override
        public boolean isList() {
            return isListPlayer();
        }

        @Override
        public void setPlayer(@NonNull SimpleExoPlayer player) {
            if (null != playerView) {
                playerView.setPlayer(player);
            }
        }

        @Override
        public boolean isLoadingShow() {
            return isLoadingLayoutShow();
        }

        @Override
        public ExoDefaultTimeBar getTimeBarView() {
            return getTimeBar();
        }

        @Override
        public void exitFull() {
            exitFullView();
        }

        @Override
        public void destroy() {
            onDestroy();
        }

    }
}
