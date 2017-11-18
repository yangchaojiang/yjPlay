package chuangyuan.ycj.yjplay.data;

import chuangyuan.ycj.videolibrary.listener.ItemVideo;

/**
 * @author yangc
 * @date 2017/10/18
 * Created by yangc on 2017/10/18.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class TestDataBean implements ItemVideo {
    private  String  uri;
    @Override
    public String getVideoUri() {
        return uri;
    }

    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
}
