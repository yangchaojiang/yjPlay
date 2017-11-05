package chuangyuan.ycj.yjplay;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import chuangyuan.ycj.yjplay.custom.MainCustomLayoutActivity;
import chuangyuan.ycj.yjplay.media.MainCustomMediaActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, MainDetailedActivity.class);
                        startActivity(intent);
                    }
                });
        findViewById(R.id.button2)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, MainListActivity.class);
                        startActivity(intent);
                    }
                });
        findViewById(R.id.button3)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, MainCustomLayoutActivity.class);
                        String uri;
                        if (Build.VERSION.SDK_INT < 23) {//低版本不支持高分辨视频
                            uri = "http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4";
                        } else {
                            //1080 视频
                            uri = "http://pic.ibaotu.com/00/34/35/51S888piCamj.mp4";
                        }
                        intent.putExtra("uri", uri);
                        startActivity(intent);
                    }
                });
        findViewById(R.id.button4)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, MainCustomLayoutActivity.class);
                        String uri;
                        if (Build.VERSION.SDK_INT < 23) {//低版本不支持高分辨视频
                            uri = "http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4";
                        } else {
                            //1080 视频
                            uri = "http://pic.ibaotu.com/00/34/35/51S888piCamj.mp4";
                        }
                        intent.putExtra("uri", uri);
                        intent.putExtra("isOnclick", true);
                        startActivity(intent);
                    }
                });
        findViewById(R.id.button5)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, GuangGaoPlayerdActivity.class);
                        intent.putExtra("isOnclick", true);
                        startActivity(intent);
                    }
                });
        findViewById(R.id.button6)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, MainCustomMediaActivity.class);
                        String uri;
                        if (Build.VERSION.SDK_INT < 23) {//低版本不支持高分辨视频
                            uri = "http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4";
                        } else {
                            //1080 视频
                            uri = "http://pic.ibaotu.com/00/34/35/51S888piCamj.mp4";
                        }
                        intent.putExtra("uri", uri);
                        intent.putExtra("isOnclick", true);
                        startActivity(intent);
                    }
                });
    }
}
