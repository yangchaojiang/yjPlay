package chuangyuan.ycj.yjplay.barrage2.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by android_ls on 2016/12/21.
 */

public class DanmakuEntity implements Parcelable {

    public static final int DANMAKU_TYPE_SYSTEM = 0;// 系统弹幕消息
    public static final int DANMAKU_TYPE_USERCHAT = 1;// 用户聊天弹幕消息

    private String avatar;
    private String name;
    private String userId;
    private int level;
    private int role;
    private int type;// 0是系统公屏，1是用户弹幕信息

    private String text;
    private ArrayList<RichMessage> richText; // 富文本

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<RichMessage> getRichText() {
        return richText;
    }

    public void setRichText(ArrayList<RichMessage> richText) {
        this.richText = richText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.name);
        dest.writeString(this.userId);
        dest.writeInt(this.level);
        dest.writeInt(this.role);
        dest.writeInt(this.type);
        dest.writeString(this.text);
        dest.writeTypedList(this.richText);
    }

    public DanmakuEntity() {
    }

    protected DanmakuEntity(Parcel in) {
        this.avatar = in.readString();
        this.name = in.readString();
        this.userId = in.readString();
        this.level = in.readInt();
        this.role = in.readInt();
        this.type = in.readInt();
        this.text = in.readString();
        this.richText = in.createTypedArrayList(RichMessage.CREATOR);
    }

    public static final Parcelable.Creator<DanmakuEntity> CREATOR = new Parcelable.Creator<DanmakuEntity>() {
        @Override
        public DanmakuEntity createFromParcel(Parcel source) {
            return new DanmakuEntity(source);
        }

        @Override
        public DanmakuEntity[] newArray(int size) {
            return new DanmakuEntity[size];
        }
    };
}
