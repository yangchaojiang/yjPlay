package chuangyuan.ycj.videolibrary.widget2;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.render.AspectRatio;
import com.google.android.exoplayer2.render.IRender;
import com.google.android.exoplayer2.render.RenderSurfaceView;
import com.google.android.exoplayer2.render.RenderTextureView;
import com.google.android.exoplayer2.ui.ExoPlayerControlView;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.util.ErrorMessageProvider;

import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.widget.ActionControlView;
import chuangyuan.ycj.videolibrary.widget.GestureControlView;
import chuangyuan.ycj.videolibrary.widget.LockControlView;


/**
 * author  yangc
 * date 2018/7/17
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 测试
 */
  class ExoPlayerView2 extends FrameLayout {
    private static final String TAG = ExoPlayerView2.class.getName();
    private static final int SURFACE_TYPE_NONE = 0;
    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;
    private final FrameLayout contentFrame;
    private final View shutterView;
    protected final IRender surfaceView;
    private final ImageView artworkView;
    protected final SubtitleView subtitleView;
    private final @Nullable
    View bufferingView;
    private final @Nullable
    TextView errorMessageView;
    protected final PlayerControlView controller;
    protected Player player;
    protected boolean useController;
    private boolean useArtwork;
    private Bitmap defaultArtwork;
    private boolean showBuffering;
    private boolean keepContentOnPlayerReset;
    private @Nullable
    ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider;
    private @Nullable
    CharSequence customErrorMessage;
    private int controllerShowTimeoutMs;
    private boolean controllerAutoShow;
    private boolean controllerHideDuringAds;
    protected boolean controllerHideOnTouch;
    private int textureViewRotation;
    protected FrameLayout contentFrameLayout;
    private IRender.IRenderHolder mRenderHolder;
    /***手势管理布局view***/
    protected final GestureControlView mGestureControlView;
    /***意图管理布局view***/
    protected final ActionControlView mActionControlView;
    /*** 锁屏管理布局***/
    protected   LockControlView mLockControlView;
    /*** The Ic back image.***/
    @DrawableRes
    private int icBackImage = R.drawable.ic_exo_back;
    /***是否在上面,是否横屏,是否列表播放 默认false,是否切换按钮*/
    protected boolean isLand, isListPlayer, isShowVideoSwitch;
    /***返回按钮*/
    protected AppCompatImageView exoControlsBack;
    /***视频加载页,错误页,进度控件,锁屏按布局,自定义预览布局,提示布局,播放按钮*/
    protected View exoLoadingLayout, exoPlayPreviewLayout, exoPreviewPlayBtn, exoBarrageLayout;
    /***水印,封面图占位,显示音频和亮度布图*/
    protected ImageView exoPlayWatermark, exoPreviewImage, exoPreviewBottomImage;
    /***多分辨率,默认Ui布局样式横屏后还原处理***/
    protected int switchIndex, setSystemUiVisibility = 0;

    public ExoPlayerView2(Context context) {
        this(context, null);
    }

    public ExoPlayerView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExoPlayerView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureControlView = new GestureControlView(getContext(), attrs, defStyleAttr);
        mActionControlView = new ActionControlView(getContext(), attrs, defStyleAttr);
     /*   mLockControlView = new LockControlView(getContext(), attrs, defStyleAttr, this);*/
        boolean shutterColorSet = false;
        int shutterColor = 0;
        int playerLayoutId = R.layout.simple_exo_view;
        boolean useArtwork = true;
        boolean useController = true;
        int surfaceType = SURFACE_TYPE_SURFACE_VIEW;
        int resizeMode = AspectRatio.AspectRatio_FIT_PARENT;
        int controllerShowTimeoutMs = PlayerControlView.DEFAULT_SHOW_TIMEOUT_MS;
        boolean controllerHideOnTouch = true;
        boolean controllerAutoShow = true;
        boolean controllerHideDuringAds = true;
        boolean showBuffering = false;
        int userWatermark = 0;
        int defaultArtworkId = 0;
        int loadId = R.layout.simple_exo_play_load;
        int preViewLayoutId = 0;
        int barrageLayoutId = 0;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlayerView, 0, 0);
            try {
                icBackImage = a.getResourceId(R.styleable.VideoPlayerView_player_back_image, icBackImage);
                userWatermark = a.getResourceId(R.styleable.VideoPlayerView_user_watermark, 0);
                isListPlayer = a.getBoolean(R.styleable.VideoPlayerView_player_list, false);
                defaultArtworkId = a.getResourceId(R.styleable.VideoPlayerView_default_artwork, defaultArtworkId);
                loadId = a.getResourceId(R.styleable.VideoPlayerView_player_load_layout_id, loadId);
                preViewLayoutId = a.getResourceId(R.styleable.VideoPlayerView_player_preview_layout_id, preViewLayoutId);
                barrageLayoutId = a.getResourceId(R.styleable.VideoPlayerView_player_barrage_layout_id, barrageLayoutId);
                int playerViewId = a.getResourceId(R.styleable.VideoPlayerView_controller_layout_id, R.layout.simple_exo_playback_control_view);
                if (preViewLayoutId == 0 && (playerViewId == R.layout.simple_exo_playback_list_view || playerViewId == R.layout.simple_exo_playback_top_view)) {
                    preViewLayoutId = R.layout.exo_default_preview_layout;
                }
                shutterColorSet = a.hasValue(R.styleable.PlayerView_shutter_background_color);
                shutterColor = a.getColor(R.styleable.PlayerView_shutter_background_color, shutterColor);
                playerLayoutId = a.getResourceId(R.styleable.PlayerView_player_layout_id, playerLayoutId);
                useArtwork = a.getBoolean(R.styleable.PlayerView_use_artwork, useArtwork);
                useController = a.getBoolean(R.styleable.PlayerView_use_controller, useController);
                surfaceType = a.getInt(R.styleable.PlayerView_surface_type, surfaceType);
                resizeMode = a.getInt(R.styleable.PlayerView_resize_mode, resizeMode);
                controllerShowTimeoutMs =
                        a.getInt(R.styleable.PlayerView_show_timeout, controllerShowTimeoutMs);
                controllerHideOnTouch =
                        a.getBoolean(R.styleable.PlayerView_hide_on_touch, controllerHideOnTouch);
                controllerAutoShow = a.getBoolean(R.styleable.PlayerView_auto_show, controllerAutoShow);
                showBuffering = a.getBoolean(R.styleable.PlayerView_show_buffering, showBuffering);
                keepContentOnPlayerReset =
                        a.getBoolean(
                                R.styleable.PlayerView_keep_content_on_player_reset, keepContentOnPlayerReset);
                controllerHideDuringAds =
                        a.getBoolean(R.styleable.PlayerView_hide_during_ads, controllerHideDuringAds);
            } finally {
                a.recycle();
            }
        }
        contentFrameLayout = (FrameLayout) LayoutInflater.from(context).inflate(playerLayoutId, this);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        // Content frame.
        contentFrame = findViewById(R.id.exo_content_frame);
        // Shutter view.
        shutterView = findViewById(R.id.exo_shutter);
        if (shutterView != null && shutterColorSet) {
            shutterView.setBackgroundColor(shutterColor);
        }
        // Create a surface view and insert it into the content frame, if there is one.
        if (contentFrame != null && surfaceType != SURFACE_TYPE_NONE) {
            LayoutParams lp = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER);
            surfaceView =
                    surfaceType == SURFACE_TYPE_TEXTURE_VIEW
                            ? new RenderTextureView(context)
                            : new RenderSurfaceView(context);
            if (surfaceType == SURFACE_TYPE_TEXTURE_VIEW) {
                ((RenderTextureView) surfaceView).setTakeOverSurfaceTexture(true);
            }
            surfaceView.getRenderView().setLayoutParams(lp);
            contentFrame.addView(surfaceView.getRenderView(), 0);
            this.surfaceView.updateAspectRatio(resizeMode);

        } else {
            surfaceView = null;
        }
        // Artwork view.
        artworkView = findViewById(R.id.exo_artwork);
        this.useArtwork = useArtwork && artworkView != null;
        // Subtitle view.
        subtitleView = findViewById(R.id.exo_subtitles);
        if (subtitleView != null) {
            subtitleView.setUserDefaultStyle();
            subtitleView.setUserDefaultTextSize();
        }

        // Buffering view.
        bufferingView = findViewById(R.id.exo_buffering);
        if (bufferingView != null) {
            bufferingView.setVisibility(View.GONE);
        }
        this.showBuffering = showBuffering;

        // Error message view.
        errorMessageView = findViewById(R.id.exo_error_message);
        if (errorMessageView != null) {
            errorMessageView.setVisibility(View.GONE);
        }

        // Playback control view.
        PlayerControlView customController = findViewById(R.id.exo_controller);
        View controllerPlaceholder = findViewById(R.id.exo_controller_placeholder);
        if (customController != null) {
            this.controller = customController;
        } else if (controllerPlaceholder != null) {
            // Propagate attrs as playbackAttrs so that PlayerControlView's custom attributes are
            // transferred, but standard FrameLayout attributes (e.g. background) are not.
            this.controller = new ExoPlayerControlView(context, null, 0, attrs);
            controller.setLayoutParams(controllerPlaceholder.getLayoutParams());
            ViewGroup parent = ((ViewGroup) controllerPlaceholder.getParent());
            int controllerIndex = parent.indexOfChild(controllerPlaceholder);
            parent.removeView(controllerPlaceholder);
            parent.addView(controller, controllerIndex);
        } else {
            this.controller = null;
        }
        this.controllerShowTimeoutMs = controller != null ? controllerShowTimeoutMs : 0;
        this.controllerHideOnTouch = controllerHideOnTouch;
        this.controllerAutoShow = controllerAutoShow;
        this.controllerHideDuringAds = controllerHideDuringAds;
        this.useController = useController && controller != null;
        exoLoadingLayout = inflate(context, loadId, null);
        intiView();
        hideController();
    }

    /**
     * Inti view.
     */
    private void intiView() {
        exoControlsBack = new AppCompatImageView(getContext());
        exoControlsBack.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        int ss = VideoPlayUtils.dip2px(getContext(), 7f);
        exoControlsBack.setId(R.id.exo_controls_back);
        exoControlsBack.setImageDrawable(ContextCompat.getDrawable(getContext(), icBackImage));
        exoControlsBack.setPadding(ss, ss, ss, ss);
        contentFrameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.exo_player_background_color));
        exoLoadingLayout.setVisibility(GONE);
        exoLoadingLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.exo_player_background_color));
        exoLoadingLayout.setClickable(true);
        contentFrameLayout.addView(mGestureControlView, contentFrameLayout.getChildCount());
        contentFrameLayout.addView(mActionControlView, contentFrameLayout.getChildCount());
        contentFrameLayout.addView(mLockControlView, contentFrameLayout.getChildCount());
        if (null != exoPlayPreviewLayout) {
            contentFrameLayout.addView(exoPlayPreviewLayout, contentFrameLayout.getChildCount());
        }
        contentFrameLayout.addView(exoLoadingLayout, contentFrameLayout.getChildCount());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(VideoPlayUtils.dip2px(getContext(), 35f), VideoPlayUtils.dip2px(getContext(), 35f));
        contentFrameLayout.addView(exoControlsBack, contentFrameLayout.getChildCount(), layoutParams);
        int index = contentFrameLayout.indexOfChild(findViewById(R.id.exo_controller_barrage));
        if (exoBarrageLayout != null) {
            contentFrameLayout.removeViewAt(index);
            exoBarrageLayout.setBackgroundColor(Color.TRANSPARENT);
            contentFrameLayout.addView(exoBarrageLayout, index);
        }
        exoPlayWatermark = findViewById(R.id.exo_player_watermark);
        //  videoLoadingShowText = findViewById(R.id.exo_loading_show_text);
        exoPreviewBottomImage = findViewById(R.id.exo_preview_image_bottom);
        if (findViewById(R.id.exo_preview_image) != null) {
            exoPreviewImage = findViewById(R.id.exo_preview_image);
            exoPreviewImage.setBackgroundResource(android.R.color.transparent);
        } else {
            exoPreviewImage = exoPreviewBottomImage;
        }
        setSystemUiVisibility = ((Activity) getContext()).getWindow().getDecorView().getSystemUiVisibility();

        exoPreviewPlayBtn = findViewById(R.id.exo_preview_play);
    }

    /** 设置水印图和封面图
     * @param userWatermark userWatermark  水印图
     * @param defaultArtworkId defaultArtworkId   封面图
     */
    protected void initWatermark(int userWatermark, int defaultArtworkId) {
        if (userWatermark != 0) {
            exoPlayWatermark.setImageResource(userWatermark);
        }
        if (defaultArtworkId != 0) {
            //setPreviewImage(BitmapFactory.decodeResource(getResources(), defaultArtworkId));
        }
    }

    public void hideController() {

    }
}
