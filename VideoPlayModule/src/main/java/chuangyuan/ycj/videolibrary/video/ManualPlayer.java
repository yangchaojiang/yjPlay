package chuangyuan.ycj.videolibrary.video;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import com.google.android.exoplayer2.util.Util;
import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
/**
 * Created by yangc on 2017/2/27.
 * E-Mail:1007181167@qq.com
 * Description： 手动播放播放器
 */
public class ManualPlayer extends GestureVideoPlayer {
    public static final String TAG = "ManualPlayer";
    private boolean isLoad = false;//已经加载
   private ImageButton exoBtn;

    public ManualPlayer(@NonNull Activity activity, VideoPlayerView playerView, @NonNull String uri) {
        super(activity, playerView, uri);
        intiView();
    }

    public ManualPlayer(@NonNull Activity activity, @NonNull VideoPlayerView playerView) {
        super(activity, playerView);
        intiView();
    }
    public ManualPlayer(@NonNull Activity activity,@Nullable int reId) {
        super(activity, reId);
        intiView();
    }


    private  void intiView(){
        setExoPlayWatermarkImg(R.mipmap.watermark_big);
        exoBtn= (ImageButton) mPlayerView.findViewById(R.id.exo_play);
        exoBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_UP) {
                    isLoad = true;
                    createPlayers();
                    hslHideView();
                    registerReceiverNet();
                    exoBtn.setOnTouchListener(null);
                }
                return false;
            }
        });

    }
    @Override
    public void setPlayUri(@NonNull Uri uri) {
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(activity.getApplicationContext(), uri);
        createPlayersNo();
    }


    @Override
    public void setPlaySwitchUri(@NonNull String[] videoUri, @NonNull String[] nameUri) {
        this.videoUri=videoUri;
        this.nameUri=nameUri;
       // exo_video_switch.setText(nameUri[0]);
        mPlayerViewListener.showSwitchName(nameUri[0]);
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(activity.getApplicationContext(),Uri.parse(videoUri[0]));
        createPlayersNo();
    }

    @Override
    public void setPlayUri(@NonNull String firstVideoUri, @NonNull String secondVideoUri) {
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(activity.getApplicationContext(), firstVideoUri, secondVideoUri);
        createPlayersNo();
    }

    @Override
    public void playVideo() {
        super.playVideo();
    }

    @Override
    public void onResume() {
        if ((Util.SDK_INT <= 23 || player == null) && isLoad) {
            createPlayers();
        } else {
            createPlayersPlay();
        }
    }
}
