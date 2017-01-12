package com.midea.fridge.fridgedoodle.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by chenxiaofei on 2016/12/31.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration{
    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildPosition(view) != 0)
            outRect.left = space;
    }
}
