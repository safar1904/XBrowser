package com.xstudio.xbrowser.view;


import android.util.AttributeSet;
import android.widget.Button;
import android.content.Context;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.xstudio.xbrowser.R;
import android.widget.RelativeLayout;
import android.view.View;

import static android.widget.RelativeLayout.LayoutParams.*;
import android.view.*;

public class MainToolbar extends RelativeLayout {
    
    public static final int MORE_BUTTON_ID = 1;
    public static final int SELECT_WINDOW_BUTTON_ID = 2;
    
    private ImageButton moreButton;
    private Button selectWindowButton;
    private UrlInputBox urlInputBox;
    
    public MainToolbar(Context context) {
        super(context);
        init();
    }
    
    public MainToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public void setOnImeActionGoListener(UrlInputEditText.OnImeActionGoListener listener) {
        urlInputBox.urlInput.setOnImeActionGoListener(listener);
    }
    
    private void init() {
        setClipChildren(false);
        setClipToPadding(false);
        setPadding(10, 5, 10, 5);
        setFocusable(true);
        setFocusableInTouchMode(true);
        
        initMoreButton();
        initSelectWindowButton();
        initUrlInputBox();
    }
    
    private void initMoreButton() {
        moreButton = new ImageButton(getContext());
        moreButton.setImageResource(R.drawable.ic_more_vert_black);
        moreButton.setBackgroundResource(android.R.color.transparent);
        moreButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
        moreButton.setId(MORE_BUTTON_ID);
        moreButton.setPadding(5, moreButton.getPaddingTop(), 5, moreButton.getPaddingBottom());
        LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.addRule(CENTER_VERTICAL);
        params.addRule(ALIGN_PARENT_RIGHT, TRUE);
        addView(moreButton, params);
    }
    
    private void initSelectWindowButton() {
        selectWindowButton = new Button(getContext());
        selectWindowButton.setText("0");
        selectWindowButton.setBackgroundResource(R.drawable.taper_border);
        selectWindowButton.setId(SELECT_WINDOW_BUTTON_ID);
        LayoutParams params = new LayoutParams(25, 25);
        params.leftMargin = 25;
        params.rightMargin = 25;
        params.addRule(CENTER_VERTICAL);
        params.addRule(LEFT_OF, MORE_BUTTON_ID);
        addView(selectWindowButton, params);
    }
    
    private void initUrlInputBox() {
        urlInputBox = new UrlInputBox(getContext());
        LayoutParams params = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params.addRule(CENTER_VERTICAL);
        params.addRule(LEFT_OF, SELECT_WINDOW_BUTTON_ID);
        params.addRule(ALIGN_PARENT_LEFT, TRUE);
        addView(urlInputBox, params);
        
        urlInputBox.urlInput.setDropDownAnchor(getId());
        urlInputBox.urlInput.setDropDownVerticalOffset(0);
        urlInputBox.urlInput.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean focused) {
                    if (focused) {
                        expandUrlInputBox(true);
                    } else {
                        expandUrlInputBox(false);
                    }
                }
            });
    }
    
    private void expandUrlInputBox(boolean expand) {
        if (getWidth() > 500) {
            return;
        }
        if (expand) {
            selectWindowButton.setVisibility(GONE);
            moreButton.setVisibility(GONE);
        } else {
            selectWindowButton.setVisibility(VISIBLE);
            moreButton.setVisibility(VISIBLE);
        }
    }
    
    class UrlInputBox extends LinearLayout {
        
        UrlInputEditText urlInput;
        
        UrlInputBox(Context context) {
            super(context);
            init();
        }
        
        void init() {
            setElevation(3);
            setClipToPadding(false);
            setBackgroundResource(R.drawable.edibox_light_background);
            setGravity(Gravity.CENTER_VERTICAL);

            urlInput = new UrlInputEditText(getContext());
            GoogleSuggestionAdapter adapter = new GoogleSuggestionAdapter(getContext(), R.layout.main_urlinput_dropdown, R.id.main_urlinput_dropdown_title);
            adapter.bindTo(urlInput);
            addView(urlInput, MATCH_PARENT, WRAP_CONTENT);
        }
        
    }
    
}
