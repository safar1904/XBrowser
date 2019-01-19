package com.xstudio.xbrowser.webkit;

import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;
import android.view.MotionEvent;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.graphics.Rect;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import android.widget.ScrollView;
import android.view.View;
import android.view.ViewGroup;

import static android.view.ViewGroup.LayoutParams.*;

public class WebkitLayout extends ScrollView implements ObservableScrollViewCallbacks {

    private View mToolbar;
    private View mContentView;
    private ViewGroup mInnerLayout;
    private int mCollapsingOffset;
    
    // Temporary hit rect calculation
    private final Rect mHitRect = new Rect();

    public WebkitLayout(Context context) {
        super(context);
        init();
    }

    public WebkitLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebkitLayout(Context context, AttributeSet attrs, int styleRes) {
        super(context, attrs, styleRes);
        init();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mToolbar != null && mContentView != null &&
                getScrollOffset() > 0) {
            super.onTouchEvent(event);
        }
        delegateTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mContentView != null) {
            mInnerLayout.setBottom(b + mCollapsingOffset);
            mContentView.setBottom(b + mCollapsingOffset);
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // TODO: Implement this method
    }

    @Override
    public void onDownMotionEvent() {
        // TODO: Implement this method
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (getScrollOffset() > 0) {
            if (getScrollY() < mCollapsingOffset / 2) {
                smoothScrollTo(0, 0);
            } else {
                smoothScrollTo(0, mCollapsingOffset);
            }
        }
    }

    public void setToolbar(View view) {
        if (mToolbar != null) {
            mInnerLayout.removeView(mToolbar);
        }
        if ((mToolbar = view) != null) {
            LayoutParams p = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            mInnerLayout.addView(mToolbar, p);
        }
    }

    public void setContentView(View view) {
        if (mContentView != null) {
            mContentView.getViewTreeObserver().removeGlobalOnLayoutListener(mContentViewOnGlobalLayoutListener);
            if (mContentView instanceof Scrollable) {
               ((Scrollable) mContentView).removeScrollViewCallbacks(this);
            }
            mInnerLayout.removeView(mContentView);
        }
        if ((mContentView = view) != null) {
            mContentView.getViewTreeObserver().addOnGlobalLayoutListener(mContentViewOnGlobalLayoutListener);
            if (mContentView instanceof Scrollable) {
                ((Scrollable) mContentView).addScrollViewCallbacks(this);
            }
            LayoutParams p = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            p.topMargin = mCollapsingOffset;
            mInnerLayout.addView(mContentView, 0, p);
        }
    }

    public void setCollapsingOffset(int offset) {
        mCollapsingOffset = Math.abs(offset);
        if (mContentView != null) {
            FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) mContentView.getLayoutParams();
            p.topMargin = mCollapsingOffset;
            mInnerLayout.updateViewLayout(mContentView, p);
        }
    }
    
    private int getScrollOffset() {
        if (mContentView != null) {
            return Math.max(0, mContentView.getMeasuredHeight() - getHeight());
        }
        return 0;
    }
    

    private void init() {
        setVerticalScrollBarEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setClipToPadding(false);

        mInnerLayout = new FrameLayout(getContext());
        addView(mInnerLayout, MATCH_PARENT, MATCH_PARENT);
    }

    private boolean delegateTouchEvent(MotionEvent event) {
        boolean consumed = false;
        final int childCount = mInnerLayout.getChildCount();
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        for (int i = 0; i < childCount; i++) {
            final View child = mInnerLayout.getChildAt(i);
            child.getHitRect(mHitRect);
            if (mHitRect.contains(x, y)) {
                consumed |= child.dispatchTouchEvent(event);
            }
        }
        return consumed;
    }
    
    private final OnGlobalLayoutListener mContentViewOnGlobalLayoutListener = new OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            if (mContentView != null) {
                if (getScrollOffset() == 0 && getScrollY() > 0) {
                    smoothScrollTo(0, 0);
                }
            }
        }
        
    };

}
