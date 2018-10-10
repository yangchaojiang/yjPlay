package chuangyuan.ycj.yjplay;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.adapter.BRVAHTestAdapter;
import chuangyuan.ycj.yjplay.custom.MainCustomLayoutActivity;
import chuangyuan.ycj.yjplay.custom.MainListInfoCustomActivity;

import static android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation;


public class MainListActivity extends AppCompatActivity {
    private RecyclerView easyRecyclerView;
    private BRVAHTestAdapter adapter;
    private Toolbar toolbar;
    private LinearLayoutManager linearLayoutManager;
    private int clickPosition;
    private boolean isReset = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MainListActivity.class.getName(), "onCreate");
        setContentView(R.layout.activity_list);
        toolbar =   findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VideoPlayerManager.getInstance().onBackPressed()) {
                    finish();
                }
            }
        });
        easyRecyclerView = (RecyclerView) findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        easyRecyclerView.setLayoutManager(linearLayoutManager);
        easyRecyclerView.addItemDecoration(new DividerDecoration(Color.GRAY, 1));
        adapter = new BRVAHTestAdapter(this);
        easyRecyclerView.setAdapter(adapter);
        adapter.bindToRecyclerView(easyRecyclerView);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            if (Build.VERSION.SDK_INT < 21) {//低版本不支持高分辨视频
                list.add(getString(R.string.uri_test_3));
            } else {
                if (i % 7 == 0) {
                    list.add(getString(R.string.uri_test_1));
                } else if (i % 7 == 1) {
                    list.add(getString(R.string.uri_test_10));
                } else if (i % 7 == 2) {
                    list.add(getString(R.string.uri_test_5));
                } else if (i % 7 == 3) {
                    list.add(getString(R.string.uri_test_6));
                } else if (i % 7 == 4) {
                    list.add(getString(R.string.uri_test_7));
                } else if (i % 7 == 5) {
                    list.add(getString(R.string.uri_test_9));
                } else if (i % 7 == 6) {
                    list.add(getString(R.string.uri_test_8));
                }
            }
        }
        list.add("/storage/sdcard0/DCIM/Camera/VID_20180829_100348.mp4");
        adapter.addData(list);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //得到要更新的item的view
                clickPosition = position;
                start(view, adapter.getItem(position).toString());

            }

        });
    }

    private void start(View view, String uri) {
        //进入详细暂停视频
        long currPosition = 0;
        ExoUserPlayer manualPlayer = VideoPlayerManager.getInstance().getVideoPlayer();
        if (manualPlayer != null) {
            isReset = false;
            currPosition = manualPlayer.getCurrentPosition();
        }
        Log.d("currPosition", currPosition + "");
        Intent intent = new Intent(MainListActivity.this, MainListInfoCustomActivity.class);
        ActivityOptionsCompat activityOptions = makeSceneTransitionAnimation(
                this, new Pair<>(view.findViewById(R.id.exo_play_context_id),
                        MainCustomLayoutActivity.VIEW_NAME_HEADER_IMAGE));
        intent.putExtra("currPosition", currPosition);
        intent.putExtra("uri", uri);
        ActivityCompat.startActivityForResult(this, intent, 10, activityOptions.toBundle());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(MainListActivity.class.getName(), "onPause");
        //如果进入详情播放则不暂停视频释放资源//为空内部已经处理
       VideoPlayerManager.getInstance().onPause(isReset);

    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoPlayerManager.getInstance().onResume();
    }

    long st;

    @Override
    protected void onDestroy() {
        VideoPlayerManager.getInstance().onDestroy();
        super.onDestroy();
        Log.d(MainListActivity.class.getName(), "耗时：" + (System.currentTimeMillis() - st));
    }

    @Override
    public void onBackPressed() {
        st = System.currentTimeMillis();
        if (VideoPlayerManager.getInstance().onBackPressed()) {
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        VideoPlayerManager.getInstance().onConfigurationChanged(newConfig);//横竖屏切换
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK && data != null) {
            boolean isEnd = data.getBooleanExtra("isEnd", false);
            long currPosition = data.getLongExtra("currPosition", 0);
            if (!isEnd) {
                Log.d("onActivityResult", "onActivityResult:" + currPosition);
                ExoUserPlayer manualPlayer = VideoPlayerManager.getInstance().getVideoPlayer();
                if (manualPlayer != null) {
                    //从详情页面需要的重新的原来view复原//否测原来无法播放
                    VideoPlayerView videoPlayerView = (VideoPlayerView) adapter.getViewByPosition(clickPosition, R.id.exo_play_context_id);
                    VideoPlayerManager.getInstance().switchTargetView(manualPlayer, videoPlayerView, false);
                    manualPlayer.setPosition(currPosition);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
