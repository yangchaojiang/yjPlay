package chuangyuan.ycj.videolibrary.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.ui.AnimUtils;

import chuangyuan.ycj.videolibrary.R;

/**
 * author  yangc
 * date 2018/3/23
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  锁屏view 控制
 */

@SuppressLint("ViewConstructor")
class LockControlView extends FrameLayout implements View.OnClickListener, AnimUtils.AnimatorListener {
    /***进度条控件*/
    private ExoDefaultTimeBar exoPlayerLockProgress;
    /***锁屏按钮*/
    private AppCompatCheckBox lockCheckBox;

    /***视频加载页,错误页,进度控件,锁屏按布局,自定义预览布局,提示布局,播放按钮*/
    private View exoPlayLockLayout;
    private final BaseView mBaseView;
    private boolean isOpenLock = true;
    private View exoControllerRight, exoControllerLeft;


    public LockControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, @NonNull final BaseView baseView) {
        super(context, attrs, defStyleAttr);
        this.mBaseView = baseView;
        exoPlayLockLayout = inflate(context, R.layout.simple_exo_play_lock, null);
        exoPlayLockLayout.setBackgroundColor(Color.TRANSPARENT);
        exoPlayerLockProgress = exoPlayLockLayout.findViewById(R.id.exo_player_lock_progress);
        lockCheckBox = exoPlayLockLayout.findViewById(R.id.exo_player_lock_btn_id);
        exoControllerRight = baseView.getPlaybackControlView().findViewById(R.id.exo_controller_right);
        exoControllerLeft = baseView.getPlaybackControlView().findViewById(R.id.exo_controller_left);
        lockCheckBox.setVisibility(isOpenLock ? VISIBLE : GONE);
        lockCheckBox.setOnClickListener(this);
        mBaseView.getPlaybackControlView().setAnimatorListener(this);
        mBaseView.getPlaybackControlView().setUpdateProgressListener(new AnimUtils.UpdateProgressListener() {
            @Override
            public void updateProgress(long position, long bufferedPosition, long duration) {
                if (exoPlayerLockProgress != null && mBaseView.isLand && lockCheckBox.isChecked()) {
                    exoPlayerLockProgress.setPosition(position);
                    exoPlayerLockProgress.setBufferedPosition(bufferedPosition);
                    exoPlayerLockProgress.setDuration(duration);
                }
            }
        });
        addView(exoPlayLockLayout, getChildCount());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallback();
    }

    /***
     * 销毁处理
     * **/
    public void onDestroy() {
        if (lockCheckBox != null) {
            lockCheckBox.setOnCheckedChangeListener(null);
        }
        if (lockCheckBox != null && lockCheckBox.animate() != null) {
            lockCheckBox.animate().cancel();
        }
        removeCallback();
    }

    /***
     * 显示隐藏加载页
     *
     * @param visibility 状态
     */
    public void showLockState(int visibility) {
        if (exoPlayLockLayout != null) {
            if (mBaseView.isLand) {
                if (lockCheckBox.isChecked()) {
                    if (visibility == View.VISIBLE) {
                        mBaseView.playerView.getControllerView().hideNo();
                        mBaseView.showBackView(GONE, true);
                    }
                } else
                    exoPlayLockLayout.setVisibility(visibility);
            } else {
                exoPlayLockLayout.setVisibility(GONE);
            }
        }
    }

    /***
     *设置是否锁屏
     * @param  isLock isLock
     * **/
    public void setLockCheck(boolean isLock) {
        lockCheckBox.setChecked(isLock);
    }

    /***
     * 锁屏按钮显示隐藏
     * **/
    private final Runnable hideAction = new Runnable() {
        @Override
        public void run() {
            if (mBaseView.isLand) {
                if (exoPlayLockLayout.getVisibility() == VISIBLE) {
                    AnimUtils.setOutAnimX(lockCheckBox, false).start();
                } else {
                    AnimUtils.setInAnimX(lockCheckBox).start();
                }
            }
        }
    };

    public boolean isLock() {
        return null != lockCheckBox && lockCheckBox.isChecked();
    }

    /**
     * 设置开启开启锁屏功能
     *
     * @param openLock 默认 true 开启   false 不开启
     */
    public void setOpenLock(boolean openLock) {
        isOpenLock = openLock;
        lockCheckBox.setVisibility(isOpenLock ? VISIBLE : GONE);
    }


    /***
     * 更新锁屏按钮状态
     * ***/
    public void updateLockCheckBox(boolean isIn) {
        if (!mBaseView.isLand) return;
        if (isIn) {
            AnimUtils.setInAnimX(lockCheckBox).start();
        } else {
            if (lockCheckBox.getTag() == null) {
                AnimUtils.setOutAnimX(lockCheckBox, false).start();
            } else {
                lockCheckBox.setTag(null);
            }
        }
    }

    public void removeCallback() {
        removeCallbacks(hideAction);
    }

    @Override
    public void onClick(View v) {
        removeCallbacks(hideAction);
        lockCheckBox.setTag(true);
        if (lockCheckBox.isChecked()) {
            mBaseView.getPlaybackControlView().setOutAnim();
            boolean shouldShowIndefinitely = mBaseView.playerView.shouldShowControllerIndefinitely();
            if (!shouldShowIndefinitely) {
                postDelayed(hideAction, mBaseView.playerView.getControllerShowTimeoutMs());
            }
        } else {
            lockCheckBox.setTag(null);
            mBaseView.playerView.showController();
            mBaseView.getPlaybackControlView().setInAnim();

        }
    }

    @Override
    public void show(boolean isIn) {
        if (!mBaseView.isLand) return;
        if (isIn) {
            showLockState(VISIBLE);
            updateLockCheckBox(true);
            if (exoControllerRight != null) {
                AnimUtils.setInAnimX(exoControllerRight).start();
            }
            if (exoControllerLeft != null) {
                AnimUtils.setInAnimX(exoControllerLeft).start();
            }
        } else {
            updateLockCheckBox(false);
            if (exoControllerLeft != null) {
                AnimUtils.setOutAnimX(exoControllerLeft, true).start();
            }
            if (exoControllerRight != null) {
                AnimUtils.setOutAnim(exoControllerRight, false);
            }
        }
    }
}
