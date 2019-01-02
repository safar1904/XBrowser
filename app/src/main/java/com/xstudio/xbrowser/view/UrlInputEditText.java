package com.xstudio.xbrowser.view;

import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.util.AttributeSet;
import android.graphics.Color;
import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.text.InputType;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.net.Uri;
import android.view.View;

public class UrlInputEditText extends AppCompatAutoCompleteTextView {
    
    final String GREEN = "#23AC14";
    final String RED = "#DB1524";
    final String GREY = "#B5B5B5";
    
    public enum UrlType { HTTPS_RISK, ONLINE, OFFLINE }
    
    private OnImeActionGoListener onImeActionGoListener;
    private UrlType urlType;
    
    private ForegroundColorSpan greenColorSpan;
    private ForegroundColorSpan greyColorSpan;
    
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
            onImeActionGoListener.onImeActionGo(getText().toString());
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
    
    private void showSoftKeyboard(boolean show) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            //imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        } else {
            //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
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
        final String https = "https";
        final String schemeDelim = "://";
        final String wholeText = getText().toString();
        final Spannable span = new SpannableString(wholeText);
        final Uri uri = Uri.parse(wholeText);
        
        if (uri.getHost() != null && uri.getPath() != null) {
            final int hostIndex = wholeText.indexOf(uri.getHost());
            final String path = uri.getPath();
            final int start = wholeText.indexOf(path, hostIndex);
            if (start == hostIndex + uri.getHost().length()) {
                final int end = wholeText.length();
                span.setSpan(new ForegroundColorSpan(Color.parseColor(GREY)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        
        boolean schemeHasSpanned = false;
        if (wholeText.startsWith(https + schemeDelim)) {
            if (urlType == UrlType.HTTPS_RISK) {
                span.setSpan(new ForegroundColorSpan(Color.parseColor(RED)), 0, https.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
        if (!schemeHasSpanned && uri.getScheme() != null) {
            final String scheme = uri.getScheme() + schemeDelim;
            span.setSpan(getGreyColorSpan(), 0, scheme.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        setText(span, BufferType.SPANNABLE);
    }
    
    public void clearSpan() {
        setText(new SpannableString(getText().toString()), BufferType.SPANNABLE);
    }
    
    private ForegroundColorSpan getGreenColorSpan() {
        if (greenColorSpan == null) {
            greenColorSpan = new ForegroundColorSpan(Color.parseColor(GREEN));
        }
        return greenColorSpan;
    }
    
    private ForegroundColorSpan getGreyColorSpan() {
        if (greyColorSpan == null) {
            greyColorSpan = new ForegroundColorSpan(Color.parseColor(GREY));
        }
        return greyColorSpan;
    }
    
    private void init() {
        setBackgroundResource(android.R.color.transparent);
        setSelectAllOnFocus(true);
        setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        setImeOptions(EditorInfo.IME_ACTION_GO);
        setGravity(Gravity.CENTER_VERTICAL);
        
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.M) {
            setTextAppearance(getContext(), android.R.style.TextAppearance);
        } else {
            setTextAppearance(android.R.style.TextAppearance);
        }
        
        setThreshold(1);
        setHint("Search or type URL");
        setUrlType(UrlType.ONLINE);
    }
    
    public static interface OnImeActionGoListener {
        void onImeActionGo(String url);
    }
    
}
