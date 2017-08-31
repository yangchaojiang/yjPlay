package com.example.listvideo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.jiang.list.wight.CustomLinearLayoutManager;
import com.jiang.list.wight.VideoPlayerManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    EasyRecyclerView easyRecyclerView;
    TestAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        easyRecyclerView= (EasyRecyclerView) findViewById(R.id.list);
        easyRecyclerView.setLayoutManager(new CustomLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        easyRecyclerView.addItemDecoration(new DividerDecoration(Color.GRAY,1));
        adapter=new TestAdapter(this);
        easyRecyclerView.setAdapter(adapter);
        List<String> list=new ArrayList<>();
        for (int i=0;i<12;i++){
            list.add("http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4");
        }
        adapter.addAll(list);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        VideoPlayerManager.getInstance().onBackPressed();
    }
}
