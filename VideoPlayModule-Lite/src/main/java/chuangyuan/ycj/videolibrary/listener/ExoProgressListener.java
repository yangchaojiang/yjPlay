package chuangyuan.ycj.videolibrary.listener;

import com.google.android.exoplayer2.offline.Downloader;

/**
 * author  yangc
 * date 2018/6/3
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
public interface ExoProgressListener {

    void onDownloadProgress(Downloader listener,float downloadPercentage,long downloadedBytes);
}
