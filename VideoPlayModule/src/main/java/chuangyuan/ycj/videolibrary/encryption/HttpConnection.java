package chuangyuan.ycj.videolibrary.encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by Sunmeng on 12/30/2016.
 * E-Mail:Sunmeng1995@outlook.com
 * Description:通过Socket建立连接
 */

public class HttpConnection extends Thread {

    private   Socket mSocket;
    private   IHttpStream mStream;
    private    InputStream mInputStream;
    private   OutputStream mOutputStream;
 private    final static boolean mIsDebug = false;
    public static final String KEY = "547fedc3a4bff6c8758987daa2a1cb84";

    HttpConnection(IHttpStream stream, Socket c) throws IOException {
        mSocket = c;
        mStream = stream;
        mInputStream = mSocket.getInputStream();
        mOutputStream = mSocket.getOutputStream();
    }

    public void run() {
        try {
            process();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(mInputStream));
        String line;
        String temp;
        String range;
        String fileName = null;
        int rangS = -1;
        int rangE = -1;
        while (true) {
            line = in.readLine();
            if (null == line || line.equals("\r\n") || line.equals(""))
                break;
            StringTokenizer s = new StringTokenizer(line);
            temp = s.nextToken();
            if (temp.equals("GET")) {
                fileName = s.nextToken();
                if (null == fileName)
                    continue;
                if (fileName.charAt(0) != '/')
                    fileName = "/" + fileName;
            } else if (mStream.acceptRange() && temp.equals("Range:")) {
                try {
                    range = s.nextToken();
                    if (null == range) continue;
                    range = range.replace("bytes=", "");
                    s = new StringTokenizer(range, "-");
                    //---start
                    if (!s.hasMoreTokens()) continue;
                    temp = s.nextToken();
                    rangS = Integer.parseInt(temp);
                    //---end
                    if (!s.hasMoreTokens()) {
                        rangE = Integer.MAX_VALUE;
                    } else {
                        temp = s.nextToken();
                        rangE = Integer.parseInt(temp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            mStream.writeStream(mOutputStream, fileName, rangS, rangE);
        } catch (Exception ignored) {
        }
        mSocket.close();
    }


    public static void sendOkResponse(OutputStream out, long length, String type) throws IOException {
        String header = "HTTP/1.1 200 OK\r\nServer: location server\r\nContent-Type: " + type + "\r\nContent-Length: " + length + "\r\nConnection: close\r\n\r\n";
        out.write(header.getBytes());
    }

    public static void sendOkResponse(OutputStream out, long length, String type, int rangS, int rangE, int rangSize) throws IOException {
        String header = "HTTP/1.1 206 Partial Content\r\nServer: location server\r\nContent-Type: " + type + "\r\nContent-Length: " + length + "\r\nAccept-Ranges: bytes\r\nContent-Range: bytes " + rangS + "-" + rangE + "/" + rangSize + "\r\nConnection: close\r\n\r\n";
        out.write(header.getBytes());
    }

    public static void send404Response(OutputStream out, String path) throws IOException {
        String header = "HTTP/1.1 404 File not found\r\nConnection: close\r\nContent-Type: text/html; charset=iso-8859-1\r\n\r\n" +
                "<html><body><table width='100%' height='100%' border=0><tr>" +
                "<td align=center valign=middle><b>404 File not found<br>The web page: " + path +
                " is invalid!<br><br>Please select valid web page by \"Content\" menu.</b></td></tr></table></body></html>\r\n";
        out.write(header.getBytes());
    }

    public static String getContentType(String fileName) {
        int i = fileName.lastIndexOf(".");
        String t = fileName;
        if (-1 != i)
            t = fileName.substring(i + 1);
        int j = t.lastIndexOf("#");
        if (-1 != j)
            t = t.substring(0, j);
        t = t.toLowerCase();
        if (t.equals("htm") || t.equals("html") || t.equals("txt") ||
                t.equals("text") || t.equals("mht") || t.equals("mhtml") ||
                t.equals("xht") || t.equals("xhtml")) {
            return "text/html";
        } else if (t.equals("jpg") || t.equals("jpeg") || t.equals("jpe")) {
            return "image/jpeg";
        } else if (t.equals("png")) {
            return "image/png";
        } else if (t.equals("bmp")) {
            return "image/bitmap";
        } else if (t.equals("css")) {
            return "text/css";
        } else if (t.equals("gif")) {
            return "image/gif";
        } else if (t.equals("js")) {
            return "application/x-javascript";
        } else if (t.equals("mp3")) {
            return "audio/mp3";
        } else if (t.equals("mp4")) {
            return "video/mpeg4";
        }
        return "application/octet-stream";
    }
}
