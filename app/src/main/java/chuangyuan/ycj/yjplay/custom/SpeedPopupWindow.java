package chuangyuan.ycj.yjplay.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.widget.BaseBelowView;
import chuangyuan.ycj.yjplay.R;

public class SpeedPopupWindow extends BaseBelowView {
    private ExoUserPlayer exoPlayerManager;
    private  String [] data=new String[]{"1.0","1.25","1.5","20","2.4"};
    private   int checkedIndex;
    /**
     * Instantiates a new Below view.
     *
     * @param c   the c
     */
    public SpeedPopupWindow(@NonNull Context c,ExoUserPlayer exoPlayerManager) {
        super(c, R.layout.custom_speed_exo_belowview);
        this.exoPlayerManager=exoPlayerManager;
    }

    public void showBelowView(@NonNull View view, boolean canceledOnTouchOutside, TextView showView) {
        super.showBelowView(view, canceledOnTouchOutside,popupWindow->{
            RadioGroup mRadioGroup = convertView.findViewById(R.id.mRadioGroup);
            RadioButton mRadioButton = convertView.findViewById(R.id.mRadioButton);
            RadioButton mRadioButton1 = convertView.findViewById(R.id.mRadioButton1);
            RadioButton mRadioButton2 = convertView.findViewById(R.id.mRadioButton2);
            RadioButton mRadioButton3 = convertView.findViewById(R.id.mRadioButton3);
            RadioButton mRadioButton4 = convertView.findViewById(R.id.mRadioButton4);
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
                case 3:
                    mRadioButton3.setChecked(true);
                    break;
                case 4:
                    mRadioButton4.setChecked(true);
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
                    case R.id.mRadioButton3:
                        index = 3;
                        break;
                    case R.id.mRadioButton4:
                        index = 4;
                        break;
                }
                checkedIndex= index;
                showView.setText(data[index]);
                exoPlayerManager.setPlaybackParameters(Float.parseFloat(data[index]),1);
                pw.dismiss();
            });
        });

    }
}
