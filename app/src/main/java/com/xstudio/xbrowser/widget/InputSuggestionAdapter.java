package com.xstudio.xbrowser.widget;

import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.text.Editable;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.LayoutInflater;
import com.xstudio.xbrowser.R;
import android.widget.TextView;
import android.text.TextWatcher;
import com.xstudio.xbrowser.util.TitleAndSubtitleHolder;
import android.view.View;
import android.view.ViewGroup;

public class InputSuggestionAdapter extends ArrayAdapter<TitleAndSubtitleHolder>
  implements View.OnClickListener, TextWatcher {
    
    private final int layoutResId;
    private final LayoutInflater inflater;
    private final EditText output;
    private final AdapterView<? extends Adapter> adapterView;
    
    public InputSuggestionAdapter(Context context, int resId, EditText editText, AdapterView<? extends Adapter> adapterView) {
        super(context, resId);
        this.layoutResId = resId;
        this.inflater = LayoutInflater.from(context);
        this.output = editText;
        this.adapterView = adapterView;
        setNotifyOnChange(true);
        output.addTextChangedListener(this);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(layoutResId, parent, false);
            view.setBackgroundResource(R.drawable.abc_item_background_holo_light);
        }
        if (view.getTag() == null && !(view.getTag() instanceof ViewHolder)) {
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(android.R.id.text1);
            viewHolder.subtitle = (TextView) view.findViewById(android.R.id.text2);
            viewHolder.position = position;
            view.setTag(viewHolder);
            
            ImageButton completionButton = (ImageButton) view.findViewById(android.R.id.button1);
            completionButton.setTag(viewHolder);
            completionButton.setOnClickListener(this);
        }
        
        TitleAndSubtitleHolder item = getItem(position);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.title.setText(item.title);
        if (item.subtitle != null) {
            viewHolder.subtitle.setVisibility(View.VISIBLE);
            viewHolder.subtitle.setText(item.subtitle);
        } else {
            viewHolder.subtitle.setVisibility(View.GONE);
        }
        
        view.setOnClickListener(this);
        
        return view;
    }

    @Override
    public void onClick(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (view.getId() == android.R.id.button1) {
            TitleAndSubtitleHolder item = getItem(viewHolder.position);
            String text = item.toString();
            output.setText(text);
            output.setSelection(text.length());
        } else if (adapterView.getOnItemClickListener() != null) {
            adapterView.getOnItemClickListener().onItemClick(adapterView, view, viewHolder.position, getItemId(viewHolder.position));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int p2, int p3, int p4) {
        // Empty
    }

    @Override
    public void onTextChanged(CharSequence s, int p2, int p3, int p4) {
        getFilter().filter(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Empty
    }
    
    
    class ViewHolder {
        TextView title;
        TextView subtitle;
        int position;
    }
    
}
