package chuangyuan.ycj.videolibrary.office;

import android.net.Uri;

import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.SegmentDownloader;
import com.google.android.exoplayer2.source.hls.offline.HlsDownloader;

import chuangyuan.ycj.videolibrary.offline.DefaultProgressDownloader;

/**
 * author  yangc
 * date 2018/4/27
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
public class WholeMediaDownloader extends DefaultProgressDownloader {

    private SegmentDownloader segmentDownloader;

    protected WholeMediaDownloader(Uri uri, DownloaderConstructorHelper constructorHelper) {
        super(uri, constructorHelper);
        segmentDownloader=new HlsDownloader(uri,constructorHelper);
    }

}
