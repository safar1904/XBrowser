package com.xstudio.xbrowser.webkit;

import android.widget.Adapter;
import android.widget.AdapterView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.widget.ArrayAdapter;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.widget.EditText;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import com.xstudio.xbrowser.R;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.TextWatcher;
import com.xstudio.xbrowser.util.TitleAndSubtitleHolder;
import com.xstudio.xbrowser.view.UrlInputEditText;
import android.view.View;

import static android.widget.RelativeLayout.LayoutParams.*;
import static com.xstudio.xbrowser.util.Measurements.*;

public class WebkitToolbar extends RelativeLayout implements View.OnClickListener, WebkitView.Toolbar {

    @Deprecated
    private static final String NO_MATCH_SUGGESTION = " ";
    
    private final AppCompatImageButton moreButton;
    private final AppCompatButton selectWindowButton;
    private final UrlInputBox urlInputBox;
    private final ListView suggestionListView;
    private final ProgressBar progressBar;
    
    private ArrayAdapter<TitleAndSubtitleHolder> suggestionAdapter;
    private SpeechRecognitionCallback speechRecogntionCallback;
    private OnRequestUrlListener onRequestUrlListener;

    public WebkitToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setElevation(dpToPx(3.5F));
        setClipToPadding(false);
        
        RelativeLayout innerLayout = new RelativeLayout(getContext());
        innerLayout.setId(innerLayout.generateViewId());
        innerLayout.setElevation(dpToPx(2F));
        innerLayout.setPadding(dpToPx(7F), 0, dpToPx(7F), 0);
        innerLayout.setClipToPadding(false);
        innerLayout.setClipChildren(false);
        addView(innerLayout, MATCH_PARENT, dpToPx(57F));

        moreButton = new AppCompatImageButton(getContext());
        moreButton.setId(moreButton.generateViewId());
        moreButton.setOnClickListener(this);
        moreButton.setPadding(dpToPx(2F), dpToPx(2F), dpToPx(2F), dpToPx(2F));
        moreButton.setScaleType(AppCompatImageButton.ScaleType.CENTER_INSIDE);
        moreButton.setImageResource(R.drawable.ic_more_vert_black);
        moreButton.setBackgroundResource(R.drawable.gradient_touch_effect);
        moreButton.setImageTintList(ContextCompat.getColorStateList(getContext(), R.drawable.darker_grey_tint));
        LayoutParams params1 = new LayoutParams(dpToPx(30F), dpToPx(30F));
        params1.addRule(CENTER_VERTICAL);
        params1.addRule(ALIGN_PARENT_RIGHT, TRUE);
        innerLayout.addView(moreButton, params1);
        
        selectWindowButton = new AppCompatButton(getContext());
        selectWindowButton.setId(selectWindowButton.generateViewId());
        selectWindowButton.setOnClickListener(this);
        selectWindowButton.setTextSize(12 /*sp*/);
        selectWindowButton.setTextColor(getContext().getResources().getColor(R.color.darkerGrey));
        selectWindowButton.setText("3");
        selectWindowButton.setPadding(dpToPx(2F), dpToPx(2F), dpToPx(2F), dpToPx(2F));
        selectWindowButton.setGravity(Gravity.CENTER);
        selectWindowButton.setBackgroundResource(R.drawable.taper_border);
        LayoutParams params2 = new LayoutParams(dpToPx(20F), dpToPx(20F));
        params2.leftMargin = dpToPx(20F);
        params2.rightMargin = dpToPx(20F);
        params2.addRule(CENTER_VERTICAL);
        params2.addRule(LEFT_OF, moreButton.getId());
        innerLayout.addView(selectWindowButton, params2);

        urlInputBox = new UrlInputBox(getContext());
        urlInputBox.urlInput.setOnImeActionGoListener(onImeActionGoListener);
        LayoutParams params3 = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params3.addRule(CENTER_VERTICAL);
        params3.addRule(LEFT_OF, selectWindowButton.getId());
        params3.addRule(ALIGN_PARENT_LEFT, TRUE);
        innerLayout.addView(urlInputBox, params3);
        
        progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        setProgress(0);
        LayoutParams params4 = new LayoutParams(MATCH_PARENT, dpToPx(2F));
        params4.addRule(BELOW, innerLayout.getId());
        addView(progressBar, params4);
        
