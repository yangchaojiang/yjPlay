package chuangyuan.ycj.yjplay.ima;
import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import chuangyuan.ycj.videolibrary.video.GestureVideoPlayer;
import chuangyuan.ycj.videolibrary.video.MediaSourceBuilder;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.data.DataSource;
public class ImaPlayerActivity extends Activity {
    private ImaAdsLoader adsLoader;
    private GestureVideoPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    private static final String TAG = "OfficeDetailedActivity";
    MediaSourceBuilder mediaSourceBuilder;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coutom3);
        ImaSdkSettings sdkSettings= ImaSdkFactory.getInstance().createImaSdkSettings();
        sdkSettings.setLanguage("zh");
        adsLoader = new ImaAdsLoader( this, Uri.parse(getString(R.string.ad_tag_url)),sdkSettings);
        mediaSourceBuilder=new MediaSourceBuilder(this,new DataSource(this.getApplication()));
        videoPlayerView =   findViewById(R.id.exo_play_context_id);
        exoPlayerManager = new GestureVideoPlayer(this,mediaSourceBuilder,videoPlayerView );
        videoPlayerView.setTitle("视频标题");
        videoPlayerView.setExoPlayWatermarkImg(R.mipmap.watermark_big);
        MediaSource contentMediaSource =mediaSourceBuilder.initMediaSource( Uri.parse(getString(R.string.uri_test_6)));
        // Compose the content media source into a new AdsMediaSource with both ads and content.
        MediaSource mediaSourceWithAds = new AdsMediaSource(contentMediaSource, mediaSourceBuilder.getDataSource(),
                adsLoader, videoPlayerView.getPlayerView().getOverlayFrameLayout());
        mediaSourceBuilder.setMediaSource(mediaSourceWithAds);
        Glide.with(this)
                .load(getString(R.string.uri_test_image))
                .fitCenter()
                .placeholder(R.mipmap.test)
                .into(videoPlayerView.getPreviewImage());

    }

    @Override
    public void onResume() {
        super.onResume();
        exoPlayerManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        exoPlayerManager.onPause();
    }


    @Override
    protected void onDestroy() {
        exoPlayerManager.onDestroy();
        super.onDestroy();
        if (adsLoader!=null){
            adsLoader.release();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        exoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (exoPlayerManager.onBackPressed()) {
            ActivityCompat.finishAfterTransition(this);

        }
    }

}
