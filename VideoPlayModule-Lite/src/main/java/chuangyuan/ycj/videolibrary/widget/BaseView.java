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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.ExoPlayerControlView;
import com.google.android.exoplayer2.ui.ExoPlayerView;

import java.util.ArrayList;
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
    /*** The constant TAG.***/
    public static final String TAG = VideoPlayerView.class.getName();
    final Activity activity;
    /***播放view*/
    protected final ExoPlayerView playerView;
    /*** 加载速度显示*/
    protected TextView videoLoadingShowText;
    /***视频加载页,错误页,进度控件,锁屏按布局,自定义预览布局,提示布局,播放按钮*/
    protected View exoLoadingLayout, exoPlayPreviewLayout, exoPreviewPlayBtn, exoBarrageLayout;
    /***水印,封面图占位,显示音频和亮度布图*/
    protected ImageView exoPlayWatermark, exoPreviewImage, exoPreviewBottomImage;
    /***手势管理布局view***/
    protected final GestureControlView mGestureControlView;
    /***意图管理布局view***/
    protected final ActionControlView mActionControlView;
    /*** 锁屏管理布局***/
    protected final LockControlView mLockControlView;
    /***锁屏管理布局***/
    protected final ExoPlayerControlView controllerView;
    /***切换*/
    protected BelowView belowView;
    /***流量提示框***/
    protected AlertDialog alertDialog;
    protected ExoPlayerListener mExoPlayerListener;
    /***返回按钮*/
    protected AppCompatImageView exoControlsBack;
    /***是否在上面,是否横屏,是否列表播放 默认false,是否切换按钮*/
    protected boolean isLand, isListPlayer, isShowVideoSwitch;
    /**是否显示返回按钮**/
    private  boolean isShowBack =true;
    /***标题左间距*/
    protected int getPaddingLeft;
    private ArrayList<String> nameSwitch;
    /***多分辨率,默认Ui布局样式横屏后还原处理***/
    protected int switchIndex, setSystemUiVisibility = 0;
    /*** The Ic back image.***/
    @DrawableRes
    private int icBackImage = R.drawable.ic_exo_back;

    /**
     * Instantiates a new Base view.
     *
     * @param context the context
     */
    public BaseView(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Base view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Base view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity =VideoPlayUtils.scanForActivity(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        playerView = new ExoPlayerView(getContext(), attrs, defStyleAttr);
        controllerView = (ExoPlayerControlView) playerView.getControllerView();
        mGestureControlView = new GestureControlView(getContext(), attrs, defStyleAttr);
        mActionControlView = new ActionControlView(getContext(), attrs, defStyleAttr);
        mLockControlView = new LockControlView(getContext(), attrs, defStyleAttr, this);
        addView(playerView, params);
        int userWatermark = 0;
        int defaultArtworkId = 0;
        int loadId = R.layout.simple_exo_play_load;
        int preViewLayoutId = 0;
        int barrageLayoutId = 0;
        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.VideoPlayerView, 0, 0);
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
            } finally {
                a.recycle();
            }
        }
        if (barrageLayoutId != 0) {
            exoBarrageLayout = inflate(context, barrageLayoutId, null);
        }
        exoLoadingLayout = inflate(context, loadId, null);
        if (preViewLayoutId != 0) {
            exoPlayPreviewLayout = inflate(context, preViewLayoutId, null);
        }
        intiView();
        initWatermark(userWatermark, defaultArtworkId);
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
        FrameLayout frameLayout = playerView.getContentFrameLayout();
        frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.exo_player_background_color));
        exoLoadingLayout.setVisibility(GONE);
        exoLoadingLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.exo_player_background_color));
        exoLoadingLayout.setClickable(true);
        frameLayout.addView(mGestureControlView, frameLayout.getChildCount());
        frameLayout.addView(mActionControlView, frameLayout.getChildCount());
        frameLayout.addView(mLockControlView, frameLayout.getChildCount());
        if (null != exoPlayPreviewLayout) {
            frameLayout.addView(exoPlayPreviewLayout, frameLayout.getChildCount());
        }
        frameLayout.addView(exoLoadingLayout, frameLayout.getChildCount());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(VideoPlayUtils.dip2px(getContext(), 35f), VideoPlayUtils.dip2px(getContext(), 35f));
        frameLayout.addView(exoControlsBack, frameLayout.getChildCount(), layoutParams);
        int index = frameLayout.indexOfChild(findViewById(R.id.exo_controller_barrage));
        if (exoBarrageLayout != null) {
            frameLayout.removeViewAt(index);
            exoBarrageLayout.setBackgroundColor(Color.TRANSPARENT);
            frameLayout.addView(exoBarrageLayout, index);
        }
        exoPlayWatermark = playerView.findViewById(R.id.exo_player_watermark);
        videoLoadingShowText = playerView.findViewById(R.id.exo_loading_show_text);

        exoPreviewBottomImage = playerView.findViewById(R.id.exo_preview_image_bottom);
        if (playerView.findViewById(R.id.exo_preview_image) != null) {
            exoPreviewImage = playerView.findViewById(R.id.exo_preview_image);
            exoPreviewImage.setBackgroundResource(android.R.color.transparent);
        } else {
            exoPreviewImage = exoPreviewBottomImage;
        }
        setSystemUiVisibility = ((Activity) getContext()).getWindow().getDecorView().getSystemUiVisibility();

        exoPreviewPlayBtn = playerView.findViewById(R.id.exo_preview_play);
    }

    /**
     * On destroy.
     */
    public void onDestroy() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if (belowView != null) {
            belowView = null;
        }
        if (exoControlsBack != null && exoControlsBack.animate() != null) {
            exoControlsBack.animate().cancel();
        }
        if (mLockControlView != null) {
            mLockControlView.onDestroy();
        }
        if (mExoPlayerListener != null) {
            mExoPlayerListener = null;
        }
        nameSwitch = null;
    }


    /***
     * 设置水印图和封面图
     * @param userWatermark userWatermark  水印图
     * @param defaultArtworkId defaultArtworkId   封面图
     */
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
     */
    protected void showDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getContext().getString(R.string.exo_play_reminder));
        alertDialog.setMessage(getContext().getString(R.string.exo_play_wifi_hint_no));
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getContext().
                getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showBtnContinueHint(View.VISIBLE);

            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showBtnContinueHint(View.GONE);
                if (mExoPlayerListener != null) {
                    mExoPlayerListener.playVideoUri();
                }

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
            ViewGroup contentView = ((Activity) getContext()).findViewById(android.R.id.content);
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
     */
    protected void showLockState(int visibility) {
        mLockControlView.showLockState(visibility);
    }

    /***
     * 显示隐藏加载页
     *
     * @param visibility 状态
     */
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
     */
    protected void showErrorState(int visibility) {
        if (visibility == View.VISIBLE) {
            playerView.hideController();
            showReplay(GONE);
            showBackView(VISIBLE, true);
            showLockState(GONE);
            showLoadState(GONE);
            showPreViewLayout(GONE);
        }
        mActionControlView.showErrorState(visibility);
    }

    /***
     * 显示按钮提示页
     *
     * @param visibility 状态
     */
    protected void showBtnContinueHint(int visibility) {
        if (visibility == View.VISIBLE) {
            showReplay(GONE);
            showErrorState(GONE);
            showPreViewLayout(GONE);
            showLoadState(GONE);
            showBackView(VISIBLE, true);
        }
        mActionControlView.showBtnContinueHint(visibility);
    }

    /***
     * 显示隐藏重播页
     *
     * @param visibility 状态
     */
    protected void showReplay(int visibility) {
        if (visibility == View.VISIBLE) {
            controllerView.hideNo();
            showErrorState(GONE);
            showBtnContinueHint(GONE);
            showPreViewLayout(GONE);
            showLockState(GONE);
            showBackView(VISIBLE, true);
            showLoadState(GONE);
        }
        mActionControlView.showReplay(visibility);
    }

    /***
     * 显示隐藏自定义预览布局
     *
     * @param visibility 状态
     */
    protected void showPreViewLayout(int visibility) {
        if (exoPlayPreviewLayout != null) {
            if (exoPlayPreviewLayout.getVisibility() == visibility) {
                return;
            }
            exoPlayPreviewLayout.setVisibility(visibility);
            if (playerView.findViewById(R.id.exo_preview_play) != null) {
                playerView.findViewById(R.id.exo_preview_play).setVisibility(visibility);
            }
        }
    }

    /***
     * 显示隐藏返回键
     *
     * @param visibility 状态
     * @param is is
     */
    protected void showBackView(int visibility, boolean is) {
        if (exoControlsBack != null) {
            //如果是竖屏和且不显示返回按钮，就隐藏
            if (!isShowBack && !isLand) {
                exoControlsBack.setVisibility(GONE);
                return;
            }
            if (isListPlayer() && !isLand) {
                exoControlsBack.setVisibility(GONE);
            } else {
                if (visibility == VISIBLE && is) {
                    exoControlsBack.setTranslationY(0);
                    exoControlsBack.setAlpha(1f);
                }
                exoControlsBack.setVisibility(visibility);
            }
        }
    }


    /***
     * 为了播放完毕后，旋转屏幕，导致播放图像消失处理
     * @param visibility 状态
     * @param bitmap the bitmap
     */
    protected void showBottomView(int visibility, Bitmap bitmap) {
        exoPreviewBottomImage.setVisibility(visibility);
        if (bitmap != null) {
            exoPreviewBottomImage.setImageBitmap(bitmap);
        }
    }


    public boolean isShowBack() {
        return isShowBack;
    }
    /**
     * 设置标题
     *
     * @param showBack  true 显示返回  false 反之
     */
    public void setShowBack(boolean showBack) {
        this.isShowBack = showBack;
    }
    /**
     * 设置标题
     *
     * @param title 内容
     */
    public void setTitle(@NonNull String title) {
        controllerView.setTitle(title);
    }

    /***
     * 显示水印图
     *
     * @param res 资源
     */
    public void setExoPlayWatermarkImg(int res) {
        if (exoPlayWatermark != null) {
            exoPlayWatermark.setImageResource(res);
        }
    }

    /**
     * 设置占位预览图
     *
     * @param previewImage 预览图
     */
    public void setPreviewImage(Bitmap previewImage) {
        this.exoPreviewImage.setImageBitmap(previewImage);
    }

    /***
     * 设置播放的状态回调 .,此方法不是外部使用，请不要调用
     *
     * @param mExoPlayerListener 回调
     */
    public void setExoPlayerListener(ExoPlayerListener mExoPlayerListener) {
        this.mExoPlayerListener = mExoPlayerListener;
    }

    /***
     * 设置开启线路切换按钮
     *
     * @param showVideoSwitch true 显示  false 不现实
     */
    public void setShowVideoSwitch(boolean showVideoSwitch) {
        isShowVideoSwitch = showVideoSwitch;
    }

    /**
     * 设置全屏按钮样式
     *
     * @param icFullscreenStyle 全屏按钮样式
     */
    public void setFullscreenStyle(@DrawableRes int icFullscreenStyle) {
        controllerView.setFullscreenStyle(icFullscreenStyle);
    }

    /**
     * 设置开启开启锁屏功能
     *
     * @param openLock 默认 true 开启   false 不开启
     */
    public void setOpenLock(boolean openLock) {
        mLockControlView.setOpenLock(openLock);
    }

    /**
     * 设置开启开启锁屏功能
     *
     * @param openLock 默认 false 不开启   true 开启
     */
    public void setOpenProgress2(boolean openLock) {
        mLockControlView.setProgress(openLock);
    }

    /**
     * Gets name switch.
     *
     * @return the name switch
     */
    protected ArrayList<String> getNameSwitch() {
        if (nameSwitch == null) {
            nameSwitch = new ArrayList<>();
        }
        return nameSwitch;
    }

    protected void setNameSwitch(ArrayList<String> nameSwitch) {
        this.nameSwitch = nameSwitch;
    }

    /**
     * Gets name switch.
     *
     * @return the name switch
     */
    protected int getSwitchIndex() {
        return switchIndex;
    }

    /**
     * 设置多分辨显示文字
     *
     * @param name        name
     * @param switchIndex switchIndex
     */
    public void setSwitchName(@NonNull List<String> name, @Size(min = 0) int switchIndex) {
        this.nameSwitch = new ArrayList<>(name);
        this.switchIndex = switchIndex;
    }

    /****
     * 获取控制类
     *
     * @return PlaybackControlView playback control view
     */
    @NonNull
    public ExoPlayerControlView getPlaybackControlView() {
        return controllerView;
    }

    /***
     * 获取当前加载布局
     *
     * @return boolean
     */
    public boolean isLoadingLayoutShow() {
        return exoLoadingLayout.getVisibility() == VISIBLE;
    }

    /***
     * 获取视频加载view
     *
     * @return View load layout
     */
    @Nullable
    public View getLoadLayout() {
        return exoLoadingLayout;
    }

    /***
     * 流量播放提示view
     *
     * @return View play hint layout
     */
    @Nullable
    public View getPlayHintLayout() {
        return mActionControlView.getPlayBtnHintLayout();
    }

    /***
     * 重播展示view
     *
     * @return View replay layout
     */
    @Nullable
    public View getReplayLayout() {
        return mActionControlView.getPlayReplayLayout();
    }

    /***
     * 错误展示view
     *
     * @return View error layout
     */
    @Nullable
    public View getErrorLayout() {
        return mActionControlView.getExoPlayErrorLayout();
    }

    /***
     * 获取手势音频view
     *
     * @return View 手势
     */
    @NonNull
    public View getGestureAudioLayout() {
        return mGestureControlView.getExoAudioLayout();
    }

    /***
     * 获取手势亮度view
     *
     * @return View gesture brightness layout
     */
    @NonNull
    public View getGestureBrightnessLayout() {
        return mGestureControlView.getExoBrightnessLayout();
    }

    /***
     * 获取手势视频进度调节view
     *
     * @return View gesture progress layout
     */
    @NonNull
    public View getGestureProgressLayout() {
        return mGestureControlView.getDialogProLayout();
    }

    /***
     * 是否属于列表播放
     *
     * @return boolean boolean
     */
    public boolean isListPlayer() {
        return isListPlayer;
    }

    /***
     * 获取全屏按钮
     * @return boolean exo fullscreen
     */
    public AppCompatCheckBox getExoFullscreen() {
        return controllerView.getExoFullscreen();
    }

    /**
     * Gets switch text.
     *
     * @return the switch text
     */
    @NonNull
    public TextView getSwitchText() {
        return controllerView.getSwitchText();
    }

    /**
     * 获取g播放控制类
     *
     * @return ExoUserPlayer play
     */
    @Nullable
    protected ExoUserPlayer getPlay() {
        if (mExoPlayerListener == null) {
            return null;
        } else {
            return mExoPlayerListener.getPlay();
        }
    }

    /***
     * 获取预览图
     *
     * @return ImageView preview image
     */
    @NonNull
    public ImageView getPreviewImage() {
        return exoPreviewImage;
    }

    /***
     * 获取内核播放view
     *
     * @return SimpleExoPlayerView player view
     */
    @NonNull
    public ExoPlayerView getPlayerView() {
        return playerView;
    }

    /**
     * 获取进度条
     *
     * @return ExoDefaultTimeBar time bar
     */
    @NonNull
    public ExoDefaultTimeBar getTimeBar() {
        return (ExoDefaultTimeBar) controllerView.getTimeBar();
    }



}
