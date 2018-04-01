package chuangyuan.ycj.yjplay.barrage2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.anbetter.danmuku.DanMuView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.helper.Phoenix;
import com.facebook.fresco.helper.utils.DensityUtil;
import com.google.gson.Gson;

import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.barrage2.model.DanmakuEntity;

public class Barrage2VideoActivity extends AppCompatActivity {

    private DanMuView mDanMuContainerRoom;
    private DanMuView mDanMuContainerBroadcast;
    private DanMuHelper mDanMuHelper;
    private boolean isHide;

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 建议放到Application的onCreate方法中进行初始化
        Phoenix.init(this);
        setContentView(R.layout.simple_exo_barrage_view_2);
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) findViewById(R.id.sdv_cover);
        ViewGroup.LayoutParams layoutParams = simpleDraweeView.getLayoutParams();
        layoutParams.width = DensityUtil.getDisplayWidth(this);
        layoutParams.height = DensityUtil.getDisplayHeight(this);

        String url = "http://ww2.sinaimg.cn/large/610dc034jw1fa42ktmjh4j20u011hn8g.jpg";
        Phoenix.with(simpleDraweeView)
                .setWidth(DensityUtil.getDisplayWidth(this))
                .setHeight(DensityUtil.getDisplayHeight(this))
                .load(url);

        mDanMuHelper = new DanMuHelper(this);

        // 全站弹幕（广播）
        mDanMuContainerBroadcast = (DanMuView) findViewById(R.id.danmaku_container_broadcast);
        mDanMuContainerBroadcast.prepare();
        mDanMuHelper.add(mDanMuContainerBroadcast);

        // 当前房间内的弹幕
        mDanMuContainerRoom = (DanMuView) findViewById(R.id.danmaku_container_room);
        mDanMuContainerRoom.prepare();
        mDanMuHelper.add(mDanMuContainerRoom);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DanmakuEntity danmakuEntity = new DanmakuEntity();
                danmakuEntity.setType(DanmakuEntity.DANMAKU_TYPE_USERCHAT);
                danmakuEntity.setName("小A");
                danmakuEntity.setAvatar("http://q.qlogo.cn/qqapp/100229475/E573B01150734A02F25D8E9C76AFD138/100");
                danmakuEntity.setLevel(23);
                danmakuEntity.setText("滚滚长江东逝水，浪花淘尽英雄~~");

                addRoomDanmaku(danmakuEntity);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jsonStr = "{\"type\":306,\"name\":\"\",\"text\":\"恭喜小A在小马过河的房间12200031赠送幸运礼物-300棒棒糖，中奖500倍，获得5000钻石。\",\"richText\":[{\"type\":\"text\",\"content\":\"恭喜\",\"color\":\"89F9DF\"},{\"type\":\"text\",\"content\":\"小A\"},{\"type\":\"text\",\"content\":\"在\",\"color\":\"89F9DF\"},{\"type\":\"text\",\"content\":\"小马过河\"},{\"type\":\"text\",\"content\":\"的房间\",\"color\":\"89F9DF\"},{\"type\":\"text\",\"content\":12200031},{\"type\":\"text\",\"content\":\"赠送\",\"color\":\"89F9DF\"},{\"type\":\"icon_gift\",\"extend\":\"text\",\"gift_id\":3816,\"content\":\"300棒棒糖\"},{\"type\":\"text\",\"content\":\"，中奖\",\"color\":\"89F9DF\"},{\"type\":\"text\",\"content\":\"500倍\",\"color\":\"FFED0A\"},{\"type\":\"text\",\"content\":\"，获得\",\"color\":\"89F9DF\"},{\"type\":\"text\",\"content\":\"5000钻石。\",\"color\":\"FFED0A\"}],\"live_id\":\"1220003114804106040\"}";

                Gson json = new Gson();
                DanmakuEntity danmakuEntity = json.fromJson(jsonStr, DanmakuEntity.class);
                danmakuEntity.setType(DanmakuEntity.DANMAKU_TYPE_SYSTEM);

                addDanmaku(danmakuEntity);
            }
        });

        mButton = (Button) findViewById(R.id.button3);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isHide = !isHide;
                mButton.setText(isHide?"显示弹幕":"隐藏弹幕");
                hideAllDanMuView(isHide);
            }
        });

    }

    /**
     * 发送一条全站弹幕
     */
    private void addDanmaku(DanmakuEntity danmakuEntity) {
        if (mDanMuHelper != null) {
            mDanMuHelper.addDanMu(danmakuEntity, true);
        }
    }

    /**
     * 发送一条房间内的弹幕
     */
    private void addRoomDanmaku(DanmakuEntity danmakuEntity) {
        if (mDanMuHelper != null) {
            mDanMuHelper.addDanMu(danmakuEntity, false);
        }
    }

    /**
     * 显示或者隐藏弹幕
     * @param hide
     */
    private void hideAllDanMuView(boolean hide) {
        if(mDanMuContainerBroadcast != null) {
            mDanMuContainerBroadcast.hideAllDanMuView(hide);
        }

        if(mDanMuContainerRoom != null) {
            mDanMuContainerRoom.hideAllDanMuView(hide);
        }
    }

    @Override
    protected void onDestroy() {
        if (mDanMuHelper != null) {
            mDanMuHelper.release();
            mDanMuHelper = null;
        }

        super.onDestroy();
    }

}
