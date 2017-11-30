package chuangyuan.ycj.yjplay.encrypt;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import chuangyuan.ycj.videolibrary.factory.EncryptedFileDataSourceFactory;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;


/**
 * Created by yangc on 2017/8/31.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  aes数据解密工厂类
 */

public class EnctyptDataSource implements DataSourceListener {
    public static final String TAG = "OfficeDataSource";
    private Context context;
    private Cipher cipher;
    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;

    /***
     * @param context context
     * @param cipher cipher
     *  @param mSecretKeySpec mSecretKeySpec
     *  @param   mIvParameterSpec     mSecretKeySpec
     * **/
    public EnctyptDataSource(Context context, Cipher cipher, SecretKeySpec mSecretKeySpec, IvParameterSpec mIvParameterSpec) {
        this.context = context;
        this.cipher = cipher;
        this.mSecretKeySpec = mSecretKeySpec;
        this.mIvParameterSpec = mIvParameterSpec;
    }
    @Override
    public DataSource.Factory getDataSourceFactory() {
        //初始化解密工厂类
        return new EncryptedFileDataSourceFactory(context,cipher, mSecretKeySpec, mIvParameterSpec);
    }
}
