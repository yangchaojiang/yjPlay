package chuangyuan.ycj.videolibrary.source;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by yangc on 2017/11/13
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 简单加密提供数据源类
 * @author yangc
 * {@link  com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory}
 */
@Deprecated
public class Encrypted1FileDataSource implements DataSource {

    /**
     * Thrown when IOException is encountered during local file read operation.
     */
    public static class FileDataSourceException extends IOException {

        public FileDataSourceException(IOException cause) {
            super(cause);
        }

    }

    private   TransferListener<? super Encrypted1FileDataSource> listener;

    private RandomAccessFile file;
    private Uri uri;
    private long bytesRemaining;
    private boolean opened;
    private String key;
    private int length;

    /**
     * @param key 加密字符key
     */
    public Encrypted1FileDataSource(@NonNull String key) {
        this(key, null);
    }
    /**
     * @param key 加密字符key
     * @param listener An optional listener.
     */
    public Encrypted1FileDataSource(@NonNull  String key, TransferListener<? super Encrypted1FileDataSource> listener) {
        this.listener = listener;
        this.key = key;
        this.length=key.getBytes().length;
    }

    /***
     * 判断是否加密
     * **/
    private boolean isEncrypted(RandomAccessFile filePath) {
        try {
            if (null == key || key.isEmpty()) {
                return false;
            }
            byte[] b = new byte[length];
            filePath.read(b);
            if (!key.equals(new String(b, "UTF-8"))) {
                filePath.seek(filePath.getFilePointer() - length);
                return false;
            } else if (key.equals(new String(b, "UTF-8"))) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long open(DataSpec dataSpec) throws  FileDataSourceException {
        try {
            uri = dataSpec.uri;
            file = new RandomAccessFile(dataSpec.uri.getPath(), "r");
            boolean ass = isEncrypted(file);
            if (ass) {
                file.seek(dataSpec.position + length);
            }
            bytesRemaining = dataSpec.length == C.LENGTH_UNSET ? file.length() - dataSpec.position
                    : dataSpec.length;
            if (bytesRemaining < 0) {
                throw new EOFException();
            }

        } catch (IOException e) {
            throw new FileDataSourceException(e);
        }

        opened = true;
        if (listener != null) {
            listener.onTransferStart(this, dataSpec);
        }

        return bytesRemaining;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws FileDataSourceException {
        if (readLength == 0) {
            return 0;
        } else if (bytesRemaining == 0) {
            return C.RESULT_END_OF_INPUT;
        } else {
            int bytesRead;
            try {
                boolean ass = isEncrypted(file);
                if (ass) {
                    file.seek(file.getFilePointer() + length);
                }
                bytesRead = file.read(buffer, offset, (int) Math.min(bytesRemaining, readLength));
            } catch (IOException e) {
                throw new  FileDataSourceException(e);
            }

            if (bytesRead > 0) {
                bytesRemaining -= bytesRead;
                if (listener != null) {
                    listener.onBytesTransferred(this, bytesRead);
                }
            }

            return bytesRead;
        }
    }

    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public void close() throws  FileDataSourceException {
        uri = null;
        try {
            if (file != null) {
                file.close();
            }
        } catch (IOException e) {
            throw new  FileDataSourceException(e);
        } finally {
            file = null;
            if (opened) {
                opened = false;
                if (listener != null) {
                    listener.onTransferEnd(this);
                }
            }
        }
    }
}