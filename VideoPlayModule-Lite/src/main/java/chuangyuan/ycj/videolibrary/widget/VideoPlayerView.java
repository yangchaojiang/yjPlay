package chuangyuan.ycj.videolibrary.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AnimUtils;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Assertions;

import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.listener.ExoPlayerViewListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;

/**
 * author yangc
 * date 2017/7/21
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 视频播放video
 */
@TargetApi(16)
public final class VideoPlayerView extends BaseView implements PlaybackControlView.VisibilityListener {

    private final Runnable hideAction = new Runnable() {
        @Override
        public void run() {
            if (isLand) {
                if (exoPlayLockLayout.getVisibility() == VISIBLE) {
                    AnimUtils.setOutAnimX(lockCheckBox, false).start();
                } else {
                    AnimUtils.setInAnimX(lockCheckBox).start();
                }
            }
        }
    };
    private final Runnable hideNavigationAction = new Runnable() {
        @Override
        public void run() {
            if (isLand) {
                setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };


    public VideoPlayerView(Context context) {
        this(context, null);
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity = (Activity) context;
        int userWatermark = 0;
        int replayId = R.layout.simple_exo_play_replay;
        int errorId = R.layout.simple_exo_play_error;
        int playerHintId = R.layout.simple_exo_play_btn_hint;
        int defaultArtworkId = 0;
        int loadId = R.layout.simple_exo_play_load;
        int videoProgressId = R.layout.simple_exo_video_progress_dialog;
        int audioId = R.layout.simple_video_audio_brightness_dialog;
        int brightnessId = R.layout.simple_video_audio_brightness_dialog;
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
                audioId = a.getResourceId(R.styleable.VideoPlayerView_player_gesture_audio_layout_id, audioId);
                videoProgressId = a.getResourceId(R.styleable.VideoPlayerView_player_gesture_progress_layout_id, videoProgressId);
                brightnessId = a.getResourceId(R.styleable.VideoPlayerView_player_gesture_bright_layout_id, brightnessId);
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
        intiView();
        intiGestureView(audioId, brightnessId, videoProgressId);
        intiClickView();
        initWatermark(userWatermark, defaultArtworkId);
    }

    /***
     * 初始化点击事件
     * **/
    private void intiClickView() {
        if (playerView.findViewById(R.id.exo_player_replay_btn_id) != null) {
            playerView.findViewById(R.id.exo_player_replay_btn_id).setOnClickListener(onClickListener);
        }
        if (playerView.findViewById(R.id.exo_player_error_btn_id) != null) {
            playerView.findViewById(R.id.exo_player_error_btn_id).setOnClickListener(onClickListener);
        }
        if (playerView.findViewById(R.id.exo_player_btn_hint_btn_id) != null) {
            playerView.findViewById(R.id.exo_player_btn_hint_btn_id).setOnClickListener(onClickListener);
        }
        exoControlsBack.setOnClickListener(onClickListener);
        videoSwitchText.setOnClickListener(onClickListener);
        playerView.findViewById(R.id.exo_video_fullscreen).setOnClickListener(onClickListener);
        if (isListPlayer && !isLand) {
            exoControlsBack.setVisibility(GONE);
        }
        if (lockCheckBox != null) {
            lockCheckBox.setOnClickListener(onClickListener);
            lockCheckBox.setVisibility(isOpenLock ? VISIBLE : GONE);
        }
        if (isPreViewTop) {
            showFullscreenView(GONE);
        }
        this.setOnSystemUiVisibilityChangeListener(uiVisibilityChangeListener);
        playerView.setControllerVisibilityListener(this);
        playerView.getUserControllerView().setAnimatorListener(animatorListener);
    }

    /***
     * 销毁处理
     * **/
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
        if (hideNavigationAction != null) {
            removeCallbacks(hideNavigationAction);
        }
        if (hideAction != null) {
            removeCallbacks(hideAction);
        }
        if (exoFullscreen != null && exoFullscreen.animate() != null) {
            exoFullscreen.animate().cancel();
        }

        if (exoControlsBack != null && exoControlsBack.animate() != null) {
            exoControlsBack.animate().cancel();
        }
        if (lockCheckBox != null && lockCheckBox.animate() != null) {
            lockCheckBox.animate().cancel();
        }
        if (activity != null && activity.isFinishing()) {
            removeAllViews();
            animatorListener = null;
            activity = null;
            exoPlayerViewListener = null;
            onClickListener = null;
            uiVisibilityChangeListener = null;
        }

    }

    /***
     * 控制类显示隐藏
     ***/
    @Override
    public void onVisibilityChange(int visibility) {
        if (activity == null) return;
        showBackView(visibility);
        showFullscreenView(visibility);
        showLockState(visibility);
        if (belowView != null && visibility == View.GONE) {
            belowView.dismissBelowView();
        }


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ManualPlayer manualPlayer = VideoPlayerManager.getInstance().getVideoPlayer();
        boolean is = isListPlayer && getPlay() != null && manualPlayer != null;
        if (is) {
            if (getPlay().toString().equals(manualPlayer.toString())) {
                manualPlayer.reset(true);
            }
        } else {
            onDestroy();
        }
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
                if (videoSwitchText.getText().toString().isEmpty() && mExoPlayerListener != null) {
                    videoSwitchText.setText(mExoPlayerListener.getSwitchName());
                }
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

    /****
     * 监听返回键
     ***/
    private void exitFullView() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        exoFullscreen.setChecked(false);
        doOnConfigurationChanged(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /****
     * 动画监听
     ***/
    private AnimUtils.AnimatorListener animatorListener = new AnimUtils.AnimatorListener() {
        @Override
        public void show(boolean isIn) {
            if (isIn) {
                if (isLand) {
                    showLockState(VISIBLE);
                    AnimUtils.setInAnimX(lockCheckBox).start();
                }
                AnimUtils.setInAnim(exoFullscreen).start();
                AnimUtils.setInAnim(exoControlsBack).start();
            } else {
                if (isLand) {
                    if (lockCheckBox.getTag() == null) {
                        AnimUtils.setOutAnimX(lockCheckBox, false).start();
                    } else {
                        lockCheckBox.setTag(null);
                    }
                }
                AnimUtils.setOutAnim(exoControlsBack, false).start();
                AnimUtils.setOutAnim(exoFullscreen, true).start();
            }
        }
    };
    /***
     * 导航虚拟监听
     ***/
    private OnSystemUiVisibilityChangeListener uiVisibilityChangeListener = new OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
            removeCallbacks(hideNavigationAction);
            if (visibility == VISIBLE && playerView != null) {
                postDelayed(hideNavigationAction, 4000);
            }
        }
    };

    /***
     * 获取监听事件
     * @return ComponentListener
     ***/
    public ExoPlayerViewListener getComponentListener() {
        return exoPlayerViewListener;
    }

    /***
     * 点击事件监听
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            if (v.getId() == R.id.exo_video_fullscreen) {
                //切竖屏
                if (VideoPlayUtils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    doOnConfigurationChanged(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    //切横屏
                } else if (VideoPlayUtils.getOrientation(activity) == Configuration.ORIENTATION_PORTRAIT) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    doOnConfigurationChanged(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            } else if (v.getId() == R.id.exo_controls_back) {
                mExoPlayerListener.onBack();
            } else if (v.getId() == R.id.exo_player_error_btn_id) {
                if (VideoPlayUtils.isNetworkAvailable(activity)) {
                    showErrorState(View.GONE);
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
                    belowView = new BelowView(activity, mExoPlayerListener.getSwitchList());
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
            } else if (v.getId() == R.id.exo_player_lock_btn_id) {
                removeCallbacks(hideAction);
                lockCheckBox.setTag(true);
                if (lockCheckBox.isChecked()) {
                    playerView.getUserControllerView().setOutAnim();
                    boolean shouldShowIndefinitely = playerView.shouldShowControllerIndefinitely();
                    if (!shouldShowIndefinitely) {
                        postDelayed(hideAction, playerView.getControllerShowTimeoutMs());
                    }
                } else {
                    lockCheckBox.setTag(null);
                    playerView.showController();
                    playerView.getUserControllerView().setInAnim();
                }
            }
        }
    };

    /**
     * 控制类监听类
     **/
    private ExoPlayerViewListener exoPlayerViewListener = new ExoPlayerViewListener() {

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
            if (exoAudioLayout != null) {
                if (exoAudioLayout.getVisibility() != VISIBLE) {
                    videoAudioPro.setMax(mMaxVolume);
                }
                exoAudioLayout.setVisibility(View.VISIBLE);
                videoAudioPro.setProgress(currIndex);

                videoAudioImg.setImageResource(currIndex == 0 ? R.drawable.ic_volume_off_white_48px : R.drawable.ic_volume_up_white_48px);
            }
        }

        @Override
        public void setBrightnessPosition(int mMaxVolume, int currIndex) {
            if (exoBrightnessLayout != null) {
                if (exoBrightnessLayout.getVisibility() != VISIBLE) {
                    videoBrightnessPro.setMax(mMaxVolume);
                    videoBrightnessImg.setImageResource(R.drawable.ic_brightness_6_white_48px);
                }
                exoBrightnessLayout.setVisibility(View.VISIBLE);
                videoBrightnessPro.setProgress(currIndex);
            }
        }

        @Override
        public void next() {
            if (playerView.getUserControllerView() != null) {
                playerView.getUserControllerView().next();
            }
        }

        @Override
        public void previous() {
            if (playerView.getUserControllerView() != null) {
                playerView.getUserControllerView().previous();
            }
        }

        @Override
        public void hideController(boolean isShowFulls) {
            if (playerView != null) {
                playerView.hideController();
                if (isShowFulls) {
                    showFullscreenView(VISIBLE);
                }
                setControllerHideOnTouch(false);
            }
        }

        @Override
        public void showControllerView() {
            if (playerView != null) {
                playerView.showController();
                setControllerHideOnTouch(true);
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
                playerView.getUserControllerView().getPlayButton().setOnTouchListener(listener);
            }
        }

        @Override
        public void reset() {
            removeCallbacks(hideNavigationAction);
            removeCallbacks(hideAction);
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
            if (getPlaybackControlView() != null) {
                getPlaybackControlView().hideNo();
                getPlaybackControlView().showNo();
                showPreview(VISIBLE);
                showFullscreenView(GONE);
            }

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
        public boolean isLock() {
            return null != lockCheckBox && lockCheckBox.isChecked();

        }
    };
}
