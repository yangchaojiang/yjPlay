package chuangyuan.ycj.yjplay;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import java.util.ArrayList;
import java.util.List;

import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;


public class MainListActivity extends AppCompatActivity {

       EasyRecyclerView easyRecyclerView;
        TestAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        easyRecyclerView= (EasyRecyclerView) findViewById(R.id.list);
        easyRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        easyRecyclerView.addItemDecoration(new DividerDecoration(Color.GRAY,1));
        adapter=new TestAdapter(this);
        easyRecyclerView.setAdapter(adapter);
        List<String> list=new ArrayList<>();
        for (int i=0;i<50;i++){
            list.add("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4");
        }
        adapter.addAll(list);
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
        if (!VideoPlayerManager.getInstance().onBackPressed()){
            finish();
        }
    }
}
