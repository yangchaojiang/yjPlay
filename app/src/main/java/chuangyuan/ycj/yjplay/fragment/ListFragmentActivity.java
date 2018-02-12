package chuangyuan.ycj.yjplay.fragment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.yjplay.R;

public class ListFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
    @Override
    public void onBackPressed() {
        if (VideoPlayerManager.getInstance().onBackPressed()) {
            finish();
        }
    }
}
