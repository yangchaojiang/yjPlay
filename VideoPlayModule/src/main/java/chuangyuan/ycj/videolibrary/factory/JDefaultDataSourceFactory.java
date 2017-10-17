package chuangyuan.ycj.videolibrary.factory;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

/**
 *
 * @author yangc
 * date 2017/8/31
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 默认提供数据源工厂类
 */

public  final  class JDefaultDataSourceFactory implements DataSource.Factory  {

    private final Context context;

    private final DataSource.Factory baseDataSourceFactory;
    /**
     * @param context A context.
     *     for {@link DefaultDataSource}.
     * @see DefaultDataSource#DefaultDataSource(Context, TransferListener, DataSource)
     */
    public JDefaultDataSourceFactory(Context context) {
        String userAgent = Util.getUserAgent(context, context.getPackageName());
        this.context = context.getApplicationContext();
        this.baseDataSourceFactory = new DefaultDataSourceFactory(context,userAgent);
    }

    @Override
    public DefaultDataSource createDataSource() {
        return new DefaultDataSource(context, null, baseDataSourceFactory.createDataSource());
    }
}
