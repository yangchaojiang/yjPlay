package chuangyuan.xiangjiang.videolibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.source.BehindLiveWindowException;

/**
 * Created by yangc on 2017/2/25.
 * E-Mail:1007181167@qq.com
 * Description：
 */
public class VideoPlayUtils {
    public static final String TAG = "VideoPlayUtils";

    /***
     * 获取地当前网速
     *
     * @param activity 活动对象
     * @return long
     **/
    public static long getTotalRxBytes(Activity activity) {
        if (activity == null) return 0;
        return TrafficStats.getUidRxBytes(activity.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    /****
     * kb 转换mb
     *
     * @param k 该参数表示kb的值
     * @return  double
     */
    public static double getM(long k) {
        double m;
        m = k / 1024.0;
        return m; //返回kb转换之后的M值
    }

    /**
     * 检查当前网络是否可用
     *@param activity  活动
     * @return boolean
     */
    public static boolean isNetworkAvailable(Activity activity) {
        if (activity == null) return false;
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /***
     *
     *是否TYPE_SOURCE 异常
     * @param e  异常
     * @return  boolean
     * ***/
    public static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

}
