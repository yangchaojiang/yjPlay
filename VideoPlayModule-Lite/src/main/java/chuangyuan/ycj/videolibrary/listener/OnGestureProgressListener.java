package chuangyuan.ycj.videolibrary.listener;


/**
 * @author yangc
 * date 2017/11/3
 * Created by yangc
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 手势操作信息回调接口
 */

public interface OnGestureProgressListener {
    /****
     * 滑动进度
     *
     * @param seekTimePosition  滑动的时间
     * @param duration         视频总长
     * @param    seekTime    滑动的时间 格式化00:00
     * @param    totalTime    视频总长 格式化00:00
     **/
    void showProgressDialog(long seekTimePosition, long duration, String seekTime, String totalTime);

}
