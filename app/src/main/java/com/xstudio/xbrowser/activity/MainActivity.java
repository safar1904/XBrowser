package com.xstudio.xbrowser.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.xstudio.xbrowser.R;
import com.xstudio.xbrowser.ThisApplication;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThisApplication.getInstance().setMainActivity(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setImmersive(true);
        
        WebView webView = (WebView) findViewById(R.id.main_WebView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///sdcard/index.html");
    }
    
}
