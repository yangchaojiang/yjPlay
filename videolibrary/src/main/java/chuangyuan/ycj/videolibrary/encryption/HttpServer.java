package chuangyuan.ycj.videolibrary.encryption;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yangc on 2017/2/25.
 * E-Mail:1007181167@qq.com
 * Description: 模拟服务端
 */

public class HttpServer {

    private final static String TAG = "HttpServer";
    private final static int SERVER_PORT = 0;
    private static HttpServer mHttpServer;
    private int mPort;
    private HttpServiceThread mServiceThread;
    private ThreadGroup mThreadGroup;
    public static HttpServer getInstance() {
        if (null == mHttpServer) {
            mHttpServer = new HttpServer();
        }
        return mHttpServer;
    }

    private HttpServer() {
        mThreadGroup = new ThreadGroup(HttpServer.class.getName());
    }

    public boolean start(IHttpStream stream, int port) {
        if (0 == port)
            port = SERVER_PORT;
        mPort = port;
        try {
            if (null != mServiceThread) {
                if (mServiceThread.isBound()) {
                    mPort = mServiceThread.getPort();
                    mServiceThread.setStream(stream);
                    return true;
                }
                mServiceThread.close();
            }
            mServiceThread = new HttpServiceThread(stream, mThreadGroup, port);
            mPort = mServiceThread.getPort();
            mServiceThread.start();
            return true;
        } catch (Exception e) {
            mServiceThread = null;
            e.printStackTrace();
        }
        return false;
    }

    public void stop() {
        if (null != mServiceThread) {
            mServiceThread.close();
            mServiceThread = null;
        }
    }


    public String getHttpAddr() {
        return "http://127.0.0.1:" + mPort;
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
    }

    public boolean isEncrypted(String filePath, String key) {
        try {
            InputStream encrypted = new FileInputStream(filePath);
            byte[] b = new byte[32];
            encrypted.read(b);
            if (!key.equals(new String(b, "UTF-8"))) {
                encrypted.close();
                return false;
            } else if (key.equals(new String(b, "UTF-8"))) {
                encrypted.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
