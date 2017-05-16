package chuangyuan.ycj.videolibrary.encryption;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Sunmeng on 12/30/2016.
 * E-Mail:Sunmeng1995@outlook.com
 * Description:
 */

public interface IHttpStream {
    boolean writeStream(OutputStream out, String path, int rangeStart, int rangeEnd) throws IOException;
    boolean isOpen();
    boolean acceptRange();

}
