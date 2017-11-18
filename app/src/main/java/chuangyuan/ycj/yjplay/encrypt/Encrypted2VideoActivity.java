package chuangyuan.ycj.yjplay.encrypt;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.UUID;

import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.R;
/**
 * Created by yangc on 2017/8/31.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:   Base64数据加密解密播放
 */

public class Encrypted2VideoActivity extends AppCompatActivity {
    private static final String TAG = "EncryptedVideoActivity";
    private static final String ENCRYPTED_FILE_NAME = "encrypted_key.mp4";
    private static final int keyBytes = Base64.DEFAULT;
    private File mEncryptedFile;
    ExoUserPlayer exoPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enctyted_video);
        VideoPlayerView mSimpleExoPlayerView = (VideoPlayerView) findViewById(R.id.exo_play_context_id);
       mEncryptedFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), ENCRYPTED_FILE_NAME);
       // mEncryptedFile=new File(Environment.getExternalStorageDirectory()+"/test3.mp4");
        exoPlayerManager = new ExoUserPlayer(this, mSimpleExoPlayerView, new EnctyptDataSource2(this,keyBytes));

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
        new DownloadAndEncryptFileTask(this, (ProgressBar) findViewById(R.id.progressBar), keyBytes, getString(R.string.uri_test_1), mEncryptedFile).execute();
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
    private  void sss(){

      /*  UUID drmSchemeUuid =getDrmUuid("widevine");
        DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
        if (drmSchemeUuid != null) {
            String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL);
            String[] keyRequestPropertiesArray = intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES);
            int errorStringId = R.string.error_drm_unknown;
            if (Util.SDK_INT < 18) {
                errorStringId = R.string.error_drm_not_supported;
            } else {
                try {
                    drmSessionManager = buildDrmSessionManagerV18(drmSchemeUuid, drmLicenseUrl,
                            keyRequestPropertiesArray);
                } catch (UnsupportedDrmException e) {
                    errorStringId = e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                            ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown;
                }
            }
            if (drmSessionManager == null) {
                showToast(getString(errorStringId));
                return;
            }
        }*/
    }
    private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManagerV18(UUID uuid,
                                                                              String licenseUrl, String[] keyRequestPropertiesArray) throws UnsupportedDrmException {
        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl,buildHttpDataSourceFactory(new DefaultBandwidthMeter()));
        if (keyRequestPropertiesArray != null) {
            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
                        keyRequestPropertiesArray[i + 1]);
            }
        }
        return new DefaultDrmSessionManager<>(uuid, FrameworkMediaDrm.newInstance(uuid), drmCallback,
                null, new Handler(), null);
    }
    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        String userAgent = Util.getUserAgent(getApplication(),getApplication().getPackageName());
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private UUID getDrmUuid(String typeString){
        switch (Util.toLowerInvariant(typeString)) {
            case "widevine":
                return C.WIDEVINE_UUID;
            case "playready":
                return C.PLAYREADY_UUID;
            case "cenc":
                return C.CLEARKEY_UUID;
            default:
                try {
                    return UUID.fromString(typeString);
                } catch (RuntimeException e) {
                    Log.d("tag","Unsupported drm type: " + typeString);
                }
        }
        return  null;
    }
}
