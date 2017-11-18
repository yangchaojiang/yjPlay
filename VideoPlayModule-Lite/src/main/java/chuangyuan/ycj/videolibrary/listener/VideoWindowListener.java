package chuangyuan.ycj.videolibrary.listener;

/**
 * @author yangc
 *         date 2017/2/25
 *         E-Mail:1007181167@qq.com
 *         Description：多个视频接口
 */

public interface VideoWindowListener {


    /***
     * 返回当前位置
     * @param currentIndex 当前视频窗口索引
     * @param  windowCount   总数
     * ***/
    void onCurrentIndex(int currentIndex, int windowCount);

}
