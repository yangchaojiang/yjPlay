package com.list.videoplayer;

import android.content.Context;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.support.annotation.DrawableRes;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.list.videoplayer.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.AUDIO_SERVICE;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static com.list.videoplayer.VideoPlayer.PLAYER_FULL_SCREEN;
import static com.list.videoplayer.VideoPlayer.PLAYER_NORMAL;
import static com.list.videoplayer.VideoPlayer.PLAYER_TINY_WINDOW;
import static com.list.videoplayer.VideoPlayer.STATE_IDLE;

/**
 * @author Jarvis
 * @version 1.0
 * @title VideoPlayer
 * @description 该类主要功能描述
 * @create 2017/6/1 上午11:27
 * @changeRecord [修改记录] <br/>
 */

public class VideoPlayerController extends FrameLayout implements
        View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {

    public static final String TAG = "VideoPlayerController";
    private Context mContext;
    private IVideoPlayerControl mVideoPlayer;

    private ImageView mImage;
    private ImageView mCenterStart;

    private LinearLayout mTop;
    private ImageView mBack;
    private TextView mTitle;

    private LinearLayout mBottom;
    private ImageView mRestartPause;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private ImageView mFullScreen;

    private LinearLayout mLoading;
    private TextView mLoadText;

    private LinearLayout mError;
    private TextView mRetry;

    private LinearLayout mCompleted;
    private TextView mReplay;
    private TextView mShare;

    private GestureDetector mDetector;
    private AudioManager mAudioManager;
    private ViewDragHelpImpl mDragHelper;

    public static final int NONE = 1;
    public static final int HORIZONTAL = 1 << 1;
    public static final int VERTICAL = 1 << 2;

    private int mPlayerState = PLAYER_NORMAL;
    private int mCurrentPlayState = STATE_IDLE;

    /**
     * 触发ACTION_DOWN时的坐标
     */
    private float mDownX;
    private float mDownY;

    private int currentVoice;
    private int maxVoice;
    private int downVol;

    private int mScreenWidth;
    private int mScreenHeight;

    private boolean topBottomVisible;
    private boolean mTouchControlProgress = false;
    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;
    private CountDownTimer mDismissTopBottomCountDownTimer;

    public VideoPlayerController(Context context) {
        super(context);
        initView(context);
    }

    public VideoPlayerController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public VideoPlayerController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.video_player_controller, this, true);
        bindView();
        initOperator();
    }

    protected void bindView() {
        mCenterStart = (ImageView) findViewById(R.id.center_start);
        mImage = (ImageView) findViewById(R.id.image);

        mTop = (LinearLayout) findViewById(R.id.top);
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);

        mBottom = (LinearLayout) findViewById(R.id.bottom);
        mRestartPause = (ImageView) findViewById(R.id.restart_or_pause);
        mPosition = (TextView) findViewById(R.id.position);
        mDuration = (TextView) findViewById(R.id.duration);
        mSeek = (SeekBar) findViewById(R.id.seek);
        mFullScreen = (ImageView) findViewById(R.id.full_screen);

        mLoading = (LinearLayout) findViewById(R.id.loading);
        mLoadText = (TextView) findViewById(R.id.load_text);

        mError = (LinearLayout) findViewById(R.id.error);
        mRetry = (TextView) findViewById(R.id.retry);

        mCompleted = (LinearLayout) findViewById(R.id.completed);
        mReplay = (TextView) findViewById(R.id.replay);
        mShare = (TextView) findViewById(R.id.share);

        mCenterStart.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mRetry.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        this.setOnClickListener(this);
    }

    private void initOperator() {
        mDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return super.onSingleTapConfirmed(e);
            }
        });

        mDragHelper = new ViewDragHelpImpl.Builder()
                .create(this, 1.0f, new ViewDragHelper.Callback() {
                    @Override
                    public boolean tryCaptureView(View child, int pointerId) {
                        return VideoPlayerController.this == child;
                    }
                })
                .builder();

        mAudioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        currentVoice = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mScreenWidth = Utils.getScreenWidth(mContext);
        mScreenHeight = Utils.getScreenHeight(mContext);
    }

    public void setTitle(String title) {
        this.mTitle.setText(title);
    }

    public void setImage(@DrawableRes int resId) {
        mImage.setImageResource(resId);
    }

    public void setImage(String imageUrl) {
        Glide.with(mContext)
                .load(imageUrl)
                .placeholder(R.drawable.img_default)
                .crossFade()
                .into(mImage);
    }

    public void setVideoPlayer(IVideoPlayerControl playerControl) {
        mVideoPlayer = playerControl;
        if (mVideoPlayer.isIdle()) {
            mBack.setVisibility(GONE);
            mTop.setVisibility(VISIBLE);
            mBottom.setVisibility(GONE);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.mPlayerState == PLAYER_TINY_WINDOW) {
            mDragHelper.viewDragHelper.processTouchEvent(ev);
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        if (v == mCenterStart) {
            if (mVideoPlayer.isIdle()) {
                mVideoPlayer.start();
            }
        } else if (v == mBack) {
            if (mVideoPlayer.isFullScreen()) {
                mVideoPlayer.exitFullScreen();
            } else if (mVideoPlayer.isTinyWindow()) {
                mVideoPlayer.exitTinyWindow();
            }
        } else if (v == mRestartPause) {
            if (mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying()) {
                mVideoPlayer.pause();
            } else if (mVideoPlayer.isPaused() || mVideoPlayer.isBufferingPaused()) {
                mVideoPlayer.restart();
            }
        } else if (v == mFullScreen) {
            if (mVideoPlayer.isFullScreen()) {
                mVideoPlayer.exitFullScreen();
            } else if (mVideoPlayer.isNormal()) {
                mVideoPlayer.enterFullScreen();
            }
        } else if (v == mRetry) {
            mVideoPlayer.release();
            mVideoPlayer.start();
        } else if (v == mReplay) {
            mRetry.performClick();
        } else if (v == mShare) {

        } else if (v == this) {
            if (mVideoPlayer.isPlaying()
                    || mVideoPlayer.isPaused()
                    || mVideoPlayer.isBufferingPlaying()
                    || mVideoPlayer.isBufferingPaused()) {
                setTopBottomVisible(!topBottomVisible);
            }
        }
    }

    private void setTopBottomVisible(boolean visible) {
        mTop.setVisibility(visible ? VISIBLE : GONE);
        mBottom.setVisibility(visible ? VISIBLE : GONE);

        if (visible) {
            if (!mVideoPlayer.isPaused() && !mVideoPlayer.isBufferingPaused()) {
                startDismissTopBottomTimer();
            }
        } else {
            cancelDismissTopBottomTimer();
        }
        topBottomVisible = visible;

    }

    private void startDismissTopBottomTimer() {
        cancelDismissTopBottomTimer();
        if (mDismissTopBottomCountDownTimer == null) {
            mDismissTopBottomCountDownTimer = new CountDownTimer(8000, 8000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                }
            };
        }
        mDismissTopBottomCountDownTimer.start();
    }

    private void cancelDismissTopBottomTimer() {
        if (mDismissTopBottomCountDownTimer != null) {
            mDismissTopBottomCountDownTimer.cancel();
        }
    }

    public void setControllerState(int playerState, int playState) {
        mPlayerState = playerState;
        mCurrentPlayState =playState;
        switch (playerState) {
            case VideoPlayer.PLAYER_NORMAL:
                mBack.setVisibility(GONE);
                mFullScreen.setVisibility(VISIBLE);
                mFullScreen.setImageResource(R.drawable.ic_player_enlarge);
                this.setOnTouchListener(null);
                break;
            case PLAYER_FULL_SCREEN:
                mBack.setVisibility(VISIBLE);
                mFullScreen.setVisibility(VISIBLE);
                mFullScreen.setImageResource(R.drawable.ic_player_shrink);
                this.setOnTouchListener((v, event) -> {

                    mDetector.onTouchEvent(event);

                    switch (MotionEventCompat.getActionMasked(event)) {
                        case ACTION_DOWN:
                            mDownX = (int) event.getX();
                            mDownY = (int) event.getY();
                            downVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            break;

                        case ACTION_MOVE:
                            float endX = event.getX();
                            float endY = event.getY();
                            float distanceX = mDownX - endX;
                            float distanceY = mDownY - endY;
                            MoveOrientation orientation = getOrientation(distanceX, distanceY);
                            if (orientation == MoveOrientation.LEFT || orientation == MoveOrientation.RIGHT) {
                                // X轴控制进度
                                mTouchControlProgress = true;
                                int position = mVideoPlayer.getCurrentPosition();
                                int duration = mVideoPlayer.getDuation();
                                int currentPosition = position - (int) distanceX * 10;
                                int progress = (int) (100f * currentPosition / duration);
                                mSeek.setProgress(progress);
                                mPosition.setText(Utils.formatTime(position));
                                mDuration.setText(Utils.formatTime(duration));
                                Log.e(TAG, "ACTION_MOVE---------position: " + position + "-------currentPosition: " + currentPosition + "-----distanceX: " + distanceX);
                            } else {
                                // Y轴控制
                                if (endX < mScreenWidth / 2) {
                                    // 右侧控制音量
                                    int touchRang = Math.min(mScreenWidth, mScreenHeight);
                                    int curvol = (int) (downVol + (distanceY / touchRang) * maxVoice);//考虑到横竖屏切换的问题
                                    int volume = Math.min(Math.max(0, curvol), maxVoice);
                                    updateVoiceProgress(volume);
                                } else {
                                    // 左侧控制亮度
                                    final double FLING_MIN_DISTANCE = 0.5;
                                    final double FLING_MIN_VELOCITY = 0.5;
                                    if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                                        updateBrightness(10);
                                    }
                                    if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                                        updateBrightness(-10);
                                    }
                                }
                            }
                            break;

                        case ACTION_UP:
                            if (mTouchControlProgress) {
                                mTouchControlProgress = false;
                                if (mVideoPlayer.isBufferingPaused() || mVideoPlayer.isPaused()) {
                                    mVideoPlayer.restart();
                                }
                                int position = (int) (mVideoPlayer.getDuation() * mSeek.getProgress() / 100f);
                                Log.e(TAG, "ACTION_UP-----------position: " + position);
                                mVideoPlayer.seekTo(position);
//                                startDismissTopBottomTimer();
                            }

                            break;
                    }

                    return false;
                });

                break;
            case VideoPlayer.PLAYER_TINY_WINDOW:
                mFullScreen.setVisibility(GONE);
                this.setOnTouchListener(null);

                break;
        }
        switch (playState) {
            case STATE_IDLE:
                break;
            case VideoPlayer.STATE_PREPARING:
                mImage.setVisibility(GONE);
                mLoading.setVisibility(VISIBLE);
                mLoadText.setText("正在准备...");
                mError.setVisibility(GONE);
                mCompleted.setVisibility(GONE);
                mTop.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                break;
            case VideoPlayer.STATE_PREPARED:
                startUpdateProgressTimer();
                break;
            case VideoPlayer.STATE_PLAYING:
                mLoading.setVisibility(GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                startDismissTopBottomTimer();
                break;
            case VideoPlayer.STATE_PAUSED:
                mLoading.setVisibility(GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                cancelDismissTopBottomTimer();
                break;
            case VideoPlayer.STATE_BUFFERING_PLAYING:
                mLoading.setVisibility(VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                mLoadText.setText("正在缓冲...");
                startDismissTopBottomTimer();
                break;
            case VideoPlayer.STATE_BUFFERING_PAUSED:
                mLoading.setVisibility(VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                mLoadText.setText("正在缓冲...");
                cancelDismissTopBottomTimer();
                break;
            case VideoPlayer.STATE_COMPLETED:
                cancelDismissTopBottomTimer();
                setTopBottomVisible(false);
                mImage.setVisibility(VISIBLE);
                mCompleted.setVisibility(VISIBLE);
                if (mVideoPlayer.isFullScreen()) {
                    mVideoPlayer.exitFullScreen();
                }
                if (mVideoPlayer.isTinyWindow()) {
                    mVideoPlayer.exitTinyWindow();
                }
                break;
            case VideoPlayer.STATE_ERROR:
                cancelDismissTopBottomTimer();
                setTopBottomVisible(false);
                mTop.setVisibility(VISIBLE);
                mError.setVisibility(VISIBLE);
                break;
        }
    }

    private void updateVoiceProgress(int progress) {
        currentVoice = progress;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
    }

    private void updateBrightness(float brightness) {
        WindowManager.LayoutParams lp = Utils.scanForActivity(mContext)
                .getWindow()
                .getAttributes();
        lp.screenBrightness += brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.1) {
            lp.screenBrightness = (float) 0.1;
        }
        Utils.scanForActivity(mContext)
                .getWindow()
                .setAttributes(lp);
    }

    private void startUpdateProgressTimer() {
        cancelDismissTopBottomTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    VideoPlayerController.this.post(() -> updateProgress());
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 300);
    }

    private void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    private void updateProgress() {
        int position = mVideoPlayer.getCurrentPosition();
        int duration = mVideoPlayer.getDuation();
        int bufferPercentage = mVideoPlayer.getBufferPercentage();
        mSeek.setSecondaryProgress(bufferPercentage);
        int progress = (int) (100f * position / duration);
        mSeek.setProgress(progress);
        mPosition.setText(Utils.formatTime(position));
        mDuration.setText(Utils.formatTime(duration));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        cancelDismissTopBottomTimer();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mVideoPlayer.isBufferingPaused() || mVideoPlayer.isPaused()) {
            mVideoPlayer.restart();
        }
        int position = (int) (mVideoPlayer.getDuation() * seekBar.getProgress() / 100f);
        mVideoPlayer.seekTo(position);
        startDismissTopBottomTimer();
    }

    public void reset() {
        topBottomVisible = false;
        cancelDismissTopBottomTimer();
        cancelUpdateProgressTimer();
        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);

        mCenterStart.setVisibility(VISIBLE);
        mImage.setVisibility(VISIBLE);

        mBottom.setVisibility(GONE);
        mFullScreen.setImageResource(R.drawable.ic_player_enlarge);

        mTop.setVisibility(VISIBLE);
        mBack.setVisibility(GONE);

        mLoading.setVisibility(GONE);
        mLoadText.setVisibility(GONE);
        mCompleted.setVisibility(GONE);
    }

    public MoveOrientation getOrientation(float dx, float dy) {
        Log.e(TAG, "dx: " + dx + "------------ dy: " + dy);
        if (Math.abs(dx) > Math.abs(dy)){
            return dx > 0 ? MoveOrientation.LEFT : MoveOrientation.RIGHT;
        }else{
            return dy > 0 ? MoveOrientation.UP : MoveOrientation.DOWN;
        }
    }

    public enum MoveOrientation {
        LEFT,
        RIGHT,
        UP,
        DOWN;
        MoveOrientation() {}
    }
}
