package chuangyuan.ycj.yjplay;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;


public class MainActivity extends Activity {

    private ManualPlayer exoPlayerManager;
    private static final String TAG = "MainActivity";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        exoPlayerManager = new ManualPlayer(this, "http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8");
        // exoPlayerManager = new ManualPlayer(this, "storage/emulated/0/demo-170322.mp4");
        //   exoPlayerManager = new ExoUserPlayer(this,"http://61.240.143.212/251/4/75/letv-uts/14/ver_00_22-1104623442-avc-796490-aac-64000-3731920-407135057-88958e5c503076b4883b08e4866463e4-1496634082010.m3u8?crypt=84aa7f2e693&b=872&nlh=4096&nlt=60&bf=64&p2p=1&video_type=mp4&termid=2&tss=ios&platid=3&splatid=347&its=12346160&qos=5&fcheck=0&amltag=19650&mltag=19650&proxy=1039176421,1039176777,467476745&uid=2099409483.rp&keyitem=GOw_33YJAAbXYE-cnQwpfLlv_b2zAkYctFVqe5bsXQpaGNn3T1-vhw..&ntm=1496842800&nkey=d38915daa2cd661bc151a8932c741d7d&nkey2=69fa0c372fb90864946f04872d9da4ce&geo=CN-1-5-2&mmsid=65215193&tm=1496824407&key=6fed92c452fd91044819bf7b79986f27&playid=0&vtype=22&cvid=613150880745&payff=1&sign=mb&dname=mobile&p1=0&p2=00&p3=003&tag=mobile&pid=10037084&pay=0&ostype=android&hwtype=iphone&uidx=0&errc=0&gn=1190&ndtype=0&vrtmcd=106&buss=19650&cips=125.34.114.75");
        //  Log.d(TAG, UtilityAdapter.FFmpegVideoGetInfo("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"));
        exoPlayerManager.setmOnBackLListener(new ExoUserPlayer.OnBackLListener() {//
            @Override
            public void onBack() {//实现你返回操作
                      finish();
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onStart() {
        super.onStart();
        exoPlayerManager.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        exoPlayerManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        exoPlayerManager.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        exoPlayerManager.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        exoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        exoPlayerManager.onBackPressed();//使用播放返回键监听
    }


}
