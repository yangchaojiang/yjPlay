package chuangyuan.ycj.videolibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.exoplayer2.ui.DefaultTimeBar;

/**
 *
 * @author yangc
 * date 2017/9/3
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  自定义进度条
 */

public class ExoDefaultTimeBar extends DefaultTimeBar {
    private  boolean  openSeek=true;
    public ExoDefaultTimeBar(Context context) {
        super(context, null);
        new DefaultTimeBar(context,null);
    }
    public ExoDefaultTimeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        new DefaultTimeBar(context,attrs);
        }

    public void setOpenSeek(boolean openSeek) {
        this.openSeek = openSeek;
    }

    @Override
    public boolean isOpenSeek() {
        return openSeek;
    }
}
