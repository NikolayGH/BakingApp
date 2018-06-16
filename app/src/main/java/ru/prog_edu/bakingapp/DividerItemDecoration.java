package ru.prog_edu.bakingapp;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private final int mPadding;

    public DividerItemDecoration(int padding) {
        this.mPadding = padding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.bottom += mPadding;
        outRect.top += mPadding;
        outRect.left = mPadding;
        outRect.right = mPadding;
    }
}
