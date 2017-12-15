package chuangyuan.ycj.videolibrary.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.exoplayer2.ui.DefaultTimeBar;

/**
 * The type Exo default time bar.
 *
 * @author yangc  date 2017/9/3 E-Mail:yangchaojiang@outlook.com Deprecated:  自定义进度条
 */
public class ExoDefaultTimeBar extends DefaultTimeBar {
    private  boolean  openSeek=true;

    /**
     * Instantiates a new Exo default time bar.
     *
     * @param context the context
     */
    public ExoDefaultTimeBar(@NonNull Context context) {
        super(context, null);
        new DefaultTimeBar(context,null);
    }

    /**
     * Instantiates a new Exo default time bar.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ExoDefaultTimeBar(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        new DefaultTimeBar(context,attrs);
        }

    /**
     * Sets open seek.
     *
     * @param openSeek the open seek
     */
    public void setOpenSeek(boolean openSeek) {
        this.openSeek = openSeek;
    }

    @Override
    public boolean isOpenSeek() {
        return openSeek;
    }
}
