package chuangyuan.ycj.videolibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.listener.ExoPlayerListener;
import chuangyuan.ycj.videolibrary.listener.ExoPlayerViewListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;

/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 视频播放video
 */

public class VideoPlayerView extends FrameLayout implements PlaybackControlView.VisibilityListener {
    public static final String TAG = VideoPlayerView.class.getName();
    protected Activity activity;
    protected SimpleExoPlayerView playerView;///播放view
    private ImageButton exo_video_fullscreen; //全屏或者竖屏
    protected TextView exo_controls_title, exo_video_switch, exo_loading_show_text; //视视频标题,清晰度切换,实时视频加载速度显示
    private View exo_loading_layout, exo_play_error_layout, exo_controls_back;//视频加载页,错误页,进度控件//返回按钮
    private View exo_play_replay_layout, exo_play_btn_hint_layout;//播放结束，提示布局
    private ImageView exoPlayWatermark, exo_preview_image;// 水印
    private BelowView belowView;//切换
    private AlertDialog alertDialog;
    private ExoDefaultTimeBar timeBar;
    private Lock lock = new ReentrantLock();
    private boolean isShowVideoSwitch;//是否切换按钮
    protected ExoPlayerListener mExoPlayerListener;
    private boolean isListPlayer;//是否列表播放// 默认false
    private final ComponentListener componentListener = new ComponentListener();

