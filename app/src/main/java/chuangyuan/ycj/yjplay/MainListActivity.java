package chuangyuan.ycj.yjplay;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;

import static android.support.v4.app.ActivityOptionsCompat.*;


public class MainListActivity extends AppCompatActivity {

    RecyclerView easyRecyclerView;
    BRVAHTestAdapter adapter;
    Toolbar toolbar;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
            if (Build.VERSION.SDK_INT<21) {//低版本不支持高分辨视频
                list.add("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4");
            }else {
            if (i%5==0){
                list.add("http://mp4.vjshi.com/2017-08-16/af83af63d018816474067b51a835f4a2.mp4");
            }else if (i%5==1){
                list.add("http://mp4.vjshi.com/2017-06-17/f57dd833c4dbc3eabf17ba0f0bfaf746.mp4");
            }else if (i%5==2){
                list.add("http://mp4.vjshi.com/2017-10-19/53bfeb9eb92c1748596eaf2a1e649020.mp4");
            }else if (i%5==3){
                list.add("http://mp4.vjshi.com/2016-10-23/a0511ea830bb0620f94a5340a1879800.mp4");
            }else if (i%5==4){
                list.add("http://mp4.vjshi.com/2016-10-21/84bafe60ef0af95a5292f66b9f692504.mp4");
            }
            }
        }
        adapter.addData(list);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (position - firstItemPosition >= 0) {
                    //得到要更新的item的view
                    start(view,adapter.getItem(position).toString());

                }
            }

        });
       /* adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (position - firstItemPosition >= 0) {
                    //得到要更新的item的view
                    View view = easyRecyclerView.getChildAt(position - firstItemPosition);
                    start(view);

                }
            }
        });*/

    }

    private void start(View view,String uri) {
        //进入详细暂停视频
        long currPosition = 0;
        ManualPlayer manualPlayer = VideoPlayerManager.getInstance().getVideoPlayer();
        if (manualPlayer != null) {
            manualPlayer.setStartOrPause(false);
            currPosition =manualPlayer.getCurrentPosition();
        }
        Log.d("currPosition", currPosition + "");
        Intent intent = new Intent(MainListActivity.this, MainCustomActivity.class);
        ActivityOptionsCompat activityOptions = makeSceneTransitionAnimation(
                this, new Pair<>(view.findViewById(R.id.item_exo_player_view),
                        MainCustomActivity.VIEW_NAME_HEADER_IMAGE));
        intent.putExtra("currPosition", currPosition);
        intent.putExtra("uri", uri);
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

    long st;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayerManager.getInstance().onDestroy();
        Log.d(MainListActivity.class.getName(),"耗时："+(System.currentTimeMillis()-st));
    }

    @Override
    public void onBackPressed() {
        st=System.currentTimeMillis();
        if (VideoPlayerManager.getInstance().onBackPressed()) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK && data != null) {
            boolean isEnd = data.getBooleanExtra("isEnd", false);
            if (!isEnd) {
                long currPosition = data.getLongExtra("currPosition", 0);
                ManualPlayer manualPlayer= VideoPlayerManager.getInstance().getVideoPlayer();
                if (manualPlayer!=null) {
                    manualPlayer.setPosition(currPosition);
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
