package com.xstudio.xbrowser.webkit;

import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.webkit.*;

public class WebkitView extends WebView {
    
    Toolbar toolbar;
    
    float startTouchX;
    float startTouchY;
    long startTouchTime;
    ViewConfiguration viewConfig;
    
    public WebkitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWebViewClient(new XWebViewClient());
        setWebChromeClient(new XWebChromeClient());
        viewConfig = ViewConfiguration.get(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            startTouchX = event.getRawX();
            startTouchY = event.getRawY();
            startTouchTime = event.getEventTime();
        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            handleTouchMove(startTouchX, startTouchY, event.getRawX(), event.getRawY());
        }
        
        return super.onTouchEvent(event);
    }
    
    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }
    
    private void updateToolbarUrl(String url) {
        if (toolbar != null) {
            toolbar.setUrl(url);
        }
    }
    
    private void updateToolbarTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }
    
    private void updateToolbarIcon(Bitmap icon) {
        if (toolbar != null) {
            toolbar.setIcon(icon);
        }
    }
    
    private void updateToolbarProgress(int progress) {
        if (toolbar != null) {
            toolbar.setProgress(progress);
        }
    }
    
    private void handleTouchMove(final float startX, final float startY, final float endX, final float endY) {
        final float diffX = startX - endX;
        final float diffY = startY - endY;
        final float absDiffX = Math.abs(diffX);
        final float absDiffY = Math.abs(diffY);
        
        if (absDiffX > absDiffY && absDiffX >= viewConfig.getScaledTouchSlop()) {
            if (diffX < 0) {
                
            } else if (diffX > 0) {
                
            }
        } else if (absDiffY > absDiffX && absDiffY >= viewConfig.getScaledTouchSlop()) {
            if (diffY < 0) {
                
            } else if (diffY > 0) {
                
            }
        }
    }
    
    public static interface Toolbar {
        
        void setUrl(String url);
        void setTitle(String title);
        void setIcon(Bitmap icon);
        void setProgress(int progress);
        
    }
    
    class XWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            updateToolbarUrl(url);
        }
        
    }
    
    class XWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            updateToolbarProgress(newProgress);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            updateToolbarIcon(icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            updateToolbarTitle(title);
        }
        
    }
    
    
}
