package chuangyuan.ycj.videolibrary.factory;

import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Util;

import chuangyuan.ycj.videolibrary.listener.LoadListener;

/**
 *
 * @author yangc
 * date 2017/10/6
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 数据缓存工厂类
 */

public class BufferingLoadControl implements LoadControl {

    private  static  final String TAG=BufferingLoadControl.class.getName();
    public static final int DEFAULT_MIN_BUFFER_MS = 15000;
    public static final int DEFAULT_MAX_BUFFER_MS = 30000;
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500;
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;

    private static final int ABOVE_HIGH_WATERMARK = 0;
    private static final int BETWEEN_WATERMARKS = 1;
    private static final int BELOW_LOW_WATERMARK = 2;
    private final DefaultAllocator allocator;
    private final long minBufferUs;
    private final long maxBufferUs;
    private final long bufferForPlaybackUs;
    private final long bufferForPlaybackAfterRebufferUs;
    private final PriorityTaskManager priorityTaskManager;

    private int targetBufferSize;
    private boolean isBuffering;
    public   boolean needBuffering = true;
    private    LoadListener listener;
    public BufferingLoadControl() {
        this(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE));
    }

    public BufferingLoadControl(DefaultAllocator allocator) {
        this(allocator, DEFAULT_MIN_BUFFER_MS, DEFAULT_MAX_BUFFER_MS,
                DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
    }

    public BufferingLoadControl(DefaultAllocator allocator, int minBufferMs, int maxBufferMs, long bufferForPlaybackMs, long bufferForPlaybackAfterRebufferMs) {
        this(allocator, minBufferMs, maxBufferMs, bufferForPlaybackMs,
                bufferForPlaybackAfterRebufferMs, null);
    }

    public BufferingLoadControl(DefaultAllocator allocator, int minBufferMs, int maxBufferMs, long bufferForPlaybackMs, long bufferForPlaybackAfterRebufferMs, PriorityTaskManager priorityTaskManager) {
        this.allocator = allocator;
        minBufferUs = minBufferMs * 2000L;
        maxBufferUs = maxBufferMs * 2000L;
        bufferForPlaybackUs = bufferForPlaybackMs * 2000L;
        bufferForPlaybackAfterRebufferUs = bufferForPlaybackAfterRebufferMs * 2000L;
        this.priorityTaskManager = priorityTaskManager;
    }

    @Override
    public void onPrepared() {
        reset(false);
    }

    @Override
    public void onTracksSelected(Renderer[] renderers, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        targetBufferSize = 0;
        for (int i = 0; i < renderers.length; i++) {
            if (trackSelections.get(i) != null) {
                targetBufferSize += Util.getDefaultBufferSize(renderers[i].getTrackType());
            }
        }
        allocator.setTargetBufferSize(targetBufferSize);
    }

    @Override
    public void onStopped() {
        Log.d(TAG,"onStopped");
        reset(true);
    }

    @Override
    public void onReleased() {
        Log.d(TAG,"onReleased");
        reset(true);
    }

    @Override
    public Allocator getAllocator() {
        Log.d(TAG,"getAllocator");
        return allocator;
    }

    @Override
    public boolean shouldStartPlayback(long bufferedDurationUs, boolean rebuffering) {
        long minBufferDurationUs = rebuffering ? bufferForPlaybackAfterRebufferUs :bufferForPlaybackUs;
        return minBufferDurationUs <= 0 || bufferedDurationUs >= minBufferDurationUs;
    }

    @Override
    public boolean shouldContinueLoading(long bufferedDurationUs) {
        int bufferTimeState = getBufferTimeState(bufferedDurationUs);
        boolean targetBufferSizeReached = allocator.getTotalBytesAllocated() >= targetBufferSize;
            if (listener!=null&&!targetBufferSizeReached){
                long pro=allocator.getTotalBytesAllocated()*100/bufferForPlaybackUs;
                Log.d(TAG,"shouldContinueLoading:"+pro);
                listener.onProgress(pro>100?100:pro);
            }

        boolean wasBuffering = isBuffering;
        isBuffering = bufferTimeState == BELOW_LOW_WATERMARK
                || (bufferTimeState == BETWEEN_WATERMARKS && isBuffering &&!targetBufferSizeReached);
        if (priorityTaskManager != null && isBuffering != wasBuffering) {
            if (isBuffering) {
                priorityTaskManager.add(C.PRIORITY_PLAYBACK);
            } else {
                priorityTaskManager.remove(C.PRIORITY_PLAYBACK);
            }
        }
        return isBuffering && needBuffering;
    }

    private int getBufferTimeState(long bufferedDurationUs) {
        return bufferedDurationUs > maxBufferUs ? ABOVE_HIGH_WATERMARK : (bufferedDurationUs < minBufferUs ? BELOW_LOW_WATERMARK : BETWEEN_WATERMARKS);
    }

    private void reset(boolean resetAllocator) {
        targetBufferSize = 0;
        if (priorityTaskManager != null && isBuffering) {
            priorityTaskManager.remove(C.PRIORITY_PLAYBACK);
        }
        isBuffering = false;
        if (resetAllocator) {
            allocator.reset();
        }
    }
    public void setListener(LoadListener listener) {
        this.listener = listener;
    }
}
