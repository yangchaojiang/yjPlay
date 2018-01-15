package chuangyuan.ycj.videolibrary.whole;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;
import chuangyuan.ycj.videolibrary.video.MediaSourceBuilder;

/**
 * Created by yangc on 2017/11/11
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 实现全多媒体数据类
 */

public class WholeMediaSource extends MediaSourceBuilder {

    public WholeMediaSource(@NonNull Context context) {
        super(context);
    }

    public WholeMediaSource(@NonNull Context context, @Nullable DataSourceListener listener) {
        super(context, listener);
    }


    @Override
    public MediaSource initMediaSource(Uri uri) {
        int streamType = VideoPlayUtils.inferContentType(uri);
        switch (streamType) {
            case C.TYPE_SS:
                return new  SsMediaSource.Factory(new DefaultSsChunkSource.Factory(getDataSource()), new DefaultDataSourceFactory(context, null,
                        getDataSource()))
                        .setMinLoadableRetryCount(5)
                        .createMediaSource(uri,mainHandler,sourceEventListener);
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(getDataSource())
                         ,new DefaultDataSourceFactory(context, null, getDataSource()))
                         .setMinLoadableRetryCount(5)
                         .createMediaSource(uri, mainHandler, sourceEventListener);
            case C.TYPE_OTHER:
                return new  ExtractorMediaSource.Factory( getDataSource())
                         .setExtractorsFactory( new DefaultExtractorsFactory())
                        .setMinLoadableRetryCount(5)
                        .setCustomCacheKey(uri.getPath())
                        .createMediaSource(uri,mainHandler,null);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(new DefaultHlsDataSourceFactory( getDataSource()))
                        .setMinLoadableRetryCount(5)
                        .createMediaSource(uri, mainHandler, sourceEventListener);

            default:
                throw new IllegalStateException(":Unsupported type: " + streamType);
        }
    }
}
