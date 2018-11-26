package com.zy.library.widget.square;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class SquareTextView extends AppCompatTextView {
    public SquareTextView(Context context) {
        super(context);
    }

    public SquareTextView(Context context, AttributeSet attrs) {
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
