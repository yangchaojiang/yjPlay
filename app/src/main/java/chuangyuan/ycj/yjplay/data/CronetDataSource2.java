package chuangyuan.ycj.yjplay.data;

import android.content.Context;

import com.google.android.exoplayer2.ext.cronet.CronetDataSource;
import com.google.android.exoplayer2.ext.cronet.CronetDataSourceFactory;
import com.google.android.exoplayer2.ext.cronet.CronetEngineWrapper;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;


/**
 * Created by yangc on 2017/8/31.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  自定义数数据源 工厂类
 */

public class CronetDataSource2 implements DataSourceListener {
    public static final String TAG = "OfficeDataSource";

    private Context context;

    public CronetDataSource2(Context context) {
        this.context = context;
    }

    @Override
    public com.google.android.exoplayer2.upstream.DataSource.Factory getDataSourceFactory() {
        // OkHttpClient okHttpClient = new OkHttpClient();
        // OkHttpDataSourceFactory OkHttpDataSourceFactory=    new OkHttpDataSourceFactory(okHttpClient, Util.getUserAgent(context, context.getApplicationContext().getPackageName()),new DefaultBandwidthMeter() );
        //使用OkHttpClient 数据源工厂
        //   return  OkHttpDataSourceFactory;
        //默认数据源工厂
        DefaultHttpDataSourceFactory httpDataSourceFactory  =new DefaultHttpDataSourceFactory(context.getPackageName(),null ,DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,true);
        return new CronetDataSourceFactory( new CronetEngineWrapper(context),   Executors.newFixedThreadPool(3),null,null,httpDataSourceFactory);

    }
}
