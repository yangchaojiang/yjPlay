package chuangyuan.ycj.yjplay;

import android.content.Intent;
import android.graphics.Color;
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

import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;


public class Main2ListActivity extends AppCompatActivity {

    RecyclerView easyRecyclerView;
    TestAdapter adapter;
    Toolbar toolbar;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
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
        adapter = new TestAdapter(this);
        easyRecyclerView.setAdapter(adapter);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4");
        }
        adapter.addAll(list);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (position - firstItemPosition >= 0) {
                    //得到要更新的item的view
                    View view = easyRecyclerView.getChildAt(position - firstItemPosition);
                    start(view);

                }
            }
        });

    }

    private void start(View view) {
        //进入详细暂停视频
        long currPosition=0;
        if (VideoPlayerManager.getInstance().getVideoPlayer() != null) {
            VideoPlayerManager.getInstance().getVideoPlayer().setStartOrPause(false);
              currPosition = VideoPlayerManager.getInstance().getVideoPlayer().getCurrentPosition();
        }
        Log.d("currPosition", currPosition + "");
        Intent intent = new Intent(Main2ListActivity.this, MainCustomActivity.class);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, new Pair<>(view.findViewById(R.id.item_exo_player_view),
                        MainCustomActivity.VIEW_NAME_HEADER_IMAGE));
        intent.putExtra("currPosition", currPosition);
        ActivityCompat.startActivityForResult(this, intent, 10, activityOptions.toBundle());
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoPlayerManager.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoPlayerManager.getInstance().onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayerManager.getInstance().onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (VideoPlayerManager.getInstance().onBackPressed()) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK && data != null) {
            boolean isEnd=data.getBooleanExtra("isEnd",false);
            if (!isEnd) {
                long currPosition = data.getLongExtra("currPosition", 0);
                if (  VideoPlayerManager.getInstance().getVideoPlayer()!=null) {
                    VideoPlayerManager.getInstance().getVideoPlayer().setPosition(currPosition);
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
