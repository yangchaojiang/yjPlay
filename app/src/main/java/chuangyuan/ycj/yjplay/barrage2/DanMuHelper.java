package chuangyuan.ycj.yjplay.barrage2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.anbetter.danmuku.model.DanMuModel;
import com.anbetter.danmuku.model.utils.DimensionUtil;
import com.anbetter.danmuku.view.IDanMuParent;
import com.anbetter.danmuku.view.OnDanMuTouchCallBackListener;
import com.anbetter.log.MLog;
import com.facebook.fresco.helper.Phoenix;
import com.facebook.fresco.helper.listener.IResult;
import com.facebook.fresco.helper.utils.CircleBitmapTransform;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import chuangyuan.ycj.yjplay.R;
import chuangyuan.ycj.yjplay.barrage2.model.DanmakuEntity;
import chuangyuan.ycj.yjplay.barrage2.model.RichTextParse;

/**
 * 弹幕库使用帮助类
 *
 * 建议凡是弹幕中涉及到的图片，大小控制在50kb以内，尺寸控制在100x100以内（单位像素）
 *
 * Created by android_ls on 2016/12/18.
 */
public final class DanMuHelper {

    private ArrayList<WeakReference<IDanMuParent>> mDanMuViewParents;
    private Context mContext;

    public DanMuHelper(Context context) {
        this.mContext = context.getApplicationContext();
        this.mDanMuViewParents = new ArrayList<>();
    }

    public void release() {
        if (mDanMuViewParents != null) {
            for (WeakReference<IDanMuParent> danMuViewParentsRef : mDanMuViewParents) {
                if (danMuViewParentsRef != null) {
                    IDanMuParent danMuParent = danMuViewParentsRef.get();
                    if (danMuParent != null)
                        danMuParent.release();
                }
            }
            mDanMuViewParents.clear();
            mDanMuViewParents = null;
        }

        mContext = null;
    }

    public void add(final IDanMuParent danMuViewParent) {
        if (danMuViewParent != null) {
            danMuViewParent.clear();
        }

        if (mDanMuViewParents != null) {
            mDanMuViewParents.add(new WeakReference<>(danMuViewParent));
        }
    }

    public void addDanMu(DanmakuEntity danmakuEntity, boolean broadcast) {
        if (mDanMuViewParents != null) {
            WeakReference<IDanMuParent> danMuViewParent = mDanMuViewParents.get(0);
            if (!broadcast) {
                danMuViewParent = mDanMuViewParents.get(1);
            }

            DanMuModel danMuView = createDanMuView(danmakuEntity);
            if (danMuViewParent != null && danMuView != null && danMuViewParent.get() != null) {
                danMuViewParent.get().add(danMuView);
            }
        }
    }

    private DanMuModel createDanMuView(final DanmakuEntity entity) {
        final DanMuModel danMuView = new DanMuModel();
        danMuView.setDisplayType(DanMuModel.RIGHT_TO_LEFT);
        danMuView.setPriority(DanMuModel.NORMAL);
        danMuView.marginLeft = DimensionUtil.dpToPx(mContext, 30);

        if (entity.getType() == DanmakuEntity.DANMAKU_TYPE_USERCHAT) {
            // 图像
            int avatarSize = DimensionUtil.dpToPx(mContext, 30);
            danMuView.avatarWidth = avatarSize;
            danMuView.avatarHeight = avatarSize;

            String avatarImageUrl = entity.getAvatar();
            Phoenix.with(mContext)
                    .setUrl(avatarImageUrl)
                    .setWidth(avatarSize)
                    .setHeight(avatarSize)
                    .setResult(new IResult<Bitmap>() {
                        @Override
                        public void onResult(Bitmap bitmap) {
                            danMuView.avatar = CircleBitmapTransform.transform(bitmap);
                        }
                    })
                    .load();

            // 等级
            int level = entity.getLevel();
            int levelResId = getLevelResId(level);
            Drawable drawable = ContextCompat.getDrawable(mContext, levelResId);
            danMuView.levelBitmap = drawable2Bitmap(drawable);
            danMuView.levelBitmapWidth = DimensionUtil.dpToPx(mContext, 33);
            danMuView.levelBitmapHeight = DimensionUtil.dpToPx(mContext, 16);
            danMuView.levelMarginLeft = DimensionUtil.dpToPx(mContext, 5);

            if (level > 0 && level < 100) {
                danMuView.levelText = String.valueOf(level);
                danMuView.levelTextColor = ContextCompat.getColor(mContext, R.color.white);
                danMuView.levelTextSize = DimensionUtil.spToPx(mContext, 14);
            }

            // 显示的文本内容
            String name = entity.getName() + "：";
            String content = entity.getText();
            SpannableString spannableString = new SpannableString(name + content);
            spannableString.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.white)),
                    0,
                    name.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            MLog.i("spannableString = " + spannableString);

