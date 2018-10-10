package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.render.IRender;
import com.google.android.exoplayer2.render.RenderTextureView;

/**
 * author  yangc
 * date 2018/7/17
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
public class ExoPlayerView extends PlayerView {
    private static final String TAG = ExoPlayerView.class.getName();
    private IRender.IRenderHolder mRenderHolder;

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

    @Override
    public void setPlayer(Player player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeListener(componentListener);
            Player.VideoComponent oldVideoComponent = this.player.getVideoComponent();
            if (oldVideoComponent != null) {
                oldVideoComponent.removeVideoListener(componentListener);
            }
            Player.TextComponent oldTextComponent = this.player.getTextComponent();
            if (oldTextComponent != null) {
                oldTextComponent.removeTextOutput(componentListener);
            }
        }
        this.player = player;
        if (useController) {
            controller.setPlayer(player);
        }
        if (subtitleView != null) {
            subtitleView.setCues(null);
        }
        updateForCurrentTrackSelections(/* isNewPlayer= */ true);
        if (player != null) {
            Player.VideoComponent newVideoComponent = player.getVideoComponent();
            if (newVideoComponent != null) {
                newVideoComponent.addVideoListener(componentListener);
            }
            Player.TextComponent newTextComponent = player.getTextComponent();
            if (newTextComponent != null) {
                newTextComponent.addTextOutput(componentListener);
            }
            player.addListener(componentListener);
            maybeShowController(false);
            updateForCurrentTrackSelections(false);
            if (surfaceView.getRenderView() instanceof RenderTextureView) {
                ((RenderTextureView) surfaceView).setTakeOverSurfaceTexture(true);
            }
            if (mRenderHolder!=null){
                bindRenderHolder(mRenderHolder);
            }
            surfaceView.setRenderCallback(mRenderCallback);
        } else {
            hideController();
            hideArtwork();
        }

    }

    private void bindRenderHolder(IRender.IRenderHolder renderHolder) {
        if (renderHolder != null) {
            renderHolder.bindPlayer((SimpleExoPlayer) player);
        }
    }

    private final IRender.IRenderCallback mRenderCallback = new IRender.IRenderCallback() {
        @Override
        public void onSurfaceCreated(IRender.IRenderHolder renderHolder, int width, int height) {
            Log.d(TAG, "onSurfaceCreated : width = " + width + ", height = " + height);
            //on surface create ,try to attach player.
            if(mRenderHolder==null){
                mRenderHolder = renderHolder;
                bindRenderHolder(mRenderHolder);
            }
        }

        @Override
        public void onSurfaceChanged(IRender.IRenderHolder renderHolder,
                                     int format, int width, int height) {
            //not handle some...
        }

        @Override
        public void onSurfaceDestroy(IRender.IRenderHolder renderHolder) {
            Log.d(TAG, "onSurfaceDestroy..."+surfaceView.toString());
            //on surface destroy detach player
            mRenderHolder = null;
        }
    };
}
