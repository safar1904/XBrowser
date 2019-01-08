package com.xstudio.xbrowser.view;

import android.support.v7.widget.AppCompatEditText;
import android.widget.ArrayAdapter;
import android.util.AttributeSet;
import android.graphics.Color;
import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.net.Uri;
import android.view.View;

public class UrlInputEditText extends AppCompatEditText {
    
    final int GREEN = Color.parseColor("#23AC14");
    final int RED = Color.parseColor("#DB1524");
    final int GREY = Color.parseColor("#B5B5B5");
    
    public enum UrlType { HTTPS_RISK, ONLINE, OFFLINE }
    
    private OnImeActionGoListener onImeActionGoListener;
    private UrlType urlType;
    
    public UrlInputEditText(Context context) {
        super(context);
        init();
    }
    
    public UrlInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void onEditorAction(int actionCode) {
        super.onEditorAction(actionCode);
        if (actionCode == EditorInfo.IME_ACTION_GO && onImeActionGoListener != null) {
            onImeActionGoListener.onImeActionGo(this, getText().toString());
        }
        clearFocus();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (!focused) {
            setSelection(0);
            spanUrl();
            showSoftKeyboard(false);
        } else {
            clearSpan();
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
    
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            clearFocus();
        }
        return false;
    }
    
    private void showSoftKeyboard(boolean show) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        } else {
            imm.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
    public void setOnImeActionGoListener(OnImeActionGoListener listener) {
        onImeActionGoListener = listener;
    }
    
    public void setUrlType(UrlType type) {
        if (type != urlType) {
            urlType = type;
            spanUrl();
        }
    }
    
    public void spanUrl() {
        final String wholeText = getText().toString();
        if (wholeText.length() == 0) {
            return;
        }
        final String https = "https";
        final String schemeDelim = "://";
        final Spannable span = new SpannableString(wholeText);
        final Uri uri = Uri.parse(wholeText);
        
        if (uri.getHost() != null && uri.getHost().length() > 0 && uri.getPath() != null) {
            final int hostIndex = wholeText.indexOf(uri.getHost());
            final int start = hostIndex + uri.getHost().length();
            final int end = wholeText.length();
            span.setSpan(getGreyColorSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        boolean schemeHasSpanned = false;
        if (wholeText.startsWith(https + schemeDelim)) {
            if (urlType == UrlType.HTTPS_RISK) {
                span.setSpan(getRedColorSpan(), 0, https.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new StrikethroughSpan(), 0, https.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                schemeHasSpanned = true;
            } else if (urlType == UrlType.ONLINE) {
                span.setSpan(getGreenColorSpan(), 0, https.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                schemeHasSpanned = true;
            }
            final int schemeIndex = wholeText.indexOf(schemeDelim, 0);
            if (schemeIndex > 0) {
                span.setSpan(getGreyColorSpan(), schemeIndex, schemeIndex + schemeDelim.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } 
        
        if (!schemeHasSpanned && ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))) {
            span.setSpan(getGreyColorSpan(), 0, (uri.getScheme() + schemeDelim).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        setText(span, BufferType.SPANNABLE);
    }
    
    public void clearSpan() {
        setText(new SpannableString(getText().toString()), BufferType.SPANNABLE);
    }
    
    private ForegroundColorSpan getGreenColorSpan() {
        return new ForegroundColorSpan(GREEN);
    }
    
    private ForegroundColorSpan getGreyColorSpan() {
        return new ForegroundColorSpan(GREY);
    }
    
    private ForegroundColorSpan getRedColorSpan() {
        return new ForegroundColorSpan(RED);
    }
    
    private void init() {
        setBackgroundResource(android.R.color.transparent);
        setSelectAllOnFocus(true);
        setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        setImeOptions(EditorInfo.IME_ACTION_GO);
        setGravity(Gravity.CENTER_VERTICAL);
        setHint("Search or type URL");
        setTextAppearance(getContext(), android.R.style.TextAppearance);
        setUrlType(UrlType.OFFLINE);
    }
    
    public static interface OnImeActionGoListener {
        void onImeActionGo(UrlInputEditText view, String url);
    }
    
}
