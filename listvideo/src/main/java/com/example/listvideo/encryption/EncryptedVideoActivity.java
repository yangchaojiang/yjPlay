package com.example.listvideo.encryption;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import chuangyuan.ycj.videolibrary.R;
import chuangyuan.ycj.videolibrary.video.GestureVideoPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

/**
 * Created by yangc on 2017/2/25.
 * E-Mail:1007181167@qq.com
 * Description:加密视频播放器
 */

public class EncryptedVideoActivity extends Activity {
    private static final String TAG = "EncryptedVideoActivity";
    VideoPlayerView simpleExoPlayerView;
    private HttpServer httpServer;
    GestureVideoPlayer gestureVideoPlayer;

    /****
     * @param filePath 视频key或者路径
     ***/
    public static void inVideoActivity(Activity context, String filePath) {
        Intent intent = new Intent(context, EncryptedVideoActivity.class);
        intent.putExtra("key", filePath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_encryption_exo_video_play);
        simpleExoPlayerView = (VideoPlayerView) findViewById(R.id.player_view);
        //String filePath = Environment.getExternalStorageDirectory().getPath()+"/VID_20170304_175413.mp4";
      String filePath = getIntent().getStringExtra("key");
        Log.i(TAG, "加密视频路径：" + filePath);
        initServer(filePath);
    }
    private void playVideo(Uri uri) {
        File file = new File(uri.getPath());
        if (!file.exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
            return;
        }
        gestureVideoPlayer = new GestureVideoPlayer(this,R.id.player_view);
        gestureVideoPlayer.setPlayUri(uri);

    }

    void initServer(final String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
            return;
        }
        httpServer = HttpServer.getInstance();
        httpServer.start(new IHttpStream() {
            @Override
            public boolean writeStream(OutputStream out, String path, int rangS, int rangE) throws IOException {
                int streamLen;
                int readLen;
                int leftLen;
                Uri uri = Uri.parse(path);
                String pathString = uri.getQueryParameter("path");
                if (pathString == null || pathString.equals("")) {
                    HttpConnection.send404Response(out, path);
                } else {
                    String type = HttpConnection.getContentType(pathString);
                    byte[] buffer = new byte[1024 * 10];
                    InputStream mMediaInputStream = new FileInputStream(filePath);
                    if (isEncrypted(filePath, HttpConnection.KEY)) {
                        mMediaInputStream.skip(32);
                    }
                    streamLen = mMediaInputStream.available();
                    if (rangS >= 0) {
                        mMediaInputStream.skip(rangS);
                        rangE = rangE > streamLen ? streamLen : rangE;
                        HttpConnection.sendOkResponse(out, rangE - rangS, type, rangS, rangE, mMediaInputStream.available());
                        leftLen = rangE - rangS;
                        while (leftLen > 0) {
                            readLen = mMediaInputStream.read(buffer);
                            out.write(buffer, 0, readLen);
                            leftLen -= readLen;
                        }
                        out.flush();
                        out.close();
                    } else {
                        HttpConnection.sendOkResponse(out, mMediaInputStream.available(), type);
                        while (true) {
                            readLen = mMediaInputStream.read(buffer);
                            if (readLen <= 0) break;
                            out.write(buffer, 0, readLen);
                        }
                        out.flush();
                        out.close();
                    }
                }
                return false;
            }

            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public boolean acceptRange() {
                return true;
            }
        }, 8080);
        Uri uri = Uri.parse(httpServer.getHttpAddr() + "/?path=" + URLEncoder.encode(filePath));
        playVideo(uri);
    }

    private boolean isEncrypted(String filePath, String key) {
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

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (gestureVideoPlayer != null)
            gestureVideoPlayer.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if (gestureVideoPlayer != null)
            gestureVideoPlayer.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gestureVideoPlayer != null)
            gestureVideoPlayer.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
    }

}
