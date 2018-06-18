/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package chuangyuan.ycj.yjplay.offline;

import android.app.Notification;

import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadManager.TaskState;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationUtil;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;

import chuangyuan.ycj.videolibrary.office.ExoWholeDownLoadManger;
import chuangyuan.ycj.videolibrary.offline.ExoDownLoadManger;
import chuangyuan.ycj.yjplay.R;

/** 下载媒体的服务. */
public class DemoDownloadService extends DownloadService {
  //前台通知的通知ID。, 一定不
  private static final int FOREGROUND_NOTIFICATION_ID = 1;//

 //用于创建低优先级通知通道的ID，或者如果应用将根据需要创建通知通道，则为{null}。, 如果指定，则每个包必须是*唯一的，如果该值太长，则该值可能会被截断
  private static final String CHANNEL_ID = "download_channel";//通知栏
  //前台通知更新之间的最大间隔，以毫秒为单位。
  public static final long DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL = 1000;
  private static final int JOB_ID = 1;
  //则该通道的用户可见名称的字符串资源标识符。, 建议的最大长度是40个字符;, 值*可能会被截断，如果它太长
 private  static  final  int hannel_name  =R.string.exo_download_notification_channel_name;
  public DemoDownloadService() {
    super(
        FOREGROUND_NOTIFICATION_ID,
        DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
        CHANNEL_ID,
            hannel_name);
  }

  @Override
  protected DownloadManager getDownloadManager() {
      return ExoWholeDownLoadManger.getSingle().getDownloadManager();
   //  return ExoDownLoadManger.getSingle().getDownloadManager();
  }


  //得到调度器
  @Override
  protected PlatformScheduler getScheduler() {
    return Util.SDK_INT >= 21 ? new PlatformScheduler(this, JOB_ID) : null;
  }

  //获得前台通知
  @Override
  protected Notification getForegroundNotification(TaskState[] taskStates) {
    return DownloadNotificationUtil.buildProgressNotification(
        /* context= */ this,
        R.drawable.exo_controls_play,
        CHANNEL_ID,
        /* contentIntent= */ null,
        /* message= */ null,
        taskStates);
  }

  /***
   * 更新进度显示通知
   * ***/
  @Override
  protected void onTaskStateChanged(TaskState taskState) {
    if (taskState.action.isRemoveAction) {
      return;
    }
    Notification notification = null;
    if (taskState.state == TaskState.STATE_COMPLETED) {
      notification =
          DownloadNotificationUtil.buildDownloadCompletedNotification(
              /* context= */ this,
              R.drawable.exo_controls_play,
              CHANNEL_ID,
              /* contentIntent= */ null,
              Util.fromUtf8Bytes(taskState.action.data));
    } else if (taskState.state == TaskState.STATE_FAILED) {
      notification =
          DownloadNotificationUtil.buildDownloadFailedNotification(
              /* context= */ this,
              R.drawable.exo_controls_play,
              CHANNEL_ID,
              /* contentIntent= */ null,
              Util.fromUtf8Bytes(taskState.action.data));
    }
    int notificationId = FOREGROUND_NOTIFICATION_ID + 1 + taskState.taskId;
    NotificationUtil.setNotification(this, notificationId, notification);
  }
}
