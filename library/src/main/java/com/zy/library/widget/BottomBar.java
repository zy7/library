package com.zy.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zy.library.R;

/**
 * version:1.0.2
 */
public class BottomBar extends LinearLayout implements View.OnClickListener {

    private int mSelectedIndex = -1;

    private int mDefaultIndex;

    private Fragment[] mFragments;

    private FragmentManager mFragmentManager;

    private @IdRes int mFragmentContainer = View.NO_ID;

    private ViewPager mViewPager;

    private OnSelectedListener mSelectListener;

    private int mDividerLineHeight;
    @ColorInt
    private int mDividerLine;

    private Paint   mPaint;

    public BottomBar(Context context) {
        super(context);
        init(context, null);
    }

    public BottomBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomBar);
        try {
            mDividerLineHeight = a.getDimensionPixelOffset(R.styleable.BottomBar_dividerLineHeight, 0);
            mDividerLine = a.getColor(R.styleable.BottomBar_dividerLine, Color.LTGRAY);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(a != null) {
                a.recycle();
            }
        }

        if(mDividerLineHeight > 0) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(mDividerLine);
            setWillNotDraw(false);
        }
    }

    public OnSelectedListener getSelectListener() {
        return mSelectListener;
    }

    public void setSelectListener(OnSelectedListener mSelectListener) {
        this.mSelectListener = mSelectListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int num = getChildCount();

        for(int i=0; i<num; i++) {
            View v = getChildAt(i);
            v.setTag(i);
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        selectItem((int)view.getTag());
    }

    public void setDefaultIndex(int index) {
        mDefaultIndex = index;
    }

    public void selectItem(int index) {
        if(index == mSelectedIndex)
            return;

        if(mSelectedIndex != -1)
            getChildAt(mSelectedIndex).setSelected(false);

        getChildAt(index).setSelected(true);
        mSelectedIndex = index;

        switchFragment(index);

        if(mSelectListener != null)
            mSelectListener.onSelected(index);
    }

    public void bindFragmentContainer(FragmentManager fm, @IdRes int container, Fragment... fragments) {
        mFragmentManager = fm;
        mFragmentContainer = container;
        mViewPager = null;
        mFragments = fragments;
        selectItem(mDefaultIndex);
    }

    public void bindFragmentContainer(FragmentManager fm, @IdRes int container, int defaultIndex, Fragment... fragments) {
        mFragmentManager = fm;
        mFragmentContainer = container;
        mViewPager = null;
        mFragments = fragments;
        selectItem(defaultIndex);
    }

    public void bindFragmentContainer(FragmentManager fm, ViewPager container, Fragment... fragments) {
        mFragmentManager = fm;
        mFragmentContainer = View.NO_ID;
        mViewPager = container;
        mFragments = fragments;
        initViewPager();
        selectItem(mDefaultIndex);
    }

    public void bindFragmentContainer(FragmentManager fm, ViewPager container, int defaultIndex, Fragment... fragments) {
        mFragmentManager = fm;
        mFragmentContainer = View.NO_ID;
        mViewPager = container;
        mFragments = fragments;
        initViewPager();
        selectItem(defaultIndex);
    }

    private void switchFragment(final int index) {
        if(mFragments == null || index < 0 || index >= mFragments.length)
            return;

        if(mFragmentContainer != View.NO_ID) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            for(int i=0; i<mFragments.length; i++) {
                if(i != index
                        && mFragments[i] != null
                        && mFragments[i].isAdded()
                        && !mFragments[i].isHidden())
                    ft.hide(mFragments[i]);
            }

            if(!mFragments[index].isAdded())
                ft.add(mFragmentContainer, mFragments[index]);

            ft.show(mFragments[index]);
            ft.commit();
        } else if(mViewPager != null) {
            mViewPager.setCurrentItem(index, true);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        child.setTag(index);
        child.setOnClickListener(this);
        super.addView(child, index, params);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        return params;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mPaint != null && mDividerLineHeight > 0) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            canvas.drawRect(0, 0, width, mDividerLineHeight, mPaint);
        }
    }

    public interface OnSelectedListener {
        void onSelected(int index);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(int w, int h, float initWeight) {
            super(w, h, initWeight);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        @Override
        protected void setBaseAttributes(TypedArray a, int widthAttr,
                                         int heightAttr) {
            if (a.hasValue(widthAttr)) {
                width = a.getLayoutDimension(widthAttr, "layout_width");
            } else {
                width = WRAP_CONTENT;
            }

            if (a.hasValue(heightAttr)) {
                height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                height = WRAP_CONTENT;
            }
        }
    }

    private void initViewPager() {
        mViewPager.setAdapter(new TabAdapter(mFragmentManager, mFragments));
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                selectItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    static class TabAdapter extends FragmentPagerAdapter {
        private Fragment[] fragments;

        public TabAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return fragments[i];
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.length;
        }

    }

}
