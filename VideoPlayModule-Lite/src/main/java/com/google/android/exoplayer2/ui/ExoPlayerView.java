package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.render.IRender;
import com.google.android.exoplayer2.ui.spherical.SphericalGLSurfaceView;
import com.google.android.exoplayer2.util.Assertions;

/**
 * author  yangc
 * date 2018/7/17
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
public class ExoPlayerView extends PlayerView {
    private static final String TAG = ExoPlayerView.class.getName();

    /**
     * Instantiates a new Exo player view.
     *
     * @param context the context
     */
    public ExoPlayerView(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Exo player view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ExoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Exo player view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public ExoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * Gets controller view.
     *
     * @return the controller view
     */
    public PlayerControlView getControllerView() {
        return controller;
    }

    /**
     * Gets content frame layout.
     *
     * @return the content frame layout
     */
    public FrameLayout getContentFrameLayout() {
        return contentFrameLayout;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!useController || player == null || ev.getActionMasked() != MotionEvent.ACTION_DOWN) {
            return false;
        }
        if (!controllerHideOnTouch) {
            return false;
        } else if (!controller.isVisible()) {
            controller.setInAnim();
            maybeShowController(true);
        } else if (controllerHideOnTouch) {
            controller.setOutAnim();
        }
        return true;
    }
}



