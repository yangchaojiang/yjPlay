package chuangyuan.ycj.videolibrary.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.google.android.exoplayer2.util.Util;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureBrightnessListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureProgressListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureVolumeListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * author yangc
 * date 2017/2/28
 * E-Mail:1007181167@qq.com
 * Description：增加手势播放器
 */
public class GestureVideoPlayer extends ExoUserPlayer {
    private static final String TAG = GestureVideoPlayer.class.getName();
    /***音量的最大值***/
    private int mMaxVolume;
    /*** 亮度值 ***/
    private float brightness = -1;
    /**** 当前音量  ***/
    private int volume = -1;
    /*** 新的播放进度 ***/
    private long newPosition = -1;
    /*** 音量管理 ***/
    private AudioManager audioManager;
    /*** 手势操作管理 ***/
    private final GestureDetector gestureDetector;
    /*** 屏幕最大宽度 ****/
    private int screeHeightPixels;
    /***格式字符 ****/
    private StringBuilder formatBuilder;
    /***格式化类 ***/
    private Formatter formatter;
    private boolean controllerHideOnTouch = true;
    /***手势进度接口实例 ***/
    private OnGestureProgressListener onGestureProgressListener;
    /***手势亮度接口实例 ***/
    private OnGestureBrightnessListener onGestureBrightnessListener;
    /***手势音频接口实例***/
    private OnGestureVolumeListener onGestureVolumeListener;

    /**
     * Instantiates a new Gesture video player.
     *
     * @param activity   the activity
     * @param playerView the player view
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
     */
    public GestureVideoPlayer(@NonNull Activity activity, @NonNull VideoPlayerView playerView) {
        this(activity, playerView, null);
    }

    /**
     * Instantiates a new Gesture video player.
     *
     * @param activity the activity
     * @param reId     the re id
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
     */
    public GestureVideoPlayer(@NonNull Activity activity, @IdRes int reId) {
        this(activity, reId, null);
    }

    /**
     * Instantiates a new Gesture video player.
     *
     * @param activity the activity
     * @param reId     the re id
     * @param listener the listener
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
     */
    public GestureVideoPlayer(@NonNull Activity activity, @IdRes int reId, @Nullable DataSourceListener listener) {
        this(activity, (VideoPlayerView) activity.findViewById(reId), listener);
    }

    /**
     * Instantiates a new Gesture video player.
     *
     * @param activity   the activity
     * @param playerView the player view
     * @param listener   the listener
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
     */
    public GestureVideoPlayer(@NonNull Activity activity, @NonNull VideoPlayerView playerView, @Nullable DataSourceListener listener) {
        super(activity, playerView, listener);
        intiViews();
        gestureDetector = new GestureDetector(activity, new PlayerGestureListener(this));
    }

    /**
     * Instantiates a new Gesture video player.
     *
     * @param activity           the activity
     * @param mediaSourceBuilder the media source builder
     * @param playerView         the player view
     * @deprecated Use {@link VideoPlayerManager.Builder} instead.
     */
    public GestureVideoPlayer(@NonNull Activity activity, @NonNull MediaSourceBuilder mediaSourceBuilder, @NonNull VideoPlayerView playerView) {
        super(activity, mediaSourceBuilder, playerView);
        intiViews();
        gestureDetector = new GestureDetector(activity, new PlayerGestureListener(this));
    }

    private void intiViews() {
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        screeHeightPixels = activity.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public void onPlayNoAlertVideo() {
        super.onPlayNoAlertVideo();
        getPlayerViewListener().setPlatViewOnTouchListener(listener);
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            if (onGestureProgressListener != null) {
                onGestureProgressListener.endGestureProgress(newPosition);
                newPosition = -1;
            } else {
                player.seekTo(newPosition);
                newPosition = -1;
            }
        }
        getPlayerViewListener().showGestureView(View.GONE);
    }

