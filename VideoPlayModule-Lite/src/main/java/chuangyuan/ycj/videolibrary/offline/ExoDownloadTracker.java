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
package chuangyuan.ycj.videolibrary.offline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.offline.ActionFile;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadManager.TaskState;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.offline.ProgressiveDownloadHelper;
import com.google.android.exoplayer2.offline.SegmentDownloadAction;
import com.google.android.exoplayer2.offline.TrackKey;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider;
import com.google.android.exoplayer2.ui.TrackNameProvider;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import chuangyuan.ycj.videolibrary.R;

/**
 * Tracks media that has been downloaded.
 *
 * <p>Tracked downloads are persisted using an {@link ActionFile}, however in a real application
 * it's expected that state will be stored directly in the application's media database, so that it
 * can be queried efficiently together with other information about the media.
 */
public class ExoDownloadTracker implements DownloadManager.Listener,Runnable{

    @Override
    public void run() {

    }

    /** Listens for changes in the tracked downloads. */
  public interface Listener {

    /** Called when the tracked downloads changed. */
    void onDownloadsChanged(int state);
  }
  private static final String TAG = "ExoDownloadTracker";
  private final Context context;
  protected final DataSource.Factory dataSourceFactory;
  private final TrackNameProvider trackNameProvider;
  private final CopyOnWriteArraySet<Listener> listeners;
  private final HashMap<Uri, DownloadAction> trackedDownloadStates;
  private final ActionFile actionFile;
  private final Handler actionFileWriteHandler;
 private  final Class<? extends  DownloadService> downloadServiceClass;

  public ExoDownloadTracker(
      Context context,
      DataSource.Factory dataSourceFactory,
      File actionFile,
      DownloadAction.Deserializer[] deserializers,Class<? extends  DownloadService> downloadServiceClass) {
    this.context = context.getApplicationContext();
    this.dataSourceFactory = dataSourceFactory;
    this.actionFile = new ActionFile(actionFile);
    this.downloadServiceClass=downloadServiceClass;
    trackNameProvider = new DefaultTrackNameProvider(context.getResources());
    listeners = new CopyOnWriteArraySet<>();
    trackedDownloadStates = new HashMap<>();
    HandlerThread actionFileWriteThread = new HandlerThread("ExoDownloadTracker");
    actionFileWriteThread.start();
    actionFileWriteHandler = new Handler(actionFileWriteThread.getLooper());
    loadTrackedActions(deserializers);
  }

  /****
   * 增加监听监事件
   * **/
  public void addListener(Listener listener) {
    listeners.add(listener);
  }
    /****
     * 移除监听监事件
     * **/
  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  public boolean isDownloaded(Uri uri) {
    return trackedDownloadStates.containsKey(uri);
  }

  @SuppressWarnings("unchecked")
  public <K> List<K> getOfflineStreamKeys(Uri uri) {
    if (!trackedDownloadStates.containsKey(uri)) {
      return Collections.emptyList();
    }
    DownloadAction action = trackedDownloadStates.get(uri);
    if (action instanceof SegmentDownloadAction) {
      return ((SegmentDownloadAction) action).keys;
    }
    return Collections.emptyList();
  }

  /***
   * 启动下载或者暂定下载
   * @param  activity activity
   *@param name
   *@param    uri extension
   * **/
  public void toggleDownload(Activity activity, String name, Uri uri, String extension) {
    if (isDownloaded(uri)) {
      DownloadAction removeAction =
          getDownloadHelper(uri, extension).getRemoveAction(Util.getUtf8Bytes(name));
      startServiceWithAction(removeAction);
    } else {
      StartDownloadDialogHelper helper =
          new StartDownloadDialogHelper(activity, getDownloadHelper(uri, extension), name);
      helper.prepare();
    }
  }

  // DownloadManager.Listener

  @Override
  public void onInitialized(DownloadManager downloadManager) {
    // Do nothing.
  }

  @Override
  public void onTaskStateChanged(DownloadManager downloadManager, TaskState taskState) {
    DownloadAction action = taskState.action;
    Uri uri = action.uri;
    Log.d(TAG,"onTaskStateChanged:"+taskState.state);
    if ((action.isRemoveAction && taskState.state == TaskState.STATE_COMPLETED)
        || (!action.isRemoveAction && taskState.state == TaskState.STATE_FAILED)) {
      // A download has been removed, or has failed. Stop tracking it.
      if (trackedDownloadStates.remove(uri) != null) {
        handleTrackedDownloadStatesChanged( taskState.state);
      }
    }else {
        handleTrackedDownloadStatesChanged( taskState.state);
    }
  }

