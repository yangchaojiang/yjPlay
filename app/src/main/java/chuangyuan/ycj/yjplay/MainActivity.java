package chuangyuan.ycj.yjplay;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;

import chuangyuan.ycj.videolibrary.utils.VideoInfoListener;
import chuangyuan.ycj.videolibrary.video.GestureVideoPlayer;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;

public class MainActivity extends AppCompatActivity {

    private GestureVideoPlayer exoPlayerManager;
    private static final String TAG = "MainActivity";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout);
        exoPlayerManager = new GestureVideoPlayer(this,R.id.exo_play_context_id);
       // exoPlayerManager = new ManualPlayer(this, "/storage/emulated/0/DCIM/Camera/VID_20170717_011150.mp4");
       //   exoPlayerManager = new ExoUserPlayerTest(this,"http://61.240.143.212/251/4/75/letv-uts/14/ver_00_22-1104623442-avc-796490-aac-64000-3731920-407135057-88958e5c503076b4883b08e4866463e4-1496634082010.m3u8?crypt=84aa7f2e693&b=872&nlh=4096&nlt=60&bf=64&p2p=1&video_type=mp4&termid=2&tss=ios&platid=3&splatid=347&its=12346160&qos=5&fcheck=0&amltag=19650&mltag=19650&proxy=1039176421,1039176777,467476745&uid=2099409483.rp&keyitem=GOw_33YJAAbXYE-cnQwpfLlv_b2zAkYctFVqe5bsXQpaGNn3T1-vhw..&ntm=1496842800&nkey=d38915daa2cd661bc151a8932c741d7d&nkey2=69fa0c372fb90864946f04872d9da4ce&geo=CN-1-5-2&mmsid=65215193&tm=1496824407&key=6fed92c452fd91044819bf7b79986f27&playid=0&vtype=22&cvid=613150880745&payff=1&sign=mb&dname=mobile&p1=0&p2=00&p3=003&tag=mobile&pid=10037084&pay=0&ostype=android&hwtype=iphone&uidx=0&errc=0&gn=1190&ndtype=0&vrtmcd=106&buss=19650&cips=125.34.114.75");
        //  Log.d(TAG, UtilityAdapter.FFmpegVideoGetInfo("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"));
        exoPlayerManager.setShowVideoSwitch(true);
        //exoPlayerManager.setPlayUri("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4");
        //exoPlayerManager.setPlayUri("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8");
       // exoPlayerManager.setPlayUri("/storage/emulated/0/DCIM/Camera/VID_20170717_011150.mp4");
        String [] test={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4","http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
        String[] name={"超清","高清","标清"};
        exoPlayerManager.setPlaySwitchUri(test,name);
        exoPlayerManager.setVideoInfoListener(new VideoInfoListener() {
            @Override
            public void onPlayStart() {

            }

            @Override
            public void onLoadingChanged() {

            }

            @Override
            public void onPlayerError(ExoPlaybackException e) {

            }

            @Override
            public void onPlayEnd() {

            }

            @Override
            public void onBack() {
                Toast.makeText(MainActivity.this,"f返回",Toast.LENGTH_LONG).show();

            }
        });

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
