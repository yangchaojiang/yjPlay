package chuangyuan.ycj.videolibrary.listener;

/**
 * The interface Load listener.
 *
 * @author yangc  date 2017/7/21 E-Mail:yangchaojiang@outlook.com Deprecated: 视频加载回调接口
 */
public interface LoadListener {
    /***
     * 进度
     * @param pro 进度值 0-100
     */
    void   onProgress(long pro);
}
