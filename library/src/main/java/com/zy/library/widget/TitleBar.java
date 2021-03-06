package com.zy.library.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zy.library.R;

import java.util.LinkedList;

/**
 * version:1.0.5
 */
public class TitleBar extends ViewGroup implements View.OnClickListener {
    private static final int DEFAULT_TITLE_COLOR = Color.WHITE;
    private static final int DEFAULT_TITLE_SIZE = sp2px(18);
    private static final int DEFAULT_SUB_TITLE_SIZE = sp2px(12);
    private static final int DEFAULT_BTN_TEXT_SIZE = sp2px(15);
    private static final int DEFAULT_BTN_PADDING = dp2px(10);
    private static final int DEFAULT_TITLE_BAR_HEIGHT = dp2px(48);

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

    private TextView mLeftTextView;

    private LinearLayout mRightLayout;

    private LinearLayout mCenterLayout;

    private TextView mCenterTextView;

    private TextView mSubTitleTextView;

    private View mCustomCenterView;

    private boolean mImmersive;

    private int mScreenWidth;

    private int mHeight;

    private int mStatusBarHeight;

    private String  mTitle;

    private float   mTitleSize;
    @ColorInt
    private int     mTitleColor;

    private float   mSubTitleSize;
    @ColorInt
    private int     mSubTitleColor;

    private float   mBtnTextSize;

    private int     mBtnTextPadding;
    @ColorInt
    private int     mBtnTextColor;

    private Drawable mBtnBackground;

    private Drawable mLeftBtnDrawable;

    private String  mLeftBtnText;

    private int mDividerLineHeight;
    @ColorInt
    private int mDividerLine;

    private Paint   mPaint;

    public TitleBar(Context context) {
        super(context);
        init(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        try {
            mTitle = a.getString(R.styleable.TitleBar_title);
            mTitleSize = a.getDimension(R.styleable.TitleBar_titleSize, DEFAULT_TITLE_SIZE);
            mTitleColor = a.getColor(R.styleable.TitleBar_titleColor, DEFAULT_TITLE_COLOR);
            mSubTitleSize = a.getDimension(R.styleable.TitleBar_subTitleSize, DEFAULT_SUB_TITLE_SIZE);
            mSubTitleColor = a.getColor(R.styleable.TitleBar_subTitleColor, mTitleColor);
            mBtnTextPadding = a.getDimensionPixelOffset(R.styleable.TitleBar_btnTextPadding, DEFAULT_BTN_PADDING);
            mBtnTextColor = a.getColor(R.styleable.TitleBar_btnTextColor, mTitleColor);
            mBtnTextSize = a.getDimension(R.styleable.TitleBar_btnTextSize, DEFAULT_BTN_TEXT_SIZE);
            mBtnBackground = a.getDrawable(R.styleable.TitleBar_btnBackground);
            mLeftBtnDrawable = a.getDrawable(R.styleable.TitleBar_leftBtnDrawable);
            mLeftBtnText = a.getString(R.styleable.TitleBar_leftBtnText);
            mImmersive = a.getBoolean(R.styleable.TitleBar_immersive, false);
            mDividerLineHeight = a.getDimensionPixelOffset(R.styleable.TitleBar_dividerLineHeight, 0);
            mDividerLine = a.getColor(R.styleable.TitleBar_dividerLine, mTitleColor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(a != null) {
                a.recycle();
            }
        }

        mHeight = DEFAULT_TITLE_BAR_HEIGHT;
        if (mImmersive) {
            mStatusBarHeight = getStatusBarHeight();
        }
        if(mDividerLineHeight > 0) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(mDividerLine);
            setWillNotDraw(false);
        }
        initView(context);
    }

    private void initView(Context context) {
        if(getId() == View.NO_ID)
            setId(R.id.id_title_bar);
        mLeftTextView = new TextView(context);
        mCenterLayout = new LinearLayout(context);
        mRightLayout = new LinearLayout(context);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

        mLeftTextView.setTextColor(mBtnTextColor);
        mLeftTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBtnTextSize);
        mLeftTextView.setSingleLine();
        mLeftTextView.setGravity(Gravity.CENTER_VERTICAL);
        setLeftButton(mLeftBtnText, mLeftBtnDrawable);
        if(mBtnBackground != null)
            mLeftTextView.setBackgroundDrawable(mBtnBackground);

        mCenterTextView = new TextView(context);
        mSubTitleTextView = new TextView(context);
        mCenterLayout.addView(mCenterTextView);
        mCenterLayout.addView(mSubTitleTextView);

        mCenterLayout.setGravity(Gravity.CENTER);
        mCenterTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize);
        mCenterTextView.setTextColor(mTitleColor);
        mCenterTextView.setSingleLine();
        mCenterTextView.setGravity(Gravity.CENTER);
        mCenterTextView.setEllipsize(TextUtils.TruncateAt.END);

        mSubTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSubTitleSize);
        mSubTitleTextView.setTextColor(mSubTitleColor);
        mSubTitleTextView.setSingleLine();
        mSubTitleTextView.setGravity(Gravity.CENTER);
        mSubTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        setTitle(mTitle);

        addView(mLeftTextView, layoutParams);
        addView(mCenterLayout);
        addView(mRightLayout, layoutParams);
    }

    public void setImmersive(boolean immersive) {
        mImmersive = immersive;
        if (mImmersive) {
            mStatusBarHeight = getStatusBarHeight();
        } else {
            mStatusBarHeight = 0;
        }
    }

    public void setHeight(int height) {
        mHeight = height;
        setMeasuredDimension(getMeasuredWidth(), mHeight);
    }

    public void setLeftClickListener(OnClickListener l) {
        mLeftTextView.setOnClickListener(l);
    }

    public void setLeftButton(String text) {
        setLeftButton(text, null);
    }

    public void setLeftButton(@StringRes int text) {
        setLeftButton(text, null);
    }

    public void setLeftDrawable(@DrawableRes int d) {
        setLeftButton(null, d);
    }

    public void setLeftDrawable(Drawable d) {
        setLeftButton(null, d);
    }

    public void setLeftButton(@StringRes int text, Drawable d) {
        setLeftButton(getContext().getString(text), d);
    }

    public void setLeftButton(@StringRes int text, @DrawableRes int d) {
        setLeftButton(getContext().getString(text), getContext().getResources().getDrawable(d));
    }

    public void setLeftButton(String text, @DrawableRes int d) {
        setLeftButton(text, getContext().getResources().getDrawable(d));
    }

    public void setLeftButton(String text, Drawable d) {
        mLeftBtnText = text;
        mLeftBtnDrawable = d;
        if(d != null && !TextUtils.isEmpty(text)) {
            mLeftTextView.setCompoundDrawablePadding(dp2px(2));
            mLeftTextView.setText(text);
            mLeftTextView.setPadding(mBtnTextPadding, 0, mBtnTextPadding, 0);
            mLeftTextView.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
            mLeftTextView.setVisibility(View.VISIBLE);
        } else if(d != null) {
            mLeftTextView.setCompoundDrawablePadding(0);
            mLeftTextView.setText(text);
            mLeftTextView.setPadding(0, 0, 0, 0);
            mLeftTextView.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
            mLeftTextView.setVisibility(View.VISIBLE);
        } else if(!TextUtils.isEmpty(text)) {
            mLeftTextView.setCompoundDrawablePadding(0);
            mLeftTextView.setText(text);
            mLeftTextView.setPadding(mBtnTextPadding, 0, mBtnTextPadding, 0);
            mLeftTextView.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
            mLeftTextView.setVisibility(View.VISIBLE);
        } else {
            mLeftTextView.setVisibility(View.GONE);
        }
    }

    public void setLeftTextSize(float size) {
        mLeftTextView.setTextSize(size);
    }

    public void setLeftTextColor(int color) {
        mLeftTextView.setTextColor(color);
    }

    public void setLeftVisible(boolean visible) {
        mLeftTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setTitle(CharSequence title) {
        int index = title == null ? -1 : title.toString().indexOf("\n");
        if (index > 0) {
            setTitle(title.subSequence(0, index), title.subSequence(index + 1, title.length()), LinearLayout.VERTICAL);
        } else {
            index = title == null ? -1 : title.toString().indexOf("\t");
            if (index > 0) {
                setTitle(title.subSequence(0, index), "  " + title.subSequence(index + 1, title.length()), LinearLayout.HORIZONTAL);
            } else {
                mCenterTextView.setText(title);
                mSubTitleTextView.setVisibility(View.GONE);
            }
        }
    }

    private void setTitle(CharSequence title, CharSequence subTitle, int orientation) {
        mCenterLayout.setOrientation(orientation);
        mCenterTextView.setText(title);
        mSubTitleTextView.setText(subTitle);
        mSubTitleTextView.setVisibility(View.VISIBLE);
    }

    public void setCenterClickListener(OnClickListener l) {
        mCenterLayout.setOnClickListener(l);
    }

    public void setTitle(int resid) {
        setTitle(getResources().getString(resid));
    }

    public void setTitleColor(int resid) {
        mCenterTextView.setTextColor(resid);
    }

    public void setTitleSize(float size) {
        mCenterTextView.setTextSize(size);
    }

    public void setTitleSize(int unit, float size) {
        mCenterTextView.setTextSize(unit, size);
    }

    public void setTitleBackground(int resid) {
        mCenterTextView.setBackgroundResource(resid);
    }

    public void setSubTitleColor(int resid) {
        mSubTitleTextView.setTextColor(resid);
    }

    public void setSubTitleSize(float size) {
        mSubTitleTextView.setTextSize(size);
    }

    public void setCustomTitle(View titleView) {
        if (titleView == null) {
            mCenterTextView.setVisibility(View.VISIBLE);
            if (mCustomCenterView != null) {
                mCenterLayout.removeView(mCustomCenterView);
            }

        } else {
            if (mCustomCenterView != null) {
                mCenterLayout.removeView(mCustomCenterView);
            }
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mCustomCenterView = titleView;
            mCenterLayout.addView(titleView, layoutParams);
            mCenterTextView.setVisibility(View.GONE);
        }
    }

    public void setActionTextColor(int colorResId) {
        mBtnTextColor = colorResId;
    }

    /**
     * Function to set a click listener for Title TextView
     *
     * @param listener the onClickListener
     */
    public void setOnTitleClickListener(OnClickListener listener) {
        mCenterTextView.setOnClickListener(listener);
    }

    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction(view);
        }
    }

    /**
     * Adds a list of {@link Action}s.
     * @param actionList the actions to add
     */
    public void addActions(ActionList actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     * Adds a new {@link Action}.
     * @param action the action to add
     */
    public View addAction(Action action) {
        final int index = mRightLayout.getChildCount();
        return addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     * @param action the action to add
     * @param index the position at which to add the action
     */
    public View addAction(Action action, int index) {
        LinearLayout.LayoutParams params;
        if(action instanceof ImageAction) {
            params = new LinearLayout.LayoutParams(mHeight, LayoutParams.MATCH_PARENT);
        } else {
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        }
        View view = inflateAction(action);
        mRightLayout.addView(view, index, params);
        return view;
    }

    /**
     * Removes all action views from this action bar
     */
    public void removeAllActions() {
        mRightLayout.removeAllViews();
    }

    /**
     * Remove a action from the action bar.
     * @param index position of action to remove
     */
    public void removeActionAt(int index) {
        mRightLayout.removeViewAt(index);
    }

    /**
     * Remove a action from the action bar.
     * @param action The action to remove
     */
    public void removeAction(Action action) {
        int childCount = mRightLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mRightLayout.getChildAt(i);
            if (view != null) {
                final Object tag = view.getTag();
                if (tag instanceof Action && tag.equals(action)) {
                    mRightLayout.removeView(view);
                }
            }
        }
    }

    /**
     * Returns the number of actions currently registered with the action bar.
     * @return action count
     */
    public int getActionCount() {
        return mRightLayout.getChildCount();
    }

    /**
     * Inflates a {@link View} with the given {@link Action}.
     * @param action the action to inflate
     * @return a view
     */
    private View inflateAction(Action action) {
        View view;
        if (action instanceof ImageAction) {
            ImageAction ia = (ImageAction) action;
            ImageView img = new ImageView(getContext());
            img.setImageResource(ia.getDrawableRes());
            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            view = img;
        } else if(action instanceof TextAction) {
            TextAction ta = (TextAction) action;
            TextView text = new TextView(getContext());
            text.setGravity(Gravity.CENTER);
            text.setText(ta.getText(getContext()));
            if(ta.getTextSize() > 0) {
                text.setTextSize(ta.getTextSize());
            } else {
                text.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBtnTextSize);
            }
            if(ta.getTextColor() != 0) {
                text.setTextColor(ta.getTextColor());
            } else {
                if (mBtnTextColor != 0) {
                    text.setTextColor(mBtnTextColor);
                }
            }
            text.setMinWidth(mHeight);
            text.setPadding(mBtnTextPadding, 0, mBtnTextPadding, 0);
            view = text;
        } else {
            view = new View(getContext());
        }

        if(mBtnBackground != null)
            view.setBackgroundDrawable(mBtnBackground.getConstantState().newDrawable());

        view.setTag(action);
        view.setOnClickListener(this);
        return view;
    }

    public View getViewByAction(Action action) {
        View view = findViewWithTag(action);
        return view;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height;
        if (heightMode != MeasureSpec.EXACTLY) {
            height = mHeight + mDividerLineHeight + mStatusBarHeight;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        } else {
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
            height = mHeight + mDividerLineHeight + mStatusBarHeight;
        }
        mScreenWidth = MeasureSpec.getSize(widthMeasureSpec);
        measureChild(mLeftTextView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mRightLayout, widthMeasureSpec, heightMeasureSpec);
        if (mLeftTextView.getMeasuredWidth() > mRightLayout.getMeasuredWidth()) {
            mCenterLayout.measure(
                    MeasureSpec.makeMeasureSpec(mScreenWidth - 2 * mLeftTextView.getMeasuredWidth(), MeasureSpec.EXACTLY)
                    , heightMeasureSpec);
        } else {
            mCenterLayout.measure(
                    MeasureSpec.makeMeasureSpec(mScreenWidth - 2 * mRightLayout.getMeasuredWidth(), MeasureSpec.EXACTLY)
                    , heightMeasureSpec);
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mLeftTextView.layout(0, mStatusBarHeight, mLeftTextView.getMeasuredWidth(), mLeftTextView.getMeasuredHeight() + mStatusBarHeight);
        mRightLayout.layout(mScreenWidth - mRightLayout.getMeasuredWidth(), mStatusBarHeight,
                mScreenWidth, mRightLayout.getMeasuredHeight() + mStatusBarHeight);
        if (mLeftTextView.getMeasuredWidth() > mRightLayout.getMeasuredWidth()) {
            mCenterLayout.layout(mLeftTextView.getMeasuredWidth(), mStatusBarHeight,
                    mScreenWidth - mLeftTextView.getMeasuredWidth(), getMeasuredHeight());
        } else {
            mCenterLayout.layout(mRightLayout.getMeasuredWidth(), mStatusBarHeight,
                    mScreenWidth - mRightLayout.getMeasuredWidth(), getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mPaint != null && mDividerLineHeight >0) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            canvas.drawRect(0, height - mDividerLineHeight, width, width, mPaint);
        }
    }

    public static int dp2px(int dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(int dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 计算状态栏高度高度
     * getStatusBarHeight
     * @return
     */
    public static int getStatusBarHeight() {
        return getInternalDimensionSize(Resources.getSystem(), STATUS_BAR_HEIGHT_RES_NAME);
    }

    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * A {@link LinkedList} that holds a list of {@link Action}s.
     */
    @SuppressWarnings("serial")
    public static class ActionList extends LinkedList<Action> {
    }

    /**
     * Definition of an action that could be performed, along with a icon to
     * show.
     */
    interface Action {
        void performAction(View view);
    }

    public static abstract class ImageAction implements Action {
        final private int mDrawableRes;

        public ImageAction(@DrawableRes int drawableRes) {
            mDrawableRes = drawableRes;
        }

        public int getDrawableRes() {
            return mDrawableRes;
        }
    }

    public static abstract class TextAction implements Action {
        final private String mText;
        final private @StringRes int mTextRes;
        final private float mTextSize; // 单位sp
        final private @ColorInt int mTextColor;

        public TextAction(String text) {
            this(text, 0, 0, 0);
        }

        public TextAction(@StringRes int text) {
            this(null, text, 0, 0);
        }

        public TextAction(String text, @StringRes int textRes, float textSizeSP, @ColorInt int textColor) {
            mText = text;
            mTextRes = textRes;
            mTextSize = textSizeSP;
            mTextColor = textColor;
        }

        public String getText() {
            return getText(null);
        }

        public String getText(Context ctx) {
            if(ctx == null || mTextRes == 0) {
                return mText;
            } else {
                return ctx.getResources().getString(mTextRes);
            }
        }

        public float getTextSize() {
            return mTextSize;
        }

        @ColorInt
        public int getTextColor() {
            return mTextColor;
        }
    }
}
