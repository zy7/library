package com.zy.library.widget.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.zy.library.widget.recyclerview.layoutmanager.TopLinearLayoutManager;


public class TopRecyclerView extends RecyclerView {

    public TopRecyclerView(Context context) {
        super(context);
    }

    public TopRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void smoothScrollToPosition(int position) {
        smoothScrollToPosition(position, 0);
    }

    public void smoothScrollToPosition(int position, int offset) {
        RecyclerView.LayoutManager lm = getLayoutManager();
        if(lm instanceof TopLinearLayoutManager) {
            ((TopLinearLayoutManager)lm).setOffsetToTop(offset);
            super.smoothScrollToPosition(position);
        }
    }
}