            danMuView.textSize = DimensionUtil.spToPx(mContext, 14);
            danMuView.textColor = ContextCompat.getColor(mContext, R.color.light_green);
            danMuView.textMarginLeft = DimensionUtil.dpToPx(mContext, 5);
            danMuView.text = spannableString;

            // 弹幕文本背景
            danMuView.textBackground = ContextCompat.getDrawable(mContext, R.drawable.corners_danmu);
            danMuView.textBackgroundMarginLeft = DimensionUtil.dpToPx(mContext, 15);
            danMuView.textBackgroundPaddingTop = DimensionUtil.dpToPx(mContext, 3);
            danMuView.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mContext, 3);
            danMuView.textBackgroundPaddingRight = DimensionUtil.dpToPx(mContext, 15);

            danMuView.enableTouch(true);
            danMuView.setOnTouchCallBackListener(new OnDanMuTouchCallBackListener() {

                @Override
                public void callBack(DanMuModel danMuView) {

                }
            });
        } else {
            // 显示的文本内容
            danMuView.textSize = DimensionUtil.spToPx(mContext, 14);
            danMuView.textColor = ContextCompat.getColor(mContext, R.color.light_green);
            danMuView.textMarginLeft = DimensionUtil.dpToPx(mContext, 5);

            if (entity.getRichText() != null) {
                danMuView.text = RichTextParse.parse(mContext, entity.getRichText(), DimensionUtil.spToPx(mContext, 18), false);
            } else {
                danMuView.text = entity.getText();
            }

            // 弹幕文本背景
            danMuView.textBackground = ContextCompat.getDrawable(mContext, R.drawable.corners_danmu);
            danMuView.textBackgroundMarginLeft = DimensionUtil.dpToPx(mContext, 15);
            danMuView.textBackgroundPaddingTop = DimensionUtil.dpToPx(mContext, 3);
            danMuView.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mContext, 3);
            danMuView.textBackgroundPaddingRight = DimensionUtil.dpToPx(mContext, 15);

            danMuView.enableTouch(false);
        }

        return danMuView;
    }

    /**
     * Drawable转换成Bitmap
     *
     * @param drawable
     * @return
     */
    public Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            // 转换成Bitmap
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            // .9图片转换成Bitmap
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ?
                            Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 设置等级
     *
     * @param level level=100表示主播
     */
    public int getLevelResId(int level) {
        int resId = R.drawable.icon_level_stage_zero;
        switch (level) {
            case 100:
//                resId = R.mipmap.lv_1000;
                break;
            case 0:
                resId = R.drawable.icon_level_stage_zero;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                resId = R.drawable.icon_level_stage_one;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                resId = R.drawable.icon_level_stage_two;
                break;
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                resId = R.drawable.icon_level_stage_three;
                break;
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                resId = R.drawable.icon_level_stage_four;
                break;
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
                resId = R.drawable.icon_level_stage_five;
                break;
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            default:
                resId = R.drawable.icon_level_stage_six;
                break;
        }

        return resId;
    }

}