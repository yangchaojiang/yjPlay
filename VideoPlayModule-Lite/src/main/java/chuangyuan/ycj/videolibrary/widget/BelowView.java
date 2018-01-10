package chuangyuan.ycj.videolibrary.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.AnimatorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import java.util.Arrays;
import java.util.List;
import chuangyuan.ycj.videolibrary.R;


/**
 * author yangc
 * date 2017/7/19
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  多线路浮层
 */
public class BelowView {
    private View convertView;
    private PopupWindow pw;
    private ListView listView;
    private OnItemClickListener onItemClickListener;
    private SwitchAdapter adapter;

    /**
     * Instantiates a new Below view.
     *
     * @param c        the c
     * @param listName the list name
     */
    public BelowView(@NonNull Context c, @Nullable List<String> listName) {
        this.convertView = View.inflate(c, R.layout.simple_exo_belowview, null);
        listView = (ListView) convertView.findViewById(R.id.list_item);
        if (listName == null) {
            listName = Arrays.asList(c.getResources().getStringArray(R.array.exo_video_switch_text));
        }
        adapter = new SwitchAdapter(c, listName);
       listView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        listView.setAdapter(adapter);
    }

    /**
     * Show below view.
     *
     * @param view                   the view
     * @param canceledOnTouchOutside the canceled on touch outside
     * @param selectIndex            selectIndex
     */
    public void showBelowView(@NonNull View view, boolean canceledOnTouchOutside, int selectIndex) {
        if (pw == null) {
            int height= (int) (view.getResources().getDimension(R.dimen.dp30)* adapter.getCount());
            adapter.setSelectIndex(selectIndex);
            this.pw = new PopupWindow(convertView, ViewGroup.LayoutParams.WRAP_CONTENT,height, false);
            this.pw.setOutsideTouchable(canceledOnTouchOutside);
            this.pw.setBackgroundDrawable(new ColorDrawable(0));
            if (onItemClickListener != null) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (onItemClickListener != null&&position!=adapter.getSelectIndex()) {
                            onItemClickListener.onItemClick(position, adapter.getItem(position));
                            adapter.setSelectIndex(position);
                        }
                    }
                });
            }
        }
        //防止导航栏显示
        pw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        pw.showAtLocation(view, Gravity.NO_GRAVITY, location[0] - view.getWidth() / 6, location[1] - pw.getHeight());
    }


    /**
     * Dismiss below view.
     */
    public void dismissBelowView() {
        if (this.pw != null && pw.isShowing()) {
            this.pw.dismiss();
        }
    }

    /**
     * Sets on item click listener.
     *
     * @param onItemClickListener the on item click listener
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * The interface On item click listener.
     */
    public interface OnItemClickListener {
        /**
         * item 点击双股事件
         *
         * @param position 索引
         * @param name     名称
         */
        void onItemClick(int position, String name);

    }
}
