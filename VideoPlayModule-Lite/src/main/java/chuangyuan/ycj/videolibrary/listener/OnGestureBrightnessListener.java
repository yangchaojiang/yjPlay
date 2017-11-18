package chuangyuan.ycj.videolibrary.listener;


/**
 * @author yangc
 * date 2017/11/3
 * Created by yangc
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 手势操作信息回调接口
 */

public interface OnGestureBrightnessListener {

    /**
     * 改变亮度
     *
     * @param mMax      最大亮度
     * @param currIndex 当前亮度
     **/
    void setBrightnessPosition(int mMax, int currIndex);

}
