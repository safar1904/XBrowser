package com.xstudio.xbrowser.webkit;

import android.util.AttributeSet;
import android.content.Context;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import android.graphics.*;

public class WebkitView extends ObservableWebView {
    
    public WebkitView(Context context) {
        super(context);
    }
    
    public WebkitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public static interface Toolbar {
        void setUrl(String url);
        void setTitle(String title);
        void setIcon(Bitmap icon);
        void setProgress(int progress);
    }
    
}
