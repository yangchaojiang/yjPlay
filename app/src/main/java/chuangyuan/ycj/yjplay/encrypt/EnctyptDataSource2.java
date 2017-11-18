package chuangyuan.ycj.yjplay.encrypt;

import android.content.Context;
import android.util.Base64;

import javax.crypto.Cipher;
import chuangyuan.ycj.videolibrary.factory.EncryptedFileDataSourceFactory;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;

/**
 * Created by yangc on 2017/8/31.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  自定义数数据源 工厂类
 */

public class EnctyptDataSource2 implements DataSourceListener {
    public static final String TAG = "DataSource";

    private Context context;
    private int  flags;

    public EnctyptDataSource2(Context context) {
        this.context = context;
    }
    /***
     * @param flags bit flags for controlling the decoder; see the
     *        constants in {@link Base64}
     * */
    public EnctyptDataSource2(Context context, int  flags) {
        this.context = context;
        this. flags=  flags;
    }

    @Override
    public com.google.android.exoplayer2.upstream.DataSource.Factory getDataSourceFactory() {
        return new EncryptedFileDataSourceFactory(context,flags);
    }

}
