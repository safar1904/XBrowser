package com.xstudio.xbrowser.view;

import android.util.AttributeSet;
import android.widget.Button;
import android.content.Context;
import android.text.Editable;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.xstudio.xbrowser.R;
import android.widget.RelativeLayout;
import android.text.TextWatcher;
import android.view.View;

import static android.widget.RelativeLayout.LayoutParams.*;

public class MainToolbar extends RelativeLayout implements View.OnClickListener {
    
    private final ImageButton moreButton;
    private final Button selectWindowButton;
    private final UrlInputBox urlInputBox;
    
    public MainToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClipChildren(false);
        setClipToPadding(false);
        setPadding(10, 5, 10, 5);
        setFocusable(true);
        setFocusableInTouchMode(true);

        moreButton = new ImageButton(getContext());
        moreButton.setId(moreButton.generateViewId());
        moreButton.setOnClickListener(this);
        moreButton.setPadding(4, 4, 4, 4);
        moreButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
        moreButton.setImageResource(R.drawable.ic_more_vert_black);
        moreButton.setBackgroundResource(R.drawable.gradient_touch_effect);
        LayoutParams params1 = new LayoutParams(45, 45);
        params1.addRule(CENTER_VERTICAL);
        params1.addRule(ALIGN_PARENT_RIGHT, TRUE);
        addView(moreButton, params1);

        selectWindowButton = new Button(getContext());
        selectWindowButton.setId(selectWindowButton.generateViewId());
        selectWindowButton.setOnClickListener(this);
        selectWindowButton.setText("1");
        selectWindowButton.setPadding(2, 2, 2, 2);
        selectWindowButton.setGravity(Gravity.CENTER);
        selectWindowButton.setBackgroundResource(R.drawable.taper_border);
        LayoutParams params2 = new LayoutParams(30, 30);
        params2.leftMargin = 25;
        params2.rightMargin = 25;
        params2.addRule(CENTER_VERTICAL);
        params2.addRule(LEFT_OF, moreButton.getId());
        addView(selectWindowButton, params2);

        urlInputBox = new UrlInputBox(getContext());
        LayoutParams params3 = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params3.addRule(CENTER_VERTICAL);
        params3.addRule(LEFT_OF, selectWindowButton.getId());
        params3.addRule(ALIGN_PARENT_LEFT, TRUE);
        addView(urlInputBox, params3);

        urlInputBox.urlInput.setDropDownAnchor(getId());
        urlInputBox.urlInput.setDropDownVerticalOffset(0);
    }
    
    public void setOnImeActionGoListener(UrlInputEditText.OnImeActionGoListener listener) {
        urlInputBox.urlInput.setOnImeActionGoListener(listener);
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == urlInputBox.actionButton.getId()) {
            if (urlInputBox.urlInput.getText().length() > 0) {
                urlInputBox.urlInput.getText().clear();
            } else {
                
            }
        }
    }
    
    private void expandUrlInputBox(boolean expand) {
        if (expand) {
            selectWindowButton.setVisibility(GONE);
            moreButton.setVisibility(GONE);
        } else {
            selectWindowButton.setVisibility(VISIBLE);
            moreButton.setVisibility(VISIBLE);
        }
    }
    
    class UrlInputBox extends RelativeLayout {
        
        final UrlInputEditText urlInput;
        final ImageButton faviconButton;
        final ImageButton actionButton;
        
        String backupUrl;
        
        UrlInputBox(Context context) {
            super(context);
            setPadding(5, 0, 5, 0);
            setElevation(3);
            setClipToPadding(false);
            setBackgroundResource(R.drawable.edibox_light_background);

            faviconButton = new ImageButton(getContext());
            faviconButton.setId(faviconButton.generateViewId());
            faviconButton.setOnClickListener(MainToolbar.this);
            faviconButton.setPadding(4, 4, 4, 4);
            faviconButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            faviconButton.setBackgroundResource(R.drawable.gradient_touch_effect);
            faviconButton.setImageResource(android.R.drawable.ic_menu_search);
            LayoutParams faviconParams = new LayoutParams(45, 45);
            faviconParams.addRule(CENTER_VERTICAL, TRUE);
            faviconParams.addRule(ALIGN_PARENT_LEFT, TRUE);
            addView(faviconButton, faviconParams);
            
            actionButton = new ImageButton(getContext());
            actionButton.setId(actionButton.generateViewId());
            actionButton.setOnClickListener(MainToolbar.this);
            actionButton.setPadding(4, 4, 4, 4);
            actionButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            actionButton.setBackgroundResource(R.drawable.gradient_touch_effect);
            actionButton.setImageResource(android.R.drawable.ic_btn_speak_now);
            actionButton.setVisibility(GONE);
            LayoutParams actionButtonParams = new LayoutParams(45, 45);
            actionButtonParams.addRule(CENTER_VERTICAL, TRUE);
            actionButtonParams.addRule(ALIGN_PARENT_RIGHT, TRUE);
            addView(actionButton, actionButtonParams);
            
            urlInput = new UrlInputEditText(getContext());
            GoogleSuggestionAdapter adapter = new GoogleSuggestionAdapter(getContext(), R.layout.main_urlinput_dropdown, R.id.main_urlinput_dropdown_title);
            adapter.add("hello World");
            adapter.bindTo(urlInput);
            LayoutParams urlInputParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            urlInputParams.addRule(CENTER_VERTICAL, TRUE);
            urlInputParams.addRule(RIGHT_OF, faviconButton.getId());
            urlInputParams.addRule(LEFT_OF, actionButton.getId());
            addView(urlInput, urlInputParams);
            
            urlInput.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    final int visibleIfFocused = (hasFocus) ? VISIBLE : GONE;
                    final int goneIfFocused = (hasFocus) ? GONE : VISIBLE;
                    expandUrlInputBox(hasFocus);
                    updateActionButtonImage();
                    faviconButton.setVisibility(goneIfFocused);
                    actionButton.setVisibility(visibleIfFocused);
                    if (hasFocus) {
                        backupUrl = urlInput.getText().toString();
                    } else {
                        urlInput.setText(backupUrl);
                    }
                }
                    
            });
            
            urlInput.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence text, int p2, int p3, int p4) {
                    // TODO: Nothing
                }

                @Override
                public void onTextChanged(CharSequence text, int p2, int p3, int p4) {
                    // TODO: Nothing
                }

                @Override
                public void afterTextChanged(Editable text) {
                    updateActionButtonImage();
                }
                
            });
        }
        
        private void updateActionButtonImage() {
            if (urlInput.getText().length() > 0) {
                actionButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            } else {
                actionButton.setImageResource(android.R.drawable.ic_btn_speak_now);
            }
        }
        
    }
    
}
