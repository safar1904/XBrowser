package com.xstudio.xbrowser.webkit;

import android.util.AttributeSet;
import android.content.res.Configuration;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.graphics.PixelFormat;
import com.xstudio.xbrowser.R;
import android.widget.RelativeLayout;
import com.xstudio.xbrowser.ThisApplication;
import android.view.View;
import com.xstudio.xbrowser.widget.ViewSelector;
import android.view.WindowManager;

import static android.widget.RelativeLayout.LayoutParams.*;

public class TabSelector extends RelativeLayout {
    
    private final Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowManagerParams;
    private boolean mIsShowing;
    private OnTabSelectedListener mOnTabSelectedListener;
    private ViewSelector mViewSelector;
    
    private final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    
    public TabSelector(Context context) {
        super(context);
        mContext = context;
        init();
    }
    
    public TabSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
        adjustLayoutSize(params);
        mWindowManager.updateViewLayout(this, params);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    hide();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }
    
    public void addTab(Tab tab) {
        mViewSelector.addItem(tab);
    }
    
    public void closeTab(int index) {
        mViewSelector.removeItem(index);
    }
    
    public void closeTab(Tab tab) {
        final int index = mViewSelector.indexOf(tab);
        mViewSelector.removeItem(index);
    }
    
    public void show() {
        if (!mIsShowing) {
            mIsShowing = addToWindow();
            for (ViewSelector.ItemProvider item : mViewSelector.getAllItems()) {
                Tab tab = (Tab) item;
                tab.updateInfo();
            }
        }
    }
    
    public void hide() {
        if (mIsShowing) {
            removeFromWindow();
            mIsShowing = false;
        }
    }
    
    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        mOnTabSelectedListener = listener;
    }
    
    private void init() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        setBackgroundResource(R.color.dim_foreground_material_light);
        
        
        mViewSelector = new MyViewSelector(mContext);
    }
    
    private boolean addToWindow() {
        try {
            final WindowManager.LayoutParams params = getWindowManagerLayoutParams();
            final View decorView = ThisApplication.getInstance().getMainActivity().getWindow().getDecorView();
            params.token = decorView.getWindowToken();
            adjustLayoutSize(params);
            mWindowManager.addView(this, params);
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void removeFromWindow() {
        try {
            mWindowManager.removeViewImmediate(this);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    
    private void adjustLayoutSize(WindowManager.LayoutParams params) {
        mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
        params.width = mDisplayMetrics.widthPixels;
        params.height = mDisplayMetrics.heightPixels;
    }
    
    private WindowManager.LayoutParams getWindowManagerLayoutParams() {
        if (mWindowManagerParams == null) {
            mWindowManagerParams = new WindowManager.LayoutParams();
            mWindowManagerParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
            mWindowManagerParams.gravity = Gravity.TOP | Gravity.LEFT;
            mWindowManagerParams.format = PixelFormat.TRANSLUCENT;
            mWindowManagerParams.windowAnimations = android.R.style.Animation_Dialog;
            mWindowManagerParams.flags &= ~(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
            mWindowManagerParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            mWindowManagerParams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            mWindowManagerParams.setTitle("TabSelector:" + Integer.toHexString(hashCode()));
            mWindowManagerParams.packageName = getContext().getPackageName();
        }
        return mWindowManagerParams;
    }
    
    
    public static interface OnTabSelectedListener {
        void onTabSelected(Tab tab);
    }
    
    
    class MyViewSelector extends ViewSelector {
        
        public MyViewSelector(Context context) {
            super(context);
        }

        @Override
        public void onClick(View view) {
            final Object tag = view.getTag();
            if (tag != null && tag instanceof Tab) {
                final Tab tab = (Tab) tag;
                tab.select();
                if (mOnTabSelectedListener != null) {
                    mOnTabSelectedListener.onTabSelected(tab);
                }
                TabSelector.this.hide();
            }
        }
        
    }
    
}
