package com.list.videoplayer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.list.videoplayer.utils.Utils;

import java.io.IOException;
import java.util.Map;

/**
 * @author Jarvis
 * @version 1.0
 * @title VideoPlayer
 * @description 该类主要功能描述
 * @create 2017/6/1 下午12:21
 * @changeRecord [修改记录] <br/>
 */

public class VideoPlayer extends FrameLayout implements IVideoPlayerControl, TextureView.SurfaceTextureListener {

    private static final String TAG = "VideoPlayer";

    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;

    public static final int STATE_BUFFERING_PLAYING = 5;
    public static final int STATE_BUFFERING_PAUSED = 6;

    public static final int STATE_COMPLETED = 7;

    public static final int PLAYER_NORMAL = 10;
    public static final int PLAYER_FULL_SCREEN = 11;
    public static final int PLAYER_TINY_WINDOW = 12;

    private int mCurrentState = STATE_IDLE;
    private int mPlayerState = PLAYER_NORMAL;

    private Context mContext;
    private FrameLayout mContainer;
    private TextureView mTextureView;
    private VideoPlayerController mController;
    private SurfaceTexture mSurfaceTexture;
    private String mUrl;
    private Map<String, String> mHeaders;
    private MediaPlayer mMediaPlayer;

    private int mBufferPercentage;

    public VideoPlayer(Context context) {
        super(context);
        initView(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        this.mContainer = new FrameLayout(mContext);
        this.mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        this.addView(mContainer, params);
    }

    public void setData(String url, Map<String, String> headers) {
        this.mUrl = url;
        this.mHeaders = headers;
    }

    public void setController(VideoPlayerController controller) {
        this.mController = controller;
        this.mController.setVideoPlayer(this);
        this.mContainer.removeView(mController);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        mContainer.addView(mController, params);
    }

    public void initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);

            mMediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                mCurrentState = STATE_PREPARED;
                mController.setControllerState(mPlayerState, mCurrentState);
                Log.e(TAG, "onPrepared ——> STATE_PREPARED");
            });
            mMediaPlayer.setOnVideoSizeChangedListener((mp, width, height) -> {
                Log.e(TAG, "onVideoSizeChanged ——> width：" + width + "，height：" + height);
            });
            mMediaPlayer.setOnCompletionListener(mp -> {
                mCurrentState = STATE_COMPLETED;
                mController.setControllerState(mPlayerState, mCurrentState);
                VideoPlayerManager.getInstance().setCurrentVideoPlayer(null);
                Log.e(TAG, "onCompletion ——> STATE_COMPLETED");
            });
            mMediaPlayer.setOnErrorListener((mp, what, extra) -> {
                mCurrentState = STATE_ERROR;
                mController.setControllerState(mPlayerState, mCurrentState);
                Log.e(TAG, "onError ——> STATE_ERROR ———— what：" + what);
                return false;
            });
            mMediaPlayer.setOnInfoListener((mp, what, extra) -> {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    mCurrentState = STATE_PLAYING;
                    mController.setControllerState(mPlayerState, mCurrentState);
                    Log.e(TAG, "onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING");
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    if (mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED) {
                        mCurrentState = STATE_BUFFERING_PAUSED;
                        Log.e(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED");
                    } else {
                        mCurrentState = STATE_BUFFERING_PLAYING;
                        Log.e(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING");
                    }
                    mController.setControllerState(mPlayerState, mCurrentState);
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    if (mCurrentState == STATE_BUFFERING_PLAYING) {
                        mCurrentState = STATE_PLAYING;
                        Log.e(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_PLAYING");
                    } else if (mCurrentState == STATE_BUFFERING_PAUSED) {
                        mCurrentState = STATE_PAUSED;
                        Log.e(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_PAUSED");
                    }
                    mController.setControllerState(mPlayerState, mCurrentState);
                } else {
                    Log.e(TAG, "onInfo ——> WHAT：" + what);
                }
                return true;
            });

            mMediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
                this.mBufferPercentage = percent;
            });

        }
    }

    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new TextureView(mContext);
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    private void addTextureView() {
        mContainer.removeView(mTextureView);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        mContainer.addView(mTextureView, 0, params);
    }

    private void openMediaPlayer() {
        try {
            mMediaPlayer.setDataSource(mContext.getApplicationContext(), Uri.parse(mUrl), mHeaders);
            mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            mController.setControllerState(mPlayerState, mCurrentState);
            Log.e(TAG, "STATE_PREPARING");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
            openMediaPlayer();
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void start() {
        VideoPlayerManager.getInstance().releaseVideoPlayer();
        VideoPlayerManager.getInstance().setCurrentVideoPlayer(this);
        if (mCurrentState == STATE_IDLE
                || mCurrentState == STATE_ERROR
                || mCurrentState == STATE_COMPLETED) {
            initMediaPlayer();
            initTextureView();
            addTextureView();
        }
    }

    @Override
    public void restart() {
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            mController.setControllerState(mPlayerState, mCurrentState);
            Log.e(TAG, "STATE_PLAYING");
        }
        if (mCurrentState == STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_BUFFERING_PLAYING;
            mController.setControllerState(mPlayerState, mCurrentState);
            Log.e(TAG, "STATE_BUFFERING_PLAYING");
        }
    }

    @Override
    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            mController.setControllerState(mPlayerState, mCurrentState);
            Log.e(TAG, "STATE_PAUSED");
        }
        if (mCurrentState == STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_BUFFERING_PAUSED;
            mController.setControllerState(mPlayerState, mCurrentState);
            Log.e(TAG, "STATE_BUFFERING_PAUSED");
        }
    }

    @Override
    public void seekTo(int pos) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(pos);
        }
    }

    @Override
    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }

    @Override
    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public boolean isFullScreen() {
        return mPlayerState == PLAYER_FULL_SCREEN;
    }

    @Override
    public boolean isTinyWindow() {
        return mPlayerState == PLAYER_TINY_WINDOW;
    }

    @Override
    public boolean isNormal() {
        return mPlayerState == PLAYER_NORMAL;
    }

    @Override
    public int getDuation() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public int getBufferPercentage() {
        return mBufferPercentage;
    }

    @Override
    public void enterFullScreen() {
        if (mPlayerState == PLAYER_FULL_SCREEN)
            return;
        Utils.hideActionBar(mContext);
        Utils.scanForActivity(mContext)
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.removeView(mContainer);
        ViewGroup contentView = (ViewGroup) Utils.scanForActivity(mContext)
                .findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        contentView.addView(mContainer, params);

        mPlayerState = PLAYER_FULL_SCREEN;
        mController.setControllerState(mPlayerState, mCurrentState);
        Log.e(TAG, "PLAYER_FULL_SCREEN");
    }

    @Override
    public boolean exitFullScreen() {
        if (mPlayerState == PLAYER_FULL_SCREEN) {
            Utils.showActionBar(mContext);
            Utils.scanForActivity(mContext)
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            ViewGroup contentView = (ViewGroup) Utils.scanForActivity(mContext)
                    .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            this.addView(mContainer, params);

            mPlayerState = PLAYER_NORMAL;
            mController.setControllerState(mPlayerState, mCurrentState);
            Log.e(TAG, "PLAYER_NORMAL");
            return true;
        }
        return false;
    }

    @Override
    public void enterTinyWindow() {
        if (mPlayerState == PLAYER_TINY_WINDOW) return;
        this.removeView(mContainer);

        ViewGroup contentView = (ViewGroup) Utils.scanForActivity(mContext)
                .findViewById(android.R.id.content);
        // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp。
        LayoutParams params = new LayoutParams(
                (int) (Utils.getScreenWidth(mContext) * 0.6f),
                (int) (Utils.getScreenWidth(mContext) * 0.6f * 9f / 16f));
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.rightMargin = Utils.dp2px(mContext, 8f);
        params.bottomMargin = Utils.dp2px(mContext, 8f);

        contentView.addView(mContainer, params);

        mPlayerState = PLAYER_TINY_WINDOW;
        mController.setControllerState(mPlayerState, mCurrentState);
    }

    @Override
    public boolean exitTinyWindow() {
        if (mPlayerState == PLAYER_TINY_WINDOW) {
            ViewGroup contentView = (ViewGroup) Utils.scanForActivity(mContext)
                    .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);

            mPlayerState = PLAYER_NORMAL;
            mController.setControllerState(mPlayerState, mCurrentState);
            Log.d(TAG, "PLAYER_NORMAL");
            return true;
        }
        return false;
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mContainer.removeView(mTextureView);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mController != null) {
            mController.reset();
        }
        mCurrentState = STATE_IDLE;
        mPlayerState = PLAYER_NORMAL;
    }
}
