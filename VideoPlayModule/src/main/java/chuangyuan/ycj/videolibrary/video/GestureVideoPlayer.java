package chuangyuan.ycj.videolibrary.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import java.util.Formatter;
import java.util.Locale;
import chuangyuan.ycj.videolibrary.R;

/**
 * Created by yangc on 2017/2/28.
 * E-Mail:1007181167@qq.com
 * Description：增加手势播放器
 */
public class GestureVideoPlayer extends ExoUserPlayer implements View.OnTouchListener {
    public static final String TAG = "GestureVideoPlayer";
    private int mMaxVolume;//音量的最大值
    private float brightness = -1;//亮度
    private int volume = -1;//音量
    private long newPosition = -1;//动画
    protected AudioManager audioManager;//音量管理
    private View exo_video_audio_brightness_layout;//控制音频和亮度布局
    private ImageView exo_video_audio_brightness_img;//显示音频和亮度布图片
    private ProgressBar exo_video_audio_brightness_pro;//显示音频和亮度
    private View exo_video_dialog_pro_layout;//控制进度布局
    private ImageView exo_video_dialog_pro_img;//显示进度
    private TextView exo_video_dialog_pro_text, exo_video_dialog_duration_text;//显示进度是text
    private GestureDetector gestureDetector;
    private int screenWidthPixels;
    private StringBuilder formatBuilder;
    private Formatter formatter;

    public GestureVideoPlayer(@NonNull Activity activity, @NonNull String url) {
        super(activity, url);
        intiView();
    }

    public GestureVideoPlayer(@NonNull Activity activity, @NonNull SimpleExoPlayerView playerView) {
        super(activity, playerView);
        intiView();
    }

    public GestureVideoPlayer(@NonNull Activity activity) {
        super(activity);
        intiView();
    }

    private void intiView() {
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        exo_video_audio_brightness_layout = activity.findViewById(R.id.exo_video_audio_brightness_layout);
        exo_video_audio_brightness_img = (ImageView) activity.findViewById(R.id.exo_video_audio_brightness_img);
        exo_video_audio_brightness_pro = (ProgressBar) activity.findViewById(R.id.exo_video_audio_brightness_pro);
        exo_video_dialog_pro_layout = activity.findViewById(R.id.exo_video_dialog_pro_layout);
        exo_video_dialog_pro_img = (ImageView) activity.findViewById(R.id.exo_video_dialog_pro_img);
        exo_video_dialog_pro_text = (TextView) activity.findViewById(R.id.exo_video_dialog_pro_text);
        exo_video_dialog_duration_text = (TextView) activity.findViewById(R.id.exo_video_dialog_duration_text);
        screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;
        gestureDetector = new GestureDetector(activity, new PlayerGestureListener());
    }

    @Override
    void playVideoUri() {
        super.playVideoUri();
        playerView.setOnTouchListener(this);
    }

    @Override
    void showReplayView(int state) {
        super.showReplayView(state);
        if (state == View.VISIBLE) {
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


    /***
     * 格式化时间
     * @param timeMs 数
     * **/
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
    @Override
    public void endGesture() {
        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            player.seekTo(newPosition);
            newPosition = -1;
        }
        exo_video_audio_brightness_layout.setVisibility(View.GONE);
        exo_video_dialog_pro_layout.setVisibility(View.GONE);
    }

    /****
     * 改变进度
     *
     * @param deltaX 滑动
     **/
    @SuppressLint("SetTextI18n")
    @Override
    public void showProgressDialog(float deltaX, String seekTime, long seekTimePosition,
                                   String totalTime, long totalTimeDuration) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
        Log.d(TAG, "currentTimeline:" + player.getDuration() + "");
        Log.d(TAG, "newPosition:" + player.getDuration() + "");
        Log.d(TAG, seekTime);
        Log.d(TAG, totalTime);
        newPosition = seekTimePosition;
        exo_video_dialog_pro_layout.setVisibility(View.VISIBLE);
        exo_video_dialog_pro_text.setText(seekTime);
        exo_video_dialog_duration_text.setText("/" + totalTime);
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent percent 滑动
     */
    @Override
    public void showVolumeDialog(float percent) {
        super.showVolumeDialog(percent);
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;
        // 变更进度条
        // int i = (int) (index * 1.5 / mMaxVolume * 100);
        //  String s = i + "%";
        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        exo_video_audio_brightness_layout.setVisibility(View.VISIBLE);
        exo_video_audio_brightness_pro.setMax(mMaxVolume);
        exo_video_audio_brightness_pro.setProgress(index);
        exo_video_audio_brightness_img.setImageResource(index == 0 ? R.drawable.ic_volume_off_white_48px : R.drawable.ic_volume_up_white_48px);
    }

    /**
     * 滑动改变亮度
     *
     * @param percent 值大小
     */
    @Override
    public synchronized void showBrightnessDialog(float percent) {
        if (brightness < 0) {
            brightness = activity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        //    $.id(R.id.app_video_brightness_box).visible();
        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        activity.getWindow().setAttributes(lpa);
        if (!exo_video_audio_brightness_layout.isShown()) {
            exo_video_audio_brightness_layout.setVisibility(View.VISIBLE);
            exo_video_audio_brightness_pro.setMax(100);
            exo_video_audio_brightness_img.setImageResource(R.drawable.ic_brightness_6_white_48px);
        }
        exo_video_audio_brightness_pro.setProgress((int) (lpa.screenBrightness * 100));
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
                if (mediaSourceBuilder == null) return false;
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

}
