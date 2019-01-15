package com.xstudio.xbrowser.webkit;

import android.util.AttributeSet;
import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

public class WebkitView extends NestedScrollWebView {
    
    public WebkitView(Context context) {
        super(context);
        init();
    }
    
    public WebkitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        setWebViewClient(new ViewClient());
        setWebChromeClient(new ChromeClient());
    }
    
    
    class ViewClient extends WebViewClient {
        
    }
    
    class ChromeClient extends WebChromeClient {
        
    }
    
}
