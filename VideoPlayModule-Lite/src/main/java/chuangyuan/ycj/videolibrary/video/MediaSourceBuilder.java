package chuangyuan.ycj.videolibrary.video;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer2.source.ClippingMediaSource;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;

import java.io.IOException;
import java.util.List;

import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.factory.JDefaultDataSourceFactory;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.listener.ItemVideo;
import chuangyuan.ycj.videolibrary.utils.VideoPlayUtils;

/**
 * author yangc
 * date 2017/2/28
 * E-Mail:1007181167@qq.com
 * Description：数据源处理类
 */
public class MediaSourceBuilder {
    private static final String TAG = MediaSourceBuilder.class.getName();
    /*** The Context.*/
    protected Context context;
    private MediaSource mediaSource;
    /*** The Listener. */
    protected DataSourceListener listener;
    private int indexType = -1;
    private List<String> videoUri;
    protected String customCacheKey;

    /***
     * 初始化
     *
     * @param context 上下文
     */
    public MediaSourceBuilder(@NonNull Context context) {
        this(context, null);
    }

    /***
     * 初始化
     *
     * @param context 上下文
     * @param listener 自定义数源工厂接口
     */
    public MediaSourceBuilder(@NonNull Context context, @Nullable DataSourceListener listener) {
        this.listener = listener;
        this.context = context.getApplicationContext();
    }

    /****
     * 初始化
     *
     * @param uri 视频的地址
     */
    void setMediaUri(@NonNull Uri uri) {
        mediaSource = initMediaSource(uri);
    }

    /****
     * 初始化
     *
     * @param uri 视频的地址
     * @param startPositionUs startPositionUs  毫秒
     *@param   endPositionUs endPositionUs       毫秒
     */
    public void setClippingMediaUri(@NonNull Uri uri, long startPositionUs, long endPositionUs) {
        mediaSource = new ClippingMediaSource(initMediaSource(uri), startPositionUs, endPositionUs);
    }

    /****
     * 初始化多个视频源，无缝衔接
     *
     * @param firstVideoUri 第一个视频， 例如例如广告视频
     * @param secondVideoUri 第二个视频
     */
    public void setMediaUri(@NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
        setMediaUri(0, firstVideoUri, secondVideoUri);
    }


    /****
     *  支持视频源动态添加
     *
     * @param videoUri videoUri
     */
    public void addMediaUri(@NonNull Uri videoUri) {
        if (mediaSource == null) {
            mediaSource = new ConcatenatingMediaSource();
        }
        if (mediaSource instanceof ConcatenatingMediaSource) {
            ConcatenatingMediaSource mediaSource2 = (ConcatenatingMediaSource) mediaSource;
            mediaSource2.addMediaSource(initMediaSource(videoUri));
        }
    }

    /****
     * 初始化多个视频源，无缝衔接
     *
     * @param indexType the index type
     * @param switchIndex the switch index
     * @param firstVideoUri 第一个视频， 例如例如广告视频
     * @param secondVideoUri 第二个视频
     */
    public void setMediaUri(@Size(min = 0) int indexType, int switchIndex, @NonNull Uri firstVideoUri, @NonNull List<String> secondVideoUri) {
        this.videoUri = secondVideoUri;
        this.indexType = indexType;
        setMediaUri(indexType, firstVideoUri, Uri.parse(secondVideoUri.get(switchIndex)));
    }

    /****
     * @param indexType 设置当前索引视频屏蔽进度
     * @param firstVideoUri 预览的视频
     * @param secondVideoUri 第二个视频
     */
    public void setMediaUri(@Size(min = 0) int indexType, @NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
        this.indexType = indexType;
        ConcatenatingMediaSource source = new ConcatenatingMediaSource();
        source.addMediaSource(initMediaSource(firstVideoUri));
        source.addMediaSource(initMediaSource(secondVideoUri));
        mediaSource = source;
    }


    /**
     * 设置多线路播放
     *
     * @param videoUri 视频地址
     * @param index    选中播放线路
     */
    public void setMediaSwitchUri(@NonNull List<String> videoUri, int index) {
        this.videoUri = videoUri;
        setMediaUri(Uri.parse(videoUri.get(index)));
    }

