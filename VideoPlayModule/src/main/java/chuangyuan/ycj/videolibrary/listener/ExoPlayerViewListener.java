package chuangyuan.ycj.videolibrary.listener;

/**
 * Created by yangc on 2017/7/21.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public interface ExoPlayerViewListener {

    void showAlertDialog();

    void showHidePro(int visibility);

    void setWatermarkImage(int res);

    void showSwitchName(String name);

    void showLoadStateView(int visibility);

    void showReplayView(int visibility);

    void showErrorStateView(int visibility);

    void setTitle(String title);

      void showNetSpeed(final String netSpeed);

    void onConfigurationChanged(int newConfig);



}
