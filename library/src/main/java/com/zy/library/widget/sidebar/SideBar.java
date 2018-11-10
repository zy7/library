package com.zy.library.widget.sidebar;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.zy.library.R;

public class SideBar extends LinearLayout {

    private static final String DEFAULT_ONE_LETTER_MEASURE_WIDTH_CASE = "M";

    private static final @ColorInt int DEFAULT_TEXT_COLOR = Color.GRAY;

    private static final float DEFAULT_TEXT_SIZE = sp2px(14);

    private static final float DEFAULT_TEXT_MARGIN = dp2px(2);

    private Drawable mSectionTouchedBackground;

    private Drawable mSectionTopDrawable;

    private Drawable mSectionBottomDrawable;

    private @ColorInt int mSectionTouchedTextColor;

    private @ColorInt int mSectionTextColor;

    private float mSectionTextSize;

    private float mSectionMargin;

    private CharSequence[] mSectionTextArray;

    private Paint mTextPaint;

    private Paint mTouchedTextPaint;

    private Paint mDrawablePaint;

    private Paint mTouchedDrawablePaint;

    private float mSectionWidth;

    private float mSectionHeight;

    private int mTotalSectionNum;

    private float touchX;

    private float touchY;

    public SideBar(Context context) {
        super(context);
        init(context, null);
    }

    public SideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.SideBar);
        try {
            mSectionTouchedBackground = a.getDrawable(R.styleable.SideBar_sectionTouchedBackground);
            mSectionTopDrawable = a.getDrawable(R.styleable.SideBar_sectionTopDrawable);
            mSectionBottomDrawable = a.getDrawable(R.styleable.SideBar_sectionBottomDrawable);
            mSectionTouchedTextColor = a.getColor(R.styleable.SideBar_sectionTouchedTextColor, DEFAULT_TEXT_COLOR);
            mSectionTextColor = a.getColor(R.styleable.SideBar_sectionTextColor, DEFAULT_TEXT_COLOR);
            mSectionTextSize = a.getDimension(R.styleable.SideBar_sectionTextSize, DEFAULT_TEXT_SIZE);
            mSectionMargin = a.getDimension(R.styleable.SideBar_sectionMargin, DEFAULT_TEXT_MARGIN);
            mSectionTextArray = a.getTextArray(R.styleable.SideBar_sectionTextArray);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        } finally {
            if(a != null) {
                a.recycle();
            }
        }

        initPaint();
        initData();
    }

    private void initPaint() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(mSectionTextColor);
        mTextPaint.setTextSize(mSectionTextSize);

        mTouchedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTouchedTextPaint.setTextAlign(Paint.Align.CENTER);
        mTouchedTextPaint.setColor(mSectionTouchedTextColor);
        mTouchedTextPaint.setTextSize(mSectionTextSize);
    }

    private void initData() {
        if(mSectionTextArray == null)
            mSectionTextArray = getResources().getTextArray(R.array.all_letters);

        float[] property = measureText(mTextPaint, DEFAULT_ONE_LETTER_MEASURE_WIDTH_CASE);
        mSectionWidth = property[0];
        mSectionHeight = property[1];

        mTotalSectionNum = mSectionTextArray.length
                + (mSectionTopDrawable == null ? 0 : 1)
                + (mSectionBottomDrawable == null ? 0 : 1);
    }

    private float[] measureText(Paint paint, String text) {
        float[] property = new float[2];
        property[0] = paint.measureText(text);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        property[1] = fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading;
        return property;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();

        if(wMode != MeasureSpec.EXACTLY) {
            width = paddingLeft + paddingRight + (int)(mSectionWidth + 0.5f);
        }

        if(hMode != MeasureSpec.EXACTLY) {
            float contentHeight = mTotalSectionNum * (mSectionHeight + mSectionMargin) - mSectionMargin;
            height = paddingTop + paddingBottom + (contentHeight > 0 ? (int)(contentHeight + 0.5f) : 0);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int width = getWidth();
        final int height = getHeight();
        final int topPadding = getPaddingTop();
        final int leftPadding = getPaddingLeft();
        final int rightPadding = getPaddingRight();
        final int bottomPadding = getPaddingBottom();


        final int middleWidth = width / 2;
        float lastY = topPadding;
        for(int i=0; i<mTotalSectionNum; i++) {
            float y = topPadding + mSectionHeight * (i + 1) + mSectionMargin * i;
            if(touchY > lastY && touchY < y) {
                Log.i("ivan", "y:" + y +",touchY:" + touchY + ",lastY:" + lastY);
                mTextPaint.setColor(mSectionTouchedTextColor);
            } else {
                mTextPaint.setColor(mSectionTextColor);
            }
            canvas.drawText(mSectionTextArray[i].toString(), middleWidth, y, mTextPaint);
            lastY = y;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchX = -1;
                touchY = -1;
                break;
            default:
                touchX = event.getX();
                touchY = event.getY();
                invalidate();
        }
        return true;
    }

    private static float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    private static float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }

}