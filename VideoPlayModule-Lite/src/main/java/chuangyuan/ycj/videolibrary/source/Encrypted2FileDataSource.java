package chuangyuan.ycj.videolibrary.source;

import android.net.Uri;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yangc on 2017/11/13
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 简单加密提供数据源类
 * @author yangc
 */

public class Encrypted2FileDataSource implements DataSource {

    /**
     * Thrown when an {@link IOException} is encountered reading a local asset.
     */
    public static final class EncryptedDataSourceException extends IOException {

        public EncryptedDataSourceException(IOException cause) {
            super(cause);
        }

    }

    private final TransferListener<? super Encrypted2FileDataSource> listener;

    private Uri uri;
    private Base64InputStream base64InputStream;
    private long bytesRemaining;
    private boolean opened;
    private  int flags;


    /**
     * @param flags bit flags for controlling the decoder; see the
     *        constants in {@link Base64}
     * @param listener An optional listener.
     */
    public Encrypted2FileDataSource( int flags, TransferListener<? super Encrypted2FileDataSource> listener) {
        this.listener = listener;
        this.flags=flags;
    }

    @Override
    public synchronized long open(DataSpec dataSpec) throws EncryptedDataSourceException {
        try {
            uri = dataSpec.uri;
            FileInputStream inputStream=null;
            if (base64InputStream==null) {
                  inputStream = new FileInputStream(uri.getPath());
                base64InputStream = new Base64InputStream(inputStream, flags);
            }
            long skipped = base64InputStream.skip(dataSpec.position);
            if (skipped < dataSpec.position) {
                throw new IOException();
            }
            if (dataSpec.length != C.LENGTH_UNSET) {
                bytesRemaining = dataSpec.length;
            } else {
                bytesRemaining = inputStream.available();
                if (bytesRemaining == Integer.MAX_VALUE) {
                    bytesRemaining = C.LENGTH_UNSET;
                }
            }
        } catch (IOException e) {
            throw new EncryptedDataSourceException(e);
        }

        opened = true;
        if (listener != null) {
            listener.onTransferStart(this, dataSpec);
        }
        return bytesRemaining;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws  EncryptedDataSourceException {
        if (readLength == 0) {
            return 0;
        } else if (bytesRemaining == 0) {
            return C.RESULT_END_OF_INPUT;
        }

        int bytesRead;
        try {
            int bytesToRead = bytesRemaining == C.LENGTH_UNSET ? readLength
                    : (int) Math.min(bytesRemaining, readLength);
            bytesRead = base64InputStream.read(buffer, offset, bytesToRead);
        } catch (IOException e) {
            throw new  EncryptedDataSourceException(e);
        }

        if (bytesRead == -1) {
            if (bytesRemaining != C.LENGTH_UNSET) {
                // End of stream reached having not read sufficient data.
                throw new  EncryptedDataSourceException(new EOFException());
            }
            return C.RESULT_END_OF_INPUT;
        }
        if (bytesRemaining != C.LENGTH_UNSET) {
            bytesRemaining -= bytesRead;
        }
        if (listener != null) {
            listener.onBytesTransferred(this, bytesRead);
        }
        return bytesRead;
    }

    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public void close() throws  EncryptedDataSourceException {
        uri = null;
        try {
            if (base64InputStream!=null){
                base64InputStream.close();
            }

        } catch (IOException e) {
            throw new EncryptedDataSourceException(e);
        } finally {
            base64InputStream=null;
            if (opened) {
                opened = false;
                if (listener != null) {
                    listener.onTransferEnd(this);
                }
            }
        }
    }
}