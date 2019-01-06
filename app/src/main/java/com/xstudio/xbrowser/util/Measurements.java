package com.xstudio.xbrowser.util;

import android.util.DisplayMetrics;
import com.xstudio.xbrowser.ThisApplication;

public class Measurements {
    
    static DisplayMetrics metrics;
    
    public static final int dpToPx(float dp){
        return Math.round(dp * getDisplayMetrics().density);
    }
    
    public static final DisplayMetrics getDisplayMetrics() {
        if (metrics == null) {
            synchronized (Measurements.class) {
                if (metrics == null) {
                    metrics = new DisplayMetrics();
                    ThisApplication.getInstance()
                        .getMainActivity()
                        .getWindowManager()
                        .getDefaultDisplay().getMetrics(metrics);
                }
            }
        }
        return metrics;
    }
    
}
