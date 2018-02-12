package chuangyuan.ycj.yjplay.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.yjplay.MainListActivity;
import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.adapter.BRVAHTestAdapter;
import chuangyuan.ycj.yjplay.custom.MainCustomLayoutActivity;
import chuangyuan.ycj.yjplay.custom.MainListInfoCustomActivity;

import static android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation;

/**
 * author  yangc
 * date 2018/1/25
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class ClassFragment extends Fragment {
    private RecyclerView easyRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private BRVAHTestAdapter adapter;
    private int clickPosition;
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static ClassFragment newInstance(int sectionNumber) {
        ClassFragment fragment = new  ClassFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_list_2,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        easyRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        easyRecyclerView.setLayoutManager(linearLayoutManager);
        easyRecyclerView.addItemDecoration(new DividerDecoration(Color.GRAY, 1));
        adapter = new BRVAHTestAdapter(getContext());
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
        ManualPlayer manualPlayer = VideoPlayerManager.getInstance().getVideoPlayer();
        if (manualPlayer != null) {
            currPosition = manualPlayer.getCurrentPosition();
        }
        Log.d("currPosition", currPosition + "");
        Intent intent = new Intent(getContext(), MainListInfoCustomActivity.class);
        ActivityOptionsCompat activityOptions = makeSceneTransitionAnimation(
                getActivity(), new Pair<>(view.findViewById(R.id.exo_play_context_id),
                        MainCustomLayoutActivity.VIEW_NAME_HEADER_IMAGE));
        intent.putExtra("currPosition", currPosition);
        intent.putExtra("uri", uri);
        ActivityCompat.startActivityForResult(getActivity(), intent, 10, activityOptions.toBundle());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(MainListActivity.class.getName(), "onPause");
        //如果进入详情播放则不暂停视频释放资源//为空内部已经处理
        VideoPlayerManager.getInstance().onPause(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        VideoPlayerManager.getInstance().onResume();
    }

    ///在activity销毁回调，viewPager
    long st;
    @Override
    public void onDestroy() {
        VideoPlayerManager.getInstance().onDestroy();
        super.onDestroy();
        Log.d(MainListActivity.class.getName(), "耗时：" + (System.currentTimeMillis() - st));
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        VideoPlayerManager.getInstance().onConfigurationChanged(newConfig);//横竖屏切换
        super.onConfigurationChanged(newConfig);
    }
}
