package chuangyuan.ycj.videolibrary.widget;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.GridView;

public class ParamsCreator {
    private int screenWidth;//屏幕宽度
    private int densityDpi;//像素密度

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
        if (screenWidth >= 1400) {//1440
            return 50;
        }
        if (screenWidth >= 1000) {//1080
            if (densityDpi >= 480)
                return 48;
            if (densityDpi >= 320)
                return 48;
            return 48;
        }
        if (screenWidth >= 700) {//720
            if (densityDpi >= 320)
                return 34;
            if (densityDpi >= 240)
                return 34;
            if (densityDpi >= 160)
                return 34;
            return 34;
        }
        if (screenWidth >= 500) {//540
            if (densityDpi >= 320)
                return 30;
            if (densityDpi >= 240)
                return 30;
            if (densityDpi >= 160)
                return 30;
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
        if (screenWidth >= 1400) {//1440
            return 12;
        }
        if (screenWidth >= 1000) {//1080
            if (densityDpi >= 480)
                return 12;
            if (densityDpi >= 320)
                return 12;
            return 12;
        }
        if (screenWidth >= 700) {//720
            if (densityDpi >= 320)
                return 8;
            if (densityDpi >= 240)
                return 8;
            if (densityDpi >= 160)
                return 8;
            return 8;
        }
        if (screenWidth >= 500) {//540
            if (densityDpi >= 320)
                return 5;
            if (densityDpi >= 240)
                return 5;
            if (densityDpi >= 160)
                return 5;
            return 5;
        }
        return 5;
    }
}
