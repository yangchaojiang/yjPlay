package chuangyuan.ycj.yjplay.encrypt;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
/**
 * Created by yangc on 2017/8/31.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  AES数据加密解密播放
 */
public class EncryptedVideoActivity extends AppCompatActivity {
    private static final String TAG = "EncryptedVideoActivity";
    public static final String AES_ALGORITHM = "AES";
    public static final String AES_TRANSFORMATION = "AES/CTR/NoPadding";
    private static final String ENCRYPTED_FILE_NAME = "encrypted5.mp4";
    private static final String ket = "1234567887654321";
    private static final String keyBytes = "1122334455667788";
    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;
    private File mEncryptedFile;
    ExoUserPlayer exoPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enctyted_video);
        VideoPlayerView mSimpleExoPlayerView = (VideoPlayerView) findViewById(R.id.exo_play_context_id);
        mEncryptedFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), ENCRYPTED_FILE_NAME);
        //实例化你的公钥和密钥
        mSecretKeySpec = new SecretKeySpec(ket.getBytes(), AES_ALGORITHM);
        mIvParameterSpec = new IvParameterSpec(keyBytes.getBytes());
        try {
            //实例化加密和解密类
            Cipher mCipher = Cipher.getInstance(AES_TRANSFORMATION);
            mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
            exoPlayerManager = new ExoUserPlayer(this, mSimpleExoPlayerView, new EnctyptDataSource(this, mCipher, mSecretKeySpec, mIvParameterSpec));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

    }

    private boolean hasFile() {
        return mEncryptedFile != null
                && mEncryptedFile.exists()
                && mEncryptedFile.length() > 0;
    }

    /***
     * 下载视频并加密
     * ***/
    public void encryptVideo(View view) {
        if (hasFile()) {
            Log.d(getClass().getCanonicalName(), "encrypted file found, no need to recreate");
            mEncryptedFile.delete();
        }
        try {
            Cipher encryptionCipher = Cipher.getInstance(AES_TRANSFORMATION);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
            //下载文件视频并加密
            new DownloadAndEncryptFileTask(this, (ProgressBar) findViewById(R.id.progressBar), getString(R.string.uri_test_1), mEncryptedFile, encryptionCipher).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放加密视频
     **/
    public void playVideo(View view) {
        if (!hasFile()) {
            Toast.makeText(this, "视频不存在", Toast.LENGTH_LONG).show();
            return;
        }
        Uri uri = Uri.fromFile(mEncryptedFile);
        exoPlayerManager.setPlayUri(uri);
        exoPlayerManager.startPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        exoPlayerManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        exoPlayerManager.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        exoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (exoPlayerManager.onBackPressed()) {
            ActivityCompat.finishAfterTransition(this);
            exoPlayerManager.onDestroy();
        }
    }


}
