package chuangyuan.ycj.yjplay.encrypt;

import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by yangc on 2017/11/11
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class Test {
    /**
     * 将数据写入一个文件
     *
     * @param destFilePath 要创建的文件的路径
     * @param data         待写入的文件数据
     * @param startPos     起始偏移量
     * @param length       要写入的数据长度
     * @param append       是否追加
     * @return 成功写入文件返回true, 失败返回false
     */
    public static boolean writeFile(String destFilePath, byte[] data, int startPos, int length, boolean append) {
        try {
            if (!createFile(destFilePath)) {
                return false;
            }
            FileOutputStream fos = new FileOutputStream(destFilePath, append);
            fos.write(data, startPos, length);
            fos.flush();
            if (null != fos) {
                fos.close();
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    /**
     * 创建一个文件，创建成功返回true
     *
     * @param filePath
     * @return
     */
    public static boolean createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                return file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static final String KEY = "547fedc3a4bff6c8758987daa2a1cb84";

    public static void main(String ars[]) throws FileNotFoundException {
      /*  try {
            String test = "D:\\VID_20171106_165835.mp4";
            String out = "D:\\test.mp4";
            FileInputStream inputStream = new FileInputStream(test);
            writeFile(out, KEY.getBytes("UTF-8"), 0, KEY.getBytes("UTF-8").length,false);
            byte[] buffer = new byte[1024 * 500];
            int readCount;
            long length = inputStream.available();
            long total = 0;
            while ((readCount = inputStream.read(buffer)) != -1) {
                // 处理下载的数据
                writeFile(out, buffer, 0, readCount,true);
                total = total + readCount;
                System.out.println("字节" + total + "总共长度" + length + "--进度：" + total * 100 / length);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static void base64(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ENCRYPTED_FILE_NAME = "encrypted_key.mp4";
                File mEncryptedFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), ENCRYPTED_FILE_NAME);
                String out = Environment.getExternalStorageDirectory()+"/test_base64.mp4";
                try {
                    FileInputStream fileInputStream = new FileInputStream(mEncryptedFile);
                    Base64InputStream base64InputStream = new Base64InputStream(fileInputStream,Base64.DEFAULT);
                    byte[] buffer = new byte[1024 * 500];
                    int readCount;
                    long total = 0;
                    FileOutputStream fos = new FileOutputStream(out,false);
                    long length =fileInputStream.available();
                    while ((readCount = base64InputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                        fos.write(buffer, 0, readCount);
                        total = total + readCount;
                        System.out.println("字节" + total + "总共长度" + length + "--进度：" + total * 100 / length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
