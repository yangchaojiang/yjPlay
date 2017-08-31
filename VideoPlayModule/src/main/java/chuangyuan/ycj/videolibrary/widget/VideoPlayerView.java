package chuangyuan.ycj.videolibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Assertions;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.utils.ExoPlayerListener;
import chuangyuan.ycj.videolibrary.utils.ExoPlayerViewListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;


/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 视频播放video
 */

public class VideoPlayerView extends FrameLayout implements PlaybackControlView.VisibilityListener {
    public static final String TAG = "VideoPlayerView";
    protected Activity activity;
    protected SimpleExoPlayerView playerView;///播放view
    private ImageButton exo_video_fullscreen; //全屏或者竖屏
    protected TextView exo_controls_title, exo_video_switch, exo_loading_show_text; //视视频标题,清晰度切换,实时视频加载速度显示
    private View exo_loading_layout, exo_play_error_layout, timeBar;//视频加载页,错误页,进度控件
    private View exo_play_replay_layout, exo_play_btn_hint_layout;//播放结束，提示布局
    private ImageView exoPlayWatermark;// 水印
    private BelowView belowView;//切换
    protected int videoHeight;//视频布局高度
    private AlertDialog alertDialog;
    private Lock lock = new ReentrantLock();
    private boolean isShowVideoSwitch;//是否切换按钮
    protected ExoPlayerListener mExoPlayerListener;
    private final ComponentListener componentListener = new ComponentListener();

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
            } finally {
                a.recycle();
            }
        }
        intiView();
        if (userWatermark != 0) {
            exoPlayWatermark.setImageResource(userWatermark);
        }
    }


    public VideoPlayerView(Context context) {
        super(context, null);
        activity = (Activity) context;
        intiView();
    }

    private void intiView() {
        playerView.setControllerVisibilityListener(this);
        exo_play_btn_hint_layout = playerView.findViewById(R.id.exo_play_btn_hint_layout);
        exo_play_replay_layout = playerView.findViewById(R.id.exo_play_replay_layout);
        exo_play_error_layout = playerView.findViewById(R.id.exo_play_error_layout);
        exoPlayWatermark = (ImageView) playerView.findViewById(R.id.exo_play_watermark);
        exo_video_fullscreen = (ImageButton) playerView.findViewById(R.id.exo_video_fullscreen);
        exo_controls_title = (TextView) playerView.findViewById(R.id.exo_controls_title);
        exo_loading_show_text = (TextView) playerView.findViewById(R.id.exo_loading_show_text);
        exo_video_switch = (TextView) playerView.findViewById(R.id.exo_video_switch);
        exo_loading_layout = playerView.findViewById(R.id.exo_loading_layout);
        timeBar = playerView.findViewById(R.id.exo_progress);
        playerView.findViewById(R.id.exo_play_btn_hint).setOnClickListener(componentListener);
        if (  playerView.findViewById(R.id.exo_controls_back)!=null) {
            playerView.findViewById(R.id.exo_controls_back).setOnClickListener(componentListener);
        }
        playerView.findViewById(R.id.exo_play_error_btn).setOnClickListener(componentListener);
        playerView.findViewById(R.id.exo_video_replay).setOnClickListener(componentListener);
        exo_video_fullscreen.setOnClickListener(componentListener);

    }


    public void onDestroy() {
        if (alertDialog != null) {
            alertDialog = null;
        }
        if (belowView != null) {
            belowView = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onVisibilityChange(int visibility) {
        if (belowView != null && visibility == View.GONE) {
            belowView.dismissBelowView();
        }
    }
    /***
     * 判断是横屏,竖屏
     *
     * @param newConfig 旋转对象
     */
    private void doOnConfigurationChanged(int newConfig) {
        if (newConfig == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            if (activity instanceof AppCompatActivity) {
                AppCompatActivity activity2 = (AppCompatActivity) activity;
                if (activity2.getSupportActionBar() != null) {
                    activity2.getSupportActionBar().hide();
                }
            }
            this.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            //获得 WindowManager.LayoutParams 属性对象
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            //直接对它flags变量操作   LayoutParams.FLAG_FULLSCREEN 表示设置全屏
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            //设置属性
            activity.getWindow().setAttributes(lp);
            //意思大致就是  允许窗口扩展到屏幕之外
            //    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //skin的宽高
        } else {//竖屏
            if (activity instanceof AppCompatActivity) {
                AppCompatActivity activity2 = (AppCompatActivity) activity;
                if (activity2.getSupportActionBar() != null) {
                    activity2.getSupportActionBar().show();
                }
            }
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
            //skin的宽高
        }
        scaleLayout(newConfig);
        showSwitch(newConfig);
    }

    //设置videoFrame的大小
    protected void scaleLayout(int newConfig) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (newConfig == Configuration.ORIENTATION_PORTRAIT) {//shiping
            params.height = videoHeight;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            params.height = outMetrics.heightPixels;
        }
        setLayoutParams(params);
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
        Log.d(TAG, "showLoadState:" + state);
        if (exo_loading_layout != null) {
            exo_loading_layout.setVisibility(state);
        }
        if (state == View.VISIBLE) {
            showErrorState(View.GONE);
            showReplay(View.GONE);
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

    public void setTitle(String title) {
        exo_controls_title.setText(title);
    }

    public void setArtwork(Bitmap defaultArtwork) {
        playerView.setDefaultArtwork(defaultArtwork);
    }

    public void setUseArtwork(boolean useArtwork) {
        playerView.setUseArtwork(useArtwork);
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


    public View getExoLoadingLayout() {
        return exo_loading_layout;
    }

    public void setExoPlayerListener(ExoPlayerListener mExoPlayerListener) {
        this.mExoPlayerListener = mExoPlayerListener;
    }

    public void setShowVideoSwitch(boolean showVideoSwitch) {
        isShowVideoSwitch = showVideoSwitch;
    }

    /**
     * 关联布局播多媒体类
     *
     * @param player 多媒体类
     ***/
    public void setPlayer(SimpleExoPlayer player) {
        playerView.setPlayer(player);

    }


    public SimpleExoPlayerView getPlayerView() {
        return playerView;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public ComponentListener getComponentListener() {
        return componentListener;
    }

    private class ComponentListener implements ExoPlayerViewListener, View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.exo_video_fullscreen) {
                if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    exo_video_fullscreen.setImageResource(R.drawable.ic_fullscreen_white);
                    doOnConfigurationChanged(Configuration.ORIENTATION_PORTRAIT);
                } else if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
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
                } else if (v.getId() == R.id.exo_play_btn_hint) {//提示播放
                    showDialog();
                }
                belowView.showBelowView(v, true);
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
