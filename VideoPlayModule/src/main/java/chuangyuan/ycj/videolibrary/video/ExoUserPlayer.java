
package chuangyuan.ycj.videolibrary.video;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.utils.VideoInfoListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.widget.BelowView;

public class ExoUserPlayer implements ExoPlayer.EventListener, View.OnClickListener, SimpleExoPlayer.VideoListener {

    private static final String TAG = ExoUserPlayer.class.getName();
    private int video_height;//视频布局高度
    private long lastTotalRxBytes = 0;//获取网速大小
    private long lastTimeStamp = 0;
    private long resumePosition;//进度
    private int resumeWindow;
    private Timer timer;//定时任务类
    private VideoInfoListener videoInfoListener;//回调信息
    protected ExoPlayerMediaSourceBuilder mediaSourceBuilder;//加载多媒体载体
    protected SimpleExoPlayerView playerView;///播放view
    protected SimpleExoPlayer player;
    protected Activity activity;
    private ImageButton exo_video_fullscreen; //全屏或者竖屏
    protected TextView exo_controls_title, exo_video_switch, exo_loading_show_text; //视视频标题,清晰度切换,实时视频加载速度显示
    private View exo_loading_layout, exo_play_error_layout, timeBar;//视频加载页,错误页,进度控件
    private View exo_play_replay_layout, exo_play_btn_hint_layout;//播放结束，提示布局
    private ImageView exoPlayWatermark;// 水印
    private OnBackLListener mOnBackLListener;
    private boolean playerNeedsSource;
    private NetworkBroadcastReceiver mNetworkBroadcastReceiver;
    private AlertDialog alertDialog;
    private Lock lock = new ReentrantLock();
    private boolean isShowVideoSwitch;//是否切换按钮
    private BelowView belowView;
    protected String[] videoUri;
    protected String[] nameUri;

    /****
     * @param activity 活动对象
     * @param uri      地址
     **/
    public ExoUserPlayer(@NonNull Activity activity, @NonNull String uri) {
        this.activity = activity;
        initView();
        setPlayUri(uri);
    }

    /****
     * @param activity   活动对象
     * @param playerView 播放控件
     **/
    public ExoUserPlayer(@NonNull Activity activity, SimpleExoPlayerView playerView) {
        this.playerView = playerView;
        this.activity = activity;
        initView();
    }

    public ExoUserPlayer(@NonNull Activity activity) {
        this.activity = activity;
        initView();
    }

