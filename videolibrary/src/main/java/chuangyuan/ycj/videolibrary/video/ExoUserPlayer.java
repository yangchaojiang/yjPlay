
package chuangyuan.ycj.videolibrary.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;

import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.utils.VideoInfoListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;

public class ExoUserPlayer implements ExoPlayer.EventListener, View.OnClickListener, OnTouchListener {


    public interface OnBackLListener {
        void onBack();
    }

    private static final String TAG = "ExoPlayerManager";
    private int video_height;//视频布局高度
    private long lastTotalRxBytes = 0;//获取网速大小
    private long lastTimeStamp = 0;
    private long resumePosition;//进度
    private int resumeWindow;
    protected AudioManager audioManager;//音量管理
    private StringBuilder formatBuilder;
    private Formatter formatter;
    private Timer timer;//定时任务类
    VideoInfoListener videoInfoListener;//回调信息
    private ExoPlayerMediaSourceBuilder mediaSourceBuilder;//加载多媒体载体
    private SimpleExoPlayerView playerView;///播放view
    protected SimpleExoPlayer player;
    protected Activity activity;
    private ImageButton exo_video_fullscreen; //全屏或者竖屏
    private TextView exo_controls_title; //视视频标题
    private View exo_loading_layout;//视频加载页
    private TextView exo_loading_show_text;//实时视频加载速度
    private View exo_play_error_layout;//错误页
    private View exo_play_replay_layout;//播放结束
    private ImageView exoPlayWatermark;// 水印
    private int screenWidthPixels;
    private int screenHeightPixels;
    private GestureDetector gestureDetector;
    private OnBackLListener mOnBackLListener;
    protected boolean playerNeedsSource;

    /****
     * @param activity     活动对象
     * @param url          地址
     **/
    public ExoUserPlayer(Activity activity, String url) {
        this.activity = activity;
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(activity.getApplicationContext(), url);
        this.playerView = (SimpleExoPlayerView) activity.findViewById(R.id.player_view);
        initView();
    }

    /****
     * @param activity     活动对象
     * @param playerView   播放控件
     * @param url          地址
     **/
    public ExoUserPlayer(Activity activity, SimpleExoPlayerView playerView, Uri url) {
        this.playerView = playerView;
        this.activity = activity;
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(activity.getApplicationContext(), url);
        initView();
    }

    /****
     * @param activity     活动对象
     * @param playerView   播放控件
     * @param url          地址
     **/
    public ExoUserPlayer(Activity activity, SimpleExoPlayerView playerView, String url) {
        this.playerView = playerView;
        this.activity = activity;
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(activity.getApplicationContext(), url);
        initView();
    }

    @SuppressLint("InflateParams")
    private void initView() {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//防锁屏
        screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;
        screenHeightPixels = activity.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        exoPlayWatermark = (ImageView) playerView.findViewById(R.id.exo_play_watermark);
        exo_video_fullscreen = (ImageButton) playerView.findViewById(R.id.exo_video_fullscreen);
        View exo_controls_back = playerView.findViewById(R.id.exo_controls_back);
        exo_controls_title = (TextView) playerView.findViewById(R.id.exo_controls_title);
        exo_loading_layout = playerView.findViewById(R.id.exo_loading_layout);
        exo_loading_show_text = (TextView) playerView.findViewById(R.id.exo_loading_show_text);
        exo_play_replay_layout = playerView.findViewById(R.id.exo_play_replay_layout);
        exo_play_error_layout = playerView.findViewById(R.id.exo_play_error_layout);
        playerView.findViewById(R.id.exo_play_error_btn).setOnClickListener(this);
        playerView.findViewById(R.id.exo_video_replay).setOnClickListener(this);
        exo_video_fullscreen.setOnClickListener(this);
        exo_controls_back.setOnClickListener(this);
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        timer = new Timer();
        video_height = playerView.getLayoutParams().height;
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        timer.schedule(task, 0, 1000); // 1s后启动任务，每2s执行一次
        gestureDetector = new GestureDetector(activity, new PlayerGestureListener());
        doOnConfigurationChanged(activity.getResources().getConfiguration().orientation);
        hslHideView();
    }

    private void hslHideView() {
        if (mediaSourceBuilder.getStreamType() == C.TYPE_HLS) {//直播隐藏进度条
            activity.findViewById(R.id.exo_progress).setVisibility(View.GONE);
            activity.findViewById(R.id.exo_duration).setVisibility(View.GONE);
            activity.findViewById(R.id.exo_position).setVisibility(View.GONE);
            activity.findViewById(R.id.ycj_video_timeSpan).setVisibility(View.GONE);
        }
    }


