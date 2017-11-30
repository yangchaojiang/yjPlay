package chuangyuan.ycj.videolibrary.factory;

import android.content.Context;
import android.util.Base64;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import chuangyuan.ycj.videolibrary.source.Encrypted1FileDataSource;
import chuangyuan.ycj.videolibrary.source.MyDefaultDataSource;

/**
 * Created by yangc on 2017/11/13
 * date 2017/8/31
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 加密提供数据源工厂类
 *
 * @author yangc
 */
public class EncryptedFileDataSourceFactory implements DataSource.Factory {
    private Context context;
    private Cipher mCipher;
    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;
    private TransferListener<? super DataSource> mTransferListener;
    private String key;
    private DataSource.Factory baseDataSourceFactory;

    /**
     * @param context A context.
     *                for {@link DefaultDataSource}.
     * @param key     key
     * {@link  com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory}.
     */
    @Deprecated
    public EncryptedFileDataSourceFactory(Context context, String key) {
        this.context = context.getApplicationContext();
        this.key = key;
    }

    public EncryptedFileDataSourceFactory(Context context, Cipher cipher, SecretKeySpec secretKeySpec, IvParameterSpec ivParameterSpec) {
        this(context, cipher, secretKeySpec, ivParameterSpec, new DefaultBandwidthMeter());
    }

    public EncryptedFileDataSourceFactory(Context context, Cipher cipher, SecretKeySpec secretKeySpec, IvParameterSpec ivParameterSpec, TransferListener<? super DataSource> listener) {
        mCipher = cipher;
        mSecretKeySpec = secretKeySpec;
        mIvParameterSpec = ivParameterSpec;
        mTransferListener = listener;
        String userAgent = Util.getUserAgent(context, context.getPackageName());
        this.context = context.getApplicationContext();
        this.baseDataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
    }

    @Override
    public DataSource createDataSource() {
        if (null != mCipher) {
            return new MyDefaultDataSource(context, mCipher, mSecretKeySpec, mIvParameterSpec, mTransferListener, baseDataSourceFactory.createDataSource());
        } else if (key != null) {
            return new Encrypted1FileDataSource(key, new DefaultBandwidthMeter());
        } else {
            return new DefaultDataSource(context, new DefaultBandwidthMeter(), baseDataSourceFactory.createDataSource());
        }
    }

}