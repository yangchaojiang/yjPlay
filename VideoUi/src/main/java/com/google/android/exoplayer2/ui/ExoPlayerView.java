package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.util.Assertions;

/**
 * author  yangc
 * date 2018/7/17
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
public class ExoPlayerView extends  PlayerView{
    public ExoPlayerView(Context context) {
        this(context,null);
    }

    public ExoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ExoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public     PlayerControlView getControllerView() {
        return controller;
    }
    public FrameLayout getContentFrameLayout() {
        return contentFrameLayout;
    }
    public AspectRatioFrameLayout getAspectRatioFrameLayout() {
        return contentFrame;
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
