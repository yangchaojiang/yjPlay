package chuangyuan.ycj.yjplay.barrage2.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;


import java.util.ArrayList;

import chuangyuan.ycj.yjplay.R;


/**
 * 解析富文本
 * <p>
 * Created by android_ls on 2016/11/25.
 */

public class RichTextParse {

    public static SpannableStringBuilder parse(final Context context, ArrayList<RichMessage> richText,
                                               int textSize, boolean isChatList) {
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (isChatList) {
            String name = "直播消息：";
            spannableStringBuilder.append(name);

            int nameColor = ContextCompat.getColor(context, R.color.live_yellow);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(nameColor),
                    0,
                    name.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (RichMessage message : richText) {
            final int length = spannableStringBuilder.length();
            if ("text".equals(message.getType())) {
                String content = message.getContent();
                spannableStringBuilder.append(content);

                String textColor = message.getColor();
                if (TextUtils.isEmpty(textColor)) {
                    textColor = "FFFFFF";
                }

                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#" + textColor)),
                        length,
                        length + content.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ("icon_gift".equals(message.getType())) {
                // 这里仅用于测试
                spannableStringBuilder.append("中奖礼物");
                final int imgSize = (int) (textSize * 1.5);
                Bitmap bitmap = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.live_gift_cucumber)).getBitmap();
                LHImageSpan imageSpan = new LHImageSpan(context, bitmap, imgSize);
                spannableStringBuilder.setSpan(imageSpan,
                        length,
                        length + 4,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//                int gifId = message.getGift_id();
//                LiveGiftInfo gift = LiveGiftUtils.get().getGift(gifId);
//                if (gift != null && !TextUtils.isEmpty(gift.image)) {
//                    spannableStringBuilder.append("中奖礼物");
//                    final int imgSize = (int) (textSize * 1.5);
//                    Phoenix.with(context)
//                            .setUrl(gift.image)
//                            .setWidth(imgSize)
//                            .setHeight(imgSize)
//                            .setResult(new IResult<Bitmap>() {
//                                @Override
//                                public void onResult(Bitmap bitmap) {
//                                    LHImageSpan imageSpan = new LHImageSpan(context, bitmap, imgSize);
//                                    spannableStringBuilder.setSpan(imageSpan,
//                                            length,
//                                            length + 4,
//                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                                }
//                            })
//                            .load();
            } else {
                String content = message.getContent();
                spannableStringBuilder.append(content);

                spannableStringBuilder.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(context,
                                R.color.light_green)),
                        length,
                        length + content.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannableStringBuilder;
    }

}
