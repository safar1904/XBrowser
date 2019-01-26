package com.xstudio.xbrowser.widget;

import android.util.AttributeSet;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.graphics.Point;
import android.graphics.Rect;
import android.widget.Scroller;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import static android.view.ViewGroup.LayoutParams;
import static android.view.View.MeasureSpec.*;

public class WindowLayout extends ViewGroup implements OnScrollChangedListener {
    
    private Scroller mScroller;
    private int mTouchSlop;
    
    private View mToolbar;
    private View mContent;
    private int mContentOffset;
    private boolean mScrolling;
    private boolean mAutoCollapsingToolbar;
    
    // Use for temporary calculation
    private Point mPrevTouch = new Point();
    
    public WindowLayout(Context context) {
        super(context);
        initView();
    }
    
    public WindowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public WindowLayout(Context context, AttributeSet attrs, int styleRes) {
        super(context, attrs, styleRes);
        initView();
    }
    
    public WindowLayout(Context context, AttributeSet attrs, int styleRes, int attrRes) {
        super(context, attrs, styleRes, attrRes);
        initView();
    }
    
    @Override
    public void onScrollChanged() {
        mScrolling = true;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        throw new RuntimeException("WindowLayout not support addView method");
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL ||
            action == MotionEvent.ACTION_OUTSIDE) {
            mScrolling = false;
            snapToolbar();
        } else if (action == MotionEvent.ACTION_DOWN) {
            mPrevTouch.x = x;
            mPrevTouch.y = y;
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (mAutoCollapsingToolbar && mScrolling) {
                final int deltaX = mPrevTouch.x - x;
                final int deltaY = mPrevTouch.y - y;
                if (Math.abs(deltaY) > Math.abs(deltaX)) {
                    int scrollY = Math.max(0, Math.min(getScrollY() + deltaY, mContentOffset));
                    if (!mScroller.isFinished()) {
                        mScroller.forceFinished(true);
                    }
                    scrollTo(0, scrollY);
                }
            }
            mPrevTouch.x = x;
            mPrevTouch.y = y;
        }
        
        return false;
    }

    @Override
    public void computeScroll() {
        if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
            final int currX = mScroller.getCurrX();
            final int currY = mScroller.getCurrY();
            scrollTo(currX, currY);
        }
        if (!mScroller.isFinished()) {
            postInvalidateOnAnimation();
        }
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
        throw new RuntimeException("WindowLayout not support addViewInLayout method");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        if (mToolbar != null && mToolbar.getVisibility() != GONE) {
            final LayoutParams params = mToolbar.getLayoutParams();
            final int w = makeMeasureSpec(getMeasuredWidth(), EXACTLY);
            final int h = getChildMeasureSpec(heightMeasureSpec, 0, params.height);
            mToolbar.measure(w, h);
        }
        
        if (mContent != null && mContent.getVisibility() != GONE) {
            final int w = makeMeasureSpec(getMeasuredWidth(), EXACTLY);
            final int h = makeMeasureSpec(mAutoCollapsingToolbar ? getMeasuredHeight() + mContentOffset : getMeasuredHeight(), AT_MOST);
            mContent.measure(w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mToolbar != null && mContent.getVisibility() != GONE) {
            final int toolbarWidth = mToolbar.getMeasuredWidth();
            final int toolbarHeight = mToolbar.getMeasuredHeight();
            mToolbar.layout(left, top, left + toolbarWidth, top + toolbarHeight);
        }
        
        if (mContent != null && mContent.getVisibility() != GONE) {
            final int parentHeight = bottom - top;
            final int contentWidth = mContent.getMeasuredWidth();
            final int contentHeight = Math.max(mContent.getMeasuredHeight(), parentHeight);
            mContent.layout(left, top + mContentOffset, left + contentWidth, top + contentHeight);
        }
    }
    
    public void setToolbar(View view) {
        if ((mToolbar = view) != null) {
            LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            super.addView(view, getChildCount(), p);
        }
    }
    
    public void setContent(View view) {
        if (mContent != null && mContent.getViewTreeObserver().isAlive()) {
            removeView(mContent);
            mContent.getViewTreeObserver().removeOnScrollChangedListener(this);
        }
        if ((mContent = view) != null) {
            LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            super.addView(view, 0, p);
            if (mContent.getViewTreeObserver().isAlive()) {
                mContent.getViewTreeObserver().addOnScrollChangedListener(this);
            }
        }
    }
    
    public void setContentOffset(int offset) {
        mContentOffset = offset;
        if (mToolbar != null && mContent != null) {
            requestLayout();
        }
    }
    
    public void setAutoCollapsingToolbar(boolean value) {
        mAutoCollapsingToolbar = value;
        if (!mAutoCollapsingToolbar) {
            if (getScrollY() > 0) {
                scrollTo(0, 0);
            }
            if (mContent.getMeasuredHeight() > getHeight()) {
                requestLayout();
            }
        }
    }
    
    private void initView() {
        mScroller = new Scroller(getContext());
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        
        setClipToOutline(true);
        setClipToPadding(false);
        setAutoCollapsingToolbar(true);
    }
    
    protected void smoothScrollTo(int x, int y) {
        smoothScrollBy(x - getScrollX(), y - getScrollY());
    }
    
    protected void smoothScrollBy(int dx, int dy) {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy);
        postInvalidateOnAnimation();
    }
    
    protected void snapToolbar() {
        final int scrollY = getScrollY();
        final int centerContentOffset = mContentOffset / 2;
        if (scrollY > centerContentOffset && scrollY < mContentOffset) {
            smoothScrollTo(0, mContentOffset);
        } else if (scrollY > 0 && scrollY < centerContentOffset) {
            smoothScrollTo(0, 0);
        } else if (scrollY == centerContentOffset) {
            smoothScrollTo(0, 0);
        }
    }
    
}
