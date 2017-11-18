package chuangyuan.ycj.videolibrary.listener;


/**
 * @author yangc
 * date 2017/11/3
 * Created by yangc
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 手势操作信息回调接口
 */

public interface OnGestureVolumeListener {
    /**
     * 改变声音
     *
     * @param mMax      d最大音量
     * @param currIndex 当前音量
     **/
    void setVolumePosition(int mMax, int currIndex);


}
