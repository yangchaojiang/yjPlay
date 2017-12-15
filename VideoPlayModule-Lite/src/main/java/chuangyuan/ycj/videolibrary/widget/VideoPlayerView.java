package chuangyuan.ycj.videolibrary.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AnimUtils;
import com.google.android.exoplayer2.ui.PlaybackControlView;

import java.util.List;

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
public final class VideoPlayerView extends BaseView {

    /**
     * Instantiates a new Video player view.
     *
     * @param context the context
     */
    public VideoPlayerView(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Video player view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public VideoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Video player view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intiClickView();
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
        getSwitchText().setOnClickListener(onClickListener);
        exoControlsBack.setOnClickListener(onClickListener);
        playerView.findViewById(R.id.exo_video_fullscreen).setOnClickListener(onClickListener);
        if (isListPlayer && !isLand) {
            exoControlsBack.setVisibility(GONE);
        }
        if (lockCheckBox != null) {
            lockCheckBox.setOnClickListener(onClickListener);
            lockCheckBox.setVisibility(isOpenLock ? VISIBLE : GONE);
        }
        this.setOnSystemUiVisibilityChangeListener(uiVisibilityChangeListener);
        playerView.setControllerVisibilityListener(visibilityListener);
        playerView.getControllerView().setAnimatorListener(animatorListener);
        playerView.getControllerView().setUpdateProgressListener(new AnimUtils.UpdateProgressListener() {
            @Override
            public void updateProgress(long position, long bufferedPosition, long duration) {
                if (exoPlayerLockProgress != null && isLand && lockCheckBox.isChecked()) {
                    exoPlayerLockProgress.setPosition(position);
                    exoPlayerLockProgress.setBufferedPosition(bufferedPosition);
                    exoPlayerLockProgress.setDuration(duration);
                }
            }
        });
    }

