package chuangyuan.ycj.videolibrary.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.AnimatorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private Context context;
    private PopupWindow pw;
    private int animationStyle;
    private ListView listView;
    private OnItemClickListener onItemClickListener;

    /**
     * Instantiates a new Below view.
     *
     * @param c        the c
     * @param listName the list name
     */
    public BelowView(@NonNull Context c, @Nullable List<String> listName) {
        this.context = c;
        this.convertView = View.inflate(c, R.layout.simple_exo_belowview, null);
        listView = (ListView) convertView.findViewById(R.id.list_item);
        if (listName == null) {
            listName = Arrays.asList(c.getResources().getStringArray(R.array.exo_video_switch_text));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(c, R.layout.simple_exo_belowview_item, listName);
        listView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        listView.setAdapter(adapter);
    }

    /**
     * Show below view.
     *
     * @param view                   the view
     * @param canceledOnTouchOutside the canceled on touch outside
     */
    public void showBelowView(@NonNull View view, boolean canceledOnTouchOutside) {
        if (pw == null) {
            int height = (int) context.getResources().getDimension(R.dimen.dp30) * listView.getAdapter().getCount() + 40;
            this.pw = new PopupWindow(convertView, ViewGroup.LayoutParams.WRAP_CONTENT, height, true);
            this.pw.setOutsideTouchable(canceledOnTouchOutside);
            if (this.animationStyle != 0) {
                this.pw.setAnimationStyle(this.animationStyle);
            }
            this.pw.setBackgroundDrawable(new ColorDrawable(0));
            if (onItemClickListener != null) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(position, listView.getAdapter().getItem(position).toString());
                        }
                    }
                });
            }
        }
        this.pw.showAsDropDown(view, -view.getWidth() / 4, 0);
    }

    /**
     * Sets animation.
     *
     * @param animationStyle the animation style
     */
    public void setAnimation(@AnimatorRes int animationStyle) {
        this.animationStyle = animationStyle;
    }

    /**
     * Gets below view.
     *
     * @return the below view
     */
    public View getBelowView() {
        return this.convertView;
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