  @Override
  public void onIdle(DownloadManager downloadManager) {
    // Do nothing.
  }
  // Internal methods
  private void loadTrackedActions(DownloadAction.Deserializer[] deserializers) {
    try {
      DownloadAction[] allActions = actionFile.load(deserializers);
      for (DownloadAction action : allActions) {
        trackedDownloadStates.put(action.uri, action);
      }
    } catch (IOException e) {
      Log.e(TAG, "Failed to load tracked actions", e);
    }
  }

  /**
   * 处理跟踪的下载状态
   * **/
  private void handleTrackedDownloadStatesChanged(int taskState) {
    for (Listener listener : listeners) {
      listener.onDownloadsChanged(taskState);
    }
    final DownloadAction[] actions = trackedDownloadStates.values().toArray(new DownloadAction[0]);
    actionFileWriteHandler.post(
        new Runnable() {
          @Override
          public void run() {
            try {
              actionFile.store(actions);
            } catch (IOException e) {
              Log.e(TAG, "Failed to store tracked actions", e);
            }
          }
        });
  }

  private void startDownload(DownloadAction action) {
    if (trackedDownloadStates.containsKey(action.uri)) {
      // This content is already being downloaded. Do nothing.
      return;
    }
    trackedDownloadStates.put(action.uri, action);
    handleTrackedDownloadStatesChanged(TaskState.STATE_STARTED);
    startServiceWithAction(action);
  }

  private void startServiceWithAction(DownloadAction action) {
    DownloadService.startWithAction(context,downloadServiceClass, action, false);
  }

  protected DownloadHelper getDownloadHelper(Uri uri, String extension) {
    int type = Util.inferContentType(uri, extension);
    switch (type) {
      case C.TYPE_OTHER:
        return new ProgressiveDownloadHelper(uri);
      default:
          throw new IllegalStateException(context.getString(R.string.media_error));
    }
  }

  private final class StartDownloadDialogHelper
      implements DownloadHelper.Callback, DialogInterface.OnClickListener {
    private final DownloadHelper downloadHelper;
    private final String name;
    private final AlertDialog.Builder builder;
    private final View dialogView;
    private final List<TrackKey> trackKeys;
    private final ArrayAdapter<String> trackTitles;
    private final ListView representationList;

    public StartDownloadDialogHelper(
            Activity activity, DownloadHelper downloadHelper, String name) {
      this.downloadHelper = downloadHelper;
      this.name = name;
      builder =
          new AlertDialog.Builder(activity)
              .setTitle(R.string.exo_download_description)
                  .setMessage(name)
              .setPositiveButton(android.R.string.ok, this)
              .setNegativeButton(android.R.string.cancel, null);

      // Inflate with the builder's context to ensure the correct style is used.
      LayoutInflater dialogInflater = LayoutInflater.from(builder.getContext());
      dialogView = dialogInflater.inflate(R.layout.start_download_dialog, null);

      trackKeys = new ArrayList<>();
      trackTitles =
          new ArrayAdapter<>(
              builder.getContext(), android.R.layout.simple_list_item_multiple_choice);
      representationList = dialogView.findViewById(R.id.representation_list);
      representationList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
      representationList.setAdapter(trackTitles);
    }

    public void prepare() {
      downloadHelper.prepare(this);
    }

    @Override
    public void onPrepared(DownloadHelper helper) {
      for (int i = 0; i < downloadHelper.getPeriodCount(); i++) {
        TrackGroupArray trackGroups = downloadHelper.getTrackGroups(i);
        for (int j = 0; j < trackGroups.length; j++) {
          TrackGroup trackGroup = trackGroups.get(j);
          for (int k = 0; k < trackGroup.length; k++) {
            trackKeys.add(new TrackKey(i, j, k));
            trackTitles.add(trackNameProvider.getTrackName(trackGroup.getFormat(k)));
          }
        }
        if (!trackKeys.isEmpty()) {
          builder.setView(dialogView);
        }
        builder.create().show();
      }
    }

    @Override
    public void onPrepareError(DownloadHelper helper, IOException e) {
      Toast.makeText(
              context.getApplicationContext(), R.string.download_start_error, Toast.LENGTH_LONG)
          .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
      ArrayList<TrackKey> selectedTrackKeys = new ArrayList<>();
      for (int i = 0; i < representationList.getChildCount(); i++) {
        if (representationList.isItemChecked(i)) {
          selectedTrackKeys.add(trackKeys.get(i));
        }
      }
      if (!selectedTrackKeys.isEmpty() || trackKeys.isEmpty()) {
        // We have selected keys, or we're dealing with single stream content.
        DownloadAction downloadAction =
            downloadHelper.getDownloadAction(Util.getUtf8Bytes(name), selectedTrackKeys);
        startDownload(downloadAction);
      }
    }
  }
}
