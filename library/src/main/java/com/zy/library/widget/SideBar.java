package com.zy.library.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zy.library.R;
import com.zy.library.widget.square.SquareImageView;
import com.zy.library.widget.square.SquareTextView;

/**
 * version:1.0.2
 */
public class SideBar extends LinearLayout {

    private static final @ColorInt int DEFAULT_TEXT_COLOR = Color.GRAY;

    private static final float DEFAULT_TEXT_SIZE = sp2px(12f);

    private static final int DEFAULT_TEXT_MARGIN = 0;

    private static final int DEFAULT_TEXT_PADDING = 0;

    private Drawable mSectionBackground;

    private Drawable mSectionDrawableTop;

    private ColorStateList mSectionTextColor;

    private float mSectionTextSize;

    private int mSectionTextPadding;

    private int mSectionMargin;

    private CharSequence[] mSectionTextArray;

    private OnSelectedListener mOnSelectedListener;

    private boolean mDrawableClickable;

    private View mSelectedView;

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.SideBar);
        try {
            mSectionBackground = a.getDrawable(R.styleable.SideBar_sectionBackground);
            mSectionDrawableTop = a.getDrawable(R.styleable.SideBar_sectionDrawableTop);
            mDrawableClickable = a.getBoolean(R.styleable.SideBar_sectionDrawableClickable, false);
            mSectionTextColor = a.getColorStateList(R.styleable.SideBar_sectionTextColor);
            mSectionTextSize = a.getDimension(R.styleable.SideBar_sectionTextSize, DEFAULT_TEXT_SIZE);
            mSectionTextPadding = a.getDimensionPixelOffset(R.styleable.SideBar_sectionTextPadding, DEFAULT_TEXT_PADDING);
            mSectionMargin = a.getDimensionPixelOffset(R.styleable.SideBar_sectionMargin, DEFAULT_TEXT_MARGIN);
            mSectionTextArray = a.getTextArray(R.styleable.SideBar_sectionTextArray);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        } finally {
            if(a != null) {
                a.recycle();
            }
        }

        initData();
        addSection();
    }

    private void initData() {
        setClickable(true);
        if(getOrientation() == LinearLayout.HORIZONTAL) {
            setGravity(Gravity.CENTER_VERTICAL);
        } else {
            setGravity(Gravity.CENTER_HORIZONTAL);
        }
        if(mSectionTextArray == null) {
            try {
                mSectionTextArray = getResources().getTextArray(R.array.all_letters);
            } catch (Resources.NotFoundException e) {
                mSectionTextArray = null;
                e.printStackTrace();
            }
        }
        if(mSectionTextColor == null) {
            mSectionTextColor = ColorStateList.valueOf(DEFAULT_TEXT_COLOR);
        }
    }

    private void addSection() {
        if(mSectionDrawableTop != null) {
            ImageView iv = createImageSection();
            iv.setImageDrawable(mSectionDrawableTop);
            addView(iv, createSectionLayoutParams(false));
        }

        int count = mSectionTextArray == null ? 0 : mSectionTextArray.length;
        for(int i=0; i<count; i++) {
            TextView tv = createTextSection();
            tv.setText(mSectionTextArray[i]);
            LayoutParams lp = createSectionLayoutParams(mSectionDrawableTop != null || i > 0);
            addView(tv, lp);
        }
    }

    private LayoutParams createSectionLayoutParams(boolean margin) {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if(margin && mSectionMargin > 0) {
            if (getOrientation() == LinearLayout.HORIZONTAL) {
                lp.setMargins(mSectionMargin, 0, 0, 0);
            } else {
                lp.setMargins(0, mSectionMargin, 0, 0);
            }
        }
        return lp;
    }

    private TextView createTextSection() {
        SquareTextView tv = new SquareTextView(getContext());
        if(mSectionBackground != null)
            tv.setBackgroundDrawable(mSectionBackground.getConstantState().newDrawable());
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSectionTextSize);
        tv.setTextColor(mSectionTextColor);
        tv.setPadding(mSectionTextPadding, mSectionTextPadding, mSectionTextPadding, mSectionTextPadding);
        tv.setIncludeFontPadding(false);
        return tv;
    }

    private ImageView createImageSection() {
        SquareImageView iv = new SquareImageView(getContext());
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return iv;
    }

    public OnSelectedListener getOnSelectedListener() {
        return mOnSelectedListener;
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.mOnSelectedListener = onSelectedListener;
    }

    public void setSelectItem(int position) {
        if(position < 0 || position > getChildCount())
            throw new IndexOutOfBoundsException("SideBar's position can't more than " + getChildCount());

        View v = getChildAt(position);
        if(v.equals(mSelectedView))
            return;

        if(mSelectedView != null)
            mSelectedView.setSelected(false);

        v.setSelected(true);
        mSelectedView = v;
        if(mOnSelectedListener != null) {
            mOnSelectedListener.OnSelected(mSelectedView, position);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        if(count > 1) {
            if(getOrientation() == LinearLayout.VERTICAL) {
                int h = getMeasuredHeight();
                int totalH = 0;
                for(int i=0; i<count; i++) {
                    totalH += getChildAt(i).getMeasuredHeight();
                }
                int totalLeft = h - totalH - getPaddingTop() - getPaddingBottom();
                if(totalLeft > 0) {
                    int margin = totalLeft / (count - 1);
                    for (int i = 0; i < count; i++) {
                        LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
                        if (i > 0) {
                            lp.setMargins(0, margin, 0, 0);
                        }
                    }
                } else {
                    for (int i = 0; i < count; i++) {
                        LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
                        lp.setMargins(0, 0, 0, 0);
                    }
                }
            } else {
                int w = getMeasuredWidth();
                int totalW = 0;
                for(int i=0; i<count; i++) {
                    totalW += getChildAt(i).getMeasuredWidth();
                }
                int totalLeft = w - totalW - getPaddingLeft() - getPaddingRight();
                if(totalLeft > 0) {
                    int margin = totalLeft / (count - 1);
                    for (int i = 0; i < count; i++) {
                        LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
                        if (i > 0) {
                            lp.setMargins(margin, 0, 0, 0);
                        }
                    }
                } else {
                    for (int i = 0; i < count; i++) {
                        LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
                        lp.setMargins(0, 0, 0, 0);
                    }
                }
            }
        }
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int x = (int) event.getX();
        int y = (int) event.getY();
        Rect frame = new Rect();

        if(mSelectedView != null) {
            mSelectedView.getHitRect(frame);
            if(contains(frame, x, y)) {
                return true;
            }
        }

        final int count = getChildCount();
        for(int i=0; i<count; i++) {
            View v = getChildAt(i);
            if(v instanceof ImageView && !mDrawableClickable)
                continue;
            v.getHitRect(frame);
            if(contains(frame, x, y)) {
                if(mSelectedView != null) {
                    mSelectedView.setSelected(false);
                }
                v.setSelected(true);
                mSelectedView = v;
                if(mOnSelectedListener != null) {
                    mOnSelectedListener.OnSelected(mSelectedView, i);
                }
                break;
            }
        }
        return true;
    }

    private boolean contains(Rect frame, int x, int y) {
        if(frame == null)
            return false;

        if(getOrientation() == LinearLayout.VERTICAL) {
            return frame.left < frame.right && frame.top < frame.bottom  // check for empty first
                    && y >= frame.top && y < frame.bottom;
        } else {
            return frame.left < frame.right && frame.top < frame.bottom  // check for empty first
                    && x >= frame.left && x < frame.right;
        }
    }

    public interface OnSelectedListener{
        void OnSelected(View v, int position);
    }

    private static int dp2px(int dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics()) + 0.5f);
    }

    private static float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    private static int sp2px(int sp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics()) + 0.5f);
    }

    private static float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }

}