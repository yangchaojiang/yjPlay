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


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Taurus on 2016/10/18.
 */
public class   AspectRatio  {
    /**
     * The constant AspectRatio_16_9.
     */
    public static final int  AspectRatio_16_9=0;
    /**
     * The constant AspectRatio_4_3.
     */
    public static final int  AspectRatio_4_3=1;
    /**
     * The constant AspectRatio_MATCH_PARENT.
     */
    public static final int  AspectRatio_MATCH_PARENT=2;
    /**
     * The constant AspectRatio_FILL_PARENT.
     */
    public static final int   AspectRatio_FILL_PARENT=3;
    /**
     * The constant AspectRatio_FIT_PARENT.
     */
    public static final int   AspectRatio_FIT_PARENT=4;
    /**
     * The constant AspectRatio_ORIGIN.
     */
    public static final int  AspectRatio_ORIGIN=5;


    /**
     * The interface Resize mode.
     */
    @IntDef({AspectRatio_16_9, AspectRatio_4_3, AspectRatio_MATCH_PARENT,AspectRatio_FILL_PARENT,AspectRatio_FIT_PARENT,AspectRatio_ORIGIN})
    @Retention(RetentionPolicy.SOURCE)
  public   @interface ResizeMode {
    }
}

