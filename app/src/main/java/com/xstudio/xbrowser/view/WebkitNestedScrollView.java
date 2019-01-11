package com.xstudio.xbrowser.view;

import android.util.AttributeSet;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.view.MotionEvent;
import android.view.View;

public class WebkitNestedScrollView extends NestedScrollView {
    
    public WebkitNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean stat = super.onInterceptTouchEvent(ev);
        View child = getChildAt(0);
        if (child != null && child instanceof WebkitView) {
            WebkitView webkit = (WebkitView) child;
            webkit.touchMe(ev);
        }
        return stat;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean stat = super.onTouchEvent(ev);
        View child = getChildAt(0);
        if (child != null && child instanceof WebkitView) {
            WebkitView webkit = (WebkitView) child;
            webkit.touchMe(ev);
        }
        return stat;
    }
    
}
