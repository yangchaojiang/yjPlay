package chuangyuan.ycj.videolibrary.listener;

import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * author  yangc
 * date 2018/6/3
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
public interface OnBelowViewListener {

     void showView(View view, List<String> name,int checkedIndex, TextView mSwitchText, ExoPlayerListener mExoPlayerListener);
}