    public void onStart() {
        if (Util.SDK_INT > 23) {
            createPlayers();
        }
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
        }
        if (activity != null && playerView != null && activity.isFinishing()) {
            playerView.removeAllViews();
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
    protected void createPlayersPlay() {
        player = createFullPlayer();

    }

    private SimpleExoPlayer createFullPlayer() {
        TrackSelection.Factory videoTrackSelectionFactory
                = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(playerView.getContext(),
                trackSelector, loadControl);
        player.addListener(this);
        playerView.setPlayer(player);
        return player;
    }

    /***
     * 播放视频
     **/
    protected void playVideo() {
        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
        if (haveResumePosition) {
            player.seekTo(resumeWindow, resumePosition);
        }
        player.setPlayWhenReady(true);
        playerView.setOnTouchListener(this);
        player.prepare(mediaSourceBuilder.getMediaSource(false), !haveResumePosition, false);
        playerNeedsSource = false;
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

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.d(TAG, "onTimelineChanged:Timeline" + timeline.getPeriodCount());

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d(TAG, "onTracksChanged");
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
                if (VideoPlayUtils.isNetworkAvailable(activity)) {
                    if (!playerNeedsSource) {
                        showErrorStateView(View.VISIBLE);
                        updateResumePosition();
                    }
                } else {
                    showErrorStateView(View.VISIBLE);
                    updateResumePosition();
                }
                updateResumePosition();
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
            // This will only occur if the user has performed a seek whilst in the error state. Update the
            // resume position so that if the user then retries, playback will resume from the position to
            // which they seeked.
            updateResumePosition();
        }
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Log.d(TAG, "onPlaybackParametersChanged:"+playbackParameters.pitch);
    }

    /****
     * 横竖屏切换
     * @param configuration  旋转
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
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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
    }

    //设置videoFrame的大小
    private void scaleLayout(int newConfig) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) playerView.getLayoutParams();
        if (newConfig == Configuration.ORIENTATION_PORTRAIT) {//shiping
            params.height = video_height;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            playerView.setLayoutParams(params);

        } else {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
             params.width =ViewGroup.LayoutParams.MATCH_PARENT;

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
            doOnConfigurationChanged(Configuration.ORIENTATION_LANDSCAPE);
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
    protected void showReplayView(int state) {
        if (exo_play_replay_layout != null) {
            exo_play_replay_layout.setVisibility(state);
        }
        if (state == View.VISIBLE) {
            showLoadStateView(View.GONE);
            showErrorStateView(View.GONE);
            playerView.setOnTouchListener(null);
        } else {
            playerView.setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
            return true;
        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }
        return false;
    }

    /****
     * 手势监听类
     *****/
    private class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean firstTouch;
        private boolean volumeControl;
        private boolean toSeek;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            return super.onDown(e);

        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = mOldX > screenWidthPixels * 0.5f;
                firstTouch = false;
            }
            if (toSeek) {
                if (mediaSourceBuilder.getStreamType() == C.TYPE_HLS)
                    return super.onScroll(e1, e2, distanceX, distanceY);//直播隐藏进度条
                deltaX = -deltaX;
                long position = player.getCurrentPosition();
                long duration = player.getDuration();
                long newPosition = (int) (position + deltaX * duration / screenWidthPixels);
                if (newPosition > duration) {
                    newPosition = duration;
                } else if (newPosition <= 0) {
                    newPosition = 0;
                }
                showProgressDialog(deltaX, stringForTime(newPosition), newPosition, stringForTime(duration), duration);
            } else {
                float percent = deltaY / playerView.getHeight();
                if (volumeControl) {
                    showVolumeDialog(percent);
                } else {
                    showBrightnessDialog(percent);
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }


    }

    /****
     * 滑动亮度
     *
     * @param percent 滑动
     **/
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

    private String stringForTime(long timeMs) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        formatBuilder.setLength(0);
        return hours > 0 ? formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
                : formatter.format("%02d:%02d", minutes, seconds).toString();
    }


    /***
     * 显示水印图
     * @param res 资源
     * ***/
    protected void setExoPlayWatermarkImg(int res) {
        try {
            if (exoPlayWatermark != null) {
                exoPlayWatermark.setImageResource(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setExo_controls_title(TextView exo_controls_title) {
        this.exo_controls_title = exo_controls_title;
    }

    public void setVideoInfoListener(VideoInfoListener videoInfoListener) {
        this.videoInfoListener = videoInfoListener;
    }

    public void setmOnBackLListener(OnBackLListener mOnBackLListener) {
        this.mOnBackLListener = mOnBackLListener;
    }

}