    private void initView() {
        if (playerView == null) {
            this.playerView = (SimpleExoPlayerView) activity.findViewById(R.id.player_view);
        }
        playerView.setControllerVisibilityListener(new PlaybackControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                Log.d(TAG, "onVisibilityChange:" + visibility + "");
                if (belowView != null && visibility == View.GONE) {
                    belowView.dismissBelowView();
                }
            }
        });
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//防锁屏
        View exo_controls_back = playerView.findViewById(R.id.exo_controls_back);
        exo_play_btn_hint_layout = playerView.findViewById(R.id.exo_play_btn_hint_layout);
        exo_play_replay_layout = playerView.findViewById(R.id.exo_play_replay_layout);
        exo_play_error_layout = playerView.findViewById(R.id.exo_play_error_layout);
        exoPlayWatermark = (ImageView) playerView.findViewById(R.id.exo_play_watermark);
        exo_video_fullscreen = (ImageButton) playerView.findViewById(R.id.exo_video_fullscreen);
        exo_controls_title = (TextView) playerView.findViewById(R.id.exo_controls_title);
        exo_loading_show_text = (TextView) playerView.findViewById(R.id.exo_loading_show_text);
        exo_video_switch = (TextView) playerView.findViewById(R.id.exo_video_switch);
        exo_loading_layout = playerView.findViewById(R.id.exo_loading_layout);
        playerView.findViewById(R.id.exo_play_btn_hint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        timeBar = playerView.findViewById(R.id.exo_progress);
        playerView.findViewById(R.id.exo_play_error_btn).setOnClickListener(this);
        playerView.findViewById(R.id.exo_video_replay).setOnClickListener(this);
        exo_video_fullscreen.setOnClickListener(this);
        exo_controls_back.setOnClickListener(this);
        timer = new Timer();
        video_height = playerView.getLayoutParams().height;
        timer.schedule(task, 0, 1000); // 1s后启动任务，每2s执行一次
    }

    /***
     * 是否云隐藏
     **/
    public void hslHideView() {
        if (mediaSourceBuilder != null && mediaSourceBuilder.getStreamType() == C.TYPE_HLS) {//直播隐藏进度条
            Log.d(TAG, "getStreamType：" + mediaSourceBuilder.getStreamType());
            timeBar.setVisibility(View.INVISIBLE);
        } else {
            timeBar.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 设置播放路径
     ***/
    public void setPlayUri(@NonNull String uri) {
        setPlayUri(Uri.parse(uri));
    }

    /****
     * @param firstVideoUri  预览的视频
     * @param secondVideoUri 第二个视频
     **/
    public void setPlayUri(@NonNull String firstVideoUri, @NonNull String secondVideoUri) {
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(activity.getApplicationContext(), firstVideoUri, secondVideoUri);
        createPlayers();
        hslHideView();
        registerReceiverNet();
    }

    /**
     * 设置多线路播放
     *
     * @param videoUri 视频地址
     * @param name     清清晰度显示名称
     **/
    public void setPlaySwitchUri(@NonNull String[] videoUri, @NonNull String[] name) {
        this.videoUri = videoUri;
        this.nameUri = name;
        exo_video_switch.setText(nameUri[0]);
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(activity.getApplicationContext(), Uri.parse(videoUri[0]));
        createPlayers();
        hslHideView();
        registerReceiverNet();
    }

    /**
     * 设置播放路径
     ***/
    public void setPlayUri(@NonNull Uri uri) {
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(activity.getApplicationContext(), uri);
        createPlayers();
        hslHideView();
        registerReceiverNet();
    }

    public void onStart() {

    }

    public void onResume() {
        if ((Util.SDK_INT <= 23 || player == null)) {
            createPlayers();
        }
    }

    public void onPause() {
        if (player != null) {
            releasePlayers();
        }
    }

    public void onStop() {
        if (Util.SDK_INT > 23) {
            releasePlayers();
        }
        unNetworkBroadcastReceiver();
    }

    private void releasePlayers() {
        if (player != null) {
            updateResumePosition();
            player.stop();
            player.release();
            player.removeListener(this);
            player = null;
        }
        if (activity.isFinishing()) {
            if (timer != null) {
                timer.cancel();
            }
            if (task != null) {
                task.cancel();
            }
            if (mediaSourceBuilder != null) {
                mediaSourceBuilder.release();
            }
            if (alertDialog != null) {
                alertDialog = null;
            }
            if (lock != null) {
                lock = null;
            }
            if (activity != null && playerView != null) {
                playerView.removeAllViews();
            }
        }


    }


    /****
     * 创建
     **/
    protected void createPlayers() {
        if (player == null) {
            player = createFullPlayer();
            playerNeedsSource = true;
        }
        playVideo();
    }

    /****
     * 创建
     **/
    void createPlayersPlay() {
        player = createFullPlayer();
    }

    private SimpleExoPlayer createFullPlayer() {
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(playerView.getContext(),
                trackSelector);
        playerView.setPlayer(player);
        return player;
    }

    /***
     * 播放视频
     **/
    protected void playVideo() {
        if (!VideoPlayUtils.isWifi(activity)) {
            playVideoUri();
        } else {
            showDialog();
        }
    }

    /***
     * 播放视频
     **/
    void playVideoUri() {
        if (player == null) {
            createPlayersPlay();
        }
        if (mediaSourceBuilder != null) {
            boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
            if (haveResumePosition) {
                player.seekTo(resumeWindow, resumePosition);
            }
            player.setPlayWhenReady(true);

            player.prepare(mediaSourceBuilder.getMediaSource(), !haveResumePosition, false);
            player.addListener(this);
            playerNeedsSource = false;
        }
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
                    showBtnContinueHintView(View.VISIBLE);

                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    showBtnContinueHintView(View.GONE);
                    playVideoUri();
                }
            });
            alertDialog.show();
        } finally {
            lock.tryLock();
        }
    }

    /****
     * 重置进度
     **/
    protected void updateResumePosition() {
        if (player != null) {
            resumeWindow = player.getCurrentWindowIndex();
            resumePosition = player.isCurrentWindowSeekable() ? Math.max(0, player.getCurrentPosition())
                    : C.TIME_UNSET;
        }
    }

    /**
     * 清除进度
     ***/
    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    /***
     * 视频播放播放
     **/
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.d(TAG, "onTimelineChanged:Timeline:getPeriodCount" + timeline.getPeriodCount());
        if (timeline.getPeriodCount() > 1) {
            if (player.getCurrentTrackGroups().length == 0) {
                timeBar.setVisibility(View.INVISIBLE);
            } else {
                timeBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d(TAG, "onTracksChanged:" + trackGroups.length);

    }

    /*****
     * 进度条控制 加载页
     *********/
    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.d(TAG, "onLoadingChanged:" + isLoading + "" + player.getPlayWhenReady());
    }

    /**
     * 视频的播放状态
     * STATE_IDLE 播放器空闲，既不在准备也不在播放
     * STATE_PREPARING 播放器正在准备
     * STATE_BUFFERING 播放器已经准备完毕，但无法立即播放。此状态的原因有很多，但常见的是播放器需要缓冲更多数据才能开始播放
     * STATE_PAUSE 播放器准备好并可以立即播放当前位置
     * STATE_PLAY 播放器正在播放中
     * STATE_ENDED 播放已完毕
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "onPlayerStateChanged:+playWhenReady:" + playWhenReady);
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                Log.d(TAG, "onPlayerStateChanged:加载中。。。");
                if (playWhenReady) {
                    showLoadStateView(View.VISIBLE);
                }
                if (videoInfoListener != null) {
                    videoInfoListener.onLoadingChanged();
                }
                break;
            case ExoPlayer.STATE_ENDED:
                Log.d(TAG, "onPlayerStateChanged:ended。。。");
                showReplayView(View.VISIBLE);
                if (videoInfoListener != null) {
                    videoInfoListener.onPlayEnd();
                }
                break;
            case ExoPlayer.STATE_IDLE://空的
                Log.d(TAG, "onPlayerStateChanged::网络状态差，请检查网络。。。");
                updateResumePosition();
                if (!VideoPlayUtils.isNetworkAvailable(activity)) {
                    if (playerNeedsSource) {
                        showErrorStateView(View.VISIBLE);
                    }
                } else {
                    showErrorStateView(View.VISIBLE);
                }
                break;
            case ExoPlayer.STATE_READY:
                Log.d(TAG, "onPlayerStateChanged:ready。。。");
                showLoadStateView(View.GONE);
                if (videoInfoListener != null) {
                    videoInfoListener.onPlayStart();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        Log.d(TAG, "onPlayerError:" + e.getLocalizedMessage());
        playerNeedsSource = true;
        if (VideoPlayUtils.isBehindLiveWindow(e)) {
            clearResumePosition();
            playVideo();
        } else {
            showErrorStateView(View.VISIBLE);
            if (videoInfoListener != null) {
                videoInfoListener.onPlayerError(e);
            }
        }

    }

    @Override
    public void onPositionDiscontinuity() {
        Log.d(TAG, "onPositionDiscontinuity:");
        if (playerNeedsSource) {
            updateResumePosition();
        }
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Log.d(TAG, "onPlaybackParametersChanged:" + playbackParameters.pitch);
    }

    /****
     * 横竖屏切换
     *
     * @param configuration 旋转
     ***/
    public void onConfigurationChanged(Configuration configuration) {
        doOnConfigurationChanged(configuration.orientation);
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
            playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
        showSwitchView(newConfig);
    }

    //设置videoFrame的大小
    private void scaleLayout(int newConfig) {
        ViewGroup.LayoutParams params = playerView.getLayoutParams();
        if (newConfig == Configuration.ORIENTATION_PORTRAIT) {//shiping
            params.height = video_height;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            playerView.setLayoutParams(params);
        } else {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        }
        playerView.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.exo_video_fullscreen) {
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                exo_video_fullscreen.setImageResource(R.drawable.ic_fullscreen_white);
            } else if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                exo_video_fullscreen.setImageResource(R.drawable.ic_fullscreen_exit_white);
            }
            doOnConfigurationChanged(activity.getResources().getConfiguration().orientation);
        } else if (v.getId() == R.id.exo_controls_back) {
            onBackPressed();
        } else if (v.getId() == R.id.exo_play_error_btn) {
            if (VideoPlayUtils.isNetworkAvailable(activity)) {
                showErrorStateView(View.GONE);
                createPlayers();
            } else {
                Toast.makeText(activity, R.string.net_network_no_hint, Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.exo_video_replay) {
            if (VideoPlayUtils.isNetworkAvailable(activity)) {
                clearResumePosition();
                showReplayView(View.GONE);
                createPlayers();
            } else {
                Toast.makeText(activity, R.string.net_network_no_hint, Toast.LENGTH_SHORT).show();
            }

        } else if (v.getId() == R.id.exo_video_switch) {
            if (belowView == null) {
                belowView = new BelowView(activity);
                belowView.setOnItemClickListener(new BelowView.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, String name) {
                        belowView.dismissBelowView();
                        if (mediaSourceBuilder != null) {
                            mediaSourceBuilder.setMediaSourceUri(Uri.parse(videoUri[position]));
                            exo_video_switch.setText(name);
                            updateResumePosition();
                            playVideoUri();
                        }
                    }
                });
            }
            belowView.showBelowView(v, true);
        }
    }


    /***
     * 网络变化任务
     **/
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (exo_loading_layout.getVisibility() == View.VISIBLE) {
                showNetSpeed(getNetSpeed());
            }

        }
    };

    /****
     * 监听返回键
     ***/
    public void onBackPressed() {
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            exo_video_fullscreen.setImageResource(R.drawable.ic_fullscreen_white);
            onConfigurationChanged(activity.getResources().getConfiguration());

        } else {
            if (mOnBackLListener != null) {
                mOnBackLListener.onBack();
            }
        }
    }

    /***
     * 显示网速
     *
     * @param netSpeed 网速的值
     ***/
    private void showNetSpeed(final String netSpeed) {
        exo_loading_show_text.post(new Runnable() {
            @Override
            public void run() {
                if (exo_loading_show_text != null) {
                    exo_loading_show_text.setText(netSpeed);
                }
            }
        });
    }

    /****
     * 获取当前网速
     *
     * @return String 二返回当前网速字符
     **/
    private String getNetSpeed() {
        String netSpeed;
        long nowTotalRxBytes = VideoPlayUtils.getTotalRxBytes(activity);
        long nowTimeStamp = System.currentTimeMillis();
        long calculationTime = (nowTimeStamp - lastTimeStamp);
        if (calculationTime == 0) {
            netSpeed = String.valueOf(1) + " kb/s";
            return netSpeed;
        }
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / calculationTime);//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        if (speed > 1024) {
            DecimalFormat df = new DecimalFormat("######0.0");
            netSpeed = String.valueOf(df.format(VideoPlayUtils.getM(speed))) + " MB/s";
        } else {
            netSpeed = String.valueOf(speed) + " kb/s";
        }
        return netSpeed;
    }

    /***
     * 是否显示切换清晰按钮
     *
     * @param newConfig 是否横竖屏
     **/
    private void showSwitchView(int newConfig) {
        if (!isShowVideoSwitch) return;
        if (newConfig == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            exo_video_switch.setVisibility(View.VISIBLE);
            exo_video_switch.setOnClickListener(this);
        } else {
            exo_video_switch.setVisibility(View.GONE);
        }
    }

    /***
     * 显示隐藏加载页
     *
     * @param state 状态
     ***/
    protected void showLoadStateView(int state) {
        Log.d(TAG, "showLoadStateView:" + state);
        if (exo_loading_layout != null) {
            exo_loading_layout.setVisibility(state);
        }
        if (state == View.VISIBLE) {
            showErrorStateView(View.GONE);
            showReplayView(View.GONE);
        }
    }

    /***
     * 显示隐藏错误页
     *
     * @param state 状态
     ***/
    protected void showErrorStateView(int state) {
        if (state == View.VISIBLE) {
            showLoadStateView(View.GONE);
            showReplayView(View.GONE);
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
    void showReplayView(int state) {
        if (exo_play_replay_layout != null) {
            exo_play_replay_layout.setVisibility(state);
        }
        if (state == View.VISIBLE) {
            showLoadStateView(View.GONE);
            showErrorStateView(View.GONE);
        }
    }

    /***
     * 显示按钮提示页
     *
     * @param state 状态
     ***/
    protected void showBtnContinueHintView(int state) {
        if (state == View.VISIBLE) {
            showLoadStateView(View.GONE);
            showReplayView(View.GONE);
            showErrorStateView(View.GONE);
        }
        if (exo_play_btn_hint_layout != null) {
            exo_play_btn_hint_layout.setVisibility(state);
        }
    }


    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.d(TAG, "onVideoSizeChanged:" + width + "height:" + height);
    }

    @Override
    public void onRenderedFirstFrame() {

    }


    protected void showBrightnessDialog(float percent) {
    }

    /****
     * 滑动进度
     *
     * @param percent           滑动
     * @param seekTime          滑动的时间
     * @param seekTimePosition  滑动的时间 int
     * @param totalTime         视频总长
     * @param totalTimeDuration 视频总长 int
     **/
    protected void showProgressDialog(float percent, String seekTime, long seekTimePosition,
                                      String totalTime, long totalTimeDuration) {
    }

    /****
     * 手势结束
     **/
    protected void endGesture() {
    }

    /****
     * 滑动音量
     *
     * @param percent 滑动
     **/
    protected void showVolumeDialog(float percent) {
    }

    /***
     * 显示水印图
     *
     * @param res 资源
     ***/
    protected void setExoPlayWatermarkImg(int res) {
        try {
            if (exoPlayWatermark != null) {
                exoPlayWatermark.setImageResource(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }


    public void setTitle(String title) {
        exo_controls_title.setText(title);
    }

    public void setVideoInfoListener(VideoInfoListener videoInfoListener) {
        this.videoInfoListener = videoInfoListener;
    }

    public void setmOnBackLListener(OnBackLListener mOnBackLListener) {
        this.mOnBackLListener = mOnBackLListener;
    }

    public TextView getExo_controls_title() {
        return exo_controls_title;
    }

    public interface OnBackLListener {
        void onBack();
    }

    public void setShowVideoSwitch(boolean showVideoSwitch) {
        isShowVideoSwitch = showVideoSwitch;
    }

    /***
     * 注册广播监听
     **/
    void registerReceiverNet() {
        if (mNetworkBroadcastReceiver == null) {
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mNetworkBroadcastReceiver = new NetworkBroadcastReceiver();
            activity.registerReceiver(mNetworkBroadcastReceiver, intentFilter);
        }
    }

    /***
     * 取消广播监听
     **/
    private void unNetworkBroadcastReceiver() {
        if (mNetworkBroadcastReceiver != null) {
            activity.unregisterReceiver(mNetworkBroadcastReceiver);
        }
    }

    private class NetworkBroadcastReceiver extends BroadcastReceiver {
        long is = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {
                    /////////////网络连接
                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        /////WiFi网络
                    } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        /////////3g网络
                        if (System.currentTimeMillis() - is > 500) {
                            is = System.currentTimeMillis();
                            updateResumePosition();
                            releasePlayers();
                            showDialog();
                        }
                    }
                    Log.d(TAG, "onReceive:" + netInfo.getType() + "__:");
                }
            }

        }
    }
}

