package chuangyuan.ycj.yjplay.custom;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import chuangyuan.ycj.videolibrary.listener.ExoPlayerListener;
import chuangyuan.ycj.videolibrary.widget.BaseBelowView;
import chuangyuan.ycj.yjplay.R;

public class CustomPopupWindow extends BaseBelowView {
    /**
     * Instantiates a new Below view.
     *
     * @param c   the c
     */
    public CustomPopupWindow(@NonNull Context c) {
        super(c, R.layout.custom_exo_belowview);
    }

    public void showBelowView(@NonNull View view, List<String> name, int checkedIndex, TextView mSwitchText, ExoPlayerListener mExoPlayerListener) {
        if (pw == null) {
            RadioGroup mRadioGroup = convertView.findViewById(R.id.mRadioGroup);
            RadioButton mRadioButton = convertView.findViewById(R.id.mRadioButton);
            RadioButton mRadioButton1 = convertView.findViewById(R.id.mRadioButton1);
            RadioButton mRadioButton2 = convertView.findViewById(R.id.mRadioButton2);
            switch (checkedIndex) {
                case 0:
                    mRadioButton.setChecked(true);
                    break;
                case 1:
                    mRadioButton1.setChecked(true);
                    break;
                case 2:
                    mRadioButton2.setChecked(true);
                    break;
            }
            mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                int index = 0;
                switch (checkedId) {
                    case R.id.mRadioButton1:
                        index = 1;
                        break;
                    case R.id.mRadioButton2:
                        index = 2;
                        break;
                }
                mSwitchText.setText(name.get(index));
                if (mExoPlayerListener != null) {
                    mExoPlayerListener.switchUri(index);
                }
                pw.dismiss();
            });
            pw = new PopupWindow(convertView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
            pw.setOutsideTouchable(true);
            pw.setBackgroundDrawable(new ColorDrawable(0));
            //防止导航栏显示
            pw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            convertView.setOnClickListener(v -> pw.dismiss());
        }
        pw.showAsDropDown(view);
    }
}
