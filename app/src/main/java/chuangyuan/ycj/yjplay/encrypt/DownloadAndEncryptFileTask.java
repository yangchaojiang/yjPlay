package chuangyuan.ycj.yjplay.encrypt;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadAndEncryptFileTask extends AsyncTask<Void, Integer, Void> {

    private String mUrl;
    private File mFile;
    private Cipher mCipher;
    private Context context;
    private ProgressBar progressBar;
    private int keyBody = -1;
    private String keyBodys;

    public DownloadAndEncryptFileTask(Context context, ProgressBar progressBar, String keyBody, String url, File file) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("You need to supply a url to a clear MP4 file to download and encrypt, or modify the code to use a local encrypted mp4");
        }
        mUrl = url;
        mFile = file;
        this.context = context;
        this.progressBar = progressBar;
        this.keyBodys = keyBody;
    }

    public DownloadAndEncryptFileTask(Context context, ProgressBar progressBar, int keyBody, String url, File file) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("You need to supply a url to a clear MP4 file to download and encrypt, or modify the code to use a local encrypted mp4");
        }
        mUrl = url;
        mFile = file;
        this.context = context;
        this.progressBar = progressBar;
        this.keyBody = keyBody;
    }

    public DownloadAndEncryptFileTask(Context context, ProgressBar progressBar, String url, File file, Cipher cipher) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("You need to supply a url to a clear MP4 file to download and encrypt, or modify the code to use a local encrypted mp4");
        }
        mUrl = url;
        mFile = file;
        mCipher = cipher;
        this.context = context;
        this.progressBar = progressBar;
    }

    private void downloadAndEncrypt() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mUrl)
                .build();
        Response response = client.newCall(request).execute();

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw new IOException("server error: " + response.code() + ", " + response.message());
        }
        InputStream inputStream = response.body().byteStream();
        if (mFile.exists()) {
            mFile.delete();
        }
        FileOutputStream fos = new FileOutputStream(mFile.getAbsolutePath(), false);
        if (keyBody > -1) {//BASE64加密处理
            byte[] buffer = new byte[1024 * 500];
            int readCount;
            long total = 0;
            Base64OutputStream outputStream = new Base64OutputStream(fos, keyBody);
            long length = response.body().contentLength();
            while ((readCount = inputStream.read(buffer)) != -1) {
                // 处理下载的数据
                outputStream.write(buffer, 0, readCount);
                total = total + readCount;
                System.out.println("字节" + total + "总共长度" + length + "--进度：" + total * 100 / length);
                publishProgress((int) (total * 100 / length));
            }
        } else if (mCipher!=null) {//aes加密处理
            CipherOutputStream cipherOutputStream = new CipherOutputStream(fos, mCipher);
            byte buffer[] = new byte[1024 * 1024 * 100];
            long length = response.body().contentLength();
            int bytesRead;
            long total = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, bytesRead);
                total = total + bytesRead;
                // 如果知道响应的长度，调用publishProgress（）更新进度
                System.out.println("字节" + total + "总共长度" + length + "--进度：" + total * 100 / length);
                publishProgress((int) (total * 100 / length));
            }
            inputStream.close();
            cipherOutputStream.close();
        } else if (keyBodys != null) {//简单加密处理
            Test.writeFile(mFile.getAbsolutePath(), keyBodys.getBytes("UTF-8"), 0, keyBodys.getBytes("UTF-8").length, false);
            byte[] buffer = new byte[1024 * 500];
            int readCount;
            long length = response.body().contentLength();
            long total = 0;
            int  pro=0;
            while ((readCount = inputStream.read(buffer)) != -1) {
                // 处理下载的数据
                Test.writeFile(mFile.getAbsolutePath(), buffer, 0, readCount, true);
                total = total + readCount;
                pro=(int) (total * 100 / length);
                System.out.println("字节" + total + "总共长度" + length + "--进度：" + pro);
                publishProgress(pro);
            }

            inputStream.close();
        }
        response.close();
    }


    @Override
    protected Void doInBackground(Void... params) {
        try {
            downloadAndEncrypt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
        Log.d(getClass().getCanonicalName(), "done");
    }

}
