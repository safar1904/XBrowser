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
import android.view.View.*;

public class MainActivity extends AppCompatActivity 
        implements TabSelector.OnTabSelectedListener {
    
    WebkitToolbar toolbar;
    WebkitView currentTab;
    AppMenu menu;
    ViewGroup rootView;
    WebkitLayout webkitLayout;
    TabSelector mTabSelector;
    
    public static final int URL_INPUT_SPEECH_REQUEST = 0x1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThisApplication.getInstance().setMainActivity(this);
        setContentView(R.layout.test);
        rootView = (ViewGroup) findViewById(R.id.main_rootlayout);
        
        currentTab = new WebkitView(this);
        currentTab.getSettings().setJavaScriptEnabled(true);
        currentTab.loadUrl("file:///sdcard/index.html");
        
        WebkitView tab2 = new WebkitView(this);
        tab2.getSettings().setJavaScriptEnabled(true);
        tab2.loadUrl("http://www.google.com");
        
        toolbar = new WebkitToolbar(this, null);
        InputSuggestionAdapter suggestionAdapter = new InputSuggestionAdapter(this, R.layout.url_input_suggestion_item, toolbar.getUrlInput(), toolbar.getSuggestionAdapterView());
        suggestionAdapter.add(new TitleAndSubtitleHolder("Hello World", "http://www.google.com", "http://www.google.com"));
        toolbar.setSuggestionAdapter(suggestionAdapter);
        toolbar.setOnRequestUrlListener(new WebkitToolbar.OnRequestUrlListener() {
            @Override
            public void requestUrl(String url) {
                currentTab.loadUrl(url);
            }
        });
        toolbar.setSpeechRecognitionCallback(new WebkitToolbar.SpeechRecognitionCallback() {
            @Override
            public void request(TextView output) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                startActivityForResult(intent, URL_INPUT_SPEECH_REQUEST);
            }
        });
        
        View v = getLayoutInflater().inflate(R.layout.main_menu_navbar, null, false);
        menu = new AppMenu(this, R.layout.main_menu_item, R.dimen.menu_item_width, R.dimen.menu_item_height);
        menu.setHeaderView(v);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        toolbar.getMoreButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.show(toolbar.getMoreButton());
            }
        });
        
        webkitLayout = (WebkitLayout) findViewById(R.id.webkit_layout);
        webkitLayout.setContentOffset(Measurements.dpToPx(57));
        webkitLayout.setToolbar(toolbar);
        webkitLayout.setContent(currentTab);
        
        mTabSelector = new TabSelector(this);
        mTabSelector.setOnTabSelectedListener(this);
        toolbar.getSelectTabButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mTabSelector.show();
            }
        });
        mTabSelector.addTab(currentTab);
        mTabSelector.addTab(tab2);
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

    @Override
    public void onTabSelected(Tab tab) {
        if (tab instanceof WebkitView) {
            currentTab = (WebkitView) tab;
            webkitLayout.setContent(currentTab);
        }
    }

}
