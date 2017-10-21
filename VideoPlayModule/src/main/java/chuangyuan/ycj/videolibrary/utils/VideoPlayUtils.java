package chuangyuan.ycj.videolibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.WindowManager;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.source.BehindLiveWindowException;



/**
 *
 * @author yangc
 * date 2017/2/25
 * E-Mail:1007181167@qq.com
 * Description：
 */
public class VideoPlayUtils {
    /***
     * 获取地当前网速
     *
     * @param activity 活动对象
     * @return long
     **/
    public static long getTotalRxBytes(@NonNull Activity activity) {
        return TrafficStats.getUidRxBytes(activity.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ?0: (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    /****
     * kb 转换mb
     *
     * @param k 该参数表示kb的值
     * @return double
     */
    public static double getM(long k) {

        double m;
        m = k / 1024.0;
        //返回kb转换之后的M值
        return m;
    }

    /**
     * 检查当前网络是否可用
     *
     * @param activity 活动
     * @return boolean
     */
    public static boolean isNetworkAvailable(@NonNull Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (NetworkInfo aNetworkInfo : networkInfo) {
                    // 判断当前网络状态是否为连接状态
                    if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检查当前网络是否可用
     *
     * @param mContext 活动
     * @return boolean
     */
    public static boolean isWifi(@NonNull Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /***
     * 是否TYPE_SOURCE 异常
     *
     * @param e 异常
     * @return boolean
     ***/
    public static boolean isBehindLiveWindow(@NonNull ExoPlaybackException e) {
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


    /***
     * 得到活动对象
     *
     * @param context 上下文
     * @return Activity
     **/
    public static Activity scanForActivity(@NonNull Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    /***
     * 得到活动对象
     *
     * @param context 上下文
     * @return AppCompatActivity
     **/
    @Nullable
    public static AppCompatActivity getAppCompActivity(@NonNull Context context) {
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    /***
     * 得到活动对象
     *
     * @param context 上下文
     **/
    public static void showActionBar(@NonNull Context context) {
        AppCompatActivity appCompActivity= getAppCompActivity(context);
        if ( appCompActivity!= null) {
            ActionBar ab = appCompActivity.getSupportActionBar();
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false);
                ab.show();
            }
        }
        scanForActivity(context).getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /***
     * 隐藏标题栏
     *
     * @param context 上下文
     **/
    public static void hideActionBar(@NonNull Context context) {
        AppCompatActivity appCompActivity= getAppCompActivity(context);
        if ( appCompActivity!= null) {
            ActionBar ab = appCompActivity.getSupportActionBar();
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false);
                ab.hide();
            }
        }
        scanForActivity(context)
                .getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /***
     * 获取当前手机横屏状态
     *
     * @param activity 活动
     * @return int
     ***/
    public static boolean isLand(@NonNull Activity activity) {
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /***
     * 获取当前手机状态
     *
     * @param activity 活动
     * @return int
     ***/
    public static int getOrientation(@NonNull Activity activity) {
        return activity.getResources().getConfiguration().orientation;
    }

    /**
     * dp转px
     *
     * @param context 山下文
     * @param dpValue dp单位
     * @return int
     */
    public static int dip2px(@NonNull Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
