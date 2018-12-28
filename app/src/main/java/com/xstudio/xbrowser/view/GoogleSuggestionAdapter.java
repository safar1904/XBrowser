package com.xstudio.xbrowser.view;

import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.text.Editable;
import android.widget.TextView;
import android.text.TextWatcher;
import com.xstudio.xbrowser.api.*;
import java.io.*;
import org.xmlpull.v1.*;
import java.util.concurrent.*;
import android.os.*;
import android.support.v4.util.*;
import java.util.*;
import com.xstudio.xbrowser.util.*;

public class GoogleSuggestionAdapter extends ArrayAdapter<String>
  implements TextWatcher {
    
    public GoogleSuggestionAdapter(Context context, int resId) {
        super(context, resId);
        setNotifyOnChange(true);
    }
      
    public GoogleSuggestionAdapter(Context context, int resId, int textViewResId) {
        super(context, resId, textViewResId);
        setNotifyOnChange(true);
    }
    
    public void bindTo(AutoCompleteTextView view) {
        view.setAdapter(this);
        view.addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        if (text.length() >= 1 && !text.equals(" ")) {
            setSuggestionsFor(text.toString());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        //TODO: Nothing
    }

    @Override
    public void afterTextChanged(Editable text) {
        //TODO: Nothing
    }

    @Override
    public int getCount() {
        return Math.min(super.getCount(), 3);
    }
    
    private void setSuggestionsFor(String words) {
        String lang = Locations.getAppLanguage();
        new GetSuggestionWordsTask().execute(words, lang);
    }
    
    class GetSuggestionWordsTask extends AsyncTask<String, Integer, List<String>> {
        
        private String words;
        
        @Override
        protected List<String> doInBackground(String[] params) {
            words = params[0];
            try {
                return GoogleSuggestions.getSuggestionWords(words, params[1]);
            } catch (IOException | XmlPullParserException e) {
                Logger.error("GetSuggestionWordsWorker", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (result != null) {
                addAll(result);
            }
        }
        
    };
    
    
}
