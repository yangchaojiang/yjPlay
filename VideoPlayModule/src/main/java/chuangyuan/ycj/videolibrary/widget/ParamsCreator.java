package chuangyuan.ycj.videolibrary.widget;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author yangc
 */
public class ParamsCreator {
    /***屏幕宽度**/
    private int screenWidth;
    /***像素密度***/
    private int densityDpi;

    public ParamsCreator(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        densityDpi = metric.densityDpi;

    }

    /**
     * 获得默认圆的半径
     *
     * @return int
     */
    public int getDefaultCircleRadius() {
        //1440
        if (screenWidth >= 1400) {
            return 50;
        }
        //1080
        if (screenWidth >= 1000) {
            if (densityDpi >= 480) {
                return 48;
            }
            if (densityDpi >= 320) {
                return 48;
            }
            return 48;
        }
        //720
        if (screenWidth >= 700) {
            if (densityDpi >= 320) {
                return 34;
            }
            if (densityDpi >= 240) {
                return 34;
            }
            if (densityDpi >= 160) {
                return 34;
            }
            return 34;
        }
        //540
        if (screenWidth >= 500) {
            if (densityDpi >= 320) {
                return 30;
            }
            if (densityDpi >= 240) {
                return 30;
            }
            if (densityDpi >= 160) {
                return 30;
            }
            return 30;
        }
        return 30;
    }

    /**
     * 获得默认圆的间距‘
     *
     * @return int
     */
    public int getDefaultCircleSpacing() {
        //1440
        if (screenWidth >= 1400) {
            return 12;
        }
        //1080
        if (screenWidth >= 1000) {
            if (densityDpi >= 480) {
                return 12;
            }
            if (densityDpi >= 320) {
                return 12;
            }
            return 12;
        }
        //720
        if (screenWidth >= 700) {
            if (densityDpi >= 320) {
                return 8;
            }
            if (densityDpi >= 240) {
                return 8;
            }
            if (densityDpi >= 160) {
                return 8;
            }
            return 8;
        }
        //540
        if (screenWidth >= 500) {
            if (densityDpi >= 320) {
                return 5;
            }
            if (densityDpi >= 240) {
                return 5;
            }
            if (densityDpi >= 160) {
                return 5;
            }
            return 5;
        }
        return 5;
    }
}
