package com.xstudio.xbrowser.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.xstudio.xbrowser.R;
import com.xstudio.xbrowser.ThisApplication;
import com.xstudio.xbrowser.widget.*;
import com.xstudio.xbrowser.util.*;
import android.content.*;
import android.speech.*;
import java.util.*;
import android.widget.TextView;
import android.support.v7.widget.*;
import android.view.*;
import android.graphics.*;
import android.support.design.widget.*;
import android.view.inputmethod.*;
import android.webkit.WebView;
import com.xstudio.xbrowser.webkit.WebkitToolbar;
import com.xstudio.xbrowser.view.AppMenu;
import com.xstudio.xbrowser.webkit.WebkitView;
import com.github.ksoichiro.android.observablescrollview.*;
import android.support.v4.view.*;
import com.xstudio.xbrowser.webkit.*;
import com.xstudio.xbrowser.webkit.WebkitLayout;

public class MainActivity extends AppCompatActivity {
    
    WebkitToolbar toolbar;
    WebView webView;
    AppMenu menu;
    ViewGroup rootView;
    WebkitLayout webkitLayout;
    
    public static final int URL_INPUT_SPEECH_REQUEST = 0x1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThisApplication.getInstance().setMainActivity(this);
        setContentView(R.layout.test);
        
        rootView = (ViewGroup) findViewById(R.id.main_rootlayout);
        
        toolbar = new WebkitToolbar(this, null);
        InputSuggestionAdapter suggestionAdapter = new InputSuggestionAdapter(this, R.layout.url_input_suggestion_item, toolbar.getUrlInput(), toolbar.getSuggestionAdapterView());
        suggestionAdapter.add(new TitleAndSubtitleHolder("Hello World", "http://www.google.com", "http://www.google.com"));
        toolbar.setSuggestionAdapter(suggestionAdapter);
        
        webView = new ObservableWebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///sdcard/index.html");
        
        toolbar.setOnRequestUrlListener(new WebkitToolbar.OnRequestUrlListener() {
            @Override
            public void requestUrl(String url) {
                webView.loadUrl(url);
            }
        });
        
        toolbar.setSpeechReconitionCallback(new WebkitToolbar.SpeechRecognitionCallback() {

            @Override
            public void request(TextView output) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                startActivityForResult(intent, URL_INPUT_SPEECH_REQUEST);
            }
                
        });
        
        webkitLayout = (WebkitLayout) findViewById(R.id.webkit_layout);
        webkitLayout.setContentOffset(Measurements.dpToPx(57));
        webkitLayout.setToolbar(toolbar);
        webkitLayout.setContent(webView);
        
        View v = getLayoutInflater().inflate(R.layout.main_menu_navbar, null, false);
        
        try {
            menu = new AppMenu(this, R.layout.main_menu_item, R.dimen.menu_item_width, R.dimen.menu_item_height);
            menu.setHeaderView(v);
            getMenuInflater().inflate(R.menu.main_menu, menu);
            toolbar.getMoreButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menu.show(toolbar.getMoreButton());
                }
            });
        } catch (Exception e) {
            StringBuilder str = new StringBuilder();
            for (StackTraceElement trace : e.getStackTrace()) {
                str.append("Class: " + trace.getClassName() + " Method: " + trace.getMethodName() + " Line: " + trace.getLineNumber() + "\n");
            }
            str.append(e.getMessage());
            Logger.error("", str.toString());
        }
        
     
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case URL_INPUT_SPEECH_REQUEST:
                if (resultCode == RESULT_OK) {
                    List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (results.size() > 0) {
                        String result = results.get(0);
                        toolbar.getUrlInput().setText(result);
                        toolbar.getUrlInput().setSelection(result.length());
                    }
                }
                break;
            default:
                break;
        }
    }

}
