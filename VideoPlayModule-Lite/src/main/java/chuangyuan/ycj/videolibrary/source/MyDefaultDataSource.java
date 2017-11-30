package chuangyuan.ycj.videolibrary.source;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.ContentDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by yangc on 2017/11/13
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
public class MyDefaultDataSource implements DataSource {

    private static final String TAG = "DefaultDataSource";
    private static final String SCHEME_ASSET = "asset";
    private static final String SCHEME_CONTENT = "content";
    private static final String SCHEME_RTMP = "rtmp";
    private final Context context;
    private final TransferListener<? super DataSource> listener;
    private final DataSource baseDataSource;
    private DataSource fileDataSource;
    private DataSource assetDataSource;
    private DataSource contentDataSource;
    private DataSource rtmpDataSource;
    private DataSource dataSource;
    private Cipher mCipher;
    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;

    /**
     * Constructs a new instance, optionally configured to follow cross-protocol redirects.
     *
     * @param context                     A context.
     * @param listener                    An optional listener.
     * @param userAgent                   The User-Agent string that should be used when requesting remote data.
     * @param allowCrossProtocolRedirects Whether cross-protocol redirects (i.e. redirects from HTTP
     *                                    to HTTPS and vice versa) are enabled when fetching remote data.
     */
    public MyDefaultDataSource(Context context, TransferListener<? super DataSource> listener,
                               String userAgent, boolean allowCrossProtocolRedirects) {
        this(context, listener, userAgent, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, allowCrossProtocolRedirects);
    }

    /**
     * Constructs a new instance, optionally configured to follow cross-protocol redirects.
     *
     * @param context                     A context.
     * @param listener                    An optional listener.
     * @param userAgent                   The User-Agent string that should be used when requesting remote data.
     * @param connectTimeoutMillis        The connection timeout that should be used when requesting remote
     *                                    data, in milliseconds. A timeout of zero is interpreted as an infinite timeout.
     * @param readTimeoutMillis           The read timeout that should be used when requesting remote data,
     *                                    in milliseconds. A timeout of zero is interpreted as an infinite timeout.
     * @param allowCrossProtocolRedirects Whether cross-protocol redirects (i.e. redirects from HTTP
     *                                    to HTTPS and vice versa) are enabled when fetching remote data.
     */
    public MyDefaultDataSource(Context context, TransferListener<? super DataSource> listener,
                               String userAgent, int connectTimeoutMillis, int readTimeoutMillis,
                               boolean allowCrossProtocolRedirects) {
        this(context, listener,
                new DefaultHttpDataSource(userAgent, null, listener, connectTimeoutMillis,
                        readTimeoutMillis, allowCrossProtocolRedirects, null));
    }
    /**
     * Constructs a new instance that delegates to a provided {@link DataSource} for URI schemes other
     * than file, asset and content.
     *
     * @param context        A context.
     *                       constants in {@link Base64}
     * @param listener       An optional listener.
     * @param baseDataSource A {@link DataSource} to use for URI schemes other than file, asset and
     *                       content. This {@link DataSource} should normally support at least http(s).
     */
    public MyDefaultDataSource(Context context, TransferListener<? super DataSource> listener,
                               DataSource baseDataSource) {
        this.context = context.getApplicationContext();
        this.listener = listener;
        this.baseDataSource = Assertions.checkNotNull(baseDataSource);
    }

    public MyDefaultDataSource(@NonNull Context context, @NonNull Cipher cipher, @NonNull SecretKeySpec secretKeySpec, @NonNull IvParameterSpec ivParameterSpec, @NonNull TransferListener<? super DataSource> listener, @NonNull DataSource baseDataSource) {
        this.context = context.getApplicationContext();
        mCipher = cipher;
        mSecretKeySpec = secretKeySpec;
        mIvParameterSpec = ivParameterSpec;
        this.listener = listener;
        this.baseDataSource = Assertions.checkNotNull(baseDataSource);
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        Assertions.checkState(dataSource == null);
        String scheme = dataSpec.uri.getScheme();
        if (Util.isLocalFileUri(dataSpec.uri)) {
            if (dataSpec.uri.getPath().startsWith("/android_asset/")) {
                dataSource = getAssetDataSource();
            } else {
                dataSource = getFileDataSource();
            }
        } else if (SCHEME_ASSET.equals(scheme)) {
            dataSource = getAssetDataSource();
        } else if (SCHEME_CONTENT.equals(scheme)) {
            dataSource = getContentDataSource();
        } else if (SCHEME_RTMP.equals(scheme)) {
            dataSource = getRtmpDataSource();
        } else {
            dataSource = baseDataSource;
        }
        // Open the source and return.
        return dataSource.open(dataSpec);
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        return dataSource.read(buffer, offset, readLength);
    }

    @Override
    public Uri getUri() {
        return dataSource == null ? null : dataSource.getUri();
    }

    @Override
    public void close() throws IOException {
        if (dataSource != null) {
            try {
                dataSource.close();
            } finally {
                dataSource = null;
            }
        }
    }

    private DataSource getFileDataSource() {
        if (fileDataSource == null) {
            if (mCipher != null && null != mSecretKeySpec && null != mIvParameterSpec) {
                fileDataSource = new EncryptedFileDataSource(mCipher, mSecretKeySpec, mIvParameterSpec, listener);
            } else {
                fileDataSource = new FileDataSource(listener);
            }
        }
        return fileDataSource;
    }

    private DataSource getAssetDataSource() {
        if (assetDataSource == null) {
            assetDataSource = new AssetDataSource(context, listener);
        }
        return assetDataSource;
    }

    private DataSource getContentDataSource() {
        if (contentDataSource == null) {
            contentDataSource = new ContentDataSource(context, listener);
        }
        return contentDataSource;
    }

    private DataSource getRtmpDataSource() {
        if (rtmpDataSource == null) {
            try {
                Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.rtmp.RtmpDataSource");
                rtmpDataSource = (DataSource) clazz.getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                Log.w(TAG, "Attempting to play RTMP stream without depending on the RTMP extension");
            } catch (InstantiationException e) {
                Log.e(TAG, "Error instantiating RtmpDataSource", e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Error instantiating RtmpDataSource", e);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "Error instantiating RtmpDataSource", e);
            } catch (InvocationTargetException e) {
                Log.e(TAG, "Error instantiating RtmpDataSource", e);
            }
            if (rtmpDataSource == null) {
                rtmpDataSource = baseDataSource;
            }
        }
        return rtmpDataSource;
    }

}