        suggestionListView = new ListView(getContext());
        suggestionListView.setOnItemClickListener(onItemClickListener);
        suggestionListView.setVisibility(GONE);
        suggestionListView.setDividerHeight(0);
        LayoutParams params5 = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        params5.addRule(BELOW, innerLayout.getId());
        addView(suggestionListView, params5);
        
    }
    
    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == urlInputBox.actionButton.getId()) {
           if (urlInputBox.urlInput.getText().length() > 0) {
                urlInputBox.urlInput.getText().clear();
            } else if (speechRecogntionCallback != null) {
                speechRecogntionCallback.request(urlInputBox.urlInput);
            }
        }
    }

    @Override
    public void setUrl(String url) {
        if (urlInputBox.urlInput.hasFocus()) {
            urlInputBox.backupUrl = url;
        } else {
            urlInputBox.urlInput.setText(url);
            urlInputBox.urlInput.spanUrl();
        }
    }

    @Override
    public void setTitle(String title) {
        // Not yet implemented
    }

    @Override
    public void setIcon(Bitmap icon) {
        urlInputBox.faviconButton.setImageBitmap(icon);
    }

    @Override
    public void setProgress(int progress) {
        progressBar.setProgress(progress);
        if (progress == 0 || progress == 100) {
            progressBar.setVisibility(GONE);
        } else {
            progressBar.setVisibility(VISIBLE);
        }
    }
    
    public void setOnRequestUrlListener(OnRequestUrlListener listener) {
        onRequestUrlListener = listener;
    }
    
    public void setOnIconClickListener(View.OnClickListener listener) {
        urlInputBox.faviconButton.setOnClickListener(listener);
    }
    
    public EditText getUrlInput() {
        return urlInputBox.urlInput;
    }
    
    public AdapterView<? extends Adapter> getSuggestionAdapterView() {
        return suggestionListView;
    }
    
    public void setSpeechReconitionCallback(SpeechRecognitionCallback callback) {
        speechRecogntionCallback = callback;
    }
    
    public void setSuggestionAdapter(ArrayAdapter<TitleAndSubtitleHolder> adapter) {
        suggestionListView.setAdapter(adapter);
        suggestionAdapter = adapter;
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
    
    private void showSuggestion(boolean show) {
        if (suggestionAdapter == null) {
            return;
        }
        if (show) {
            suggestionListView.setVisibility(VISIBLE);
        } else {
            suggestionListView.setVisibility(GONE);
        }
    }
    
    
    private final OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            TitleAndSubtitleHolder item = (TitleAndSubtitleHolder) adapterView.getItemAtPosition(position);
            String url = item.toString();
            if (onRequestUrlListener != null) {
                urlInputBox.urlInput.clearFocus();
                onRequestUrlListener.requestUrl(url);
            }
        }
    };
    
    private final UrlInputEditText.OnImeActionGoListener onImeActionGoListener = new UrlInputEditText.OnImeActionGoListener() {
        @Override
        public void onImeActionGo(UrlInputEditText view, String url) {
            if (onRequestUrlListener != null) {
                onRequestUrlListener.requestUrl(url);
            }
        }
    };
    
    
    public static interface SpeechRecognitionCallback {
        void request(TextView output);
    }

    public static interface OnRequestUrlListener {
        void requestUrl(String url);
    }

    private class UrlInputBox extends RelativeLayout {
        
        private final UrlInputEditText urlInput;
        private final AppCompatImageButton faviconButton;
        private final AppCompatImageButton actionButton;
        private String backupUrl;

        private UrlInputBox(Context context) {
            super(context);
            setPadding(dpToPx(3F), 0, dpToPx(3F), 0);
            setElevation(dpToPx(2F));
            setClipToPadding(false);
            setFocusable(true);
            setFocusableInTouchMode(true);
            setBackgroundResource(R.drawable.edibox_light_background);

            faviconButton = new AppCompatImageButton(getContext());
            faviconButton.setId(faviconButton.generateViewId());
            faviconButton.setOnClickListener(WebkitToolbar.this);
            faviconButton.setPadding(dpToPx(2F), dpToPx(2F), dpToPx(2F), dpToPx(2F));
            faviconButton.setScaleType(AppCompatImageButton.ScaleType.CENTER_INSIDE);
            faviconButton.setBackgroundResource(R.drawable.gradient_touch_effect);
            faviconButton.setImageResource(android.R.drawable.ic_menu_compass);
            LayoutParams faviconParams = new LayoutParams(dpToPx(30F), dpToPx(30F));
            faviconParams.addRule(CENTER_VERTICAL, TRUE);
            faviconParams.addRule(ALIGN_PARENT_LEFT, TRUE);
            addView(faviconButton, faviconParams);

            actionButton = new AppCompatImageButton(getContext());
            actionButton.setId(actionButton.generateViewId());
            actionButton.setOnClickListener(WebkitToolbar.this);
            actionButton.setPadding(dpToPx(2F), dpToPx(2F), dpToPx(2F), dpToPx(2F));
            actionButton.setScaleType(AppCompatImageButton.ScaleType.CENTER_INSIDE);
            actionButton.setBackgroundResource(R.drawable.gradient_touch_effect);
            actionButton.setImageResource(android.R.drawable.ic_btn_speak_now);
            actionButton.setVisibility(GONE);
            LayoutParams actionButtonParams = new LayoutParams(dpToPx(30F), dpToPx(30F));
            actionButtonParams.addRule(CENTER_VERTICAL, TRUE);
            actionButtonParams.addRule(ALIGN_PARENT_RIGHT, TRUE);
            addView(actionButton, actionButtonParams);

            urlInput = new UrlInputEditText(getContext());
            LayoutParams urlInputParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            urlInputParams.addRule(CENTER_VERTICAL, TRUE);
            urlInputParams.addRule(RIGHT_OF, faviconButton.getId());
            urlInputParams.addRule(LEFT_OF, actionButton.getId());
            addView(urlInput, urlInputParams);

            urlInput.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        int visibleIfFocused = (hasFocus) ? VISIBLE : GONE;
                        int goneIfFocused = (hasFocus) ? GONE : VISIBLE;
                        expandUrlInputBox(hasFocus);
                        updateActionButtonImage();
                        faviconButton.setVisibility(goneIfFocused);
                        actionButton.setVisibility(visibleIfFocused);
                        if (hasFocus) {
                            backupUrl = urlInput.getText().toString();
                        } else {
                            urlInput.setText(backupUrl);
                            urlInput.spanUrl();
                        }
                        WebkitToolbar.this.showSuggestion(hasFocus);
                    }
                });

            urlInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence text, int p2, int p3, int p4) {
                        //TODO: Nothing
                    }

                    @Override
                    public void onTextChanged(CharSequence text, int p2, int p3, int p4) {
                        if (suggestionAdapter != null) {
                            WebkitToolbar.this.suggestionAdapter.getFilter().filter(text.length() > 0 ? text : NO_MATCH_SUGGESTION);
                        }
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
