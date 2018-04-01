package chuangyuan.ycj.yjplay.barrage2.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class LHImageSpan extends ImageSpan {

    private Context mContext;
    private Drawable mDrawable;
    private int mSize;

    public LHImageSpan(Context context, Bitmap b, int size) {
        super(context, b);
        mContext = context;
        mSize = size;

        mDrawable = context != null
                ? new BitmapDrawable(context.getResources(), b)
                : new BitmapDrawable(b);

        if (mSize > 0) {
            mDrawable.setBounds(0, 0, mSize, mSize);
        } else {
            mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(),
                    mDrawable.getIntrinsicHeight());
        }
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();
        int transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}

