package chuangyuan.ycj.videolibrary.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import chuangyuan.ycj.videolibrary.R;

/**
 * Created by yangc on 2017/7/19.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class BelowView {
    private View convertView;
    private Context context;
    private PopupWindow pw;
    private int animationStyle;
    private ListView listView;
    private   OnItemClickListener onItemClickListener;

    public BelowView(Context c) {
        this.context = c;
        this.convertView = View.inflate(c, R.layout.simple_exo_belowview, null);
        String[] datas = c.getResources().getStringArray(R.array.exo_video_switch_text);
        listView = (ListView) convertView.findViewById(R.id.list_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(c, R.layout.simple_exo_belowview_item, datas);
        listView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        listView.setAdapter(adapter);
    }


    public void showBelowView(View view, boolean CanceledOnTouchOutside) {
        if (pw == null) {
            int sss = (int) context.getResources().getDimension(R.dimen.dp30) * listView.getAdapter().getCount() + 40;
            this.pw = new PopupWindow(convertView, ViewGroup.LayoutParams.WRAP_CONTENT, sss, true);
            this.pw.setOutsideTouchable(CanceledOnTouchOutside);
            if (this.animationStyle != 0) {
                this.pw.setAnimationStyle(this.animationStyle);
            }
            this.pw.setBackgroundDrawable(new ColorDrawable(0));
            if (onItemClickListener!=null){
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (onItemClickListener!=null){
                            onItemClickListener.onItemClick(position,listView.getAdapter().getItem(position).toString());
                        }
                    }
                });
            }
        }
        this.pw.showAsDropDown(view, -view.getWidth() / 4-20, 0);
    }

    public void setAnimation(int animationStyle) {
        this.animationStyle = animationStyle;
    }

    public View getBelowView() {
        return this.convertView;
    }

    public void dismissBelowView() {
        if (this.pw != null && pw.isShowing()) {
            this.pw.dismiss();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public  interface  OnItemClickListener{

                void onItemClick( int position,String name);

    }
}