    /***
     * 销毁处理
     * **/
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hideNavigationAction != null) {
            removeCallbacks(hideNavigationAction);
        }
        if (hideAction != null) {
            removeCallbacks(hideAction);
        }
        if (activity != null && activity.isFinishing()) {
            removeAllViews();
            animatorListener = null;
            exoPlayerViewListener = null;
            onClickListener = null;
            uiVisibilityChangeListener = null;
            onItemClickListener = null;
            visibilityListener = null;
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
        //横屏
        if (newConfig == Configuration.ORIENTATION_LANDSCAPE) {
            if (isLand) {
                return;
            }
            isLand = true;
            VideoPlayUtils.hideActionBar(activity);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            //判断是否开启多线路支持
            if (isShowVideoSwitch) {
                TextView switchText = getSwitchText();
                switchText.setVisibility(VISIBLE);
                if (switchText.getText().toString().isEmpty() && !getNameSwitch().isEmpty()) {
                    switchText.setText(getNameSwitch().get(switchIndex));
                }
            }
            lockCheckBox.setChecked(false);
            //列表显示
            showListBack(VISIBLE);
            //显示锁屏按钮
            showLockState(VISIBLE);
            //显更改全屏按钮选中，自动旋转屏幕
            getExoFullscreen().setChecked(true);
        } else {//竖屏
            if (!isLand) {
                return;
            }
            isLand = false;
            activity.getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE);
            VideoPlayUtils.showActionBar(activity);
            //多线路支持隐藏
            getSwitchText().setVisibility(GONE);
            //列表播放
            showListBack(GONE);
            //隐藏锁屏按钮移除
            showLockState(GONE);
            //更改全屏按钮选中，自动旋转屏幕
            getExoFullscreen().setChecked(false);
        }
        scaleLayout(newConfig);

    }

    /***
     * 列表显示返回按钮
     * @param visibility visibility
     * **/
    private void showListBack(int visibility) {
        if (isListPlayer()) {
            if (visibility == VISIBLE) {
                exoControlsBack.setVisibility(VISIBLE);
                getPaddingLeft = playerView.getControllerView().getTitleText().getPaddingLeft();
                playerView.getControllerView().getTitleText().
                        setPadding(VideoPlayUtils.dip2px(getContext(), 35), 0, 0, 0);
            } else {
                playerView.getControllerView().getTitleText().setPadding(getPaddingLeft, 0, 0, 0);
            }
            showBackView(visibility, false);
        }
    }

    /***
     * 获取监听事件
     * @return ComponentListener component listener
     */
    public ExoPlayerViewListener getComponentListener() {
        return exoPlayerViewListener;
    }

    /****
     * 监听返回键
     ***/
    private void exitFullView() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getExoFullscreen().setChecked(false);
        doOnConfigurationChanged(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /***
     * 显示隐藏全屏按钮
     *
     * @param visibility 状态
     */
    public void showFullscreenTempView(int visibility) {
        AppCompatCheckBox compatCheckBox = (AppCompatCheckBox) playerView.findViewById(R.id.sexo_video_fullscreen);
        compatCheckBox.setVisibility(visibility);
        compatCheckBox.setButtonDrawable(playerView.getControllerView().getIcFullscreenSelector());
        compatCheckBox.setOnClickListener(onClickListener);
    }

    /***
     * 锁屏按钮显示隐藏
     * **/
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
    /***
     * 虚拟导航键显示和隐藏
     * **/
    private final Runnable hideNavigationAction = new Runnable() {
        @Override
        public void run() {
            if (isLand) {
                setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
    /****
     * 控制类显示隐藏监听
     ***/
    private PlaybackControlView.VisibilityListener visibilityListener = new PlaybackControlView.VisibilityListener() {
        @Override
        public void onVisibilityChange(int visibility) {
            if (activity == null) return;
            showBackView(visibility, false);
            showLockState(visibility);
            if (belowView != null && visibility == View.GONE) {
                belowView.dismissBelowView();
            }
        }
    };
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
     * 点击事件监听
     */
    private  View.OnClickListener onClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            if (v.getId() == R.id.exo_video_fullscreen || v.getId() == R.id.sexo_video_fullscreen) {
                //切竖屏
                if (VideoPlayUtils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE) {
                    doOnConfigurationChanged(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    //切横屏
                } else if (VideoPlayUtils.getOrientation(activity) == Configuration.ORIENTATION_PORTRAIT) {
                    doOnConfigurationChanged(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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
                    showBottomView(GONE, null);
                    mExoPlayerListener.replayPlayers();
                } else {
                    Toast.makeText(activity, R.string.net_network_no_hint, Toast.LENGTH_SHORT).show();
                }
                //切换
            } else if (v.getId() == R.id.exo_video_switch) {
                if (belowView == null) {
                    belowView = new BelowView(activity, getNameSwitch());
                    belowView.setOnItemClickListener(new BelowView.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, String name) {
                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(position, name);
                            } else {
                                mExoPlayerListener.switchUri(position);
                            }
                            getSwitchText().setText(name);
                            belowView.dismissBelowView();
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
                    playerView.getControllerView().setOutAnim();
                    boolean shouldShowIndefinitely = playerView.shouldShowControllerIndefinitely();
                    if (!shouldShowIndefinitely) {
                        postDelayed(hideAction, playerView.getControllerShowTimeoutMs());
                    }
                } else {
                    lockCheckBox.setTag(null);
                    playerView.showController();
                    playerView.getControllerView().setInAnim();
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
            getTimeBar().setVisibility(visibility);
        }

        @Override
        public void setWatermarkImage(int res) {
            if (exoPlayWatermark != null) {
                exoPlayWatermark.setImageResource(res);
            }
        }

        @Override
        public void showSwitchName(@NonNull String name) {
            getSwitchText().setText(name);
        }

        @Override
        public void showLoadStateView(int visibility) {
            showLoadState(visibility);
        }

        @Override
        public void showReplayView(int visibility) {
            if (playerView != null && playerView.getVideoSurfaceView() instanceof TextureView) {
                TextureView surfaceView = (TextureView) playerView.getVideoSurfaceView();
                showBottomView(VISIBLE, surfaceView.getBitmap());
            }
            showReplay(visibility);
        }

        @Override
        public void showErrorStateView(int visibility) {
            showErrorState(visibility);
        }

        @Override
        public void setTitles(@NonNull String title) {
            setTitle(title);
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
            if (playerView.getControllerView() != null) {
                playerView.getControllerView().next();
            }
        }

        @Override
        public void previous() {
            if (playerView.getControllerView() != null) {
                playerView.getControllerView().previous();
            }
        }

        @Override
        public void hideController(boolean isShowFulls) {
            if (playerView != null) {
                playerView.hideController();
                if (isShowFulls) {
                    showFullscreenTempView(VISIBLE);
                }
                setControllerHideOnTouch(false);
            }
        }

        @Override
        public void showController(boolean isShowFulls) {
            if (isShowFulls) {
                showFullscreenTempView(GONE);
            }
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
            showPreViewLayout(visibility);
            showBottomView(GONE, null);
        }

        @Override
        public void setPlayerBtnOnTouch(OnTouchListener listener) {
            if (playerView != null) {
                playerView.getControllerView().getPlayButton().setOnTouchListener(listener);
                if (playerView.findViewById(R.id.exo_preview_play) != null) {
                    playerView.findViewById(R.id.exo_preview_play).setOnTouchListener(listener);
                }
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
            }
            showPreViewLayout(VISIBLE);

        }

        @Override
        public int getHeight() {
            return playerView == null ? 0 : playerView.getHeight();
        }

        @Override
        public void setPlatViewOnTouchListener(OnTouchListener listener) {
            if (playerView != null) {
                playerView.setOnTouchListener(listener);
            }
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
        public void setOpenSeek(boolean openSeek) {
            getTimeBar().setOpenSeek(openSeek);
        }

        @Override
        public void exitFull() {
            exitFullView();
        }

        @Override
        public boolean isLock() {
            return null != lockCheckBox && lockCheckBox.isChecked();

        }

        @Override
        public void setSwitchName(@NonNull List<String> name, int switchIndex) {
            VideoPlayerView.this.setSwitchName(name, switchIndex);
        }

    };
}
