package chuangyuan.ycj.videolibrary.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import chuangyuan.ycj.videolibrary.R;

/**
 * Created by yangc on 2017/2/28.
 * E-Mail:1007181167@qq.com
 * Description：增加手势播放器
 */
public class GestureVideoPlayer extends ExoUserPlayer {
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


    public GestureVideoPlayer(Activity activity,String url) {
        super(activity, url);
        intiView();
    }

    public GestureVideoPlayer(Activity activity, SimpleExoPlayerView playerView) {
        super(activity, playerView);
        intiView();
    }

    public GestureVideoPlayer(Activity activity) {
        super(activity);
        intiView();
    }
    private void intiView() {
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        exo_video_audio_brightness_layout = activity.findViewById(R.id.exo_video_audio_brightness_layout);
        exo_video_audio_brightness_img = (ImageView) activity.findViewById(R.id.exo_video_audio_brightness_img);
        exo_video_audio_brightness_pro = (ProgressBar) activity.findViewById(R.id.exo_video_audio_brightness_pro);
        exo_video_dialog_pro_layout = activity.findViewById(R.id.exo_video_dialog_pro_layout);
        exo_video_dialog_pro_img = (ImageView) activity.findViewById(R.id.exo_video_dialog_pro_img);
        exo_video_dialog_pro_text = (TextView) activity.findViewById(R.id.exo_video_dialog_pro_text);
        exo_video_dialog_duration_text = (TextView) activity.findViewById(R.id.exo_video_dialog_duration_text);

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


}
