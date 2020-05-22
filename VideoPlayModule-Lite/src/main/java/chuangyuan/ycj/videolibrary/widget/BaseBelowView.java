package chuangyuan.ycj.videolibrary.widget;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import chuangyuan.ycj.videolibrary.R;


/**
 * author yangc
 * date 2017/7/19
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  多线路浮层
 */
public abstract class BaseBelowView {
    protected View convertView;
    protected PopupWindow pw;

    /**
     * Instantiates a new Below view.
     *
     * @param c   the c
     * @param res 布局文件
     */
    public BaseBelowView(@NonNull Context c, @LayoutRes int res) {
        this.convertView = View.inflate(c, res, null);
    }

    /**
     * Show below view.
     *
     * @param view                   the view
     * @param canceledOnTouchOutside the canceled on touch outside
     */
    protected void showBelowView(@NonNull View view, boolean canceledOnTouchOutside,OnInitViewListener initViewListener) {
        if (pw == null) {
            this.pw = new PopupWindow(convertView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
            this.pw.setOutsideTouchable(canceledOnTouchOutside);
            this.pw.setAnimationStyle(R.style.AnimationRightFade);
            this.pw.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.switch_bg));
            convertView.setOnClickListener(v -> pw.dismiss());
            //防止导航栏显示
            pw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            if (initViewListener!=null){
                initViewListener.initView(this.pw);
            }
        }
        this.pw.showAtLocation(view, Gravity.RIGHT, 0, 300);
    }

    /**
     * Dismiss below view.
     */
    public void dismissBelowView() {
        if (this.pw != null && pw.isShowing()) {
            this.pw.dismiss();
        }
    }


    /***
     * 初始化view回调
     * ***/
    public  interface OnInitViewListener{
        void initView(PopupWindow popupWindow);
    }

}
