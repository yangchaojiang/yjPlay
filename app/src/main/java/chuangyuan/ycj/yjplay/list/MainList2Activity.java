package chuangyuan.ycj.yjplay.list;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.adapter.BRVAHTest2Adapter;
import chuangyuan.ycj.yjplay.custom.MainCustomLayoutActivity;
import chuangyuan.ycj.yjplay.custom.MainListInfoCustomActivity;
import chuangyuan.ycj.yjplay.data.VideoDataBean;

import static android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation;


public class MainList2Activity extends AppCompatActivity {
    private static final String TAG = MainList2Activity.class.getName();
    private RecyclerView easyRecyclerView;
    private BRVAHTest2Adapter adapter;
    private Toolbar toolbar;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        easyRecyclerView = findViewById(R.id.list);
        linearLayoutManager = new GridLayoutManager(this, 2,LinearLayoutManager.VERTICAL, false);
        easyRecyclerView.setLayoutManager(linearLayoutManager);
        easyRecyclerView.addItemDecoration(new DividerDecoration(Color.GRAY, 1));
        adapter = new BRVAHTest2Adapter(this);
        easyRecyclerView.setAdapter(adapter);
        adapter.bindToRecyclerView(easyRecyclerView);
        adapter.addData(VideoDataBean.getDatas());
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapters, View view, int position) {
                //得到要更新的item的view
                start(view, adapter.getItem(position));
            }

        });

    }

    private void start(View view, VideoDataBean uri) {
        Intent intent = new Intent(MainList2Activity.this, MainListInfoCustomActivity.class);
        ActivityOptionsCompat activityOptions = makeSceneTransitionAnimation(
                this, new Pair<>(view.findViewById(R.id.exo_play_context_ids),
                        MainCustomLayoutActivity.VIEW_NAME_HEADER_IMAGE));
        intent.putExtra("uri", uri.getVideoUri());
        intent.putExtra("imageUri", uri.getImageUri());
        intent.putExtra("type", 3);
        intent.putExtra("currPosition", 0);
        ActivityCompat.startActivityForResult(this, intent, 10, activityOptions.toBundle());
    }

}
