

package chuangyuan.ycj.videolibrary.video;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.listener.ItemVideo;

/**
 * @author yangc
 *         date 2017/2/28
 *         E-Mail:1007181167@qq.com
 *         Description：数据源处理类
 */
public class MediaSourceBuilder {
    private static final String TAG = MediaSourceBuilder.class.getName();
    private Context context;
    private Handler mainHandler = null;
    private MediaSource mediaSource;
    private DataSourceListener listener;
    private AdaptiveMediaSourceEventListener adaptiveMediaSourceEventListener;
    private int indexType = -1;
    private List<String> videoUri;
    private List<String> nameUri;

    /***
     * 初始化
     *
     * @param context 下文
     **/
    public MediaSourceBuilder(@NonNull Context context) {
        this(context, null);
    }

    /***
     * 初始化
     *
     * @param listener 自定义数源工厂接口
     **/
    public MediaSourceBuilder(@NonNull Context context, @Nullable DataSourceListener listener) {
        this.listener = listener;
        this.context = context.getApplicationContext();
        mainHandler = new Handler();
    }

    /****
     * 初始化
     *
     * @param uri     视频的地址
     ***/
    void setMediaUri(@NonNull Uri uri) {
        mediaSource= initMediaSource(uri);
    }

    /****
     * 初始化
     *
     * @param uris     视频的地址列表
     ***/
    public void setMediaUri(@NonNull Uri... uris) {
        MediaSource[] firstSources = new MediaSource[uris.length];
        int i = 0;
        for (Uri item : uris) {
            firstSources[i] = initMediaSource(item);
            i++;
        }
        mediaSource = new ConcatenatingMediaSource(firstSources);
    }

    /****
     * 初始化多个视频源，无缝衔接
     *
     * @param firstVideoUri  第一个视频， 例如例如广告视频
     * @param secondVideoUri 第二个视频
     ***/
    void setMediaUri(@NonNull String firstVideoUri, @NonNull String secondVideoUri) {
        setMediaUri(Uri.parse(firstVideoUri), Uri.parse(secondVideoUri));

    }

    /****
     * 初始化多个视频源，无缝衔接
     *
     * @param firstVideoUri  第一个视频， 例如例如广告视频
     * @param secondVideoUri 第二个视频
     ***/
    public void setMediaUri(@NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
        setMediaUri(0, firstVideoUri, secondVideoUri);
    }


    /****
     * @param  indexType  设置当前索引视频屏蔽进度
     * @param firstVideoUri  预览的视频
     * @param secondVideoUri 第二个视频

     **/
    public void setMediaUri(@Size(min = 0) int indexType, @NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
        this.indexType = indexType;
        initDataConcatenatingMediaSource(Util.inferContentType(secondVideoUri.getLastPathSegment()), firstVideoUri, secondVideoUri);
    }

    /**
     * 设置多线路播放
     *
     * @param videoUri 视频地址
     * @param name     清清晰度显示名称
     * @param index    选中播放线路
     **/
    public void setMediaSwitchUri(@NonNull List<String> videoUri, @NonNull List<String> name, int index) {
        this.videoUri = videoUri;
        this.nameUri = name;
        setMediaUri(Uri.parse(videoUri.get(index)));
    }

    /****
     * 初始化
     *
     * @param uris     视频的地址列表
     ***/
    public <T extends ItemVideo> void setMediaUri(@NonNull List<T> uris) {
        MediaSource[] firstSources = new MediaSource[uris.size()];
        mainHandler = new Handler();
        int i = 0;
        for (T item : uris) {
            if (item.getVideoUri() != null) {
                firstSources[i] = initMediaSource(Uri.parse(item.getVideoUri()));
            }
            i++;
        }
        mediaSource = new ConcatenatingMediaSource(firstSources);
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
     * 获取视频数据源
     **/
    MediaSource getMediaSource() {
        return mediaSource;
    }

    /***
     * 设置自定义视频数据源
     * @param mediaSource 你的数据源
     **/
    public void setMediaSource(MediaSource mediaSource) {
        this.mediaSource = mediaSource;
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
     * 释放资源
     **/
    public void release() {
        if (mediaSource != null) {
            mediaSource.releaseSource();
        }
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(context);
            mainHandler = null;
        }
    }

    /****
     * 销毁资源
     **/
    public void destroy() {
        release();
        indexType = -1;
        videoUri = null;
        nameUri = null;
        listener = null;
    }

    /**
     * 获取视频所在索引
     *
     * @return int
     **/
    public int getIndexType() {
        return indexType;
    }

    /**
     * 设置视频所在索引
     *
     * @param indexType 值
     **/
    public void setIndexType(@Size(min = 0) int indexType) {
        this.indexType = indexType;
    }

    /**
     * 获取视频线路地址
     *
     * @return List<String>
     **/
    @NonNull
      List<String> getVideoUri() {
        return videoUri;
    }

    /**
     * 获取视频线路名称
     *
     * @return List<String>
     **/
    @NonNull
      List<String> getNameUri() {
        return nameUri;
    }

    /**
     * 用于通知自适应的回调接口获取视频线路名称
     *
     * @param adaptiveMediaSourceEventListener 实例
     **/
    public void setAdaptiveMediaSourceEventListener(AdaptiveMediaSourceEventListener adaptiveMediaSourceEventListener) {
        this.adaptiveMediaSourceEventListener = adaptiveMediaSourceEventListener;
    }

    /****
     * 初始化多个视频源，无缝衔接
     *
     * @param firstVideoUri  第一个视频， 例如广告视频
     * @param secondVideoUri 第二个视频
     ***/
    private void initDataConcatenatingMediaSource(int streamType, Uri firstVideoUri, Uri secondVideoUri) {
        MediaSource firstSource = initMediaSource(firstVideoUri);
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

    /****
     * 初始化视频源，无缝衔接
     *
     * @param uri        视频的地址
     ***/
    public MediaSource initMediaSource(Uri uri) {
        int streamType = Util.inferContentType(uri);
        switch (streamType) {
            case C.TYPE_SS:
                return   new SsMediaSource(uri, new DefaultDataSourceFactory(context, null,
                        getDataSource()),
                        new DefaultSsChunkSource.Factory(getDataSource()),
                        mainHandler, adaptiveMediaSourceEventListener);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, new DefaultDataSourceFactory(context, null, getDataSource()),
                        new DefaultDashChunkSource.Factory(getDataSource()), mainHandler, adaptiveMediaSourceEventListener);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, new DefaultHlsDataSourceFactory(getDataSource()), 5, mainHandler, adaptiveMediaSourceEventListener);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, getDataSource(), new DefaultExtractorsFactory(), mainHandler, null);
            default:
                throw new IllegalStateException("Unsupported type: " + streamType);
        }
    }
}
