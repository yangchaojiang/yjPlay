package chuangyuan.ycj.yjplay.media;

import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.source.ads.AdsLoader;

import java.io.IOException;

/**
 * author  yangc
 * date 2018/3/15
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class MyAdsLoader implements AdsLoader {
    @Override
    public void setSupportedContentTypes(int... contentTypes) {

    }

    @Override
    public void attachPlayer(ExoPlayer player, EventListener eventListener, ViewGroup adUiViewGroup) {
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
