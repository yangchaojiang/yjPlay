package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.C;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * author  yangc
 * date 2018/7/17
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
public class ExoPlayerControlView extends PlayerControlView {
    /******自己定义方法hide*/
    @DrawableRes
    int icFullscreenSelector = R.drawable.ic_fullscreen_selector;
    private final AppCompatCheckBox exoFullscreen;
    private final TextView videoSwitchText;
    private final TextView controlsTitleText;
    private final View exoControllerBottom;
    private View exoControllerTop;
    private AnimUtils.AnimatorListener animatorListener;
    private final CopyOnWriteArraySet<AnimUtils.UpdateProgressListener> listenerCopyOnWriteArraySet;

    /**
     * Instantiates a new Exo player control view.
     *
     * @param context the context
     */
    public ExoPlayerControlView(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Exo player control view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ExoPlayerControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Exo player control view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public ExoPlayerControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, attrs);
    }

    /**
     * Instantiates a new Exo player control view.
     *
     * @param context       the context
     * @param attrs         the attrs
     * @param defStyleAttr  the def style attr
     * @param playbackAttrs the playback attrs
     */
    public ExoPlayerControlView(Context context, AttributeSet attrs, int defStyleAttr, AttributeSet playbackAttrs) {
        super(context, attrs, defStyleAttr, playbackAttrs);
        listenerCopyOnWriteArraySet=new CopyOnWriteArraySet<>();
        if (playbackAttrs != null) {
            TypedArray a =
                    context
                            .getTheme()
                            .obtainStyledAttributes(playbackAttrs, R.styleable.PlayerControlView, 0, 0);
            try {
                icFullscreenSelector = a.getResourceId(R.styleable.PlayerControlView_player_fullscreen_image_selector, icFullscreenSelector);
            } finally {
                a.recycle();
            }
        }
        /*我控件布局*/
        exoFullscreen = findViewById(R.id.exo_video_fullscreen);
        videoSwitchText = findViewById(R.id.exo_video_switch);
        controlsTitleText = findViewById(R.id.exo_controls_title);
        exoControllerBottom = findViewById(R.id.exo_controller_bottom);
        exoControllerTop = findViewById(R.id.exo_controller_top);
        if (exoControllerTop == null) {
            exoControllerTop = controlsTitleText;
        }
        if (exoFullscreen != null) {
            exoFullscreen.setButtonDrawable(icFullscreenSelector);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
        removeCallbacks(updateProgressAction);
        removeCallbacks(hideAction);
        releaseAnim();
    }

    @Override
    public void hide() {
        if (isVisible()) {
            setVisibility(GONE);
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            removeCallbacks(hideAction);
            hideAtMs = C.TIME_UNSET;
        }
    }

    /**
     * 设置标题
     *
     * @param title 内容
     */
    public void setTitle(@NonNull String title) {
        controlsTitleText.setText(title);
    }

    /**
     * Hides the controller.
     */
    public void hideNo() {
        if (isVisible()) {
            setVisibility(GONE);
            removeCallbacks(updateProgressAction);
            removeCallbacks(hideAction);
            hideAtMs = C.TIME_UNSET;
        }
    }

    /**
     * Show no.
     */
    public void showNo() {
        updateAll();
        requestPlayPauseFocus();
        controlDispatcher.dispatchSetPlayWhenReady(player, false);
        removeCallbacks(updateProgressAction);
        removeCallbacks(hideAction);
        controlsTitleText.setAlpha(1f);
        controlsTitleText.setTranslationY(0);
        if (!isVisible()) {
            setVisibility(VISIBLE);
        }
    }

    /**
     * Gets play button.
     *
     * @return the play button
     */
    public View getPlayButton() {
        return playButton;
    }

    /**
     * Gets exo fullscreen.
     *
     * @return the exo fullscreen
     */
    public AppCompatCheckBox getExoFullscreen() {
        return exoFullscreen;
    }

    /**
     * Gets switch text.
     *
     * @return the switch text
     */
    public TextView getSwitchText() {
        return videoSwitchText;
    }

    /**
     * Gets exo controller top.
     *
     * @return the exo controller top
     */
    public View getExoControllerTop() {
        return exoControllerTop;
    }

    /**
     * Gets time bar.
     *
     * @return the time bar
     */
    public TimeBar getTimeBar() {
        return timeBar;
    }

    /**
     * 设置全屏按钮样式
     *
     * @param icFullscreenStyle 全屏按钮样式
     */
    public void setFullscreenStyle(@DrawableRes int icFullscreenStyle) {
        this.icFullscreenSelector = icFullscreenStyle;
        if (getExoFullscreen() != null) {
            getExoFullscreen().setButtonDrawable(icFullscreenStyle);
        }
    }

    /**
     * Gets ic fullscreen selector.
     *
     * @return the ic fullscreen selector
     */
    public int getIcFullscreenSelector() {
        return icFullscreenSelector;
    }


    /**
     * Release anim.
     */
    public void releaseAnim() {
        if (exoControllerTop != null && exoControllerTop.animate() != null) {
            exoControllerTop.animate().cancel();
        }
        if (exoControllerBottom != null && exoControllerBottom.animate() != null) {
            exoControllerBottom.animate().cancel();
        }
        listenerCopyOnWriteArraySet.clear();
    }

    /**
     * 设置移动谈出动画
     **/
    @Override
    public void setOutAnim() {
        if (controlsTitleText != null && exoControllerBottom != null) {
            if (animatorListener != null) {
                animatorListener.show(false);
            }
            AnimUtils.setOutAnim(exoControllerBottom, true).start();
            AnimUtils.setOutAnim(exoControllerTop, false)
                    .setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            if (view != null) {
                                hide();
                            }
                        }

                        @Override
                        public void onAnimationCancel(View view) {
                        }
                    })
                    .start();
        } else {
            hide();
        }
    }

    @Override
    protected void updateProgress(long position, long bufferedPosition, long duration) {
        super.updateProgress(position, bufferedPosition, duration);
        for (AnimUtils.UpdateProgressListener updateProgressListener : listenerCopyOnWriteArraySet) {
            updateProgressListener.updateProgress(position, bufferedPosition, duration);
        }
    }

    /**
     * 设置移动谈入动画
     **/
    public void setInAnim() {
        if (controlsTitleText != null && exoControllerBottom != null) {
            if (animatorListener != null) {
                animatorListener.show(true);
            }
            AnimUtils.setInAnim(exoControllerTop).setListener(null).start();
            AnimUtils.setInAnim(exoControllerBottom).start();
        }
    }

    /***
     * 设置动画回调
     * @param animatorListener animatorListener
     */
    public void setAnimatorListener(AnimUtils.AnimatorListener animatorListener) {
        this.animatorListener = animatorListener;
    }

    /***
     * 设置进度回调
     * @param updateProgressListener updateProgressListener
     */
    public void addUpdateProgressListener(@NonNull AnimUtils.UpdateProgressListener updateProgressListener) {
        listenerCopyOnWriteArraySet.add(updateProgressListener);
    }

    /***
     * 移除设置进度回调
     * @param updateProgressListener updateProgressListener
     */
    public void removeUpdateProgressListener(@NonNull AnimUtils.UpdateProgressListener updateProgressListener) {
        listenerCopyOnWriteArraySet.remove(updateProgressListener);
    }

}
