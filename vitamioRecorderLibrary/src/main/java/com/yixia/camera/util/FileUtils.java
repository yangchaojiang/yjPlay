package com.yixia.camera.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {

    /** 拼接路径
     *  concatPath("/mnt/sdcard", "/DCIM/Camera")  	=>		/mnt/sdcard/DCIM/Camera
     *  concatPath("/mnt/sdcard", "DCIM/Camera")  	=>		/mnt/sdcard/DCIM/Camera
     *  concatPath("/mnt/sdcard/", "/DCIM/Camera")  =>		/mnt/sdcard/DCIM/Camera
     * */



    /** 检测文件是否可用 */
    public static boolean checkFile(File f) {
        if (f != null && f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.length() > 0))) {
            return true;
        }
        return false;
    }
	public static String getSdPath() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
		} else {
			return Environment.getDataDirectory().getAbsolutePath()+"/";
		}
	}

    /** 检测文件是否可用 */
    public static boolean checkFile(String path) {
        if (StringUtils.isNotEmpty(path)) {
            File f = new File(path);
            if (f != null && f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.length() > 0)))
                return true;
        }
        return false;
    }





	public static boolean deleteFile(File f) {
		if (f != null && f.exists() && !f.isDirectory()) {
			return f.delete();
		}
		return false;
	}

	public static void deleteDir(File f) {
		if (f != null && f.exists() && f.isDirectory()) {
			for (File file : f.listFiles()) {
				if (file.isDirectory())
					deleteDir(file);
				file.delete();
			}
			f.delete();
		}
	}

	public static void deleteDir(String f) {
		if (f != null && f.length() > 0) {
			deleteDir(new File(f));
		}
	}

	public static boolean deleteFile(String f) {
		if (f != null && f.length() > 0) {
			return deleteFile(new File(f));
		}
		return false;
	}

	/**
	 * 文件拷贝
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	public static boolean fileCopy(String from, String to) {
		boolean result = false;

		int size = 1 * 1024;

		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			byte[] buffer = new byte[size];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
			result = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
		}
		return result;
	}



}
