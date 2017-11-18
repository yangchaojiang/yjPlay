package chuangyuan.ycj.videolibrary.source;

import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 *
 * @author yangc
 * date 2017/8/31
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: aes加密提供数据源类
 */

public final class EncryptedFileDataSource implements DataSource {

  private final TransferListener<? super EncryptedFileDataSource> mTransferListener;
  private StreamingCipherInputStream mInputStream;
  private Uri mUri;
  private long mBytesRemaining;
  private boolean mOpened;
  private Cipher mCipher;
  private SecretKeySpec mSecretKeySpec;
  private IvParameterSpec mIvParameterSpec;

  public EncryptedFileDataSource(Cipher cipher, SecretKeySpec secretKeySpec, IvParameterSpec ivParameterSpec, TransferListener<? super EncryptedFileDataSource> listener) {
    mCipher = cipher;
    mSecretKeySpec = secretKeySpec;
    mIvParameterSpec = ivParameterSpec;
    mTransferListener = listener;
  }

  @Override
  public long open(DataSpec dataSpec) throws EncryptedFileDataSourceException {
    // if we're open, we shouldn't need to open again, fast-fail
    if (mOpened) {
      return mBytesRemaining;
    }
    // #getUri is part of the contract...
    mUri = dataSpec.uri;
    // put all our throwable work in a single block, wrap the error in a custom Exception
    try {
      setupInputStream();
      skipToPosition(dataSpec);
      computeBytesRemaining(dataSpec);
    } catch (IOException e) {
      throw new EncryptedFileDataSourceException(e);
    }
    // if we made it this far, we're open
    mOpened = true;
    // notify
    if (mTransferListener != null) {
      mTransferListener.onTransferStart(this, dataSpec);
    }
    // report
    return mBytesRemaining;
  }

  private void setupInputStream() throws FileNotFoundException {
    File encryptedFile = new File(mUri.getPath());
    FileInputStream fileInputStream = new FileInputStream(encryptedFile);
    mInputStream = new StreamingCipherInputStream(fileInputStream, mCipher, mSecretKeySpec, mIvParameterSpec);
  }

  private void skipToPosition(DataSpec dataSpec) throws IOException {
    mInputStream.forceSkip(dataSpec.position);
  }

  private void computeBytesRemaining(DataSpec dataSpec) throws IOException {
    if (dataSpec.length != C.LENGTH_UNSET) {
      mBytesRemaining = dataSpec.length;
    } else {
      mBytesRemaining = mInputStream.available();
      if (mBytesRemaining == Integer.MAX_VALUE) {
        mBytesRemaining = C.LENGTH_UNSET;
      }
    }
  }

  @Override
  public int read(byte[] buffer, int offset, int readLength) throws EncryptedFileDataSourceException {
    // fast-fail if there's 0 quantity requested or we think we've already processed everything
    if (readLength == 0) {
      return 0;
    } else if (mBytesRemaining == 0) {
      return C.RESULT_END_OF_INPUT;
    }
    // constrain the read length and try to read from the cipher input stream
    int bytesToRead = getBytesToRead(readLength);
    int bytesRead;
    try {
      bytesRead = mInputStream.read(buffer, offset, bytesToRead);
    } catch (IOException e) {
      throw new EncryptedFileDataSourceException(e);
    }
    // if we get a -1 that means we failed to read - we're either going to EOF error or broadcast EOF
    if (bytesRead == -1) {
      if (mBytesRemaining != C.LENGTH_UNSET) {
        throw new EncryptedFileDataSourceException(new EOFException());
      }
      return C.RESULT_END_OF_INPUT;
    }
    // we can't decrement bytes remaining if it's just a flag representation (as opposed to a mutable numeric quantity)
    if (mBytesRemaining != C.LENGTH_UNSET) {
      mBytesRemaining -= bytesRead;
    }
    // notify
    if (mTransferListener != null) {
      mTransferListener.onBytesTransferred(this, bytesRead);
    }
    // report
    return bytesRead;
  }

  private int getBytesToRead(int bytesToRead) {
    if (mBytesRemaining == C.LENGTH_UNSET) {
      return bytesToRead;
    }
    return (int) Math.min(mBytesRemaining, bytesToRead);
  }

  @Override
  public Uri getUri() {
    return mUri;
  }

  @Override
  public void close() throws EncryptedFileDataSourceException {
    mUri = null;
    try {
      if (mInputStream != null) {
        mInputStream.close();
      }

    } catch (IOException e) {
      throw new EncryptedFileDataSourceException(e);
    } finally {
      mInputStream = null;
      mCipher=null;
      mIvParameterSpec=null;
      mSecretKeySpec=null;
      if (mOpened) {
        mOpened = false;
        if (mTransferListener != null) {
          mTransferListener.onTransferEnd(this);
        }
      }
    }
  }

  public  final class EncryptedFileDataSourceException extends IOException {
    public EncryptedFileDataSourceException(IOException cause) {
      super(cause);
    }
  }

  public  static   class StreamingCipherInputStream extends CipherInputStream {

    private static final int AES_BLOCK_SIZE = 16;

    private InputStream mUpstream;
    private Cipher mCipher;
    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;

    public StreamingCipherInputStream(InputStream inputStream, Cipher cipher, SecretKeySpec secretKeySpec, IvParameterSpec ivParameterSpec) {
      super(inputStream, cipher);
      mUpstream = inputStream;
      mCipher = cipher;
      mSecretKeySpec = secretKeySpec;
      mIvParameterSpec = ivParameterSpec;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      return super.read(b, off, len);
    }

    public long forceSkip(long bytesToSkip) throws IOException {
      long skipped = mUpstream.skip(bytesToSkip);
      try {
        int skip = (int) (bytesToSkip % AES_BLOCK_SIZE);
        long blockOffset = bytesToSkip - skip;
        long numberOfBlocks = blockOffset / AES_BLOCK_SIZE;
        // from here to the next inline comment, i don't understand
        BigInteger ivForOffsetAsBigInteger = new BigInteger(1, mIvParameterSpec.getIV()).add(BigInteger.valueOf(numberOfBlocks));
        byte[] ivForOffsetByteArray = ivForOffsetAsBigInteger.toByteArray();
        IvParameterSpec computedIvParameterSpecForOffset;
        if (ivForOffsetByteArray.length < AES_BLOCK_SIZE) {
          byte[] resizedIvForOffsetByteArray = new byte[AES_BLOCK_SIZE];
          System.arraycopy(ivForOffsetByteArray, 0, resizedIvForOffsetByteArray, AES_BLOCK_SIZE - ivForOffsetByteArray.length, ivForOffsetByteArray.length);
          computedIvParameterSpecForOffset = new IvParameterSpec(resizedIvForOffsetByteArray);
        } else {
          computedIvParameterSpecForOffset = new IvParameterSpec(ivForOffsetByteArray, ivForOffsetByteArray.length - AES_BLOCK_SIZE, AES_BLOCK_SIZE);
        }
        mCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, computedIvParameterSpecForOffset);
        byte[] skipBuffer = new byte[skip];
        // i get that we need to update, but i don't get how we're able to take the shortcut from here to the previous comment
        mCipher.update(skipBuffer, 0, skip, skipBuffer);
        Arrays.fill(skipBuffer, (byte) 0);
      } catch (Exception e) {
        return 0;
      }
      return skipped;
    }

    // We need to return the available bytes from the upstream.
    // In this implementation we're front loading it, but it's possible the value might change during the lifetime
    // of this instance, and reference to the stream should be retained and queried for available bytes instead
    @Override
    public int available() throws IOException {
      return mUpstream.available();
    }

  }

}