package chuangyuan.ycj.videolibrary.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import chuangyuan.ycj.videolibrary.R;


/**
 * author yangc
 * date 2017/7/19
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  多线路浮层
 */
class BelowView extends BaseBelowView {
    private ListView listView;
    private OnItemClickListener onItemClickListener;
    private SwitchAdapter adapter;

    /**
     * Instantiates a new Below view.
     *
     * @param c        the c
     * @param listName the list name
     */
    BelowView(@NonNull Context c, @Nullable List<String> listName) {
        super(c, R.layout.simple_exo_belowview);
        listView = convertView.findViewById(R.id.list_item);
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
        super.showBelowView(view, canceledOnTouchOutside, popupWindow->{
            adapter.setSelectIndex(selectIndex);
            if (onItemClickListener != null) {
                listView.setOnItemClickListener((parent, view1, position, id) -> {
                    if (onItemClickListener != null && position != adapter.getSelectIndex()) {
                        onItemClickListener.onItemClick(position, adapter.getItem(position));
                        adapter.setSelectIndex(position);
                    }
                });
            }
        });
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
