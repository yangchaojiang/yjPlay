package chuangyuan.ycj.videolibrary.video;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.google.android.exoplayer2.C;

import java.util.Formatter;
import java.util.Locale;

import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * @author yangc
 * date 2017/2/28
 * E-Mail:1007181167@qq.com
 * Description：增加手势播放器
 */
public class GestureVideoPlayer extends ExoUserPlayer implements View.OnTouchListener {
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
    private GestureDetector gestureDetector;
    /*** 屏幕最大宽度 ****/
    private int screenWidthPixels;
    /***格式字符 ****/
    private StringBuilder formatBuilder;
    /****格式化类 ***/
    private Formatter formatter;

    public GestureVideoPlayer(@NonNull Activity activity, @NonNull VideoPlayerView playerView) {
        this(activity, playerView, null);
    }

    public GestureVideoPlayer(@NonNull Activity activity, @IdRes int reId) {
        this(activity, reId, null);
    }

    public GestureVideoPlayer(@NonNull Activity activity, @IdRes int reId, @Nullable DataSourceListener listener) {
        this(activity, (VideoPlayerView) activity.findViewById(reId), listener);
    }

    public GestureVideoPlayer(@NonNull Activity activity, @NonNull VideoPlayerView playerView, @Nullable DataSourceListener listener) {
        super(activity, playerView, listener);
        intiViews();
    }

    private void intiViews() {
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;
        gestureDetector = new GestureDetector(activity, new PlayerGestureListener());
    }

    @Override
    public void onPlayNoAlertVideo() {
        super.onPlayNoAlertVideo();
        mPlayerViewListener.setPlatViewOnTouchListener(this);

    }

    @CallSuper
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //竖屏
        if (!VideoPlayUtils.isLand(activity)) {
            //竖屏不执行手势
            return false;
        }
        if (gestureDetector.onTouchEvent(event)) {
            return false;
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

    /***
     * 格式化时间
     *
     * @param timeMs 数
     **/
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

    /**
     * 手势结束
     */
    private void endGesture() {
        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            player.seekTo(newPosition);
            newPosition = -1;
        }
        if (mPlayerViewListener != null) {
            mPlayerViewListener.showGestureView(View.GONE);
        }
    }

    /****
     * 滑动进度
     *
     * @param deltaX            滑动
     * @param seekTime          滑动的时间
     * @param seekTimePosition  滑动的时间 int
     * @param totalTime         视频总长
     * @param totalTimeDuration 视频总长 int
     **/
    private void showProgressDialog(float deltaX, String seekTime, long seekTimePosition,
                                    String totalTime, long totalTimeDuration) {
        Log.d(TAG, "currentTimeline:" + player.getContentPosition() + "");
        Log.d(TAG, "newPosition:" + player.getDuration() + "");
        Log.d(TAG, seekTime);
        Log.d(TAG, totalTime);
        newPosition = seekTimePosition;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(seekTime);
        stringBuilder.append("/");
        stringBuilder.append(totalTime);
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.simple_exo_style_color));
        SpannableString spannableString = new SpannableString(stringBuilder.toString());
        spannableString.setSpan(blueSpan, 0, seekTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (mPlayerViewListener != null) {
            mPlayerViewListener.setTimePosition(spannableString);
        }
        stringBuilder = null;
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
        // 变更进度条
        // int i = (int) (index * 1.5 / mMaxVolume * 100);
        //  String s = i + "%";
        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        if (mPlayerViewListener != null) {
            mPlayerViewListener.setVolumePosition(mMaxVolume, index);
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

        if (mPlayerViewListener != null) {
            mPlayerViewListener.setBrightnessPosition(100, (int) (lpa.screenBrightness * 100));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioManager = null;
        gestureDetector = null;
        formatBuilder = null;
        formatter = null;
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
                assert mediaSourceBuilder != null;
                if (mediaSourceBuilder.getStreamType() == C.TYPE_HLS) {
                    //直播隐藏进度条
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }
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
                float percent = deltaY / mPlayerViewListener.getHeight();
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
