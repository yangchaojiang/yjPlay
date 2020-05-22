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

import java.util.ArrayList;
import java.util.List;

import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.factory.DefaultCacheDataSourceFactory;
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
    /**
     * 广告视频
     **/
    private MediaSource aDmediaSource;

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
        if (aDmediaSource != null) {
            ConcatenatingMediaSource source = new ConcatenatingMediaSource();
            if (getIndexType() == 0) {
                source.addMediaSource(aDmediaSource);
                source.addMediaSource(initMediaSource(uri));
            } else {
                source.addMediaSource(initMediaSource(uri));
                source.addMediaSource(aDmediaSource);
            }
            mediaSource = source;
        } else {
            mediaSource = initMediaSource(uri);
        }

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
     * 添加广告视频
     *
     * @param indexType  广告视频插入位置，开头和末尾
     * @param firstVideoUri 广告视频
     */
    public void setAdMediaUri(@Size(min = 0) int indexType, @NonNull Uri firstVideoUri) {
        this.indexType = indexType;
        aDmediaSource = initMediaSource(firstVideoUri);
    }

    /****
     * @param indexType 设置当前索引视频屏蔽进度
     * @param firstVideoUri 预览的视频
     * @param secondVideoUri 第二个视频
     * @deprecated  {{@link #setAdMediaUri(int, Uri),#setMediaUri(Uri)}}
     */
    public void setMediaUri(@Size(min = 0) int indexType, @NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
        this.indexType = indexType;
        ConcatenatingMediaSource source = new ConcatenatingMediaSource();
        aDmediaSource = initMediaSource(firstVideoUri);
        if (getIndexType() == 0) {
            source.addMediaSource(aDmediaSource);
            source.addMediaSource(initMediaSource(secondVideoUri));
        } else {
            source.addMediaSource(initMediaSource(secondVideoUri));
            source.addMediaSource(aDmediaSource);
        }
        mediaSource = source;
    }

    /****
     * 初始化多个视频源，无缝衔接
     * @param <T> T
     * @param switchIndex the switch 设置多线路索引
     * @param secondVideoUri 第二个视频
     */
    public <T extends ItemVideo> void setMediaSwitchUri(int switchIndex, @NonNull List<T> secondVideoUri) {
        this.videoUri = null;
        this.videoUri = new ArrayList<>();
        for (T item : secondVideoUri) {
            this.videoUri.add(item.getVideoUri());
        }
        setMediaSwitchUri(this.videoUri, switchIndex);
    }

    /****
     * 初始化多个视频源，无缝衔接
     * @param <T> T
     * @param indexType the index type 设置广告索引
     * @param switchIndex the switch 设置多线路索引
     * @param firstVideoUri 第一个视频， 例如例如广告视频
     * @param secondVideoUri 第二个视频
     * @deprecated  {@link #setAdMediaUri(int, Uri),#setPlaySwitchUri(int, List, List)}  此方法已过期
     */
    public <T extends ItemVideo> void setMediaSwitchUri(@Size(min = 0) int indexType, int switchIndex, @NonNull Uri firstVideoUri, @NonNull List<T> secondVideoUri) {
        this.indexType = indexType;
        this.videoUri = null;
        this.videoUri = new ArrayList<>();
        for (T item : secondVideoUri) {
            this.videoUri.add(item.getVideoUri());
        }
        setAdMediaUri(indexType, firstVideoUri);
        setMediaSwitchUri(this.videoUri, switchIndex);
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
        ConcatenatingMediaSource source = new ConcatenatingMediaSource(firstSources);
        if (aDmediaSource != null) {
            if (getIndexType() == 0) {
                source.addMediaSource(0, aDmediaSource);
            } else {
                source.addMediaSource(aDmediaSource);
            }
        }
        mediaSource = source;
    }


    /***
     * 设置循环播放视频   Integer.MAX_VALUE 无线循环
     * @param loopingCount 必须大于0
     * @param mediaSource  播放媒体来源
     */
    public void setLoopingMediaSource(@Size(min = 1) int loopingCount, MediaSource mediaSource) {
        this.mediaSource = new LoopingMediaSource(mediaSource, loopingCount);
    }


    /****
     * 设置剪贴视频。播放视频部分。使用试看视频
     *
     * @param mediaSource 播放媒体来源
     * @param startPositionUs startPositionUs  毫秒
     *@param   endPositionUs endPositionUs       毫秒
     */
    public void setClippingMediaUri(@NonNull MediaSource mediaSource, long startPositionUs, long endPositionUs) {
        this.mediaSource = new ClippingMediaSource(mediaSource, startPositionUs * 1000, endPositionUs * 1000);
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
            DataSource.Factory source = listener.getDataSourceFactory();
            if (source instanceof DefaultCacheDataSourceFactory) {
                DefaultCacheDataSourceFactory cacheDataSource = (DefaultCacheDataSourceFactory) source;
                cacheDataSource.release();
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
