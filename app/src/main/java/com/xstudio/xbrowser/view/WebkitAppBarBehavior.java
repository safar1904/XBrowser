package com.xstudio.xbrowser.view;

import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.view.MotionEvent;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.support.v4.view.ViewCompat;

public class WebkitAppBarBehavior extends AppBarLayout.Behavior {

    public WebkitAppBarBehavior() {
        super();
    }
    
    public WebkitAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        if (target instanceof NestedScrollView) {
            NestedScrollView nestedScrolllView = (NestedScrollView) target;
            View content = nestedScrolllView.getChildAt(0);
            return ViewCompat.canScrollVertically(content, 1) || ViewCompat.canScrollVertically(content, -1);
        }
        return false;
    }
    
}
