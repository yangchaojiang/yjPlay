package chuangyuan.ycj.videolibrary.encryption;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by yangc on 2017/2/25.
 * E-Mail:1007181167@qq.com
 * Description:服务端建立连接
 */

class HttpServiceThread extends Thread {

    private ServerSocket mSocket;
    private int mPort;
    private IHttpStream mStream;
    private volatile boolean mStop;

    HttpServiceThread(IHttpStream stream, ThreadGroup group, int port) throws IOException {
        super(group, "Listener:" + port);
        mStream = stream;
        mPort = port;
        mSocket = new ServerSocket(port);
        mSocket.setSoTimeout(600000);
        if (!mSocket.getReuseAddress())
            mSocket.setReuseAddress(true);
    }

    final boolean isBound() {
        return (mSocket != null && mSocket.isBound());
    }

    final int getPort() {
        if (null != mSocket)
            return mSocket.getLocalPort();
        return 0;
    }

    void close() {
        mStop = true;
        interrupt();
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized void setStream(IHttpStream stream) {
        mStream = stream;
    }

    public void run() {
        while (!mStop) {
            try {
                Socket client = mSocket.accept();
                synchronized (mStream) {
                    if (null != mStream && mStream.isOpen()) {
                        HttpConnection c = new HttpConnection(mStream, client);
                        c.start();
                    }
                }
            } catch (InterruptedIOException ignored) {
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }
}
