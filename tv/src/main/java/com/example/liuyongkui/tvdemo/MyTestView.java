package com.example.liuyongkui.tvdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by LIUYONGKUI726 on 2016-03-22.
 */
public class MyTestView extends TextView {

    private final  static String  Tag = "MyTestView";

    public MyTestView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyTestView(Context context) {
        super(context);
    }

    public MyTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {



        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(Tag, "onMeasure()");
        Toast.makeText(getContext(), "onMeasure", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);
        Log.d(Tag, "onLayout()");
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        Log.d(Tag, "onDraw");
    }
}
