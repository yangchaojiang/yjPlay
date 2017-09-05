

package chuangyuan.ycj.videolibrary.video;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
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

import chuangyuan.ycj.videolibrary.factory.JDefaultDataSourceFactory;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;

/**
 * Created by yangc on 2017/2/28.
 * E-Mail:1007181167@qq.com
 * Description：数据源处理类
 */
public  final  class MediaSourceBuilder {
    private  String TAG=MediaSourceBuilder.class.getName();
    private Context context;
    private int streamType;
    private Handler mainHandler = null;
    private   MediaSource mediaSource;
    private   DataSourceListener listener;

    /***
     * 初始化
     * **/
    public MediaSourceBuilder(DataSourceListener listener){
        this.listener=listener;
    }
    /****
     *初始化多个视频源，无缝衔接
     * @param firstVideoUri  第一个视频， 例如例如广告视频
     *  @param   secondVideoUri   第二个视频
     * ***/
      void setMediaSourceUri(Context context, String firstVideoUri, String secondVideoUri) {
        this.context = context;
        Uri mSecondVideoUri = Uri.parse(secondVideoUri);
        this.streamType = Util.inferContentType(mSecondVideoUri.getLastPathSegment());
        initDataConcatenatingMediaSource(Uri.parse(firstVideoUri), mSecondVideoUri);
    }

    /****
     *初始化
     * @param context   上下文
     * @param uri  视频的地址
     * ***/
      void setMediaSourceUri(Context context, Uri uri) {
        this.context = context;
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
                MediaSource secondSource = new SsMediaSource(secondVideoUri, new DefaultDataSourceFactory(context, null,getDataSource()),
                        new DefaultSsChunkSource.Factory(getDataSource()),
                        mainHandler, null);
                mediaSource = new ConcatenatingMediaSource(firstSource, secondSource);
                break;
            case C.TYPE_DASH:
                secondSource = new DashMediaSource(secondVideoUri,
                        new DefaultDataSourceFactory(context, null, getDataSource()),
                        new DefaultDashChunkSource.Factory(getDataSource()),
                        mainHandler, null);
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
     *初始化视频源，无缝衔接
     * @param uri  视频的地址
     *  @param   streamType    视频类型
     * ***/
    private MediaSource initData(Uri uri, int streamType) {
        switch (streamType) {
            case C.TYPE_SS:
                mediaSource = new SsMediaSource(uri, new DefaultDataSourceFactory(context, null,
                        getDataSource()),
                        new DefaultSsChunkSource.Factory(getDataSource()),
                        mainHandler, null);
                break;
            case C.TYPE_DASH:
                mediaSource = new DashMediaSource(uri,new DefaultDataSourceFactory(context, null,getDataSource()),
                        new DefaultDashChunkSource.Factory(getDataSource()),mainHandler, null);
                break;
            case C.TYPE_HLS:
                mediaSource=new HlsMediaSource(uri,new DefaultHlsDataSourceFactory(getDataSource()),5,mainHandler,null);
                break;
            case C.TYPE_OTHER:
                mediaSource = new ExtractorMediaSource(uri, getDataSource(), new DefaultExtractorsFactory(), mainHandler, null);
                break;
            default:
                throw new IllegalStateException("Unsupported type: " + streamType);
        }
        return mediaSource;
    }

    /***
     * 获取视频类型
     * **/
      MediaSource getMediaSource() {
        return mediaSource;
    }
    /***
     * 获取链接类型
     * @return int
     ***/
    int getStreamType() {
        return streamType;
    }

    /***
     * 初始化数据源工厂
     * **/
    private DataSource.Factory getDataSource() {
      Log.d(TAG,"Factory:"+(listener==null));
        if(listener!=null){
            return listener.getDataSourceFactory();
        } else {
            return new JDefaultDataSourceFactory(context);
        }

    }

    /****
     * 自定义data数据源
     * @param  listener  接口实现
     * **/
    public void setListener(DataSourceListener listener) {
        this.listener = listener;
    }

    /****
     * 释放资源
     * **/
    public void release() {
        if (mediaSource != null) {
            mediaSource.releaseSource();
        }
        if (mainHandler!=null){
            mainHandler=null;
        }
    }


}
