package com.list.videoplayer;

import android.support.v4.widget.ViewDragHelper;
import android.view.ViewGroup;

/**
 * @author Jarvis
 * @version 1.0
 * @title VideoPlayer
 * @description 该类主要功能描述
 * @create 2017/6/2 下午3:11
 * @changeRecord [修改记录] <br/>
 */

public class ViewDragHelpImpl {

    public ViewDragHelper viewDragHelper;

    public ViewDragHelpImpl(Builder builder) {
        viewDragHelper = ViewDragHelper.create(
                builder.parentView,
                builder.sensitivity,
                builder.callback);
    }

    public static class Builder {
        private ViewGroup parentView;
        private float sensitivity;
        private ViewDragHelper.Callback callback;

        public Builder create(ViewGroup parentView, float sensitivity, ViewDragHelper.Callback callback) {
            this.parentView = parentView;
            this.sensitivity = sensitivity;
            this.callback = callback;
            return this;
        }

        public ViewDragHelpImpl builder() {
            return new ViewDragHelpImpl(this);
        }
    }
}
