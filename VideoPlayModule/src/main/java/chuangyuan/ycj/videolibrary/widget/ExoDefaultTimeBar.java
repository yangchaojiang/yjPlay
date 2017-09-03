package chuangyuan.ycj.videolibrary.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.exoplayer2.ui.DefaultTimeBar;

/**
 * Created by yangc on 2017/9/3.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class ExoDefaultTimeBar extends DefaultTimeBar {
    public static final String TAG = "ExoDefaultTimeBar";

    public ExoDefaultTimeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        new DefaultTimeBar(context,attrs);
    }
}
