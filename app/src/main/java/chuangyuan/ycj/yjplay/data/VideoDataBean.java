package chuangyuan.ycj.yjplay.data;

import java.util.ArrayList;

public class VideoDataBean {

    private String imageUri;
    private String videoUri;

    public VideoDataBean(String imageUri, String videoUri) {
        this.imageUri = imageUri;
        this.videoUri = videoUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public static ArrayList<VideoDataBean> getDatas(){
        ArrayList<VideoDataBean> videoDataBeans=new ArrayList< >();
        videoDataBeans.add(new VideoDataBean(
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2Ftoutiao_iiilab_154157989889006.jpg",
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2F8LgSACxi.mp4"));

        videoDataBeans.add(new VideoDataBean(
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2Ftoutiao_iiilab_154157989889006.jpg",
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2FMq1VoAn5.mp4"));

        videoDataBeans.add(new VideoDataBean(
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2Feo8TTfQh_Moment.jpg",
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2FC1Aqld6J.mp4"));
        videoDataBeans.add(new VideoDataBean(
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2Ftoutiao_iiilab_154157989889006.jpg",
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2F8LgSACxi.mp4"));

        videoDataBeans.add(new VideoDataBean(
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2Ftoutiao_iiilab_154157989889006.jpg",
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2FMq1VoAn5.mp4"));

        videoDataBeans.add(new VideoDataBean(
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2Feo8TTfQh_Moment.jpg",
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2FC1Aqld6J.mp4"));
        videoDataBeans.add(new VideoDataBean(
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2Ftoutiao_iiilab_154157989889006.jpg",
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2F8LgSACxi.mp4"));

        videoDataBeans.add(new VideoDataBean(
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2Ftoutiao_iiilab_154157989889006.jpg",
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2FMq1VoAn5.mp4"));

        videoDataBeans.add(new VideoDataBean(
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2Feo8TTfQh_Moment.jpg",
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2FC1Aqld6J.mp4"));
        videoDataBeans.add(new VideoDataBean(
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2Ftoutiao_iiilab_154157989889006.jpg",
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2F8LgSACxi.mp4"));

        videoDataBeans.add(new VideoDataBean(
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2Ftoutiao_iiilab_154157989889006.jpg",
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2FMq1VoAn5.mp4"));

        videoDataBeans.add(new VideoDataBean(
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2Feo8TTfQh_Moment.jpg",
                "https://keba-app-input.oss-cn-beijing.aliyuncs.com/20181106%2FC1Aqld6J.mp4"));
        return  videoDataBeans;
    }
}

