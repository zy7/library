package com.zy.library.widget.sidebar.section;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ImageSection implements Section {

    public final static int NORMAL = 0;
    public final static int CIRCLE = 1;
    public final static int ROUND  = 2;

    @IntDef({NORMAL, CIRCLE, ROUND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}

    @Type
    public int type;


}
