package com.yixia.camera.util;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.Toast;

import com.yixia.camera.R;

import java.io.File;
import java.io.IOException;

/**
 * @desc:声音工具类，包括录音，播放录音等
 */
public class SoundUtil {
	private static final double EMA_FILTER = 0.6;
	private static SoundUtil INSTANCE;
	private static MediaRecorder mMediaRecorder;
	private double mEMA = 0.0;
	private MediaPlayer mMediaPlayer;

	public SoundUtil() {
	}

	public static SoundUtil getInstance() {
		if (INSTANCE == null) {
			synchronized (SoundUtil.class) {
				if (INSTANCE == null) {
					INSTANCE = new SoundUtil();
				}
			}
		}

		return INSTANCE;
	}

	/**
	 * 初始化
	 */
	private static void initMedia() throws Exception {
		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		}
	}

	/**
	 * 开始录音
	 *
	 * @param name
	 *  声音存储的路径
	 */
	public void startRecord(Context context, String name) {
		try {
			initMedia();
		} catch (Exception e1) {
			e1.printStackTrace();
			Toast.makeText(context, "麦克风不可用", 0).show();
		}
		StringBuilder sb = getFilePath(context, name);
		mMediaRecorder.setOutputFile(sb.toString());
		Log.e("fff", "录音路径:" + sb.toString());
		try {
			mMediaRecorder.prepare();
			mMediaRecorder.start();

			mEMA = 0.0;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public StringBuilder getFilePath(Context context, String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(getDiskFielsDir(context));
		sb.append(File.separator);
		sb.append(name);
		return sb;
	}

	/**
	 * 获得录音的文件名
	 *
	 * @param
	 * @param
	 * @return
	 */
	public String getRecordFileName() {

		return System.currentTimeMillis() + ".aac";
	}

	/**
	 * 停止录音
	 */
	public void stopRecord() throws IllegalStateException {
		if (mMediaRecorder != null) {
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	/**
	 * 获得缓存路径
	 *
	 * @param
	 * @return
	 */
	public String getDiskCacheDir(Context context) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return cachePath;
	}

	/**
	 * 获取录音地址
	 *
	 * @param context
	 * @return
	 */
	// public String getDiskFielsDir(Context context) {
	// String cachePath;
	// if (Environment.MEDIA_MOUNTED.equals(Environment
	// .getExternalStorageState())
	// || !Environment.isExternalStorageRemovable()) {
	// cachePath = context.getExternalFilesDir(
	// android.os.Environment.DIRECTORY_MUSIC).getPath();
	// } else {
	// cachePath = context.getFilesDir().getPath();
	// }
	// return cachePath;
	// }

	/**
	 * 获得缓存路径
	 *
	 * @param
	 * @return
	 */
	public String getDiskFielsDir(Context context) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			File file = null;
			if (file == null) {
				File musicFile = context
						.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
				if (musicFile == null) {

				}
				String path = context.getExternalFilesDir(
						Environment.DIRECTORY_MUSIC).getPath();
				file = new File(path);
			}
			if (!file.exists()) {
				file.mkdirs();
			}
			cachePath = file.getPath();
		} else {
			cachePath = context.getFilesDir().getPath();
		}
		return cachePath;
	}

	public double getAmplitude() {
		if (mMediaRecorder != null)
			return (mMediaRecorder.getMaxAmplitude() / 2700.0);
		else
			return 0;

	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		return mEMA;
	}

	/**
	 * @Description
	 * @param
	 */
	public void playRecorder(final Context context, String filePath,
							 Boolean flag, final ImageView imageView, final boolean flag1,
							 final Integer position) {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		}
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			if (!flag) {// 播放本地语音
				File file = new File(filePath);
				if (file.exists()) {
					mMediaPlayer.setDataSource(filePath);
					mMediaPlayer.prepare();
				} else {
					// 不存在语音文件
				}
			} else {// 播放网络语音
//				wrieFile(filePath, context, position);
				mMediaPlayer.setDataSource(filePath);
				mMediaPlayer.prepare();
			}
			mMediaPlayer.start();
			mMediaPlayer
					.setOnCompletionListener(new OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {
							mMediaPlayer.release();
							mMediaPlayer = null;
							if (flag1) {
								imageView
										.setImageResource(R.drawable.voice_playing_l);
							} else {
								imageView
										.setImageResource(R.drawable.voice_playing_r);
							}
						}
					});
			if (flag1) {
//				imageView.setImageResource(R.mipmap.voice_left);

			} else {
//				imageView.setImageResource(R.mipmap.voice_right);

			}
			AnimationDrawable animationDrawable = (AnimationDrawable) imageView
					.getDrawable();
			animationDrawable.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

//	/*** 文件下载到本地 **/
//	private void wrieFile(String url, Context context, final Integer position) {
//		TwitterRestClient.get(url, new FileAsyncHttpResponseHandler(context) {
//			@Override
//			public void onSuccess(int arg0, PreferenceActivity.Header[] arg1, File file) {
//				// TODO Auto-generated method stub
//				//GroupMessageActivity.adapter.getItem(position).setFilePath(
//	//					file.getPath());
//////				if (GroupMessageActivity.typeInfo == 0) {
//////					MyDBServlet.testUpdateSet("filePath", file.getPath(),
//////							"repid",
//////							GroupMessageActivity.adapter.getItem(position)
//////									.getRepid());
////				} else {
////					MyDBServlet.testUpdateSet("filePath", file.getPath(),
////							"talid",
////							GroupMessageActivity.adapter.getItem(position)
////									.getTalid());
////				}
//			}
//
//			@Override
//			public void onFailure(int arg0, PreferenceActivity.Header[] arg1, Throwable arg2,
//								  File arg3) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//	}

	public static int dipToPX(final Context ctx, float dip) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dip, ctx.getResources().getDisplayMetrics());
	}

	/**
	 * sp*ppi/160 =px
	 *
	 * @param ctx
	 * @param dip
	 * @return
	 */
	public static int spToPX(final Context ctx, float sp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				ctx.getResources().getDisplayMetrics());
	}
}
