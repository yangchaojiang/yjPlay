package chuangyuan.ycj.videolibrary.utils;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import java.lang.reflect.Constructor;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.video.MediaSourceBuilder;


/**
 * The type Video play utils.
 *
 * @author yangc          date 2017/2/25         E-Mail:1007181167@qq.com         Description：
 */
public class VideoPlayUtils {
    /***
     * 获取地当前网速
     *
     * @param activity 活动对象
     * @return long total rx bytes
     */
    public static long getTotalRxBytes(@NonNull Context activity) {
        return TrafficStats.getUidRxBytes(activity.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    /****
     * kb 转换mb
     *
     * @param k 该参数表示kb的值
     * @return double m
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
     * @return boolean boolean
     */
    public static boolean isNetworkAvailable(@NonNull Context activity) {
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
     * 检查当前是否TV
     *
     * @param mContext 活动
     * @return boolean boolean
     */
    public static boolean isTv(@NonNull Context mContext) {
        return getUiMode(mContext) == Configuration.UI_MODE_TYPE_TELEVISION;
    }

    private static int getUiMode(@NonNull Context mContext) {
        UiModeManager uiModeManager = (UiModeManager) mContext.getSystemService(Context.UI_MODE_SERVICE);
        assert uiModeManager != null;
        Log.d("getUiMode",uiModeManager.getCurrentModeType()+"");
        return uiModeManager.getCurrentModeType();

    }

    /**
     * 检查当前网络是否可用
     *
     * @param mContext 活动
     * @return boolean boolean
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
     * @return boolean boolean
     */
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

    @Nullable
    public static ViewGroup getViewGroup(@NonNull Context context) {
        return (ViewGroup) (scanForActivity(context)).findViewById(Window.ID_ANDROID_CONTENT);
    }

    /**
     * 获取屏幕的宽度px
     *
     * @param context 上下文
     * @return 屏幕宽px
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高度px
     *
     * @param context 上下文
     * @return 屏幕高px
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        assert windowManager != null;
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.heightPixels;
    }

    /***
     * 得到活动对象
     *
     * @param context 上下文
     * @return Activity activity
     */
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
     * @return AppCompatActivity app comp activity
     */
    @Nullable
    private static AppCompatActivity getAppCompActivity(@NonNull Context context) {
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
     */
    public static void showActionBar(@NonNull Context context) {
        AppCompatActivity appCompActivity = getAppCompActivity(context);
        if (appCompActivity != null) {
            ActionBar ab = appCompActivity.getSupportActionBar();
            if (ab != null) {
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
     */
    public static void hideActionBar(@NonNull Context context) {
        AppCompatActivity appCompActivity = getAppCompActivity(context);
        if (appCompActivity != null) {
            ActionBar ab = appCompActivity.getSupportActionBar();
            if (ab != null) {
                ab.hide();
            }
        }
        scanForActivity(context)
                .getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 获取ActionBar高度
     *
     * @param activity activity
     * @return ActionBar高度
     */
    public static int getActionBarHeight(Activity activity) {
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        }
        return 0;
    }

    /**
     * 获取状态栏高度
     *
     * @param context 上下文
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /***
     * 获取当前手机横屏状态
     *
     * @param activity 活动
     * @return int boolean
     */
    public static boolean isLand(@NonNull Context activity) {
        Resources resources = activity.getResources();
        assert resources != null;
        Configuration configuration = resources.getConfiguration();
        Assertions.checkState(configuration != null);
        return resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }


    /**
     * dp转px
     *
     * @param context 山下文
     * @param dpValue dp单位
     * @return int int
     */
    public static int dip2px(@NonNull Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * Makes a best guess to infer the type from a file name.
     *
     * @param fileName Name of the file. It can include the path of the file.
     * @return The content type.
     */
    /**
     * Makes a best guess to infer the type from a {@link Uri}.
     *
     * @param uri The {@link Uri}.
     * @return The content type.
     */
    @C.ContentType
    public static int inferContentType(Uri uri) {
        String path = uri.getPath();
        return path == null ? C.TYPE_OTHER : inferContentType(path);
    }


    /**
     * Infer content type int.
     *
     * @param fileName the file name
     * @return the int
     */
    @C.ContentType
    private static int inferContentType(String fileName) {
        fileName = Util.toLowerInvariant(fileName);
        if (fileName.matches(".*m3u8.*")) {
            return C.TYPE_HLS;
        } else if (fileName.matches(".*mpd.*")) {
            return C.TYPE_DASH;
        } else if (fileName.matches(".*\\.ism(l)?(/manifest(\\(.+\\))?)?")) {
            return C.TYPE_SS;
        } else {
            return C.TYPE_OTHER;
        }
    }

    /**
     * Infer content type MediaSourceBuilder.
     *
     * @param mContext mContext
     * @param listener listener
     * @return the MediaSourceBuilder
     */
    @NonNull
    public static MediaSourceBuilder buildMediaSourceBuilder(@NonNull Context mContext, @Nullable DataSourceListener listener) {
        try {
            Class<?> clazz = Class.forName("chuangyuan.ycj.videolibrary.whole.WholeMediaSource");
            Constructor<?> constructor = clazz.getConstructor(Context.class, DataSourceListener.class);
            return (MediaSourceBuilder) constructor.newInstance(mContext, listener);
        } catch (Exception e) {
            return new MediaSourceBuilder(mContext, listener);
        }
    }
}



