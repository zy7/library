package com.zy.library.widget.square;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;

public class SquareImageView extends AppCompatImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int s = w > h ? w : h;
        int measureSpec = MeasureSpec.makeMeasureSpec(s, MeasureSpec.EXACTLY);
        super.onMeasure(measureSpec, measureSpec);
    }
}
