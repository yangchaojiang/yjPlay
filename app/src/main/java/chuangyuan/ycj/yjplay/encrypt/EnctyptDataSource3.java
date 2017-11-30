package chuangyuan.ycj.yjplay.encrypt;

import android.content.Context;
import android.util.Base64;

import chuangyuan.ycj.videolibrary.factory.EncryptedFileDataSourceFactory;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;

/**
 * Created by yangc on 2017/8/31.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:    简单解密数据工厂类
 */

public class EnctyptDataSource3 implements DataSourceListener {
    public static final String TAG = "OfficeDataSource";

    private Context context;
    private String  keyBody;

    /***
     * @param context context
     * @param    keyBody 你的视频文件key内容
     * ***/
    public EnctyptDataSource3(Context context, String  keyBody) {
        this.context = context;
        this. keyBody=  keyBody;
    }

    @Override
    public com.google.android.exoplayer2.upstream.DataSource.Factory getDataSourceFactory() {
        //初始化解密工厂类
        return new EncryptedFileDataSourceFactory(context,keyBody);
    }
}
