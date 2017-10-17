

package chuangyuan.ycj.videolibrary.video;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.listener.ItemVideo;

/**
 *
 * @author yangc
 * date 2017/2/28
 * E-Mail:1007181167@qq.com
 * Description：数据源处理类
 */
public final class MediaSourceBuilder {
    private static final String TAG = MediaSourceBuilder.class.getName();
    private Context context;
    private Handler mainHandler = null;
    private MediaSource mediaSource;
    private DataSourceListener listener;
    private AdaptiveMediaSourceEventListener adaptiveMediaSourceEventListener;

    /***
     * 初始化
     *
     * @param listener 自定义数源工厂接口
     **/
    public MediaSourceBuilder(@Nullable DataSourceListener listener) {
        this.listener = listener;
    }

    /****
     * 初始化多个视频源，无缝衔接
     *
     * @param firstVideoUri  第一个视频， 例如例如广告视频
     * @param secondVideoUri 第二个视频
     ***/
    void setMediaSourceUri(@NonNull Context context, @NonNull String firstVideoUri, @NonNull String secondVideoUri) {
        setMediaSourceUri(context, Uri.parse(firstVideoUri), Uri.parse(secondVideoUri));

    }

    /****
     * 初始化多个视频源，无缝衔接
     *
     * @param firstVideoUri  第一个视频， 例如例如广告视频
     * @param secondVideoUri 第二个视频
     ***/
    void setMediaSourceUri(@NonNull Context context, @NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
        this.context = context;
        mainHandler = new Handler();
        initDataConcatenatingMediaSource(Util.inferContentType(secondVideoUri.getLastPathSegment()), firstVideoUri, secondVideoUri);
    }

    /****
     * 初始化
     *
     * @param context 上下文
     * @param uri     视频的地址
     ***/
    void setMediaSourceUri(@NonNull Context context, @NonNull Uri uri) {
        this.context = context;
        mainHandler = new Handler();
        initData(uri);
    }

    /****
     * 初始化
     *
     * @param context 上下文
     * @param uris     视频的地址列表
     ***/
    void setMediaSourceUri(@NonNull Context context, @NonNull Uri... uris) {
        MediaSource[] firstSources = new MediaSource[uris.length];
        this.context = context;
        mainHandler = new Handler();
        int i = 0;
        for (Uri item : uris) {
            firstSources[i] = initData(item);
            i++;
        }
        mediaSource = new ConcatenatingMediaSource(firstSources);
    }

    /****
     * 初始化
     *
     * @param context 上下文
     * @param uris     视频的地址列表
     ***/
    void setMediaSourceUri(@NonNull Context context, @NonNull List<ItemVideo> uris) {
        MediaSource[] firstSources = new MediaSource[uris.size()];
        this.context = context;
        mainHandler = new Handler();
        int i = 0;
        for (ItemVideo item : uris) {
            if (item != null && item.getVideoUri() != null) {
                firstSources[i] = initData(Uri.parse(item.getVideoUri()));
            }
            i++;
        }
        mediaSource = new ConcatenatingMediaSource(firstSources);
    }

    /****
     * 初始化多个视频源，无缝衔接
     *
     * @param firstVideoUri  第一个视频， 例如广告视频
     * @param secondVideoUri 第二个视频
     ***/
    private void initDataConcatenatingMediaSource(int streamType, Uri firstVideoUri, Uri secondVideoUri) {
        MediaSource firstSource = initData(firstVideoUri);
        switch (streamType) {
            case C.TYPE_SS:
                MediaSource secondSource = new SsMediaSource(secondVideoUri, new DefaultDataSourceFactory(context, null, getDataSource()),
                        new DefaultSsChunkSource.Factory(getDataSource()),
                        mainHandler, adaptiveMediaSourceEventListener);
                mediaSource = new ConcatenatingMediaSource(firstSource, secondSource);
                break;
            case C.TYPE_DASH:
                secondSource = new DashMediaSource(secondVideoUri,
                        new DefaultDataSourceFactory(context, null, getDataSource()),
                        new DefaultDashChunkSource.Factory(getDataSource()),
                        mainHandler, adaptiveMediaSourceEventListener);
                mediaSource = new ConcatenatingMediaSource(firstSource, secondSource);
                break;
            case C.TYPE_HLS:
                secondSource = new HlsMediaSource(secondVideoUri, getDataSource(), mainHandler, null);
                mediaSource = new ConcatenatingMediaSource(firstSource, secondSource);
                break;
            case C.TYPE_OTHER:
                secondSource = new ExtractorMediaSource(secondVideoUri, getDataSource(),
                        new DefaultExtractorsFactory(), mainHandler, null);
                mediaSource = new ConcatenatingMediaSource(firstSource, secondSource);
                break;
            default: {
                throw new IllegalStateException("Unsupported type: " + streamType);
            }
        }
    }

