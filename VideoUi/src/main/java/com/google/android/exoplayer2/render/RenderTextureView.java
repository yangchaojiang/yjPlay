/*
 * Copyright 2017 jiajunhui<junhui_jia@163.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.google.android.exoplayer2.render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import com.google.android.exoplayer2.SimpleExoPlayer;
import java.lang.ref.WeakReference;

/**
 * Created by Taurus on 2017/11/19.
 *
 * 使用TextureView时，需要开启硬件加速（系统默认是开启的）。
 * 如果硬件加速是关闭的，会造成{@link SurfaceTextureListener#onSurfaceTextureAvailable(SurfaceTexture, int, int)}不执行。
 *
 */

public class RenderTextureView extends TextureView implements IRender {

    final String TAG = "RenderTextureView";

    private IRenderCallback mRenderCallback;
    private RenderMeasure mRenderMeasure;

    private SurfaceTexture mSurfaceTexture;

    private boolean mTakeOverSurfaceTexture;

    public RenderTextureView(Context context) {
        this(context, null);
    }

    public RenderTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRenderMeasure = new RenderMeasure();
        setSurfaceTextureListener(new InternalSurfaceTextureListener());
    }

    /**
     * If you want to take over the life cycle of SurfaceTexture,
     * please set the tag to true.
     * @param takeOverSurfaceTexture
     */
    public void setTakeOverSurfaceTexture(boolean takeOverSurfaceTexture){
        this.mTakeOverSurfaceTexture = takeOverSurfaceTexture;
    }

    public boolean isTakeOverSurfaceTexture() {
        return mTakeOverSurfaceTexture;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mRenderMeasure.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mRenderMeasure.getMeasureWidth(),mRenderMeasure.getMeasureHeight());
    }

    @Override
    public void setRenderCallback(IRenderCallback renderCallback) {
        this.mRenderCallback = renderCallback;
    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        if(videoSarNum > 0 && videoSarDen > 0){
            mRenderMeasure.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
            requestLayout();
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        mRenderMeasure.setVideoRotation(degree);
        setRotation(degree);
    }

    @Override
    public void updateAspectRatio(AspectRatio aspectRatio) {
        mRenderMeasure.setAspectRatio(aspectRatio);
        requestLayout();
    }

    @Override
    public void updateVideoSize(int videoWidth, int videoHeight) {
        Log.d(TAG,"onUpdateVideoSize : videoWidth = " + videoWidth + " videoHeight = " + videoHeight);
        mRenderMeasure.setVideoSize(videoWidth, videoHeight);
        requestLayout();
    }

    @Override
    public View getRenderView() {
        return this;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG,"onTextureViewAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG,"onTextureViewDetachedFromWindow");
    }

    @Override
    public void release() {
        if(mSurfaceTexture!=null){
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if(mSurface!=null){
            mSurface.release();
            mSurface = null;
        }
        setSurfaceTextureListener(null);
    }

    private  Surface mSurface;

    void setSurface(Surface surface){
        this.mSurface = surface;
    }

    Surface getSurface() {
        return mSurface;
    }

    SurfaceTexture getOwnSurfaceTexture(){
        return mSurfaceTexture;
    }

    private static final class InternalRenderHolder implements IRenderHolder{

        private WeakReference<Surface> mSurfaceRefer;
        private WeakReference<RenderTextureView> mTextureRefer;

        public InternalRenderHolder(RenderTextureView textureView, SurfaceTexture surfaceTexture){
            mTextureRefer = new WeakReference<>(textureView);
            mSurfaceRefer = new WeakReference<>(new Surface(surfaceTexture));
        }

        RenderTextureView getTextureView(){
            if(mTextureRefer!=null){
                return mTextureRefer.get();
            }
            return null;
        }

        @Override
        public void bindPlayer(SimpleExoPlayer player) {
            RenderTextureView textureView = getTextureView();
            if(player!=null && mSurfaceRefer!=null && textureView!=null){
                SurfaceTexture surfaceTexture = textureView.getOwnSurfaceTexture();
                SurfaceTexture useTexture = textureView.getSurfaceTexture();
                boolean isReleased = false;
                //check the SurfaceTexture is released is Android O.
                if(surfaceTexture!=null && Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    isReleased = surfaceTexture.isReleased();
                }
                boolean available = surfaceTexture!=null && !isReleased;
                //When the user sets the takeover flag and SurfaceTexture is available.
                if(textureView.isTakeOverSurfaceTexture()
                        && available
                        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    //if SurfaceTexture not set or current is null, need set it.
                    if(!surfaceTexture.equals(useTexture)){
                        textureView.setSurfaceTexture(surfaceTexture);
                        Log.d("RenderTextureView","****setSurfaceTexture****");
                    }else{
                        Surface surface = textureView.getSurface();
                        //release current Surface if not null.
                        if(surface!=null){
                            surface.release();
                        }
                        //create Surface use update SurfaceTexture
                        Surface newSurface = new Surface(surfaceTexture);
                        //set it for player
                        player.setVideoSurface(newSurface);
                        //record the new Surface
                       // textureView.setSurface(newSurface);
                        Log.d("RenderTextureView","****bindSurface****");
                    }
                }else{
                    Surface surface = mSurfaceRefer.get();
                    if(surface!=null){
                        player.setVideoSurface(surface);
                        //record the Surface
                        textureView.setSurface(surface);
                        Log.d("RenderTextureView","****bindSurface****");
                    }
                }
            }
        }

    }

    private class InternalSurfaceTextureListener implements SurfaceTextureListener{

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.d(TAG,"<---onSurfaceTextureAvailable---> : width = " + width + " height = " + height);
            if(mRenderCallback!=null){
                mRenderCallback.onSurfaceCreated(
                        new InternalRenderHolder(RenderTextureView.this, surface), width, height);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.d(TAG,"onSurfaceTextureSizeChanged : width = " + width + " height = " + height);
            if(mRenderCallback!=null){
                mRenderCallback.onSurfaceChanged(
                        new InternalRenderHolder(RenderTextureView.this,surface), 0, width, height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.d(TAG,"***onSurfaceTextureDestroyed***");
            if(mRenderCallback!=null){
                mRenderCallback.onSurfaceDestroy(
                        new InternalRenderHolder(RenderTextureView.this,surface));
            }
            if(mTakeOverSurfaceTexture)
                mSurfaceTexture = surface;
            return !mTakeOverSurfaceTexture;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }

    }

}
