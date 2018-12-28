package com.xstudio.xbrowser.api;

import java.util.Locale;

public final class Locations {
    
    public static String getAppLanguage() {
        return Locale.getDefault().getLanguage();
    }
    
}
