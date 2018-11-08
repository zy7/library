package com.zy.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.zy.library.R;

/**
 * version:1.0.1
 */
public class CountdownTextView extends AppCompatTextView implements Runnable {
    /** 次数 */
    private final static int DEFAULT_TOTAL_TIMES = 60;
    /** 计时单位:毫秒 */
    private final static int DEFAULT_INTERVAL_TIME = 1000;

    private int mTotalCountDown;

    private int mLeftCountDown;

    private int mCountDownInterval;

    private CharSequence mRecordText;

    private boolean mCancelled;

    private OnCountDownListener mOnCountDownListener;

    public CountdownTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CountdownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CountdownTextView);
        try {
            mTotalCountDown = a.getInteger(R.styleable.CountdownTextView_totalTimes, DEFAULT_TOTAL_TIMES);
            mCountDownInterval = a.getInteger(R.styleable.CountdownTextView_interval, DEFAULT_INTERVAL_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(a != null) {
                a.recycle();
            }
        }
    }

    public void setCountDown(int totalCountDown) {
        this.mTotalCountDown = totalCountDown;
    }

    public void setCountDown(int totalCountDown, int countDownInterval) {
        this.mTotalCountDown = totalCountDown;
        this.mCountDownInterval = countDownInterval;
    }

    public void setOnCountDownListener(OnCountDownListener onCountDownListener) {
        this.mOnCountDownListener = onCountDownListener;
    }

    public synchronized final void cancel() {
        mCancelled = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setClickable(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        mCancelled = true;
        removeCallbacks(this);
        super.onDetachedFromWindow();
    }

    @Override
    public boolean performClick() {
        startCount();
        return super.performClick();
    }

    private void startCount() {
        setEnabled(false);
        mCancelled = false;
        mRecordText = getText();
        mLeftCountDown = mTotalCountDown;
        post(this);
    }

    private void endCount() {
        mCancelled = false;
        setText(mRecordText);
        setEnabled(true);
    }

    @Override
    public void run() {
        if (mLeftCountDown <= 0 || mCancelled) {
            endCount();
            if(mOnCountDownListener != null)
                mOnCountDownListener.onCountFinish(this);
        } else {
            int left = mLeftCountDown--;
            postDelayed(this, mCountDownInterval);
            if(mOnCountDownListener != null) {
                mOnCountDownListener.onCountDown(this, left);
            }
        }
    }

    public interface OnCountDownListener {
        void onCountDown(CountdownTextView view, int leftCountDown);
        void onCountFinish(CountdownTextView view);
    }
}