    /***
     * 字幕文件设置
     * **/
    private void subtitle(Uri uri, Uri subtitleUri) {
        // Build the video MediaSource.
        Format subtitleFormat = Format.createTextSampleFormat(null, // An identifier for the track. May be null.
                MimeTypes.APPLICATION_SUBRIP, // The mime type. Must be set correctly.
                1, // Selection flags for the track.
                null); // The subtitle language. May be null.
        MediaSource subtitleSource = new SingleSampleMediaSource(subtitleUri, getDataSource(), subtitleFormat, C.TIME_UNSET);
// Plays the video with the sideloaded subtitle.
        mediaSource = new MergingMediaSource(initData(uri), subtitleSource);
    }


    /****
     * 初始化视频源，无缝衔接
     *
     * @param uri        视频的地址
     ***/
    private MediaSource initData(Uri uri) {
        int streamType = Util.inferContentType(uri);
        switch (streamType) {
            case C.TYPE_SS:
                mediaSource = new SsMediaSource(uri, new DefaultDataSourceFactory(context, null,
                        getDataSource()),
                        new DefaultSsChunkSource.Factory(getDataSource()),
                        mainHandler, adaptiveMediaSourceEventListener);
                break;
            case C.TYPE_DASH:
                mediaSource = new DashMediaSource(uri, new DefaultDataSourceFactory(context, null, getDataSource()),
                        new DefaultDashChunkSource.Factory(getDataSource()), mainHandler, adaptiveMediaSourceEventListener);
                break;
            case C.TYPE_HLS:
                mediaSource = new HlsMediaSource(uri, new DefaultHlsDataSourceFactory(getDataSource()), 5, mainHandler, adaptiveMediaSourceEventListener);
                break;
            case C.TYPE_OTHER:
                mediaSource = new ExtractorMediaSource(uri, getDataSource(), new DefaultExtractorsFactory(), mainHandler, null);
                break;
            default:
                throw new IllegalStateException("Unsupported type: " + streamType);
        }
        return mediaSource;
    }

    /**
     * 返回循环播放实例
     *
     * @return LoopingMediaSource
     ***/
     LoopingMediaSource setLooping(@Size(min = 1) int loopCount) {
        return new LoopingMediaSource(mediaSource, loopCount);
    }

    /***
     * 获取视频类型
     **/
    MediaSource getMediaSource() {
        return mediaSource;
    }

    /***
     * 获取链接类型
     *
     * @return int
     ***/
    int getStreamType() {
        return 1;
    }

    /***
     * 初始化数据源工厂
     **/
    private DataSource.Factory getDataSource() {
        Log.d(TAG, "Factory:" + (listener == null));
        if (listener != null) {
            return listener.getDataSourceFactory();
        } else {
            return new DefaultDataSourceFactory(context, context.getPackageName());
        }

    }

    /****
     * 自定义data数据源
     *
     * @param listener 接口实现
     **/
    public void setListener(@Nullable DataSourceListener listener) {
        this.listener = listener;
    }

    /****
     * 释放资源
     **/
    public void release() {
        if (mediaSource != null) {
            mediaSource.releaseSource();
        }
        if (mainHandler != null) {
            mainHandler = null;
        }
    }

}
