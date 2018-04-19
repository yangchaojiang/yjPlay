package chuangyuan.ycj.videolibrary.video;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.ArrayList;

/**
 * author  yangc
 * date 2018/4/17
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
public class ExoDataBean extends View.BaseSavedState {

    private  boolean isLand;
    private  int setSystemUiVisibility;
    private  int switchIndex;
    private ArrayList<String> nameSwitch;

    public ExoDataBean(Parcel source) {
        super(source);
    }



    public ExoDataBean(Parcelable superState) {
        super(superState);
    }

    public ArrayList<String> getNameSwitch() {
        return nameSwitch;
    }

    public void setNameSwitch(ArrayList<String> nameSwitch) {
        this.nameSwitch = nameSwitch;
    }

    public boolean isLand() {
        return isLand;
    }

    public void setLand(boolean land) {
        isLand = land;
    }

    public int getSetSystemUiVisibility() {
        return setSystemUiVisibility;
    }

    public void setSetSystemUiVisibility(int setSystemUiVisibility) {
        this.setSystemUiVisibility = setSystemUiVisibility;
    }

    public int getSwitchIndex() {
        return switchIndex;
    }

    public void setSwitchIndex(int switchIndex) {
        this.switchIndex = switchIndex;
    }

}
