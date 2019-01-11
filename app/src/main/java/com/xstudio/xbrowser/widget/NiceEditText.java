package com.xstudio.xbrowser.widget;

import android.util.AttributeSet;
import android.support.v7.widget.AppCompatEditText;
import android.content.Context;
import android.text.Editable;
import android.view.KeyEvent;
import android.graphics.Rect;
import android.text.SpannableString;
import com.xstudio.xbrowser.text.TextSpanner;
import android.text.TextWatcher;

public class NiceEditText extends AppCompatEditText {
    
    public static enum SpanStrategy { SPAN_ALWAYS, SPAN_WHEN_FOCUS, SPAN_WHEN_NOT_FOCUS, NO_SPAN }
    
    private SpanStrategy spanStrategy = SpanStrategy.SPAN_ALWAYS;
    private TextSpanner textSpanner;
    
    public NiceEditText(Context context) {
        super(context);
    }
    
    public NiceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused && spanStrategy == SpanStrategy.SPAN_WHEN_NOT_FOCUS) {
            clearSpan();
        } else if (!focused && spanStrategy == SpanStrategy.SPAN_WHEN_FOCUS) {
            clearSpan();
        } else {
            spanAll();
        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            clearFocus();
        }
        return super.onKeyPreIme(keyCode, event);
    }
    
    public void setSpanStrategy(SpanStrategy strategy) {
        spanStrategy = strategy;
    }
    
    public void setTextSpanner(TextSpanner spanner) {
        textSpanner = spanner;
    }
    
    public void forceSpanAll() {
        if (textSpanner != null) {
            setTextKeepState(textSpanner.span(getText().toString()), BufferType.SPANNABLE);
        }
    }
    
    public void clearSpan() {
        setTextKeepState(new SpannableString(getText().toString()), BufferType.SPANNABLE);
    }
    
    private void spanAll() {
        if (textSpanner != null) {
            boolean span = false;
            if (spanStrategy == SpanStrategy.SPAN_ALWAYS) {
                span = true;
            } else if (spanStrategy == SpanStrategy.SPAN_WHEN_FOCUS && hasFocus()) {
                span = true;
            } else if (spanStrategy == SpanStrategy.SPAN_WHEN_NOT_FOCUS && !hasFocus()) {
                span = true;
            }
            if (span) {
                setTextKeepState(textSpanner.span(getText().toString()), BufferType.SPANNABLE);
            }
        }
    }
    
}
