package com.xstudio.xbrowser.webkit;

import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.view.MotionEvent;
import android.view.View;
import com.xstudio.xbrowser.view.*;

@CoordinatorLayout.DefaultBehavior(DefaultBehavior.class)
public class WebkitAppBarLayout extends AppBarLayout {
    
    public WebkitAppBarLayout(Context context) {
        super(context);
    }
    
    public WebkitAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        
    }

    public static class DefaultBehavior extends AppBarLayout.Behavior {

        public DefaultBehavior() {
            super();
        }

        public DefaultBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
            if (directTargetChild.canScrollVertically(-1) ||
                directTargetChild.canScrollVertically(1)) {
                return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
            }
            return false;
        }

        @Override
        public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
            return false;
        }

        @Override
        boolean canDragView(AppBarLayout view) {
            return false;
        }

        @Override
        public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
            return false;
        }

    }
    
}