    /****
     * 初始化
     *
     * @param <T>     你的实体类
     * @param uris 视频的地址列表\
     */
    public <T extends ItemVideo> void setMediaUri(@NonNull List<T> uris) {
        MediaSource[] firstSources = new MediaSource[uris.size()];
        int i = 0;
        for (T item : uris) {
            if (item.getVideoUri() != null) {
                firstSources[i] = initMediaSource(Uri.parse(item.getVideoUri()));
            }
            i++;
        }
        mediaSource = new ConcatenatingMediaSource(firstSources);
    }


    /***
     * 设置循环播放视频   Integer.MAX_VALUE 无线循环
     *
     * @param loopingCount 必须大于0
     */
    public void setLoopingMediaSource(@Size(min = 1) int loopingCount, Uri videoUri) {
        mediaSource = new LoopingMediaSource(initMediaSource(videoUri), loopingCount);
    }

    /***
     * 设置自定义视频数据源
     * @param mediaSource 你的数据源
     */
    public void setMediaSource(MediaSource mediaSource) {
        this.mediaSource = mediaSource;
    }

    /***
     * 获取视频数据源
     * @return the media source
     */
    public MediaSource getMediaSource() {
        return mediaSource;
    }


    /***
     * 初始化数据源工厂
     * @return DataSource.Factory data source
     */
    public DataSource.Factory getDataSource() {
        if (listener != null) {
            return listener.getDataSourceFactory();
        } else {
            return new JDefaultDataSourceFactory(context);
        }
    }

    /***
     * 移除多媒体
     * @param index 要移除数据
     */
    public void removeMedia(int index) {
        if (mediaSource instanceof ConcatenatingMediaSource) {
            ConcatenatingMediaSource source = (ConcatenatingMediaSource) mediaSource;
            source.getMediaSource(index).releaseSource(null);
            source.removeMediaSource(index);
        }
    }


    /****
     * 销毁资源
     */
    public void destroy() {

        if (listener != null) {
            DataSource source = listener.getDataSourceFactory().createDataSource();
            if (source instanceof CacheDataSource) {
                CacheDataSource cacheDataSource = (CacheDataSource) source;
                try {
                    cacheDataSource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            listener = null;
        }
        indexType = -1;
        videoUri = null;
    }

    /**
     * 获取视频所在索引
     *
     * @return int index type
     */
    public int getIndexType() {
        return indexType;
    }

    /**
     * 设置视频所在索引
     *
     * @param indexType 值
     */
    public void setIndexType(@Size(min = 0) int indexType) {
        this.indexType = indexType;
    }

    /**
     * 获取视频线路地址
     *
     * @return List<String> video uri
     */
    List<String> getVideoUri() {
        return videoUri;
    }


    /**
     * 设置自定义键唯一标识原始流。用于缓存索引。*默认值是{ null }。.
     *
     * @param customCacheKey 唯一标识原始流的自定义密钥。用于缓存索引。
     * @throws IllegalStateException If one of the {@code create} methods has already been called.
     */
    public void setCustomCacheKey(@NonNull String customCacheKey) {
        this.customCacheKey = customCacheKey;
    }

    /****
     * 初始化视频源，无缝衔接
     *
     * @param uri 视频的地址
     * @return MediaSource media source
     */
    public MediaSource initMediaSource(Uri uri) {
        DefaultExtractorsFactory mDefaultExtractorsFactory = new DefaultExtractorsFactory();
        mDefaultExtractorsFactory.setMp4ExtractorFlags(Mp4Extractor.FLAG_WORKAROUND_IGNORE_EDIT_LISTS);
        int streamType = VideoPlayUtils.inferContentType(uri);
        switch (streamType) {
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(getDataSource())
                        .setExtractorsFactory(mDefaultExtractorsFactory)
                        .setMinLoadableRetryCount(5)
                        .setCustomCacheKey(customCacheKey == null ? uri.toString() : customCacheKey)
                        .createMediaSource(uri);
            default:
                throw new IllegalStateException(context.getString(R.string.media_error));
        }
    }
}
