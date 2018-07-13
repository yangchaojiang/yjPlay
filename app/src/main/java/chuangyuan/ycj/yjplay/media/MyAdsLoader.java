package chuangyuan.ycj.yjplay.media;

import android.net.Uri;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsLoader;

import java.io.IOException;

import chuangyuan.ycj.videolibrary.video.MediaSourceBuilder;
import chuangyuan.ycj.yjplay.R;

/**
 * author  yangc
 * date 2018/3/15
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class MyAdsLoader implements AdsLoader {
    MediaSourceBuilder mediaSourceBuilder;
    public MyAdsLoader( MediaSourceBuilder mediaSourceBuilder){
         this.mediaSourceBuilder=mediaSourceBuilder;
    }
    @Override
    public void setSupportedContentTypes(int... contentTypes) {

    }

    @Override
    public void attachPlayer(ExoPlayer player, EventListener eventListener, ViewGroup adUiViewGroup) {
        Log.d("MyAdsLoader","MyAdsLoader");
        MediaSource source =mediaSourceBuilder.initMediaSource(Uri.parse(adUiViewGroup.getContext().getString(R.string.uri_test_9)));
        player.prepare(source );
    }

    @Override
    public void detachPlayer() {

    }

    @Override
    public void release() {

    }

    @Override
    public void handlePrepareError(int adGroupIndex, int adIndexInAdGroup, IOException exception) {

    }
}
