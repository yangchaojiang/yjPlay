package chuangyuan.ycj.videolibrary.whole;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
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
        int streamType = Util.inferContentType(uri);
        switch (streamType) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, new DefaultDataSourceFactory(context, null,
                        getDataSource()),
                        new DefaultSsChunkSource.Factory(getDataSource()),
                        mainHandler, sourceEventListener);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, new DefaultDataSourceFactory(context, null, getDataSource()),
                        new DefaultDashChunkSource.Factory(getDataSource()), mainHandler, sourceEventListener);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, new DefaultHlsDataSourceFactory(getDataSource()), 5, mainHandler, sourceEventListener);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, getDataSource(), new DefaultExtractorsFactory(), mainHandler, null);
            default:
                throw new IllegalStateException("Unsupported type: " + streamType);
        }
    }
}
