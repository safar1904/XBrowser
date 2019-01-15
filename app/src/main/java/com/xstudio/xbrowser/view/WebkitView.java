package com.xstudio.xbrowser.view;

import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.webkit.*;
import android.view.*;
import com.xstudio.xbrowser.util.*;
import android.support.design.widget.*;
import com.xstudio.xbrowser.*;

public class WebkitView extends WebView {
    
    Toolbar toolbar;
    
    float startTouchX;
    float startTouchY;
    long startTouchTime;
    ViewConfiguration viewConfig;
    
    CoordinatorLayout root;
    
    public WebkitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWebViewClient(new XWebViewClient());
        setWebChromeClient(new XWebChromeClient());
        viewConfig = ViewConfiguration.get(context);
        
        
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        int dt = t - oldt;
        if (root != null) {
            root.onNestedScroll(this, 0, dt, 0, 0);
        }
    }

    public void setRoot(CoordinatorLayout root) {
        this.root = root;
    }
    
    public boolean touchMe(MotionEvent event) {
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
