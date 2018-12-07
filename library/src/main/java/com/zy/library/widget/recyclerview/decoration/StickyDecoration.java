package com.zy.library.widget.recyclerview.decoration;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class StickyDecoration extends RecyclerView.ItemDecoration {

    private Stickyable mStickyAdapter;

    private RecyclerView.ViewHolder mStickyHolder;

    private OnStickyChangeListener mListener;

    private int mCurStickyPosition = -1;

    private int mStickyMarginTop;

    public StickyDecoration() {}

    public StickyDecoration(OnStickyChangeListener mListener) {
        this.mListener = mListener;
    }

    public void setOnStickyChangeListener(OnStickyChangeListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if(mStickyAdapter == null) {
            RecyclerView.Adapter adapter = parent.getAdapter();
            if(!(adapter instanceof Stickyable))
                return;

            mStickyAdapter = (Stickyable) adapter;
            mStickyHolder = mStickyAdapter.onCreateStickyHolder(parent);
        }

        final int firstPos = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
        final int lastPos  = ((LinearLayoutManager) parent.getLayoutManager()).findLastVisibleItemPosition();
        final int childCount = parent.getChildCount();
        final int height = mStickyHolder.itemView.getMeasuredHeight();
        boolean findSticky = false;
        boolean isBindData = false;

        if(firstPos < 0)
            return;

        for(int i=0; i<childCount; i++) {
            final View child    = parent.getChildAt(i);
            final int  childPos = parent.getChildAdapterPosition(child);

            if(childPos < firstPos || childPos > lastPos)
                continue;

            final int top = child.getTop();
            if(top > height && height > 0)
                break;

            if(mStickyAdapter.isStickyItem(childPos)) {
                findSticky = true;

                if(top <= 0) {
                    mStickyMarginTop = 0;
                    isBindData = bindData(parent, childPos);
                } else if(top > 0 && top <= height) {
                    mStickyMarginTop = top - height;
                    isBindData = bindData(parent, mStickyAdapter.getLastStickyPosition(firstPos));
                }
            }
        }

        if(!findSticky) {
            mStickyMarginTop = 0;
            isBindData = bindData(parent, mStickyAdapter.getLastStickyPosition(firstPos));
        }

        drawView(isBindData, c, mStickyHolder.itemView, 0, mStickyMarginTop);
    }

    private boolean bindData(RecyclerView parent, final int position) {
        if(position < 0)
            return false;

        if(mStickyAdapter.isDataChange() || mCurStickyPosition != position) {
            mCurStickyPosition = position;
            mStickyAdapter.onBindStickyHolder(mStickyHolder, mCurStickyPosition);
            measureLayoutView(parent.getMeasuredWidth(), mStickyHolder.itemView);
            if (mListener != null)
                mListener.onStickyChange(mCurStickyPosition);
        }

        return true;
    }

    private void measureLayoutView(int parentWidth, View view) {
        if (view == null || !view.isLayoutRequested())
            return;

        int widthSpec = View.MeasureSpec.makeMeasureSpec(parentWidth, View.MeasureSpec.EXACTLY);
        int heightSpec;

        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp != null && lp.height > 0) {
            heightSpec = View.MeasureSpec.makeMeasureSpec(lp.height, View.MeasureSpec.EXACTLY);
        } else {
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }

        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    private void drawView(boolean isBindData, Canvas c, View v, float x, float y) {
        if (!isBindData || c == null || v == null)
            return;

        int saveCount = c.save();
        c.translate(x, y);
        v.draw(c);
        c.restoreToCount(saveCount);
    }

    public interface Stickyable<VH extends RecyclerView.ViewHolder> {
        /** 根据数据更新后重置sticky */
        boolean isDataChange();

        /** 获取上一个sticky position */
        int getLastStickyPosition(int position);

        boolean isStickyItem(int position);

        VH onCreateStickyHolder(@NonNull ViewGroup parent);

        void onBindStickyHolder(@NonNull VH holder, int position);
    }

    public interface OnStickyChangeListener {
        void onStickyChange(int position);
    }
}
