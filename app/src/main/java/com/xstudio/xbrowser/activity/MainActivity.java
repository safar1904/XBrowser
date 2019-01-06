package com.xstudio.xbrowser.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.xstudio.xbrowser.R;
import com.xstudio.xbrowser.ThisApplication;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.xstudio.xbrowser.webkit.*;
import android.webkit.*;
import com.xstudio.xbrowser.view.*;
import android.widget.*;
import com.xstudio.xbrowser.util.*;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThisApplication.getInstance().setMainActivity(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        WebkitToolbar toolbar = (WebkitToolbar) findViewById(R.id.main_toolbar);
        InputSuggestionAdapter suggestionAdapter = new InputSuggestionAdapter(this, R.layout.url_input_suggestion_item, toolbar.getUrlInput(), toolbar.getSuggestionAdapterView());
        suggestionAdapter.add(new TitleAndSubtitleHolder("Hello World", "http://www.google.com", "http://www.google.com"));
        toolbar.setSuggestionAdapter(suggestionAdapter);
        
        final WebkitView webView = (WebkitView) findViewById(R.id.main_webview);
        webView.setToolbar(toolbar);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///sdcard/index.html");
        
        toolbar.setOnRequestUrlListener(new WebkitToolbar.OnRequestUrlListener() {
            @Override
            public void requestUrl(String url) {
                webView.loadUrl(url);
            }
        });
        
    }

    @Override
    public void onBackPressed() {
        if (OnBackPressedHelper.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
    
}
