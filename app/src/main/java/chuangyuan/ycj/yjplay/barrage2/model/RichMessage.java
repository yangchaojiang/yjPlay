package chuangyuan.ycj.yjplay.barrage2.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android_ls on 2016/12/21.
 */

public class RichMessage implements Parcelable {

    private String type;
    private String content;
    private String color;
    private String extend;
    private int gift_id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public int getGift_id() {
        return gift_id;
    }

    public void setGift_id(int gift_id) {
        this.gift_id = gift_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.content);
        dest.writeString(this.color);
        dest.writeString(this.extend);
        dest.writeInt(this.gift_id);
    }

    public RichMessage() {
    }

    protected RichMessage(Parcel in) {
        this.type = in.readString();
        this.content = in.readString();
        this.color = in.readString();
        this.extend = in.readString();
        this.gift_id = in.readInt();
    }

    public static final Parcelable.Creator<RichMessage> CREATOR = new Parcelable.Creator<RichMessage>() {
        @Override
        public RichMessage createFromParcel(Parcel source) {
            return new RichMessage(source);
        }

        @Override
        public RichMessage[] newArray(int size) {
            return new RichMessage[size];
        }
    };
}