    public VideoPlayerView(Context context) {
        super(context, null);
        activity = (Activity) context;
        intiView();
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        activity = (Activity) context;
        intiView();
    }

    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity = (Activity) context;
        playerView = new SimpleExoPlayerView(getContext(), attrs);
        addView(playerView);
        int userWatermark = 0;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VideoPlayerView, 0, 0);
            try {
                userWatermark = a.getResourceId(R.styleable.VideoPlayerView_user_watermark, 0);
                isListPlayer = a.getBoolean(R.styleable.VideoPlayerView_player_list, false);
            } finally {
                a.recycle();
            }
        }
        intiView();
        if (userWatermark != 0) {
            exoPlayWatermark.setImageResource(userWatermark);
        }
    }

    private void intiView() {
        exo_play_btn_hint_layout = playerView.findViewById(R.id.exo_play_btn_hint_layout);
        exo_play_replay_layout = playerView.findViewById(R.id.exo_play_replay_layout);
        exo_play_error_layout = playerView.findViewById(R.id.exo_play_error_layout);
        exoPlayWatermark = (ImageView) playerView.findViewById(R.id.exo_play_watermark);
        exo_video_fullscreen = (ImageButton) playerView.findViewById(R.id.exo_video_fullscreen);
        exo_controls_title = (TextView) playerView.findViewById(R.id.exo_controls_title);
        exo_loading_show_text = (TextView) playerView.findViewById(R.id.exo_loading_show_text);
        exo_video_switch = (TextView) playerView.findViewById(R.id.exo_video_switch);
        exo_preview_image = (ImageView) playerView.findViewById(R.id.exo_preview_image);
        exo_loading_layout = playerView.findViewById(R.id.exo_loading_layout);
        timeBar = (ExoDefaultTimeBar) playerView.findViewById(R.id.exo_progress);
        playerView.findViewById(R.id.exo_play_btn_hint).setOnClickListener(componentListener);
        exo_controls_back = playerView.findViewById(R.id.exo_controls_back);
        if (exo_controls_back != null) {
            exo_controls_back.setOnClickListener(componentListener);
        }
        playerView.findViewById(R.id.exo_play_error_btn).setOnClickListener(componentListener);
        playerView.findViewById(R.id.exo_video_replay).setOnClickListener(componentListener);
        exo_video_fullscreen.setOnClickListener(componentListener);
        playerView.setControllerVisibilityListener(this);
    }

    /****
     * 获取控制类
     ***/
    public PlaybackControlView getPlaybackControlView() {
        if (playerView != null)
            return playerView.getUseControllerView();
        return null;
    }

    public void onDestroy() {
        showReplay(GONE);
        showLoadState(GONE);
        showErrorState(GONE);
        showReplay(GONE);
        if (getPlaybackControlView() != null) {
            getPlaybackControlView().showNo();
            getPlaybackControlView().onDetachedFromWindow();
        }
        if (exo_preview_image != null) {
            exo_preview_image.setVisibility(VISIBLE);
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if (belowView != null) {
            belowView = null;
        }
        if (activity.isFinishing()) {
            removeAllViews();
            activity = null;
        }
    }

    /***
     * 控制类显示隐藏
     ***/
    @Override
    public void onVisibilityChange(int visibility) {
        if (belowView != null && visibility == View.GONE) {
            belowView.dismissBelowView();
            if (exo_preview_image != null) {
                exo_preview_image.setVisibility(GONE);
            }
        }
    }

    /***
     * 设置是横屏,竖屏
     *
     * @param newConfig 旋转对象
     */
    private void doOnConfigurationChanged(int newConfig) {
        if (newConfig == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            VideoPlayUtils.hideActionBar(activity);
            this.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            //获得 WindowManager.LayoutParams 属性对象
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            //直接对它flags变量操作   LayoutParams.FLAG_FULLSCREEN 表示设置全屏
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            //设置属性
            activity.getWindow().setAttributes(lp);
            //意思大致就是  允许窗口扩展到屏幕之外
            //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            if (isListPlayer && exo_controls_back != null) {
                exo_controls_back.setVisibility(VISIBLE);
            }
        } else {//竖屏
            VideoPlayUtils.showActionBar(activity);
            playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            //获得 WindowManager.LayoutParams 属性对象
            WindowManager.LayoutParams lp2 = activity.getWindow().getAttributes();
            //LayoutParams.FLAG_FULLSCREEN 强制屏幕状态条栏弹出
            lp2.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //设置属性
            activity.getWindow().setAttributes(lp2);
            //不允许窗口扩展到屏幕之外  clear掉了
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //显示状态栏
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (isListPlayer && exo_controls_back != null) {
                exo_controls_back.setVisibility(GONE);
            }
        }
        showSwitch(newConfig);
        scaleLayout(newConfig);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (getPlay() != null && VideoPlayerManager.getInstance().getVideoPlayer() != null) {
            if (getPlay().toString().equals(VideoPlayerManager.getInstance().getVideoPlayer().toString())) {
                ((ManualPlayer) getPlay()).reset();
            }
        }
    }

    /***
     * 设置内容横竖屏内容
     *
     * @param newConfig 旋转对象
     */
    protected void scaleLayout(int newConfig) {
        if (newConfig == Configuration.ORIENTATION_PORTRAIT) {//shiping
            ViewGroup parent = (ViewGroup) playerView.getParent();
            if (parent != null) {
                parent.removeView(playerView);
            }
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            this.addView(playerView, params);
        } else {
            ViewGroup parent = (ViewGroup) playerView.getParent();
            if (parent != null) {
                parent.removeView(playerView);
            }
            ViewGroup contentView = (ViewGroup) VideoPlayUtils.scanForActivity(activity)
                    .findViewById(android.R.id.content);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            contentView.addView(playerView, params);
        }
    }

    /***
     * 是否显示切换清晰按钮
     *
     * @param newConfig 是否横竖屏
     **/
    private void showSwitch(int newConfig) {
        if (!isShowVideoSwitch) return;
        if (newConfig == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            exo_video_switch.setVisibility(View.VISIBLE);
            exo_video_switch.setOnClickListener(componentListener);
        } else {
            exo_video_switch.setVisibility(View.GONE);
        }
    }

    /***
     * 显示隐藏加载页
     *
     * @param state 状态
     ***/
    private void showLoadState(int state) {
        if (exo_loading_layout != null) {
            exo_loading_layout.setVisibility(state);
        }
        if (state == View.VISIBLE) {
            showErrorState(View.GONE);
            showReplay(View.GONE);
            playerView.hideController();
        }
    }

    /***
     * 显示隐藏错误页
     *
     * @param state 状态
     ***/
    private void showErrorState(int state) {
        if (state == View.VISIBLE) {
            showLoadState(View.GONE);
            showReplay(View.GONE);
            playerView.setOnTouchListener(null);
        }
        if (exo_play_error_layout != null) {
            exo_play_error_layout.setVisibility(state);
        }
    }


    /***
     * 显示隐藏重播页
     *
     * @param state 状态
     ***/
    private void showReplay(int state) {
        if (exo_play_replay_layout != null) {
            exo_play_replay_layout.setVisibility(state);
        }
        if (state == View.VISIBLE) {
            showLoadState(View.GONE);
            showErrorState(View.GONE);
            playerView.hideController();
        }
    }

    /***
     * 显示按钮提示页
     *
     * @param state 状态
     ***/
    protected void showBtnContinueHint(int state) {
        if (state == View.VISIBLE) {
            showLoadState(View.GONE);
            showReplay(View.GONE);
            showErrorState(View.GONE);
            showBtnContinueHint(GONE);
        }
        if (exo_play_btn_hint_layout != null) {
            exo_play_btn_hint_layout.setVisibility(state);
        }
    }

    /****
     * 监听返回键
     ***/
    public void exitFullView() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        exo_video_fullscreen.setImageResource(R.drawable.ic_fullscreen_white);
        doOnConfigurationChanged(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /***
     * 显示网络提示框
     ***/
    private void showDialog() {
        try {
            lock.lock();
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
        } finally {
            lock.tryLock();
        }
    }


    /**
     * 关联布局播多媒体类
     *
     * @param player 多媒体类
     ***/
    public void setPlayer(SimpleExoPlayer player) {
        playerView.setPlayer(player);
    }

    /**
     * 设置标题
     *
     * @param title 内容
     **/
    public void setTitle(String title) {
        exo_controls_title.setText(title);
    }

    /**
     * 设置占位预览图
     *
     * @param defaultArtwork 预览图
     **/
    public void setArtwork(Bitmap defaultArtwork) {
        playerView.setDefaultArtwork(defaultArtwork);
    }

    /**
     * 设置是是否占位预览图
     *
     * @param useArtwork true 显示  false 隐藏
     **/
    public void setUseArtwork(boolean useArtwork) {
        playerView.setUseArtwork(useArtwork);
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
     * 获取当前布局
     */
    public View getExoLoadingLayout() {
        return exo_loading_layout;
    }

    public boolean isListPlayer() {
        return isListPlayer;
    }

    /**
     * 获取g播放控制类
     *
     * @return ExoUserPlayer
     **/
    public ExoUserPlayer getPlay() {
        if (mExoPlayerListener != null) {
            return mExoPlayerListener.getPlay();
        }
        return null;
    }

    /***
     * 获取预览图
     ***/
    public ImageView getPreviewImage() {
        return exo_preview_image;
    }

    /***
     * 获取内核播放view
     **/
    public SimpleExoPlayerView getPlayerView() {
        return playerView;
    }

    /**
     * 获取进度条
     **/
    public ExoDefaultTimeBar getTimeBar() {
        return timeBar;
    }

    /***
     * 获取监听事件
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
                if (VideoPlayUtils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE) {//横屏
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    exo_video_fullscreen.setImageResource(R.drawable.ic_fullscreen_white);
                    doOnConfigurationChanged(Configuration.ORIENTATION_PORTRAIT);
                } else if (VideoPlayUtils.getOrientation(activity) == Configuration.ORIENTATION_PORTRAIT) {//竖屏
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    exo_video_fullscreen.setImageResource(R.drawable.ic_fullscreen_exit_white);
                    doOnConfigurationChanged(Configuration.ORIENTATION_LANDSCAPE);
                }

            } else if (v.getId() == R.id.exo_controls_back) {
                mExoPlayerListener.onBack();
            } else if (v.getId() == R.id.exo_play_error_btn) {
                if (VideoPlayUtils.isNetworkAvailable(activity)) {
                    showErrorStateView(View.GONE);
                    mExoPlayerListener.onCreatePlayers();
                } else {
                    Toast.makeText(activity, R.string.net_network_no_hint, Toast.LENGTH_SHORT).show();
                }
            } else if (v.getId() == R.id.exo_video_replay) {
                if (VideoPlayUtils.isNetworkAvailable(activity)) {
                    showReplayView(View.GONE);
                    mExoPlayerListener.replayPlayers();
                } else {
                    Toast.makeText(activity, R.string.net_network_no_hint, Toast.LENGTH_SHORT).show();
                }

            } else if (v.getId() == R.id.exo_video_switch) {//切换
                if (belowView == null) {
                    belowView = new BelowView(activity);
                    belowView.setOnItemClickListener(new BelowView.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, String name) {
                            belowView.dismissBelowView();
                            exo_video_switch.setText(name);
                            mExoPlayerListener.switchUri(position, name);
                        }
                    });
                }
                belowView.showBelowView(v, true);
            } else if (v.getId() == R.id.exo_play_btn_hint) {//提示播放
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
            timeBar.setVisibility(visibility);

        }

        @Override
        public void setWatermarkImage(int res) {
            if (exoPlayWatermark != null) {
                exoPlayWatermark.setImageResource(res);
            }
        }

        @Override
        public void showSwitchName(String name) {
            exo_video_switch.setText(name);
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
        public void setTitle(String title) {
            exo_controls_title.setText(title);
        }

        /***
         * 显示网速
         *
         * @param netSpeed 网速的值
         ***/
        @Override
        public void showNetSpeed(final String netSpeed) {
            exo_loading_show_text.post(new Runnable() {
                @Override
                public void run() {
                    if (exo_loading_show_text != null) {
                        exo_loading_show_text.setText(netSpeed);
                    }
                }
            });
        }

        @Override
        public void onConfigurationChanged(int newConfig) {
            doOnConfigurationChanged(newConfig);
        }

    }
}
