package com.xstudio.xbrowser.webkit;

import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.content.Context;
import com.xstudio.xbrowser.view.Views;
import com.xstudio.xbrowser.widget.ViewSelector;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.*;

public class WebkitView extends WebView implements Tab {
    
    protected Client mClient;
    protected ChromeClient mChromeClient;
    
    private Bitmap mFavicon;
    private ViewSelector.ItemCallback mTabCallback;
    
    public WebkitView(Context context) {
        super(context);
        init();
    }
    
    public WebkitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public Bitmap getIcon() {
        return mFavicon;
    }

    @Override
    public Bitmap getScreenshot() {
        return Views.getBitmapFromView(this);
    }

    @Override
    public int getIndex() {
        return mTabCallback.getIndex();
    }

    @Override
    public <T extends ViewSelector.ItemCallback> void setCallback(T callback) {
        mTabCallback = callback;
    }
    
    @Override
    public void select() {
        // EMPTY
    }

    @Override
    public void close() {
        // EMPTY
    }

    @Override
    public void updateInfo() {
        updateIcon();
        updateTitle();
        updateScreenshot();
    }
    
    private void init() {
        mClient = new Client();
        mChromeClient = new ChromeClient();
        setWebViewClient(mClient);
        setWebChromeClient(mChromeClient);
    }
    
    
    public static interface Toolbar {
        void setUrl(String url);
        void setIcon(Bitmap icon);
        void setTitle(String title);
        void setProgress(int progress);
    }
    
    private void updateIcon() {
        if (mTabCallback != null) {
            mTabCallback.updateIcon(mFavicon);
        }
    }
    
    private void updateTitle() {
        if (mTabCallback != null) {
            mTabCallback.updateTitle(getTitle());
        }
    }
    
    private void updateScreenshot() {
        if (mTabCallback != null) {
            mTabCallback.updateScreenshot(Views.getBitmapFromView(this));
        }
    }
    
    
    class Client extends WebViewClient {
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            updateScreenshot();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            updateScreenshot();
        }

    }
    
    class ChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            updateTitle();
        }
        
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
            mFavicon = icon;
            updateIcon();
        }
        
    }
    
}
