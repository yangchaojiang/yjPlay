

package chuangyuan.ycj.videolibrary.video;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import okhttp3.OkHttpClient;

/**
 * Created by yangc on 2017/2/28.
 * E-Mail:1007181167@qq.com
 * Description：数据源处理类
 */
public class ExoPlayerMediaSourceBuilder {
    private static final String TAG = "ExoPlayerMediaSourceBuilder";
    private DefaultBandwidthMeter bandwidthMeter;
    private Context context;
    private int streamType;
    private Handler mainHandler = new Handler();
    private OkHttpClient okHttpClient;
    private MediaSource mediaSource;

    /****
     *初始化
     * @param context   上下文
     * @param uri  视频的地址
     * ***/
    public ExoPlayerMediaSourceBuilder(Context context, String uri) {
        this.context = context;
        this.bandwidthMeter = new DefaultBandwidthMeter();
        Uri mSecondVideoUri = Uri.parse(uri);
        this.streamType = Util.inferContentType(Uri.parse(uri).getLastPathSegment());
        initData(mSecondVideoUri, streamType);
    }

    /****
     *初始化多个视频源，无缝衔接
     * @param firstVideoUri  第一个视频， 例如例如广告视频
     *  @param   secondVideoUri   第二个视频
     * ***/
    public ExoPlayerMediaSourceBuilder(Context context, String firstVideoUri, String secondVideoUri) {
        this.context = context;
        this.bandwidthMeter = new DefaultBandwidthMeter();
        Uri mSecondVideoUri = Uri.parse(secondVideoUri);
        this.streamType = Util.inferContentType(mSecondVideoUri.getLastPathSegment());
        initDataConcatenatingMediaSource(Uri.parse(firstVideoUri), mSecondVideoUri);
    }

    /****
     *初始化
     * @param context   上下文
     * @param uri  视频的地址
     * ***/
    public ExoPlayerMediaSourceBuilder(Context context, Uri uri) {
        this.context = context;
        this.bandwidthMeter = new DefaultBandwidthMeter();
        this.streamType = Util.inferContentType(uri.getLastPathSegment());
        initData(uri, streamType);
    }

    /****
     *初始化多个视频源，无缝衔接
     * @param firstVideoUri  第一个视频， 例如例如广告视频
     *  @param   secondVideoUri   第二个视频
     * ***/
    private void initDataConcatenatingMediaSource(Uri firstVideoUri, Uri secondVideoUri) {
        MediaSource firstSource = initData(firstVideoUri, Util.inferContentType(firstVideoUri.getLastPathSegment()));
        switch (streamType) {
            case C.TYPE_SS:
                MediaSource secondSource = new SsMediaSource(secondVideoUri, new DefaultDataSourceFactory(context, null,
                        getHttpDataSourceFactory()),
                        new DefaultSsChunkSource.Factory(getDataSourceFactory()),
                        mainHandler, null);
                mediaSource = new ConcatenatingMediaSource(firstSource, secondSource);
                break;
            case C.TYPE_DASH:
                secondSource = new DashMediaSource(secondVideoUri,
                        new DefaultDataSourceFactory(context, null,
                                getHttpDataSourceFactory()),
                        new DefaultDashChunkSource.Factory(getDataSourceFactory()),
                        mainHandler, null);
                mediaSource = new ConcatenatingMediaSource(firstSource, secondSource);
                break;
            case C.TYPE_HLS:
                secondSource = new HlsMediaSource(secondVideoUri, getDataSourceFactory(), mainHandler, null);
                mediaSource = new ConcatenatingMediaSource(firstSource, secondSource);
                break;
            case C.TYPE_OTHER:
                secondSource = new ExtractorMediaSource(secondVideoUri, getDataSourceFactory(),
                        new DefaultExtractorsFactory(), mainHandler, null);
                //  LoopingMediaSource loopingSource = new LoopingMediaSource(mediaSource);
                mediaSource = new ConcatenatingMediaSource(firstSource, secondSource);
                break;
            default: {
                throw new IllegalStateException("Unsupported type: " + streamType);
            }
        }
    }

    /****
     *初始化视频源，无缝衔接
     * @param uri  视频的地址
     *  @param   streamType    视频类型
     * ***/
    private MediaSource initData(Uri uri, int streamType) {
        switch (streamType) {
            case C.TYPE_SS:
                mediaSource = new SsMediaSource(uri, new DefaultDataSourceFactory(context, null,
                        getHttpDataSourceFactory()),
                        new DefaultSsChunkSource.Factory(getDataSourceFactory()),
                        mainHandler, null);
                break;
            case C.TYPE_DASH:
                mediaSource = new DashMediaSource(uri,
                        new DefaultDataSourceFactory(context, null,
                                getHttpDataSourceFactory()),
                        new DefaultDashChunkSource.Factory(getDataSourceFactory()),
                        mainHandler, null);
                break;
            case C.TYPE_HLS:
                mediaSource = new HlsMediaSource(uri, getDataSourceFactory(), mainHandler, null);
                break;
            case C.TYPE_OTHER:
                mediaSource = new ExtractorMediaSource(uri, getDataSourceFactory(),
                        new DefaultExtractorsFactory(), mainHandler, null);
                //  LoopingMediaSource loopingSource = new LoopingMediaSource(mediaSource);
                break;
            default:
                throw new IllegalStateException("Unsupported type: " + streamType);
        }
        return mediaSource;
    }

    /***
     * 获取视频类型
     * **/
    public MediaSource getMediaSource() {
        return mediaSource;
    }

    /***
     * 初始化数据源工厂
     * **/
    private DataSource.Factory getDataSourceFactory() {
        return new DefaultDataSourceFactory(context, bandwidthMeter,
                getHttpDataSourceFactory());
    }

    /***
     * 初始化数据源工厂
     * **/
    private DataSource.Factory getHttpDataSourceFactory() {
        // return new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "yjPlay"), bandwidthMeter);
        okHttpClient = new OkHttpClient();
        return new OkHttpDataSourceFactory(okHttpClient, Util.getUserAgent(context, context.getApplicationContext().getPackageName()), bandwidthMeter);
    }


    /***
     * 获取链接类型
     * @return int
     ***/
    public int getStreamType() {
        return streamType;
    }


    public void release() {
        if (okHttpClient != null) {
            okHttpClient = null;
        }
        if (mediaSource != null) {
            mediaSource.releaseSource();
            mediaSource = null;
        }
    }
}
