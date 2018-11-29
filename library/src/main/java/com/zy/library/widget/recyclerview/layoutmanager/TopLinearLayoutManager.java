package com.zy.library.widget.recyclerview.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;

public class TopLinearLayoutManager extends LinearLayoutManager {
    private int offsetToTop;

    public TopLinearLayoutManager(Context context) {
        super(context);
    }

    public TopLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void setOffsetToTop(int offsetToTop) {
        this.offsetToTop = offsetToTop;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        TopLinearSmoothScroller linearSmoothScroller =
                new TopLinearSmoothScroller(recyclerView.getContext());
        linearSmoothScroller.setTargetPosition(position);
        linearSmoothScroller.setOffsetToTop(offsetToTop);
        startSmoothScroll(linearSmoothScroller);
    }

    public static class TopLinearSmoothScroller extends LinearSmoothScroller {

        private int offsetToTop;

        public void setOffsetToTop(int offsetToTop) {
            this.offsetToTop = offsetToTop;
        }

        public TopLinearSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return boxStart - viewStart + offsetToTop;
        }
    }
}
