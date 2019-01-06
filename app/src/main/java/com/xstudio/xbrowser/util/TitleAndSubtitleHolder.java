package com.xstudio.xbrowser.util;

public class TitleAndSubtitleHolder {
    
    public final String title;
    public final String subtitle;
    private String hint;
    
    public TitleAndSubtitleHolder(String title, String subtitle) {
        this(title, subtitle, null);
    }
    
    public TitleAndSubtitleHolder(String title, String subtitle, String hint) {
        this.title = title;
        this.subtitle = subtitle;
        this.hint = hint;
    }

    @Override
    public String toString() {
        if (hint != null) return hint;
        if (title != null) return title;
        if (subtitle != null) return subtitle;
        return super.toString();
    }
    
}