    /****
     * 滑动进度
     *
     * @param  seekTimePosition  滑动的时间
     * @param  duration         视频总长
     * @param  seekTime    滑动的时间 格式化00:00
     * @param  totalTime    视频总长 格式化00:00
     **/
    private void showProgressDialog(long seekTimePosition, long duration, String seekTime, String totalTime) {
        newPosition = seekTimePosition;
        if (onGestureProgressListener != null) {
            onGestureProgressListener.showProgressDialog(seekTimePosition, duration, seekTime, totalTime);
        } else {
            String stringBuilder = seekTime + "/" + totalTime;
            ForegroundColorSpan blueSpan = new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.simple_exo_style_color));
            SpannableString spannableString = new SpannableString(stringBuilder);
            spannableString.setSpan(blueSpan, 0, seekTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getPlayerViewListener().setTimePosition(spannableString);
        }
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent percent 滑动
     */
    private void showVolumeDialog(float percent) {
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0) {
                volume = 0;
            }
        }
        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume) {
            index = mMaxVolume;
        } else if (index < 0) {
            index = 0;
        }
        // 变更进度条 // int i = (int) (index * 1.5 / mMaxVolume * 100);
        //  String s = i + "%";  // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        if (onGestureVolumeListener != null) {
            onGestureVolumeListener.setVolumePosition(mMaxVolume, index);
        } else {
            getPlayerViewListener().setVolumePosition(mMaxVolume, index);
        }
    }

    /**
     * 滑动改变亮度
     *
     * @param percent 值大小
     */
    private synchronized void showBrightnessDialog(float percent) {
        if (brightness < 0) {
            brightness = activity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        activity.getWindow().setAttributes(lpa);
        if (onGestureBrightnessListener != null) {
            onGestureBrightnessListener.setBrightnessPosition(100, (int) (lpa.screenBrightness * 100));
        } else {
            getPlayerViewListener().setBrightnessPosition(100, (int) (lpa.screenBrightness * 100));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioManager = null;
        formatBuilder = null;
        if (formatter != null) {
            formatter.close();
        }
        formatter = null;
        onGestureBrightnessListener = null;
        onGestureProgressListener = null;
        onGestureVolumeListener = null;
        listener = null;
    }

    /***
     * 设置手势touch 事件
     * @param controllerHideOnTouch true 启用  false 关闭
     */
    public void setPlayerGestureOnTouch(boolean controllerHideOnTouch) {
        this.controllerHideOnTouch = controllerHideOnTouch;
    }

    /***
     * 实现自定义进度监听事件
     * @param onGestureProgressListener 实例
     */
    public void setOnGestureProgressListener(OnGestureProgressListener onGestureProgressListener) {
        this.onGestureProgressListener = onGestureProgressListener;
    }

    /***
     * 实现自定义亮度手势监听事件
     * @param onGestureBrightnessListener 实例
     */
    public void setOnGestureBrightnessListener(OnGestureBrightnessListener onGestureBrightnessListener) {
        this.onGestureBrightnessListener = onGestureBrightnessListener;
    }

    /***
     * 实现自定义音频手势监听事件
     * @param onGestureVolumeListener 实例
     */
    public void setOnGestureVolumeListener(OnGestureVolumeListener onGestureVolumeListener) {
        this.onGestureVolumeListener = onGestureVolumeListener;
    }

    private View.OnTouchListener listener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!controllerHideOnTouch) {
                return false;
            } else if (getPlayerViewListener().isLock()) {
                return false;
            } else if (!VideoPlayUtils.isLand(activity)) {
                //竖屏不执行手势
                return false;
            }
            if (gestureDetector != null && gestureDetector.onTouchEvent(event)) {
                return true;
            }
            // 处理手势结束
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    endGesture();
                    break;
                default:
            }
            return false;
        }
    };

    /****
     * 手势监听类
     *****/
    private class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean firstTouch;
        private boolean volumeControl;
        private boolean toSeek;
        private WeakReference<GestureVideoPlayer> weakReference;

        private PlayerGestureListener(GestureVideoPlayer gestureVideoPlayer) {
            weakReference = new WeakReference<>(gestureVideoPlayer);
        }

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
            if (null == weakReference || weakReference.get() == null) {
                return false;
            }
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = mOldX > screeHeightPixels * 0.5f;
                firstTouch = false;
            }
            if (toSeek) {
                deltaX = -deltaX;
                long position = player.getCurrentPosition();
                long duration = player.getDuration();
                long newPosition = (int) (position + deltaX * duration / screeHeightPixels);
                if (newPosition > duration) {
                    newPosition = duration;
                } else if (newPosition <= 0) {
                    newPosition = 0;
                }
                showProgressDialog(newPosition, duration, Util.getStringForTime(formatBuilder, formatter, newPosition), Util.getStringForTime(formatBuilder, formatter, duration));
            } else {
                float percent = deltaY / getPlayerViewListener().getHeight();
                if (volumeControl) {
                    showVolumeDialog(percent);
                } else {
                    showBrightnessDialog(percent);
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}
